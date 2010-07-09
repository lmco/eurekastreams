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

import org.eurekastreams.server.domain.stream.StreamView.Type;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link GetCoreStreamViewIdCacheMapper}.
 *
 */
public class GetCoreStreamViewIdCacheMapperTest
{
    /**
     * System under test.
     */
    private GetCoreStreamViewIdCacheMapper sut;

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
     * Mocked instance of the cache object.
     */
    private final Cache cacheMock = context.mock(Cache.class);

    /**
     * Prepare sut for test.
     */
    @Before
    public void setup()
    {
        sut = new GetCoreStreamViewIdCacheMapper();
        sut.setCache(cacheMock);
    }

    /**
     * Test successfully retrieving the Everyone StreamView Id.
     */
    @Test
    public void testRetrieveEveryone()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(cacheMock).get(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE);
                will(returnValue(3L));
            }
        });


        Long result = sut.execute(Type.EVERYONE);
        assertEquals(new Long(3), result);

        context.assertIsSatisfied();
    }

    /**
     * Test successfully retrieving the ParentOrg StreamView Id.
     */
    @Test
    public void testRetrieveParentOrg()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(cacheMock).get(CacheKeys.CORE_STREAMVIEW_ID_PARENTORG);
                will(returnValue(3L));
            }
        });


        Long result = sut.execute(Type.PARENTORG);
        assertEquals(new Long(3), result);

        context.assertIsSatisfied();
    }

    /**
     * Test successfully retrieving the Everyone StreamView Id.
     */
    @Test
    public void testRetrievePeopleFollow()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(cacheMock).get(CacheKeys.CORE_STREAMVIEW_ID_PEOPLEFOLLOW);
                will(returnValue(3L));
            }
        });


        Long result = sut.execute(Type.PEOPLEFOLLOW);
        assertEquals(new Long(3), result);

        context.assertIsSatisfied();
    }

    /**
     * Test successfully retrieving the Starred StreamView Id.
     */
    @Test
    public void testRetrieveStarred()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(cacheMock).get(CacheKeys.CORE_STREAMVIEW_ID_STARRED);
                will(returnValue(3L));
            }
        });


        Long result = sut.execute(Type.STARRED);
        assertEquals(new Long(3), result);

        context.assertIsSatisfied();
    }

    /**
     * Test Failure.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFailure()
    {
        sut.execute(Type.NOTSET);
    }
}
