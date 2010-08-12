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
package org.eurekastreams.server.persistence.mappers.cache;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.chained.PartialMapperResponse;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Bulk entity stream ID mapper test.
 */
public class BulkEntityStreamIdsMapperTest
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
    private BulkEntityStreamIdsMapper sut;

    /**
     * Mock cache.
     */
    private Cache cacheMock = context.mock(Cache.class);

    /**
     * Setup test fixtures.
     */
    @Before
    public final void setUp()
    {
        sut = new BulkEntityStreamIdsMapper();
        sut.setCache(cacheMock);
    }

    /**
     * Tests mapping the ID of a person.
     */
    @Test
    public void testExecuteForPerson()
    {
        final Long personId = 42L;
        final Long expectedEntityStreamId = 5L;

        final Map<Long, EntityType> request = new HashMap<Long, EntityType>();

        request.put(personId, EntityType.PERSON);

        final Map<String, Object> cacheResults = new HashMap<String, Object>();
        cacheResults.put(CacheKeys.PERSON_ENTITITY_STREAM_VIEW_ID + personId, expectedEntityStreamId);

        context.checking(new Expectations()
        {
            {
                oneOf(cacheMock).multiGet(with(any(Collection.class)));
                will(returnValue(cacheResults));
            }
        });

        PartialMapperResponse<Map<Long, EntityType>, List<Long>> results = sut.execute(request);

        assertEquals(1, results.getResponse().size());
        assertEquals(expectedEntityStreamId, results.getResponse().get(0));

        context.assertIsSatisfied();
    }

    /**
     * Tests mapping a person with partial results.
     */
    @Test
    public void testExecuteForPersonPartialResults()
    {
        final Long personId = 0L;

        final Map<Long, EntityType> request = new HashMap<Long, EntityType>();

        request.put(personId, EntityType.PERSON);

        final Map<String, Object> cacheResults = new HashMap<String, Object>();

        context.checking(new Expectations()
        {
            {
                oneOf(cacheMock).multiGet(with(any(Collection.class)));
                will(returnValue(cacheResults));
            }
        });

        PartialMapperResponse<Map<Long, EntityType>, List<Long>> results = sut.execute(request);

        Assert.assertTrue(results.hasUnhandledRequest());

        context.assertIsSatisfied();
    }

    /**
     * Tests mapping the entity stream ID of a group.
     */
    @Test
    public void testExecuteForGroup()
    {
        final Long groupId = 1L;
        final Long expectedEntityStreamId = 9L;

        final Map<Long, EntityType> request = new HashMap<Long, EntityType>();

        request.put(groupId, EntityType.GROUP);

        final Map<String, Object> cacheResults = new HashMap<String, Object>();
        cacheResults.put(CacheKeys.GROUP_ENTITITY_STREAM_VIEW_ID + groupId, expectedEntityStreamId);

        context.checking(new Expectations()
        {
            {
                oneOf(cacheMock).multiGet(with(any(Collection.class)));
                will(returnValue(cacheResults));
            }
        });

        PartialMapperResponse<Map<Long, EntityType>, List<Long>> results = sut.execute(request);

        assertEquals(1, results.getResponse().size());
        assertEquals(expectedEntityStreamId, results.getResponse().get(0));

        context.assertIsSatisfied();
    }

    /**
     * Tests mapping a group with partial results.
     */
    @Test
    public void testExecuteForGroupPartialResults()
    {
        final Long groupId = 0L;

        final Map<Long, EntityType> request = new HashMap<Long, EntityType>();

        request.put(groupId, EntityType.GROUP);

        final Map<String, Object> cacheResults = new HashMap<String, Object>();

        context.checking(new Expectations()
        {
            {
                oneOf(cacheMock).multiGet(with(any(Collection.class)));
                will(returnValue(cacheResults));
            }
        });

        PartialMapperResponse<Map<Long, EntityType>, List<Long>> results = sut.execute(request);

        Assert.assertTrue(results.hasUnhandledRequest());

        context.assertIsSatisfied();
    }

    /**
     * Tests mapping for an unhandled. type.
     */
    @Test(expected = RuntimeException.class)
    public void testExecuteForUnhandledType()
    {
        final Map<Long, EntityType> request = new HashMap<Long, EntityType>();
        request.put(0L, EntityType.NOTSET);

        sut.execute(request);
    }

}
