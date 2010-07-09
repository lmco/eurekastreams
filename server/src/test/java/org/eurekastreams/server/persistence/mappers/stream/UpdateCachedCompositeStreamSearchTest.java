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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * Tests deleting a composite stream from the cache.
 */
@TransactionConfiguration(defaultRollback = false)
public class UpdateCachedCompositeStreamSearchTest extends CachedMapperTest
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
    UpdateCachedCompositeStreamSearch sut;

    /**
     * Cache.
     */
    @Autowired
    Cache cache;

    /**
     * CompositeStream id used in tests.
     */
    private static final long COMPOSITE_STREAM_SEARCH_ID = 1L;

    /**
     * Stream View mock object.
     */
    private StreamSearch streamSearchMock = context.mock(StreamSearch.class, "streamSearch");

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        cache.delete(CacheKeys.COMPOSITE_STREAM_SEARCH_BY_ID + COMPOSITE_STREAM_SEARCH_ID);
        // check for the composite stream search object in the cache.
        StreamSearch resultStreamSearch =
                (StreamSearch) cache.get(CacheKeys.COMPOSITE_STREAM_SEARCH_BY_ID + COMPOSITE_STREAM_SEARCH_ID);
        assertNull(resultStreamSearch);
    }

    /**
     * Test that the cache with no entry has the entry populated.
     */
    @Test
    public void testExecuteMissingFromCache()
    {
        context.checking(new Expectations()
        {
            {
                allowing(streamSearchMock).getId();
                will(returnValue(COMPOSITE_STREAM_SEARCH_ID));
            }
        });

        sut.execute(streamSearchMock);

        StreamSearch resultStreamSearch =
                (StreamSearch) cache.get(CacheKeys.COMPOSITE_STREAM_SEARCH_BY_ID + COMPOSITE_STREAM_SEARCH_ID);
        assertNotNull(resultStreamSearch);

        context.assertIsSatisfied();
    }

    /**
     * Test that an existing entry is replaced with another entry.
     */
    @Test
    public void testExecuteEntryInCache()
    {
        context.checking(new Expectations()
        {
            {
                allowing(streamSearchMock).getId();
                will(returnValue(COMPOSITE_STREAM_SEARCH_ID));
            }
        });

        sut.execute(streamSearchMock);

        context.assertIsSatisfied();
    }

}
