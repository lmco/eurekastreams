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

import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests mapper to get cached alert counts.
 */
public class GetCachedAlertCountByUserIdTest extends CachedMapperTest
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
     * Mock sync mapper.
     */
    private final SyncUnreadApplicationAlertCountCacheByUserId syncMapperMock = context
            .mock(SyncUnreadApplicationAlertCountCacheByUserId.class);

    /**
     * System under test.
     */
    private GetCachedAlertCountByUserId sut;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new GetCachedAlertCountByUserId(syncMapperMock);
        sut.setCache(getCache());
    }

    /**
     * Sample user id.
     */
    private static final long USER_ID = 123;

    /**
     * Sample count.
     */
    private static final int COUNT = 25;

    /**
     * Sample cache key.
     */
    private static final String CACHE_KEY = CacheKeys.UNREAD_APPLICATION_ALERT_COUNT_BY_USER + USER_ID;

    /**
     * Tests execute method with initial value in cache.
     */
    @Test
    public void testExecuteWithCachedValue()
    {
        getCache().set(CACHE_KEY, COUNT);
        int result = sut.execute(USER_ID);
        assertEquals(COUNT, result);
        context.assertIsSatisfied();
    }

    /**
     * Tests execute method with no initial value in cache.
     */
    @Test
    public void testExecuteWithoutCachedValue()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(syncMapperMock).execute(USER_ID);
                will(returnValue(COUNT));
            }
        });

        int result = sut.execute(USER_ID);
        assertEquals(COUNT, result);
        context.assertIsSatisfied();
    }
}
