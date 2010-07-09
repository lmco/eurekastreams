/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;
import org.hibernate.Query;

/**
 * Mapper to get a Set of IDs of all of the organizations recursively below the input org id, loading from cache if
 * possible, from DB if not, then populating the cache.
 */
public class GetRecursiveChildOrgIds extends CachedDomainMapper
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(GetRecursiveChildOrgIds.class);

    /**
     * Looks in cache for the necessary DTOs and returns them if found. Otherwise, makes a database call, puts them in
     * cache, and returns them.
     * 
     * @param inOrgId
     *            the ID of the organization to fetch child organizations for
     * @return list of Organization IDs of all recursive children Organizations of the input one
     */
    @SuppressWarnings("unchecked")
    public Set<Long> execute(final Long inOrgId)
    {
        log.info("Looking for recursive set of organizations for org id " + inOrgId);

        String cacheKey = CacheKeys.ORGANIZATION_RECURSIVE_CHILDREN + inOrgId;
        Set<Long> recursiveOrgIds = (Set<Long>) getCache().get(cacheKey);
        if (recursiveOrgIds != null)
        {
            // already in cache - return it
            log.info("Found the organization's recursive org set in cache for org #" + inOrgId + ".  Returning it.");
            return recursiveOrgIds;
        }

        log.info("Couldn't find the organization's recursive org set in cache for org #" + inOrgId
                + " - loading from database, then storing in cache.");

        // not in cache - need to fetch it
        recursiveOrgIds = new HashSet<Long>();
        recurse(inOrgId, recursiveOrgIds);

        // add the list to cache
        getCache().set(cacheKey, recursiveOrgIds);

        return recursiveOrgIds;
    }

    /**
     * Recursively load inOrgChildIds with the IDs of the child organizations of the org with id inOrgId.
     * 
     * @param inOrgId
     *            the ID of the org to load children for
     * @param inOrgChildIds
     *            the Set to store the IDs of the children organizations in
     */
    @SuppressWarnings("unchecked")
    private void recurse(final Long inOrgId, final Set<Long> inOrgChildIds)
    {
        String queryString = "SELECT id FROM Organization WHERE "
                + "parentOrganization.id = :parentOrgId AND id != parentOrganization.id";
        Query query = getHibernateSession().createQuery(queryString);
        query.setParameter("parentOrgId", inOrgId);
        List<Long> results = query.list();

        // loop across the results, storing children in collections of their direct parents
        for (Long childOrgId : results)
        {
            inOrgChildIds.add(childOrgId);
            recurse(childOrgId, inOrgChildIds);
        }
    }
}
