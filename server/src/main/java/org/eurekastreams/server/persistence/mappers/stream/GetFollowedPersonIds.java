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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * Mapper to return ids of people that a person has followed.
 *
 */
public class GetFollowedPersonIds extends CachedDomainMapper implements DomainMapper<Long, List<Long>>
{
    /**
     * Returns list of people a given user has followed.
     *
     * @param userId
     *            The user id to find followed people for.
     * @return the list of followed ids.
     */
    @SuppressWarnings("unchecked")
    public List<Long> execute(final Long userId)
    {
        String key = CacheKeys.PEOPLE_FOLLOWED_BY_PERSON + userId;

        // Looks for the item in the cache
        List<Long> followedKeys = getCache().getList(key);

        // If nothing in cache, gets from database and sets in the cache
        if (followedKeys == null)
        {
            Query q = getEntityManager().createQuery(
                    "select f.pk.followingId from Follower f where f.pk.followerId = :id").setParameter("id", userId);

            List<Long> results = q.getResultList();

            followedKeys = new ArrayList<Long>(results);
            getCache().setList(key, results);
        }

        return followedKeys;
    }

}
