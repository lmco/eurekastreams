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

import org.apache.commons.lang.NotImplementedException;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for CompositeStreamLoaderStarred class. This is an integration test
 * even though the class defers the real work to the GetStarredActivityIds
 * mapper.
 *
 */
public class CompositeStreamLoaderStarredTest extends CachedMapperTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    }; 
    
    /**
     * System under test.
     */
    @Autowired
    CompositeStreamLoaderStarred compositeStreamLoaderStarred;
    
    /**
     * Cache.
     */
    @Autowired
    Cache memcachedCache;
    
    /**
     * User id used in tests.
     */
    private final long userId = 99L;   
    
    /**
     * CompositeStream mock.
     */
    private StreamView compositeStream = context.mock(StreamView.class);
    
    /**
     * test getActivityIds method.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGetActivityIds()
    {        
        //assert that cache is empty for compositeStream of interest.
        assertNull(memcachedCache.getList(CacheKeys.STARRED_BY_PERSON_ID + userId));

        //This should get ids for compositeStream and load them into cache.
        List<Long> results = compositeStreamLoaderStarred.getActivityIds(compositeStream, userId);
        
        //verify correct number of activities from initial call.
        assertEquals(1, results.size());
        
        //verify that it's now in cache with correct number of activities.
        assertEquals(1, (memcachedCache.getList(CacheKeys.STARRED_BY_PERSON_ID + userId)).size());        
    }
    
    /**
     * Test getActivityRestrictions method. Throws exception, should not be called.
     */
    @Test(expected = NotImplementedException.class)
    public void testGetActivityRestrictions()
    {
        compositeStreamLoaderStarred.getActivityRestrictions(compositeStream, userId);
    }
    
    /**
     * Test setIdListToCache method. Throws exception, should not be called.
     */
    @Test(expected = NotImplementedException.class)
    public void testSetIdListToCache()
    {
        compositeStreamLoaderStarred.setIdListToCache(null, null, userId);
    }
}
