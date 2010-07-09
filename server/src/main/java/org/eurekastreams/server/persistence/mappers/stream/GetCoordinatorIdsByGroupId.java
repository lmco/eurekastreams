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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * Returns coordinator person ids for a given group id from cache if available, otherwise 
 * from DB.
 *
 */
public class GetCoordinatorIdsByGroupId extends BaseArgCachedDomainMapper<Long, List<Long>>
{
    /**
     * Returns coordinator person ids for a given group id from cache if available, otherwise 
     * from DB.
     * @param inGroupId The group id.
     * @return list of coordinator person ids.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Long> execute(final Long inGroupId)
    {
        String key = CacheKeys.COORDINATOR_PERSON_IDS_BY_GROUP_ID + inGroupId;
        
        // Looks for the item in the cache
        List<Long> coordinatorKeys = getCache().getList(key);

        // If nothing in cache, gets from database and sets in the cache
        if (coordinatorKeys == null)
        {
            String queryString = "SELECT p.id FROM Person p, DomainGroup g WHERE p member of g.coordinators"
                + " AND g.id = :groupId";
            Query query = getEntityManager().createQuery(queryString).setParameter("groupId", inGroupId);
            coordinatorKeys = query.getResultList();  
            
            getCache().setList(key, coordinatorKeys);
        }
        
        return coordinatorKeys;
    }

}
