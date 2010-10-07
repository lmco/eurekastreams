/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.EntityCacheUpdater;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByIds;
import org.eurekastreams.server.search.modelview.OrganizationModelView;

/**
 * Responsible for the maintenance of the OrganizationHierarchyCache.
 */
public class OrganizationHierarchyCacheLoader extends CachedDomainMapper implements EntityCacheUpdater<Organization>
{
    /**
     * Logger instance.
     */
    private Log log = LogFactory.getLog(OrganizationHierarchyCacheLoader.class);

    /**
     * Mapper to read all organizations.
     */
    private GetOrganizationsByIds organizationMapper;

    /**
     *Initialize the Organization hierarchy cache - intended to run on system start-up.
     */
    public void initialize()
    {
        log.info("Initializing the Organization Cache");

        long start = System.currentTimeMillis();
        long stepStart;

        // maps to hold children and recursive children
        Map<Long, List<Long>> childMap = new HashMap<Long, List<Long>>();
        Map<Long, List<Long>> recursiveChildMap = new HashMap<Long, List<Long>>();
        Map<Long, List<Long>> parentMap = new HashMap<Long, List<Long>>();

        // ------
        // build the child hierarchy
        log.info("Querying and building direct children cache");
        stepStart = System.currentTimeMillis();
        Long rootOrgId = queryAllOrganizations(childMap);
        log.info("Done: " + (System.currentTimeMillis() - stepStart) + " ms.");

        if (rootOrgId == 0)
        {
            log.error("No root organization found while warming the Org hierarchy cache.  "
                    + "If you haven't created a root org yet, disregard this notice.");
            return;
        }

        // ------
        // build the recursive child hierarchy
        log.info("Populating the recursive organization hierarchy cache");
        stepStart = System.currentTimeMillis();
        recurseChildren(rootOrgId, childMap, recursiveChildMap);
        log.info("Done: " + (System.currentTimeMillis() - stepStart) + " ms.");

        // ------
        // populate the parent hierarchy
        log.info("Populating the parent organization hierarchy cache");
        stepStart = System.currentTimeMillis();
        populateParentOrgCache(rootOrgId, childMap, recursiveChildMap, parentMap);
        log.info("Done " + (System.currentTimeMillis() - stepStart) + " ms.");

        log.info("Storing org hierarchy to cache.");
        stepStart = System.currentTimeMillis();

        log.info("Stored hierarchy to cache - " + (System.currentTimeMillis() - stepStart) + " ms.");
        writeToCache(rootOrgId, childMap, recursiveChildMap, parentMap);
        log.info("Organization cache initialization completed - " + (System.currentTimeMillis() - start) + " ms.");
    }

    /**
     * Query the database for all organizations, only requesting the id and parent org id. Populate the direct children
     * organization cache collection. This should be as efficient as possible, making a single query to the database.
     * 
     * Remarks: Consider chunking this in batches of configurable size
     * 
     * @param childOrgMap
     *            map to store OrgId -> Child OrgIds
     * 
     * @return the root org id
     */
    private Long queryAllOrganizations(final Map<Long, List<Long>> childOrgMap)
    {
        Long rootOrgId = 0L;

        // gets all orgs and puts them in cache as OrganizationModelView objects
        List<OrganizationModelView> allOrgs = organizationMapper.execute();

        // loop across the results, storing children in collections of their direct parents
        for (OrganizationModelView org : allOrgs)
        {
            Long orgId = org.getEntityId();
            String shortName = org.getShortName();
            Long parentOrgId = org.getParentOrganizationId();

            // add the short name to cache
            getCache().set(CacheKeys.ORGANIZATION_BY_SHORT_NAME + shortName, orgId);

            if (orgId.equals(parentOrgId))
            {
                // found the root organization
                rootOrgId = orgId;
                log.info("Found root organization: " + orgId);
                continue; // don't add it as a direct child of itself in the cache
            }

            // add org as a direct child of parent org
            if (!childOrgMap.containsKey(parentOrgId))
            {
                childOrgMap.put(parentOrgId, new ArrayList<Long>());
            }

            if (!childOrgMap.get(parentOrgId).contains(orgId))
            {
                childOrgMap.get(parentOrgId).add(orgId);
            }
        }
        return rootOrgId;
    }

    /**
     * Return a set of all children organization ids recursively while populating the recursive children collection for
     * the input and org and all recursive children.
     * 
     * @param inOrgId
     *            the org to recurse
     * @param childOrgMap
     *            map of child org ids for parent org ids
     * @param recursiveChildMap
     *            map of org id to recursive child org ids
     * @return the list of all of the input org id's child organization, recursively
     */
    private List<Long> recurseChildren(final long inOrgId, final Map<Long, List<Long>> childOrgMap,
            final Map<Long, List<Long>> recursiveChildMap)
    {
        if (!childOrgMap.containsKey(inOrgId))
        {
            // leaf node
            return new ArrayList<Long>();
        }

        List<Long> recursiveChildren = new ArrayList<Long>();
        for (Long childOrgId : childOrgMap.get(inOrgId))
        {
            if (!recursiveChildren.contains(childOrgId))
            {
                recursiveChildren.add(childOrgId);
            }
            recursiveChildren.addAll(recurseChildren(childOrgId, childOrgMap, recursiveChildMap));
        }

        // update the cache with all the recursive children
        recursiveChildMap.put(inOrgId, recursiveChildren);

        // return the children for recursion
        return recursiveChildren;
    }

