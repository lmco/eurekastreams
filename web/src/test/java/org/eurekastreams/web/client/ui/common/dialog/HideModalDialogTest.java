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
package org.eurekastreams.web.client.ui.common.dialog;

import org.eurekastreams.commons.client.ui.WidgetCommand;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.junit.GWTMockUtilities;

/**
 * Tests the dialog hiding command.
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
        }
    };

    /**
     * Mock dialog.
     */
    private Dialog dialogMock = null;

    /**
     * Dialog factory.
     */
    private DialogFactory factory = null;

    /**
     * Instance of command to test.
     */
    private WidgetCommand sut = null;

    /**
     * Pre-test initialization.
     */
    @Before
    public final void setUp()
    {
        GWTMockUtilities.disarm();
        dialogMock = context.mock(Dialog.class);
        factory = new DialogFactory(dialogMock);
        sut = factory.getCommand("hideModalDialog");
    }

    /**
     * Tests executing the command.
     */
    @Test
    public final void testExecute()
    {
        final Sequence executeSequence = context.sequence("executeSequence");

        /* set the view expections */
        context.checking(new Expectations()
        {
            {
                oneOf(dialogMock).hide();
                inSequence(executeSequence);

                oneOf(dialogMock).setBgVisible(false);
                inSequence(executeSequence);
            }
        });

        sut.execute();

        context.assertIsSatisfied();
    }

    /**
     * Post-test tear down.
     */
    @After
    public final void tearDown()
    {
        GWTMockUtilities.restore();
    }
}
