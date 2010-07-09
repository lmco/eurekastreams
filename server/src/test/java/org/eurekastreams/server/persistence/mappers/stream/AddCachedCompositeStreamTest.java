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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.testhelpers.SimpleMemoryCache;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * Tests deleting a composite stream from the cache.
 */
@TransactionConfiguration(defaultRollback = false)
public class AddCachedCompositeStreamTest extends CachedMapperTest
{
    /**
     * mock context.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * 
     */
    private StreamView streamViewMock = context.mock(StreamView.class);

    /**
     * System under test.
     */
    private AddCachedCompositeStream sut;

    /**
     * Cache.
     */
    private Cache cache;

    /**
     * User id used in tests.
     */
    private static final long PERSON_ID = 999L;

    /**
     * CompositeStream id used in tests.
     */
    private static final long COMPOSITE_STREAM_ID = 883L;

    /**
     * CompositeStream id #2 to use.
     */
    private static final long COMPOSITE_STREAM_ID_2 = 669L;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        cache = new SimpleMemoryCache();

        UserCompositeStreamIdsMapper mapper = new UserCompositeStreamIdsMapper();
        mapper.setCache(cache);
        mapper.setEntityManager(getEntityManager());

        sut = new AddCachedCompositeStream(mapper);
        sut.setEntityManager(getEntityManager());
        sut.setCache(cache);
    }

    /**
     * test.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteNonEmptyCompositeStreamList()
    {
        // assert the user has no composite streams in the cache.
        List<Long> result = cache.getList(CacheKeys.COMPOSITE_STREAM_IDS_BY_PERSON_ID + PERSON_ID);
        assertNull(result);

        // add one composite stream to the list and put into the cache.
        result = new ArrayList<Long>();
        result.add(COMPOSITE_STREAM_ID_2);
        cache.setList(CacheKeys.COMPOSITE_STREAM_IDS_BY_PERSON_ID + PERSON_ID, result);
        assertEquals(1, (cache.getList(CacheKeys.COMPOSITE_STREAM_IDS_BY_PERSON_ID + PERSON_ID)).size());

        context.checking(new Expectations()
        {
            {
                allowing(streamViewMock).getId();
                will(returnValue(COMPOSITE_STREAM_ID));
            }
        });

        sut.execute(PERSON_ID, streamViewMock);

        assertNotNull(cache.get(CacheKeys.COMPOSITE_STREAM_BY_ID + COMPOSITE_STREAM_ID));
        assertEquals(2, (cache.getList(CacheKeys.COMPOSITE_STREAM_IDS_BY_PERSON_ID + PERSON_ID)).size());
        context.assertIsSatisfied();
    }
    
    /**
     * test.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteWithItemAlreadyAdded()
    {
        // assert the user has no composite streams in the cache.
        List<Long> result = cache.getList(CacheKeys.COMPOSITE_STREAM_IDS_BY_PERSON_ID + PERSON_ID);
        assertNull(result);

        // add one composite stream to the list and put into the cache.
        result = new ArrayList<Long>();
        result.add(COMPOSITE_STREAM_ID_2);
        result.add(COMPOSITE_STREAM_ID);
        cache.setList(CacheKeys.COMPOSITE_STREAM_IDS_BY_PERSON_ID + PERSON_ID, result);
        assertEquals(2, (cache.getList(CacheKeys.COMPOSITE_STREAM_IDS_BY_PERSON_ID + PERSON_ID)).size());

        context.checking(new Expectations()
        {
            {
                allowing(streamViewMock).getId();
                will(returnValue(COMPOSITE_STREAM_ID));
            }
        });

        sut.execute(PERSON_ID, streamViewMock);

        assertNotNull(cache.get(CacheKeys.COMPOSITE_STREAM_BY_ID + COMPOSITE_STREAM_ID));
        assertEquals(2, (cache.getList(CacheKeys.COMPOSITE_STREAM_IDS_BY_PERSON_ID + PERSON_ID)).size());
        context.assertIsSatisfied();
    }

    /**
     * test.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteEmptyCompositeStreamList()
    {
        context.checking(new Expectations()
        {
            {
                allowing(streamViewMock).getId();
                will(returnValue(COMPOSITE_STREAM_ID));
            }
        });

        sut.execute(PERSON_ID, streamViewMock);

        assertNotNull(cache.get(CacheKeys.COMPOSITE_STREAM_BY_ID + COMPOSITE_STREAM_ID));
        assertEquals(1, (cache.getList(CacheKeys.COMPOSITE_STREAM_IDS_BY_PERSON_ID + PERSON_ID)).size());
        context.assertIsSatisfied();
    }
}
