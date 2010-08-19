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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.eurekastreams.server.persistence.mappers.chained.PartialMapperResponse;
import org.eurekastreams.server.service.actions.strategies.activity.ListCollider;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the bulk activity streams partial mapper.
 * 
 */
public class BulkActivityStreamsMapperTest
{
    /**
     * Context for building mock objects.
     */
    private static final Mockery CONTEXT = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private static BulkActivityStreamsMapper sut;

    /**
     * Cache mock.
     */
    private static Cache cacheMock = CONTEXT.mock(Cache.class);

    /**
     * Collider mock.
     */
    private static ListCollider colliderMock = CONTEXT.mock(ListCollider.class);

    /**
     * Max items to return.
     */
    private static final int MAX_ITEMS = 10000;

    /**
     * Setup test fixtures.
     */
    @BeforeClass
    public static final void setUp()
    {
        sut = new BulkActivityStreamsMapper();
        sut.setCache(cacheMock);
        sut.setMaxItems(MAX_ITEMS);
        sut.setOrCollider(colliderMock);
    }

    /**
     * Executes with no items left to send to the other mapper.
     */
    @Test
    public final void executeWithNoRemainingItemsTest()
    {
        List<Long> request = new ArrayList<Long>();
        request.add(0L);
        request.add(1L);
        request.add(2L);
        request.add(3L);
        request.add(4L);

        final Map<String, ArrayList<Long>> results = new HashMap<String, ArrayList<Long>>();

        for (Long itemId : request)
        {
            results.put(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + itemId, new ArrayList<Long>());
        }

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(cacheMock).multiGetList(with(any(Collection.class)));
                will(returnValue(results));

                exactly(results.keySet().size()).of(colliderMock).collide(with(any(List.class)), with(any(List.class)),
                        with(equal(MAX_ITEMS)));
            }
        });

        PartialMapperResponse<List<Long>, List<Long>> response = sut.execute(request);

        Assert.assertFalse(response.hasUnhandledRequest());

        CONTEXT.assertIsSatisfied();
    }

    /**
     * Execute with remaining items to send to the other mapper.
     */
    @Test
    public final void executeWithRemainingItemsTest()
    {
        List<Long> request = new ArrayList<Long>();
        request.add(0L);
        request.add(1L);
        request.add(2L);
        request.add(3L);
        request.add(4L);

        final Map<String, ArrayList<Long>> results = new HashMap<String, ArrayList<Long>>();
        results.put(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + request.get(0), new ArrayList<Long>());
        results.put(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + request.get(request.size() - 1), new ArrayList<Long>());

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(cacheMock).multiGetList(with(any(Collection.class)));
                will(returnValue(results));

                exactly(results.keySet().size()).of(colliderMock).collide(with(any(List.class)), with(any(List.class)),
                        with(equal(MAX_ITEMS)));
            }
        });

        PartialMapperResponse<List<Long>, List<Long>> response = sut.execute(request);

        Assert.assertTrue(response.hasUnhandledRequest());

        CONTEXT.assertIsSatisfied();
    }

    /**
     * Execute wth no items found.
     */
    @Test
    public final void executeWithNoItemsFound()
    {
        List<Long> request = new ArrayList<Long>();
        request.add(0L);
        request.add(1L);
        request.add(2L);
        request.add(3L);
        request.add(4L);

        final Map<String, ArrayList<Long>> results = new HashMap<String, ArrayList<Long>>();

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(cacheMock).multiGetList(with(any(Collection.class)));
                will(returnValue(results));

                exactly(results.keySet().size()).of(colliderMock).collide(with(any(List.class)), with(any(List.class)),
                        with(equal(MAX_ITEMS)));
            }
        });

        PartialMapperResponse<List<Long>, List<Long>> response = sut.execute(request);

        Assert.assertTrue(response.hasUnhandledRequest());

        CONTEXT.assertIsSatisfied();
    }

    /**
     * Execute with empty request.
     */
    @Test
    public final void executeWithEmptyRequest()
    {
        List<Long> request = new ArrayList<Long>();

        final Map<String, ArrayList<Long>> results = new HashMap<String, ArrayList<Long>>();

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(cacheMock).multiGetList(with(any(Collection.class)));
                will(returnValue(results));

                exactly(results.keySet().size()).of(colliderMock).collide(with(any(List.class)), with(any(List.class)),
                        with(equal(MAX_ITEMS)));
            }
        });

        PartialMapperResponse<List<Long>, List<Long>> response = sut.execute(request);

        Assert.assertFalse(response.hasUnhandledRequest());

        CONTEXT.assertIsSatisfied();
    }
}
