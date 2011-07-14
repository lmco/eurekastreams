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
package org.eurekastreams.web.client.events;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.junit.GWTMockUtilities;


/**
 * Tests the event bus.
 *
 */
public class EventBusTest
{
    /**
     * JMock context for making mocks.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
            GWTMockUtilities.disarm();
        }
    };

    /**
     * Mock of an observer to a "String" type event.
     */
    private final Observer<String> stringObserver = context.mock(Observer.class, "stringObserver");

    /**
     * Mock of an observer to a "String" type event.
     */
    private final Observer<String> stringObserver2 = context.mock(Observer.class, "stringObserver2");

    /**
     * Mock of an observer to a "Boolean" type event.
     */
    private final Observer<Boolean> booleanObserver = context.mock(Observer.class, "booleanObserver");

    /**
     * Mock of an observer to an "Integer" type event.
     */
    private final Observer<Integer> integerObserver = context.mock(Observer.class, "integerObserver");

    /**
     * The event bus.
     */
    private EventBus sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new EventBus();
    }

    /**
     * Just tests the singleton. Used for coverage cause its a silly test.
     */
    @Test
    public void getInstance()
    {
        Assert.assertEquals(sut.getClass(), EventBus.getInstance().getClass());
    }

    /**
     * Notifies the observers test.
     */
    @Test
    public void notifyObservers()
    {
        final String event = new String("I'm an event!");

        sut.addObserver(new String(), stringObserver);
        sut.addObserver(new String(), stringObserver2);
        sut.addObserver(new Boolean(true), booleanObserver);

        context.checking(new Expectations()
        {
            {
                oneOf(stringObserver).update(event);
                oneOf(stringObserver2).update(event);
            }
        });

        sut.notifyObservers(event);
        context.assertIsSatisfied();
    }

    /**
     * Tests that one observer throwing an exception doesn't keep the rest from running.
     */
    @Test
    public void notifyObserversWithAnException()
    {
        final String event = new String("I'm an event!");

        sut.addObserver(new String(), stringObserver);
        sut.addObserver(new String(), stringObserver2);
        sut.addObserver(new Boolean(true), booleanObserver);

        context.checking(new Expectations()
        {
            {
                oneOf(stringObserver).update(event);
                will(throwException(new RuntimeException("I'm ill-behaved!")));
                oneOf(stringObserver2).update(event);
                will(throwException(new RuntimeException("So am I!")));
            }
        });

        sut.notifyObservers(event);
        context.assertIsSatisfied();
    }

    /**
     * Remove all observers.
     */
    @Test
    public void clear()
    {
        // add some observers
        sut.addObserver(String.class, stringObserver);
        sut.addObserver(Boolean.class, booleanObserver);

        // take the action
        sut.clear();

        // now raise the events and make sure no one listens
        sut.notifyObservers("A string event");
        sut.notifyObservers(Boolean.TRUE);

        context.assertIsSatisfied();
    }

    /**
     * Tests buffering and restoring events.
     */
    @Test
    public void testBufferRestore()
    {
        // add some observers
        sut.addObserver(String.class, stringObserver);
        sut.addObserver(Boolean.class, booleanObserver);

        // buffer
        sut.bufferObservers();

        // add more observers
        sut.addObserver(String.class, stringObserver2);
        sut.addObserver(Integer.class, integerObserver);

        // restore
        sut.restoreBufferedObservers();

        // expect to only see the observers registered before the buffering
        final String stringEvent = "I'm an event!";
        final Boolean booleanEvent = Boolean.TRUE;
        context.checking(new Expectations()
        {
            {
                oneOf(stringObserver).update(stringEvent);
                oneOf(booleanObserver).update(booleanEvent);

                // just to be really clear about it...
                never(stringObserver2);
                never(integerObserver);
            }
        });

        sut.notifyObservers(stringEvent);
        sut.notifyObservers(booleanEvent);
        sut.notifyObservers(new Integer(1));
        context.assertIsSatisfied();
    }

    /**
     * Tests removing an observer.
     */
    @Test
    public void testRemove()
    {
        sut.addObserver(String.class, stringObserver);
        sut.addObserver(String.class, stringObserver2);

        sut.removeObserver(String.class, stringObserver);

        final String stringEvent = "I'm an event!";
        context.checking(new Expectations()
        {
            {
                oneOf(stringObserver2).update(stringEvent);

                // just to be really clear about it...
                never(stringObserver);
            }
        });

        sut.notifyObservers(stringEvent);
        context.assertIsSatisfied();
    }

    /**
     * Tests removing an observer.
     */
    @Test
    public void testRemoveOnlyOne()
    {
        sut.addObserver(String.class, stringObserver);

        sut.removeObserver(String.class, stringObserver);

        final String stringEvent = "I'm an event!";
        context.checking(new Expectations()
        {
            {
                // just to be really clear about it...
                never(stringObserver);
            }
        });

        sut.notifyObservers(stringEvent);
        context.assertIsSatisfied();
    }

    /**
     * Tests removing an observer.
     */
    @Test
    public void testRemoveNotFound()
    {
        final String stringEvent = "I'm an event!";

        sut.addObserver(String.class, stringObserver2);

        sut.removeObserver(stringEvent, stringObserver);

        context.checking(new Expectations()
        {
            {
                oneOf(stringObserver2).update(stringEvent);

                // just to be really clear about it...
                never(stringObserver);
            }
        });

        sut.notifyObservers(stringEvent);
        context.assertIsSatisfied();
    }

    /**
     * Tests removing an observer.
     */
    @Test
    public void testRemoveNoneFound()
    {
        sut.removeObserver(String.class, stringObserver);

        sut.notifyObservers("I'm an event!");
        context.assertIsSatisfied();
    }
}
