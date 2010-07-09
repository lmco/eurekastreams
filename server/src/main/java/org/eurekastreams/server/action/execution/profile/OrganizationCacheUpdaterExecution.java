/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.profile;

import java.io.Serializable;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.profile.OrganizationCacheUpdaterRequest;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.GetPrivateGroupsByUserId;
import org.eurekastreams.server.persistence.mappers.cache.OrgParentHierarchyCacheCleaner;
import org.eurekastreams.server.persistence.mappers.cache.SaveOrganizationCoordinatorIdsToCache;

/**
 * Execution to perform async tasks after an organization has been updated. This currently includes:
 * 
 * 1. updating the activity search string for all people that are coordinators of the organization 2. save updated list
 * of coordinators to cache. 3. Conditionally clear the Recursive Org Children ID cache for every Org up the tree from
 * input org (only done on create).
 * 
 * TODO: Actions should not be manipulating the cache directly, this should be done via mapper.
 */
public class OrganizationCacheUpdaterExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Instance of the logger.
     */
    private Log log = LogFactory.make();

    /**
     * Mapper to get the private group ids a user has the ability to view activities for through an org/group
     * coordinator role.
     */
    private final GetPrivateGroupsByUserId privateGroupIdsCacheMapper;

    /**
     * Cache mapper for Organization coordinators.
     */
    private SaveOrganizationCoordinatorIdsToCache saveOrgCoordinatorIdsToCacheDAO;

    /**
     * Mapper to clean the cache of recursive org ids up the tree.
     */
    private OrgParentHierarchyCacheCleaner orgParentHierarchyCacheCleaner;

    /**
     * Instance of the cache mapper.
     */
    private final Cache cache;

    /**
     * Constructor.
     * 
     * @param inPrivateGroupIdsCacheMapper
     *            mapper to retrieve the private group ids that a user has access to view activities through a org/group
     *            coord role.
     * @param inCache
     *            - instance of the cache client to access the cache.
     * @param inOrgCoordinatorCacheManager
     *            {@link SaveOrganizationCoordinatorIdsToCache}.
     * @param inOrgParentHierarchyCacheCleaner
     *            {@link OrgParentHierarchyCacheCleaner}.
     * 
     */
    public OrganizationCacheUpdaterExecution(final GetPrivateGroupsByUserId inPrivateGroupIdsCacheMapper,
            final Cache inCache, final SaveOrganizationCoordinatorIdsToCache inOrgCoordinatorCacheManager,
            final OrgParentHierarchyCacheCleaner inOrgParentHierarchyCacheCleaner)
    {
        privateGroupIdsCacheMapper = inPrivateGroupIdsCacheMapper;
        cache = inCache;
        saveOrgCoordinatorIdsToCacheDAO = inOrgCoordinatorCacheManager;
        orgParentHierarchyCacheCleaner = inOrgParentHierarchyCacheCleaner;
    }

    /**
     * Perform the action, updating the coordinator cache list for a Domain Group and rebuilding the security-scoped
     * activity search strings for all coordinators of a Domain Group.
     * 
     * @param inActionContext
     *            the action context with the org id to update cache for
     * @return null
     */
    @Override
    public Serializable execute(final ActionContext inActionContext)
    {
        log.info("Performing action");

        OrganizationCacheUpdaterRequest request = (OrganizationCacheUpdaterRequest) inActionContext.getParams();

        Set<Long> coordinatorIds = request.getCoordinatorIds();

        for (Long coordinatorPersonId : coordinatorIds)
        {
            // Remove the cache list of the private group ids that the current user
            // has the ability to view activities for through org/group coordinator access.
            cache.delete(CacheKeys.PRIVATE_GROUP_IDS_VIEWABLE_BY_PERSON_AS_COORDINATOR + coordinatorPersonId);
            // Rebuild that cache list for that particular person.
            privateGroupIdsCacheMapper.execute(coordinatorPersonId);
        }

        saveOrgCoordinatorIdsToCacheDAO.execute(request);

        if (request.getClearRecursiveOrgChildernUpTree())
        {
            log.info("Organization just peristed - now cleaning the recursive child cache up the tree for org #"
                    + request.getOrganizationId());
            orgParentHierarchyCacheCleaner.execute(request.getOrganizationId());
        }

        log.info("Action complete");
        return null;
    }
}
