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
package org.eurekastreams.web.client.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import junit.framework.Assert;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.search.modelview.AuthenticationType;
import org.eurekastreams.web.client.events.EventBus;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests the session.
 */
public class SessionTest
{
    /**
     * System under test.
     */
    Session sut = Session.getInstance();

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
     * Test the action processor property.
     */
    @Test
    public final void actionProcessorPropertyTest()
    {
        ActionProcessor processor = context.mock(ActionProcessor.class);

        sut.setActionProcessor(processor);

        Assert.assertEquals(processor, sut.getActionProcessor());
    }

    /**
     * Test the event bus property.
     */
    @Test
    public final void eventBusPropertyTest()
    {
        EventBus eventBus = context.mock(EventBus.class);

        sut.setEventBus(eventBus);

        Assert.assertEquals(eventBus, sut.getEventBus());
    }

    /**
     * Test the current person property.
     */
    @Test
    public final void currentPersonPropertyTest()
    {
        Person person = context.mock(Person.class);

        sut.setCurrentPerson(person);

        Assert.assertEquals(person, sut.getCurrentPerson());
    }

    /**
     * Test the authentication type property.
     */
    @Test
    public final void authenticationTypeTest()
    {
        //verify default value
        assertEquals(AuthenticationType.NOTSET, sut.getAuthenticationType());

        //set new value
        sut.setAuthenticationType(AuthenticationType.FORM);

        //verify new value
        assertEquals(AuthenticationType.FORM, sut.getAuthenticationType());
    }

    /**
     * Test the periodic event manager property.
     */
    @Test
    public final void periodicEventManagerPropertyTest()
    {
        PeriodicEventManager evtMgr = context.mock(PeriodicEventManager.class);

        sut.setPeriodicEventManager(evtMgr);

        assertSame(evtMgr, sut.getPeriodicEventManager());
    }

}
