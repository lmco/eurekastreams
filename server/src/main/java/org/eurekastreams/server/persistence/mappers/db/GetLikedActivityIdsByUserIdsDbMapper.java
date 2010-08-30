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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.stream.LikedActivity;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Gets a list of Liked activity ids for a given user.
 */
public class GetLikedActivityIdsByUserIdsDbMapper extends CachedDomainMapper implements
        DomainMapper<Collection<Long>, Collection<Collection<Long>>>
{
    /**
     * Looks in the cache for Liked activities. If data is not cached, goes to database.
     * 
     * @param userId
     *            the user id to find followers for.
     * @return the list of follower ids.
     */
    @SuppressWarnings("unchecked")
    public Collection<Collection<Long>> execute(final Collection<Long> userIds)
    {
        Collection<Collection<Long>> results = new ArrayList<Collection<Long>>();

        List<Long> keys;
        
        for (long userId : userIds)
        {
            Query q = getEntityManager()
                    .createQuery("from LikedActivity where personId = :id ORDER BY activityId DESC").setParameter("id",
                            userId);
            
            List<LikedActivity> items = q.getResultList();

            keys = new ArrayList<Long>();
            for (LikedActivity f : items)
            {
                keys.add(f.getActivityId());
            }
            
            results.add(keys);
        }

        return results;
    }
}