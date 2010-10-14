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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Mapper to get a Set of Coordinator IDs from a list of organizations, loading from cache if possible, from DB if not,
 * then populating the cache.
 */
public class GetOrgCoordinators extends CachedDomainMapper
{
    /**
     * Prefix for the cache key this mapper is responsible for.
     */
    String cacheKeyPreFix = CacheKeys.ORGANIZATION_COORDINATORS_BY_ORG_ID;

    /**
     * Constructor.
     * 
     * @param inGetOrgCoordinatorIdsMapper
     *            the db mapper to get the coordinator person ids for an organization
     */
    public GetOrgCoordinators(final DomainMapper<Long, Set<Long>> inGetOrgCoordinatorIdsMapper)
    {
        getOrgCoordinatorIdsMapper = inGetOrgCoordinatorIdsMapper;
    }

    /**
     * Mapper to get org coordinator ids from the database.
     */
    private DomainMapper<Long, Set<Long>> getOrgCoordinatorIdsMapper;

    /**
     * Looks in cache for IDs and returns them if found. Otherwise, makes a database call, puts them in cache, and
     * returns them.
     * 
     * @param inOrgId
     *            the IDs of the organization to fetch child organizations for
     * @return Set of Organization Coordinator IDs.
     */
    public Set<Long> execute(final Long inOrgId)
    {
        HashSet<Long> ids = new HashSet<Long>();
        ids.add(inOrgId);
        return execute(ids);
    }

    /**
     * Looks in cache for IDs and returns them if found. Otherwise, makes a database call, puts them in cache, and
     * returns them.
     * 
     * @param inOrgIds
     *            the IDs of the organizations to fetch child organizations for
     * @return Set of Organization Coordinator IDs.
     */
    @SuppressWarnings("unchecked")
    public Set<Long> execute(final Set<Long> inOrgIds)
    {
        Set<Long> coordinatorIdList = new HashSet<Long>();

        List<String> coordinatorKeyList = new LinkedList<String>();

        // Load all Org Coordinator calls into a list for a multiGet.
        for (Long orgId : inOrgIds)
        {
            coordinatorKeyList.add(cacheKeyPreFix + orgId);
        }

        Map<String, Object> recursiveOrgCoordIds = getCache().multiGet(coordinatorKeyList);

        // Get all coord Ids from cache.
        Iterator mapIt = recursiveOrgCoordIds.entrySet().iterator();
        while (mapIt.hasNext())
        {
            Map.Entry pairs = (Map.Entry) mapIt.next();
            Set<Long> orgCoordinators = new HashSet((Collection) pairs.getValue());
            coordinatorIdList.addAll(orgCoordinators);
        }

        // Determines if any of the keys were missing from the cache
        List<Long> uncachedCoordinatorKeys = new ArrayList<Long>();
        for (Long orgKeys : inOrgIds)
        {
            if (!recursiveOrgCoordIds.containsKey(cacheKeyPreFix + orgKeys))
            {
                uncachedCoordinatorKeys.add(orgKeys);
            }
        }

        // Get the rest from db, put in cache.
        // TODO: update this to make one DB call - why loop?
        for (int i = 0; i < uncachedCoordinatorKeys.size(); i++)
        {
            coordinatorIdList.addAll(getAndStoreOrgCoordinators(uncachedCoordinatorKeys.get(i)));
        }

        return coordinatorIdList;
    }

    /**
     * Get and store all org coordinators for a single org.
     * 
     * @param inOrgId
     *            the ID of the org to load coordinators for.
     * @return Set of coordinator Ids for a org.
     */
    @SuppressWarnings("unchecked")
    private Set<Long> getAndStoreOrgCoordinators(final Long inOrgId)
    {
        Set<Long> coordinatorResults = getOrgCoordinatorIdsMapper.execute(inOrgId);
        getCache().set(cacheKeyPreFix + inOrgId, coordinatorResults);
        return new HashSet(coordinatorResults);
    }
}
