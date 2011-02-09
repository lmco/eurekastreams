/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.dialog;

import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;

import org.eurekastreams.commons.client.ui.WidgetCommand;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.PreDialogHideEvent;
import org.eurekastreams.web.client.ui.Session;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.junit.GWTMockUtilities;

/**
 * Tests the hide modal dialog command.
 */
public class HideModalDialogTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
            GWTMockUtilities.disarm();
        }
    };

    /** Mock dialog. */
    private final Dialog dialog = context.mock(Dialog.class);

    /** Dialog factory. */
    private DialogFactory factory = null;

    /** Instance of the command to test. */
    private WidgetCommand sut = null;

    /** Fixture: session. */
    private final Session session = context.mock(Session.class);

    /** Fixture: event bus. */
    private final EventBus eventBus = context.mock(EventBus.class);

    /**
     * Pre-test initialization.
     */
    @Before
    public final void setUp()
    {
        factory = new DialogFactory(dialog);
        sut = factory.getCommand("hideModalDialog");
        Session.setInstance(session);
    }

    /**
     * Post-test tear down.
     */
    @After
    public final void tearDown()
    {
        Session.setInstance(null);
    }

    /**
     * Tests executing the command.
     */
    @Test
    public final void testExecute()
    {

        context.checking(new Expectations()
        {
            {
                oneOf(dialog).hide();
                oneOf(dialog).setBgVisible(false);
                allowing(session).getEventBus();
                will(returnValue(eventBus));
                oneOf(eventBus).notifyObservers(with(equalInternally(new PreDialogHideEvent(dialog))));
            }
        });

        sut.execute();
        context.assertIsSatisfied();
    }
}
