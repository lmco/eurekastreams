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
package org.eurekastreams.server.persistence.mappers.chained;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.service.actions.strategies.activity.ListCollider;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the ListColliderAdapter.
 */
public class ListColliderAdapterTest
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
    private ListColliderAdapter sut;

    /**
     * Mock of the collider.
     */
    private ListCollider colliderMock = context.mock(ListCollider.class);

    /**
     * Max results to find.
     */
    private static final int MAX_RESULTS = 10;

    /**
     * Setup test fixtures.
     */
    @Before
    public void setUp()
    {
        sut = new ListColliderAdapter(colliderMock, MAX_RESULTS);
    }

    /**
     * Tests combining two lists.
     */
    @Test
    public void testCombine()
    {
        final List<Long> listA = new ArrayList<Long>();
        final List<Long> listB = new ArrayList<Long>();

        context.checking(new Expectations()
        {
            {
                oneOf(colliderMock).collide(listA, listB, MAX_RESULTS);
            }
        });

        sut.combine(listA, listB);

        context.assertIsSatisfied();
    }
}
