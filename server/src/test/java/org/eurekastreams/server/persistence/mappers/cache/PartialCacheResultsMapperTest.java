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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.cache.testhelpers.SimpleMemoryCache;
import org.eurekastreams.server.persistence.mappers.chained.PartialMapperResponse;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for PartialCacheResultsMapper.
 */
public class PartialCacheResultsMapperTest
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
     * Mocked cache key suffix transformer.
     */
    private CacheKeySuffixTransformer<String> suffixKeyTransformer = context.mock(CacheKeySuffixTransformer.class);

    /**
     * Cache to use.
     */
    private Cache cache = new SimpleMemoryCache();

    /**
     * System under test.
     */
    private PartialCacheResultsMapper<String, Object> sut;

    /**
     * Cache key prefix to use.
     */
    private static String cacheKeyPrefix = "SDLFKJSDF:";

    /**
     * Cache key suffixes.
     */
    private List<String> cacheKeySuffixes = new ArrayList<String>();

    /**
     * Setup method - instantiate SUT and give it fresh cache.
     */
    @Before
    public void before()
    {
        sut = new PartialCacheResultsMapper(suffixKeyTransformer, cacheKeyPrefix);
        cache = new SimpleMemoryCache();
        sut.setCache(cache);

        // setup cache key suffixes
        cacheKeySuffixes.add("one");
        cacheKeySuffixes.add("two");
        cacheKeySuffixes.add("three");
    }

    /**
     * Test finding all values in cache.
     */
    @Test
    public void testFindAll()
    {
        Object result1 = new Object();
        Object result2 = new Object();
        Object result3 = new Object();

        context.checking(new Expectations()
        {
            {
                oneOf(suffixKeyTransformer).transform("one");
                will(returnValue("ONE"));

                oneOf(suffixKeyTransformer).transform("two");
                will(returnValue("TWO"));

                oneOf(suffixKeyTransformer).transform("three");
                will(returnValue("THREE"));
            }
        });

        // fill cache with the values that it contains
        cache.set(cacheKeyPrefix + "ONE", result1);
        cache.set(cacheKeyPrefix + "TWO", result2);
        cache.set(cacheKeyPrefix + "THREE", result3);

        // execute sut
        PartialMapperResponse<Collection<String>, Collection<Object>> response = sut.execute(cacheKeySuffixes);

        // make sure there's no follow-up request
        assertFalse(response.hasUnhandledRequest());

        // make sure the responses were found
        assertEquals(3, response.getResponse().size());
        assertTrue(response.getResponse().contains(result1));
        assertTrue(response.getResponse().contains(result2));
        assertTrue(response.getResponse().contains(result3));

        // check assertions
        context.assertIsSatisfied();
    }

    /**
     * Test finding no values in cache.
     */
    @Test
    public void testFindNone()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(suffixKeyTransformer).transform("one");
                will(returnValue("ONE"));

                oneOf(suffixKeyTransformer).transform("two");
                will(returnValue("TWO"));

                oneOf(suffixKeyTransformer).transform("three");
                will(returnValue("THREE"));
            }
        });

        // execute sut
        PartialMapperResponse<Collection<String>, Collection<Object>> response = sut.execute(cacheKeySuffixes);

        // make sure there's a follow-up request with the pre-transformed keys
        assertTrue(response.hasUnhandledRequest());
        assertEquals(3, response.getUnhandledRequest().size());
        assertTrue(response.getUnhandledRequest().contains("one"));
        assertTrue(response.getUnhandledRequest().contains("two"));
        assertTrue(response.getUnhandledRequest().contains("three"));

        // make sure there are no results
        assertEquals(0, response.getResponse().size());

        // check assertions
        context.assertIsSatisfied();
    }

    /**
     * Test finding no values in cache.
     */
    @Test
    public void testFindPartial()
    {
        Object result1 = new Object();
        Object result2 = new Object();

        context.checking(new Expectations()
        {
            {
                oneOf(suffixKeyTransformer).transform("one");
                will(returnValue("ONE"));

                oneOf(suffixKeyTransformer).transform("two");
                will(returnValue("TWO"));

                oneOf(suffixKeyTransformer).transform("three");
                will(returnValue("THREE"));
            }
        });

        // fill cache with the values that it contains
        cache.set(cacheKeyPrefix + "ONE", result1);
        cache.set(cacheKeyPrefix + "TWO", result2);

        // execute sut
        PartialMapperResponse<Collection<String>, Collection<Object>> response = sut.execute(cacheKeySuffixes);

        // make sure there's a follow-up request with the pre-transformed keys
        assertTrue(response.hasUnhandledRequest());
        assertEquals(1, response.getUnhandledRequest().size());
        assertTrue(response.getUnhandledRequest().contains("three"));

        // make sure there are no results
        assertEquals(2, response.getResponse().size());
        assertTrue(response.getResponse().contains(result1));
        assertTrue(response.getResponse().contains(result2));

        // check assertions
        context.assertIsSatisfied();
    }
}
