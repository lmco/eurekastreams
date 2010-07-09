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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for GetCoordinatorIdsByGroupId.
 * 
 */
public class GetCoordinatorIdsByGroupIdTest extends CachedMapperTest
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
    private GetCoordinatorIdsByGroupId sut;

    /**
     * Id for dataset.xml group with coordinators.
     */
    private final long groupId = 1L;

    /**
     * Id for dataset.xml coordinator.
     */
    private final long smithersId = 98L;

    /**
     * Id for dataset.xml coordinator.
     */
    private final long fordId = 42L;

    /**
     * Cache.
     */
    Cache cacheMock = context.mock(Cache.class);

    /**
     * Cache that's wired into the SUT before we get our grubby hands on it so
     * we don't screw up tests downstream.
     */
    @Autowired
    private final Cache autowiredCache = null;

    /**
     * Setup for tests.
     */
    @Before
    public void testSetUp()
    {
        sut.setCache(cacheMock);
    }

    /**
     * Restore the autowired cache.
     */
    @After
    public void tearDown()
    {
        sut.setCache(autowiredCache);
    }
    
    /**
     * test execute method.
     */
    @Test
    public void testExecute()
    {
        // setup cache expectations
        final List<Long> coordIds = new ArrayList<Long>(2);
        coordIds.add(fordId);
        coordIds.add(smithersId);

        context.checking(new Expectations()
        {
            {
                oneOf(cacheMock).getList(
                        CacheKeys.COORDINATOR_PERSON_IDS_BY_GROUP_ID + groupId);
                will(returnValue(null));

                oneOf(cacheMock).setList(
                        CacheKeys.COORDINATOR_PERSON_IDS_BY_GROUP_ID + groupId,
                        coordIds);
            }
        });

        // exercise sut.
        List<Long> results = sut.execute(groupId);

        // assert results are as expected
        assertEquals(2, results.size());
        assertTrue(results.contains(smithersId));
        assertTrue(results.contains(fordId));

        context.assertIsSatisfied();
    }

}
