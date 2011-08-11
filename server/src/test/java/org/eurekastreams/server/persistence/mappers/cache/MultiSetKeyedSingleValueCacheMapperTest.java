/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
import java.util.Collections;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.cache.testhelpers.SimpleMemoryCache;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests MultiSetKeyedSingleValueCacheMapper.
 */
public class MultiSetKeyedSingleValueCacheMapperTest
{
    /** Test data. */
    private static final String KEY_PREFIX = "MyPrefix:";

    /** Test data. */
    private static final List<Integer> KEYS = Collections.singletonList(42);

    /** Test data. */
    private static final String CACHE_KEY = makeKey(KEYS.get(0));

    /** Test data. */
    private static final List<Long> CACHE_DATA = Collections.singletonList(1L);

    /** Fixture: cache. */
    private SimpleMemoryCache cache;

    /** SUT. */
    private MultiSetKeyedSingleValueCacheMapper<Integer> sut;

    /**
     * Creates a key.
     *
     * @param suffix
     *            Key suffix.
     * @return Key.
     */
    private static String makeKey(final Integer suffix)
    {
        return KEY_PREFIX + suffix;
    }

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        cache = new SimpleMemoryCache();
        sut = new MultiSetKeyedSingleValueCacheMapper<Integer>(KEY_PREFIX);
        sut.setCache(cache);
    }

    /**
     * Tests refreshing.
     */
    @Test
    public void testRefreshSingleNew()
    {
        sut.refresh(KEYS, CACHE_DATA);

        assertEquals(CACHE_DATA.get(0), cache.get(CACHE_KEY));
    }

    /**
     * Tests refreshing.
     */
    @Test
    public void testRefreshSingleOverwrite()
    {
        Long oldData = 4L;

        cache.set(CACHE_KEY, oldData);
        assertEquals(oldData, cache.get(CACHE_KEY));

        sut.refresh(KEYS, CACHE_DATA);

        assertEquals(CACHE_DATA.get(0), cache.get(CACHE_KEY));
    }

    /**
     * Tests refreshing.
     */
    @Test
    public void testRefreshMultiNew()
    {
        List<Integer> keys = Arrays.asList(2, 4, 6, 8);
        List<Long> values = Arrays.asList(3L, 5L, 7L, 9L);

        sut.refresh(keys, values);

        for (int i = 0; i < 4; i++)
        {
            assertEquals(values.get(i), cache.get(makeKey(keys.get(i))));
        }
    }

    /**
     * Tests refreshing.
     */
    @Test
    public void testRefreshMultiOverwrite()
    {
        List<Integer> keys = Arrays.asList(2, 4, 6, 8);
        List<Long> values = Arrays.asList(3L, 5L, 7L, 9L);

        for (int i = 0; i < 4; i++)
        {
            cache.set(makeKey(keys.get(i)), 1L);
        }

        sut.refresh(keys, values);

        for (int i = 0; i < 4; i++)
        {
            assertEquals(values.get(i), cache.get(makeKey(keys.get(i))));
        }
    }
}
