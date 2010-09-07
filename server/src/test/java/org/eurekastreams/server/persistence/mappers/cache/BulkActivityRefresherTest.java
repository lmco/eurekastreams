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
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the bulk activity refresher.
 */
public class BulkActivityRefresherTest
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
    private static BulkActivityRefresher sut;

    /**
     * Cache fed into the loader.
     */
    private static Cache cache = CONTEXT.mock(Cache.class);

    /**
     * Setup fixtures.
     */
    @BeforeClass
    public static final void setup()
    {
        sut = new BulkActivityRefresher();
        sut.setCache(cache);
    }

    /**
     * Tests refreshing.
     */
    @Test
    public void testRefresh()
    {
        final List<ActivityDTO> activities = new ArrayList<ActivityDTO>();
        activities.add(new ActivityDTO()
        {
            {
                setId(1L);
            }
        });

        activities.add(new ActivityDTO()
        {
            {
                setId(2L);
            }
        });

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(cache).set(CacheKeys.ACTIVITY_BY_ID + activities.get(0).getId(), activities.get(0));
                oneOf(cache).set(CacheKeys.ACTIVITY_BY_ID + activities.get(1).getId(), activities.get(1));
            }
        });

        sut.refresh(Arrays.asList(1L, 2L), activities);

        CONTEXT.assertIsSatisfied();
    }
}
