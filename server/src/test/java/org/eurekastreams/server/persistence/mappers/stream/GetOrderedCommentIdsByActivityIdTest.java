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
 * Test for GetOrderedCommentIdsByActivity.
 *
 */
public class GetOrderedCommentIdsByActivityIdTest extends CachedMapperTest
{
    /**
     * Activity id to get comments for (from dataset.xml).
     */
    private final long activityId = 6789L;

    /**
     * System under test.
     */
    @Autowired
    private GetOrderedCommentIdsByActivityId sut;
    
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
        String key = CacheKeys.COMMENT_IDS_BY_ACTIVITY_ID + activityId;        
        
        //assert that cache is empty for item of interest.
        assertNull(memcachedCache.getList(key));
        
        List<Long> results = sut.execute(activityId);
        
        //assert correct # of results and that it's sorted asc.
        assertEquals(3, results.size());
        assertEquals(1, results.get(0).longValue());
        assertEquals(2, results.get(1).longValue());
        assertEquals(3, results.get(2).longValue());
        
        //verify that it's now in cache with correct number of activities.
        assertEquals(3, (memcachedCache.getList(key)).size());
        
        
        
    }

}
