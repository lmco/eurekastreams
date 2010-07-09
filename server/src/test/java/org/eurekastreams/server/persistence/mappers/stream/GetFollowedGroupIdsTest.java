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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for GetFollowedGroupIds class.
 *
 */
public class GetFollowedGroupIdsTest extends CachedMapperTest
{
    /**
     * Test group id.
     */
    private final long groupId = 1L;

    /**
     * Test user id.
     */
    private final long smithersId = 98L;

    /**
     * System under test.
     */
    @Autowired
    private GetFollowedGroupIds getFollowedGroupIds;
    
    /**
     * Cache.
     */
    @Autowired
    Cache memcachedCache;

    /**
     * test.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute()
    {
        String key = CacheKeys.GROUPS_FOLLOWED_BY_PERSON + smithersId;
        
        //assert that cache is empty for item of interest.
        assertNull(memcachedCache.getList(key));
        
        List<Long> results = getFollowedGroupIds.execute(smithersId);
        assertEquals(1, results.size());
        assertEquals(new Long(groupId), results.get(0));
        
        //verify that it's now in cache with correct number of activities.
        assertEquals(1, (memcachedCache.getList(key)).size());
        assertEquals(groupId, (memcachedCache.getList(key)).get(0).longValue());
    }

}
