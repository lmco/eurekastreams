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
import java.util.List;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the bulk activity streams refresher. Note that this code doesn't do anything yet.
 */
public class BulkActivityStreamsRefresherTest
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
    private BulkActivityStreamsRefresher sut;

    /**
     * Mock cache.
     */
    private Cache cacheMock = context.mock(Cache.class);

    /**
     * Setup the test fixtures.
     */
    @Before
    public final void setUp()
    {
        sut = new BulkActivityStreamsRefresher();
        sut.setCache(cacheMock);
    }

    /**
     * Tests refreshing.
     */
    @Test
    public final void testRefresh()
    {
        final List<Long> request = new ArrayList<Long>();
        final List<Long> data = new ArrayList<Long>();

        sut.refresh(request, data);
    }
}
