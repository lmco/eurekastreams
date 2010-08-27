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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eurekastreams.server.persistence.mappers.cache.testhelpers.SimpleMemoryCache;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests GetKeyedSingleValueCacheMapper.
 */
public class GetKeyedSingleValueCacheMapperTest
{
    /** Test data. */
    private static final String KEY_PREFIX = "MyPrefix:";

    /** Test data. */
    private static final Integer KEY_VALUE = 42;

    /** Test data. */
    private static final String CACHE_DATA = "Item1Value";

    /** Fixture: cache. */
    private SimpleMemoryCache cache;

    /** SUT. */
    private GetKeyedSingleValueCacheMapper<Integer, String> sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        cache = new SimpleMemoryCache();
        cache.set(KEY_PREFIX + KEY_VALUE, CACHE_DATA);
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteFound()
    {
        sut = new GetKeyedSingleValueCacheMapper<Integer, String>(KEY_PREFIX);
        sut.setCache(cache);
        String result = sut.execute(KEY_VALUE);
        assertEquals(CACHE_DATA, result);
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteNotFound1()
    {
        sut = new GetKeyedSingleValueCacheMapper<Integer, String>(KEY_PREFIX);
        sut.setCache(cache);
        String result = sut.execute(5);
        assertNull(result);
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteNotFound2()
    {
        sut = new GetKeyedSingleValueCacheMapper<Integer, String>("WrongPrefix:");
        sut.setCache(cache);
        String result = sut.execute(KEY_VALUE);
        assertNull(result);
    }

}
