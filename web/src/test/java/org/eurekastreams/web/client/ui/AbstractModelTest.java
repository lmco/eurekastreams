/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the AbstractModel class.
 */
public class AbstractModelTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Types of events to use for testing.
     */
    private enum EventTypes
    {
        /**
         * An event that handlers exist for.
         */
        TestEvent,
        /**
         * An event that handlers don't exist for.
         */
        TestEventNoListeners
    }

    /**
     * System under test.
     */
    AbstractModel sut;

    /** Mock change handler. */
    ModelChangeListener changeHandler = context.mock(ModelChangeListener.class, "changeHandler");

    /** Mock change handler. */
    ModelChangeListener changeHandler2 = context.mock(ModelChangeListener.class, "changeHandler2");

    /**
     * Sets up the test.
     */
    @Before
    public final void setUp()
    {
        sut = new AbstractModel()
        {
        };
    }

    /**
     * Tests the change handlers.
     */
    @Test
    public final void testWithChangeHandlers()
    {
        sut.addChangeListener(EventTypes.TestEvent, changeHandler);
        sut.addChangeListener(EventTypes.TestEvent, changeHandler2);

        context.checking(new Expectations()
        {
            {
                oneOf(changeHandler).onChange();
                oneOf(changeHandler2).onChange();
            }
        });

        sut.notifyChangeListeners(EventTypes.TestEvent);

        context.assertIsSatisfied();
    }

    /**
     * Tests the change handlers when none exist for an event type.
     */
    @Test
    public final void testWithNoChangeHandlers()
    {
        sut.addChangeListener(EventTypes.TestEvent, changeHandler);

        context.checking(new Expectations()
        {
            {
                never(changeHandler).onChange();
            }
        });

        sut.notifyChangeListeners(EventTypes.TestEventNoListeners);

        context.assertIsSatisfied();
    }
}
