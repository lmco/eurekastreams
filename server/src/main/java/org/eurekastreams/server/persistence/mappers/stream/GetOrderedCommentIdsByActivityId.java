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

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * Mapper to return a list of comment ids for a given activity id.
 * 
 */
public class GetOrderedCommentIdsByActivityId extends CachedDomainMapper implements DomainMapper<Long, List<Long>>
{

    /**
     * Returns a list of comment ids for a given activity id.
     * 
     * @param inActivityId
     *            The activity id.
     * @return A list of comment ids for a given activity id.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Long> execute(final Long inActivityId)
    {
        String key = CacheKeys.COMMENT_IDS_BY_ACTIVITY_ID + inActivityId;
        
        // Looks for the item in the cache
        List<Long> results = getCache().getList(key);

        // If nothing in cache, gets from database and sets in the cache
        if (results == null)
        {        
            Query q = getEntityManager().createQuery(
                    "SELECT c.id FROM Comment c WHERE c.target.id = :activityId " 
                    + "ORDER BY c.id ASC");
            q.setParameter("activityId", inActivityId);
            
            results = (List<Long>) q.getResultList();
            
            getCache().setList(key, results);
        }

        return results;
    }
}
