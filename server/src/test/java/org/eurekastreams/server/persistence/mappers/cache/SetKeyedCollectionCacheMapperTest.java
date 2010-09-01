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

import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.cache.testhelpers.SimpleMemoryCache;
import org.junit.Before;
import org.junit.Test;


/**
 *  Tests refreshing a collection.
 */
public class SetKeyedCollectionCacheMapperTest
{
    /** Test data. */
    private static final String KEY_PREFIX = "MyPrefix:";

    /** Test data. */
    private static final List<Integer> KEY_VALUE = Arrays.asList(42);;

    /** Test data. */
    private static final String CACHE_KEY = KEY_PREFIX + KEY_VALUE.get(0);

    /** Test data. */
    private static final List<List<Long>> CACHE_DATA = Arrays.asList(Arrays.asList(1L));

    /** Fixture: cache. */
    private SimpleMemoryCache cache;

    /** SUT. */
    private SetKeyedCollectionCacheMapper<Integer> sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        cache = new SimpleMemoryCache();
        sut = new SetKeyedCollectionCacheMapper<Integer>(KEY_PREFIX);
        sut.setCache(cache);
    }

    /**
     * Tests refreshing.
     */
    @Test
    public void testRefreshNew()
    {
        sut.refresh(KEY_VALUE, CACHE_DATA);

        assertEquals(CACHE_DATA.get(0).get(0), cache.getList(CACHE_KEY).get(0));
    }

    /**
     * Tests refreshing.
     */
    @Test
    public void testRefreshOverwrite()
    {
        List<Long> oldData = Arrays.asList(4L);
        
        cache.setList(CACHE_KEY, oldData);
        assertEquals(oldData.get(0), cache.getList(CACHE_KEY).get(0));

        sut.refresh(KEY_VALUE, CACHE_DATA);

        assertEquals(CACHE_DATA.get(0).get(0), cache.getList(CACHE_KEY).get(0));
    }
}
