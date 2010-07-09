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
package org.eurekastreams.server.persistence.mappers;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Mapper to get a Set of IDs of all of the organizations recursively above the input org id, loading from cache if
 * possible, from DB if not, then populating the cache.
 */
public class GetRecursiveParentOrgIds extends CachedDomainMapper
{
    /**
     * Local instance of logger.
     */
    private final Log logger = LogFactory.make();

    /**
     * Looks in cache for the necessary DTOs and returns them if found. Otherwise, makes a database call, puts them in
     * cache, and returns them.
     *
     * @param inOrgId
     *            the ID of the organization to fetch child organizations for.
     * @return list of Organization IDs of all recursive parent Organizations of inOrgId.
     *
     *         Note: If the Org id passed in is the root org, an empty list of organizations will be passed back.
     */
    public List<Long> execute(final Long inOrgId)
    {
        String cacheKey = CacheKeys.ORGANIZATION_PARENTS_RECURSIVE + inOrgId;
        List<Long> parentOrgIds = getCache().getList(cacheKey);
        if (parentOrgIds == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Retrieve hierarchy for org id: " + inOrgId);
            }
            // not in cache - need to fetch it
            parentOrgIds = new ArrayList<Long>();
            // If the orgId passed in is the same as its parent org id, we have
            // the root org and there is no reason to recurse through the tree.
            Long parentOrgId = getParentOrgId(inOrgId);
            if (!parentOrgId.equals(inOrgId))
            {
                recurse(inOrgId, parentOrgIds);
            }
            // add the list to cache
            getCache().setList(cacheKey, parentOrgIds);
        }
        return parentOrgIds;
    }

    /**
     * Recursively load inParentOrgIds with the IDs of the parent organizations of the org with id inOrgId.
     *
     * @param inOrgId
     *            the ID of the org to load children for
     * @param inParentOrgIds
     *            the Set to store the IDs of the parent organizations in
     */
    private void recurse(final Long inOrgId, final List<Long> inParentOrgIds)
    {
        Long parentOrgId = getParentOrgId(inOrgId);

        if (!parentOrgId.equals(inOrgId))
        {
            recurse(parentOrgId, inParentOrgIds);
        }

        if (!parentOrgId.equals(inOrgId))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Adding " + parentOrgId + " to the list");
            }
            inParentOrgIds.add(parentOrgId);
        }
    }

    /**
     * Helper method to retrieve the parent org id.
     * @param inOrgId - id of the org to find the parent org id for.
     * @return parent org id of the passed in org id.
     */
    private Long getParentOrgId(final Long inOrgId)
    {
        String queryString = "SELECT parentOrganization.id FROM Organization WHERE id = :orgId";
        Query query = getEntityManager().createQuery(queryString);
        query.setParameter("orgId", inOrgId);
        Long parentOrgId = (Long) query.getSingleResult();
        if (logger.isDebugEnabled())
        {
            logger.debug("Parent org id for org: " + inOrgId + " is: " + parentOrgId);
        }
        return parentOrgId;
    }
}
