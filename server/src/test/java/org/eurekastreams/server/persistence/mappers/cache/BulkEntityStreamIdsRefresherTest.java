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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.server.domain.EntityType;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Bulk entity stream id refresher test.
 */
public class BulkEntityStreamIdsRefresherTest
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
    private BulkEntityStreamIdsRefresher sut;

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
        sut = new BulkEntityStreamIdsRefresher();
        sut.setCache(cacheMock);
    }

    /**
     * Tests executing the refresher.
     */
    @Test
    public void testExecute()
    {
        final long personId1 = 42L;
        final long personId2 = 43L;
        final long groupId1 = 44L;
        final long groupId2 = 45L;

        final Map<Long, EntityType> request = new HashMap<Long, EntityType>();
        request.put(personId1, EntityType.PERSON);
        request.put(personId2, EntityType.PERSON);
        request.put(groupId1, EntityType.GROUP);
        request.put(groupId2, EntityType.GROUP);

        final List<Long> data = new ArrayList<Long>();
        data.add(1L);
        data.add(2L);
        data.add(3L);
        data.add(4L);

        context.checking(new Expectations()
        {
            {
                oneOf(cacheMock).set(CacheKeys.PERSON_ENTITITY_STREAM_VIEW_ID + personId1, data.get(0));
                oneOf(cacheMock).set(CacheKeys.PERSON_ENTITITY_STREAM_VIEW_ID + personId2, data.get(1));
                oneOf(cacheMock).set(CacheKeys.GROUP_ENTITITY_STREAM_VIEW_ID + groupId1, data.get(2));
                oneOf(cacheMock).set(CacheKeys.GROUP_ENTITITY_STREAM_VIEW_ID + groupId2, data.get(3));
            }
        });

        sut.refresh(request, data);

        context.assertIsSatisfied();
    }

    /**
     * Execute the refresher with an empty request. Expected to not do anything.
     */
    @Test
    public void testExecuteEmptyRequest()
    {

        final Map<Long, EntityType> request = new HashMap<Long, EntityType>();

        final List<Long> data = new ArrayList<Long>();

        sut.refresh(request, data);

        context.assertIsSatisfied();
    }

    /**
     * Tests executing with an unhandled type.
     */
    @Test(expected = RuntimeException.class)
    public void testExecuteForUnhandledType()
    {
        final Map<Long, EntityType> request = new HashMap<Long, EntityType>();
        request.put(0L, EntityType.NOTSET);

        List<Long> data = new ArrayList<Long>();
        data.add(1L);

        sut.refresh(request, data);
    }
}
