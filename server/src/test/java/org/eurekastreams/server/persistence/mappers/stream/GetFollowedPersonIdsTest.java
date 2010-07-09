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
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for GetFollowedPersonIds.
 *
 */
public class GetFollowedPersonIdsTest extends CachedMapperTest
{
    /**
     * Test user id.
     */
    private final long mrburnsId = 99;

    /**
     * Test user id.
     */
    private final long smithersId = 98;

    /**
     * System under test.
     */
    @Autowired
    private GetFollowedPersonIds getFollowedPersonIds;
    
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
        String key = "PerFld:" + smithersId;
        
        //assert that cache is empty for item of interest.
        assertNull(memcachedCache.get(key));
        
        List<Long> results = getFollowedPersonIds.execute(smithersId);
        assertEquals(2, results.size());
        assertEquals(new Long(mrburnsId), results.get(1));
        assertEquals(new Long(smithersId), results.get(0));
        
        //verify that it's now in cache with correct number of activities.
        assertEquals(2, memcachedCache.getList(key).size());
        assertEquals(mrburnsId, memcachedCache.getList(key).get(1).longValue());
        assertEquals(smithersId, memcachedCache.getList(key).get(0).longValue());
    }

}
