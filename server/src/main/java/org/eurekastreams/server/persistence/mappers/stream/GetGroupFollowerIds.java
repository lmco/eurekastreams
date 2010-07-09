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
 * Gets a list of person ids who are followers of a given group.
 */
public class GetGroupFollowerIds extends CachedDomainMapper implements DomainMapper<Long, List<Long>>
{
    /**
     * Looks in the cache for followers of a group. If data is not cached, goes to database.
     *
     * @param groupId
     *            the group id to find followers for.
     * @return the list of follower ids.
     */
    @SuppressWarnings("unchecked")
    public List<Long> execute(final Long groupId)
    {
        String key = CacheKeys.FOLLOWERS_BY_GROUP + groupId;

        // Looks for the item in the cache
        List<Long> followerKeys = getCache().getList(key);

        // If nothing in cache, gets from database and sets in the cache
        if (followerKeys == null)
        {
            Query q = getEntityManager().createQuery(
                    "select gf.pk.followerId from GroupFollower gf where gf.pk.followingId = :id").setParameter("id",
                    groupId);

            List<Long> results = q.getResultList();
            getCache().setList(key, results);
            followerKeys = new ArrayList<Long>(results);
        }

        return followerKeys;
    }
}
