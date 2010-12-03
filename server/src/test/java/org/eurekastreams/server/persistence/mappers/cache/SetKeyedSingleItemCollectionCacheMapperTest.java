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
package org.eurekastreams.server.persistence.mappers.cache;

import static junit.framework.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.cache.testhelpers.SimpleMemoryCache;
import org.junit.Before;
import org.junit.Test;

/**
 *  Tests refreshing a collection with one item.
 */
public class SetKeyedSingleItemCollectionCacheMapperTest
{
    /** Test data. */
    private static final String KEY_PREFIX = "MyPrefix:";

    /** Test data. */
    private static final List<Integer> KEYS = Collections.singletonList(42);

    /** Test data. */
    private static final String CACHE_KEY = KEY_PREFIX + KEYS.get(0);

    /** Test data. */
    private static final List<Long> CACHE_DATA = Collections.singletonList(1L);

    /** Fixture: cache. */
    private SimpleMemoryCache cache;

    /** SUT. */
    private SetKeyedSingleItemCollectionCacheMapper<Integer> sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        cache = new SimpleMemoryCache();
        sut = new SetKeyedSingleItemCollectionCacheMapper<Integer>(KEY_PREFIX);
        sut.setCache(cache);
    }

    /**
     * Tests refreshing.
     */
    @Test
    public void testRefreshNew()
    {
        sut.refresh(KEYS, CACHE_DATA);
        
        assertEquals(CACHE_DATA.get(0), cache.get(CACHE_KEY));
    }

    /**
     * Tests refreshing.
     */
    @Test
    public void testRefreshOverwrite()
    {
        Long oldData = 4L;
        
        cache.set(CACHE_KEY, oldData);
        assertEquals(oldData, cache.get(CACHE_KEY));

        sut.refresh(KEYS, CACHE_DATA);

        assertEquals(CACHE_DATA.get(0), cache.get(CACHE_KEY));
    }
}