    /**
     * Populate the parent org hash using the child org hash and root org.
     * 
     * @param parentOrgId
     *            the starting point for the parent org populating
     * @param childMap
     *            Map of org id to direct child org ids
     * @param recursiveChildMap
     *            Map of org id to recursive children org id
     * @param parentMap
     *            Map of org id to recursive parent org id
     * 
     */
    private void populateParentOrgCache(final Long parentOrgId, final Map<Long, List<Long>> childMap,
            final Map<Long, List<Long>> recursiveChildMap, final Map<Long, List<Long>> parentMap)
    {
        if (!recursiveChildMap.containsKey(parentOrgId))
        {
            return;
        }

        // add parent org to all children, recursively
        for (long childOrgId : recursiveChildMap.get(parentOrgId))
        {
            if (!parentMap.containsKey(childOrgId))
            {
                parentMap.put(childOrgId, new ArrayList<Long>());
            }

            if (!parentMap.get(childOrgId).contains(parentOrgId))
            {
                parentMap.get(childOrgId).add(parentOrgId);
            }
        }

        // recurse for direct children - must recurse from the direct children instead of recursive children because
        // order is important
        for (long childOrgId : childMap.get(parentOrgId))

        {
            populateParentOrgCache(childOrgId, childMap, recursiveChildMap, parentMap);
        }
    }

    /**
     * Write the org hierarchy information to cache.
     * 
     * @param rootOrgId
     *            The root organization id
     * @param childMap
     *            Map of org Id to direct children org ids
     * @param recursiveChildMap
     *            Map of org id to recursive child org ids
     * @param parentMap
     *            Map of org id to recursive parent orgs
     */
    private void writeToCache(final Long rootOrgId, final Map<Long, List<Long>> childMap,
            final Map<Long, List<Long>> recursiveChildMap, final Map<Long, List<Long>> parentMap)
    {
        // write the org parents
        for (Long orgId : parentMap.keySet())
        {
            getCache().setList(CacheKeys.ORGANIZATION_PARENTS_RECURSIVE + orgId, parentMap.get(orgId));
        }

        // write the org children
        for (Long orgId : childMap.keySet())
        {
            getCache().set(CacheKeys.ORGANIZATION_DIRECT_CHILDREN + orgId, new HashSet<Long>(childMap.get(orgId)));
        }

        // write the recursive org children
        for (Long orgId : recursiveChildMap.keySet())
        {
            getCache().set(CacheKeys.ORGANIZATION_RECURSIVE_CHILDREN + orgId,
                    new HashSet<Long>(recursiveChildMap.get(orgId)));
        }
    }

    /**
     * Organization updater implementation - fired when an organization entity is updated. Nothing that we currently
     * store in cache can change, so this method does nothing right now.
     * 
     * @param inUpdatedOrganization
     *            the organization just updated
     */
    @Override
    public void onPostUpdate(final Organization inUpdatedOrganization)
    {
        // clear the cache
        log.info("Removing org #" + inUpdatedOrganization.getId() + " from cache onUpdate.");
        getCache().delete(CacheKeys.ORGANIZATION_BY_ID + inUpdatedOrganization.getId());
        log.info("Removing leader ids for org #" + inUpdatedOrganization.getId() + " from cache onUpdate.");
        getCache().delete(CacheKeys.ORGANIZATION_LEADERS_BY_ORG_ID + inUpdatedOrganization.getId());

    }

    /**
     * Organization persist implementation - fired when an organization entity is persisted.
     * 
     * @param inNewOrganization
     *            the organization just created
     */
    @Override
    public void onPostPersist(final Organization inNewOrganization)
    {
        if (log.isInfoEnabled())
        {
            log.info("Adding new organization to cache - Organization: " + inNewOrganization.toString());
        }
        long orgId = inNewOrganization.getId();
        if (orgId <= 0)
        {
            throw new RuntimeException("Can't add this organization to cache until "
                    + "it's been inserted in the database.");
        }

        long parentOrgId = inNewOrganization.getParentOrganization().getId();

        log.info("Cleaning Org Tree from cache onPostPersist.");
        getCache().delete(CacheKeys.ORGANIZATION_TREE_DTO);

        log.info("Deleting direct children org id cache of parent org to " + orgId + ": " + parentOrgId);
        getCache().delete(CacheKeys.ORGANIZATION_DIRECT_CHILDREN + parentOrgId);
    }

    /**
     * @param inOrganizationMapper
     *            the organizationMapper to set
     */
    public void setOrganizationMapper(final GetOrganizationsByIds inOrganizationMapper)
    {
        this.organizationMapper = inOrganizationMapper;
    }
}
