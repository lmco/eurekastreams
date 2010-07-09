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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.GroupFollower;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests ReorderFollowedGroupIds class.
 */
public class ReorderFollowedGroupIdsTest extends CachedMapperTest
{
    /**
     * Test user id.
     */
    private static final long FORDP_ID = 42L;

    /**
     * System under test.
     */
    @Autowired
    private ReorderFollowedGroupIds sut;

    /**
     * Cache.
     */
    @Autowired
    Cache memcachedCache;

    /**
     * Test execute method.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute()
    {
        // Check the initial order
        String query = "from GroupFollower where followerId=:followerId order by groupstreamindex";
        Query q = getEntityManager().createQuery(query).setParameter("followerId", FORDP_ID);
        List<GroupFollower> result = q.getResultList();
        assertEquals(1L, result.get(0).getFollowingId());
        assertEquals(2L, result.get(1).getFollowingId());
        assertEquals(3L, result.get(2).getFollowingId());

        String key = CacheKeys.GROUPS_FOLLOWED_BY_PERSON + FORDP_ID;
        List<Long> groupIds = Arrays.asList(2L, 1L, 3L);

        sut.execute(FORDP_ID, groupIds);

        assertEquals(2L, ((List<Long>) memcachedCache.get(key)).get(0).longValue());
        assertEquals(1L, ((List<Long>) memcachedCache.get(key)).get(1).longValue());
        assertEquals(3L, ((List<Long>) memcachedCache.get(key)).get(2).longValue());

        // Check the resulting order
        query = "from GroupFollower where followerId=:followerId order by groupstreamindex";
        q = getEntityManager().createQuery(query).setParameter("followerId", FORDP_ID);
        result = q.getResultList();
        assertEquals(2L, result.get(0).getFollowingId());
        assertEquals(1L, result.get(1).getFollowingId());
        assertEquals(3L, result.get(2).getFollowingId());
    }
}
