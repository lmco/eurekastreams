/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.notification.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests LockedUserRecipientFilter.
 */
public class LockedUserRecipientFilterTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** SUT. */
    private LockedUserRecipientFilter sut;

    /** Fixture: person. */
    private final PersonModelView person = context.mock(PersonModelView.class);

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new LockedUserRecipientFilter();
    }

    /**
     * Tests shouldFilter.
     */
    @Test
    public void testShouldFilterLocked()
    {
        context.checking(new Expectations()
        {
            {
                allowing(person).isAccountLocked();
                will(returnValue(true));
            }
        });

        assertTrue(sut.shouldFilter(null, person, null, null));
        context.assertIsSatisfied();
    }

    /**
     * Tests shouldFilter.
     */
    @Test
    public void testShouldFilterNotLocked()
    {
        context.checking(new Expectations()
        {
            {
                allowing(person).isAccountLocked();
                will(returnValue(false));
            }
        });

        assertFalse(sut.shouldFilter(null, person, null, null));
        context.assertIsSatisfied();
    }

}
