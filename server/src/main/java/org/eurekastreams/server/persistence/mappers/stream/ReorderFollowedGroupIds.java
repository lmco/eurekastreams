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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * Change the order of the list of followed group ids.
 */
public class ReorderFollowedGroupIds extends CachedDomainMapper
{
    /**
     * Update the order of followed groups in cache and db.
     * 
     * @param userId
     *            The user id to find followed groups for.
     * @param groupIds
     *            The newly ordered list of group ids.
     */
    public void execute(final long userId, final List<Long> groupIds)
    {
        String key = CacheKeys.GROUPS_FOLLOWED_BY_PERSON + userId;

        // Set in database
        EntityManager mgr = getEntityManager();
        for (int i = 0; i < groupIds.size(); i++)
        {
            String query = "update GroupFollower set groupstreamindex = :groupstreamindex "
                    + "where followerId=:followerId and followingId=:followingId";

            Query q = mgr.createQuery(query).setParameter("groupstreamindex", i).setParameter("followerId",
                    userId).setParameter("followingId", groupIds.get(i));
            
            q.executeUpdate();
        }
        
        // Set new list in cache
        getCache().set(key, groupIds);
    }
}
