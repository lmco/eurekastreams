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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.service.actions.strategies.activity.ListCollider;
import org.eurekastreams.server.service.actions.strategies.activity.OrSortedListCollider;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the bulk activity stream DB mapper.
 */
public class BulkActivityStreamsDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private BulkActivityStreamsDbMapper sut = null;
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
     * Mock collider.
     */
    private ListCollider colliderMock = context.mock(ListCollider.class);
    
    /**
     * Max items.
     */
    private static final int MAX_ITEMS = 10000;

    /**
     * Setup fixtures.
     */
    @Before
    public void before()
    {
        sut = new BulkActivityStreamsDbMapper();
        sut.setEntityManager(getEntityManager());
        sut.setMaxItems(MAX_ITEMS);
        sut.setOrCollider(colliderMock);
    }

    /**
     * Executes a test with a mock collider.
     */
    @Test
    public void testWithResultsWithColliderMock()
    {
        final List<Long> request = new ArrayList<Long>();
        request.add(1L);
        request.add(2L);

        context.checking(new Expectations()
        {
            {
                exactly(2).of(colliderMock).collide(with(any(List.class)), with(any(List.class)),
                        with(equal(MAX_ITEMS)));
            }
        });

        sut.execute(request);

        context.assertIsSatisfied();
    }

    /**
     * Executes a test with a real collider.
     * Verifies correct results.
     */
    @Test
    public void testWithResultsWithRealCollider()
    {
        sut.setOrCollider(new OrSortedListCollider());

        final int expectedSize = 2;
        
        final List<Long> request = new ArrayList<Long>();
        request.add(1L);
        request.add(2L);

        List<Long> results = sut.execute(request);
        
        Assert.assertEquals(expectedSize, results.size());

        sut.setOrCollider(colliderMock);
    }

    /**
     * Executes a test with no results.
     */
    @Test
    public void testWithoutResults()
    {
        sut.setOrCollider(new OrSortedListCollider());
        
        final int expectedSize = 0;
        
        final List<Long> request = new ArrayList<Long>();
        request.add(0L);

        List<Long> results = sut.execute(request);

        Assert.assertEquals(expectedSize, results.size());

        sut.setOrCollider(colliderMock);
    }
}
