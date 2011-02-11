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
package org.eurekastreams.web.client.ui.common.dialog;

import static org.junit.Assert.assertSame;

import org.eurekastreams.commons.client.ui.WidgetCommand;
import org.eurekastreams.commons.client.ui.WidgetFactory;
import org.eurekastreams.web.client.AnonymousClassInterceptor;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.ClickListener;

/**
 * Tests the dialog controller.
 *
 *
 */
public class DialogControllerTest
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
     * The mock dialog.
     */
    private Dialog dialogMock = null;

    /**
     * The mock factory.
     */
    private WidgetFactory factoryMock;

    /**
     * The controller to test.
     */
    private DialogController sut = null;

    /**
     * Intercepts the close click listener.
     */
    private final AnonymousClassInterceptor<ClickListener> closeClickInt =
        new AnonymousClassInterceptor<ClickListener>();

    /**
     * The close command.
     */
    private final WidgetCommand closeCommand = context
            .mock(WidgetCommand.class);

    /**
     * Pre-test initialization.
     */
    @Before
    public final void setUp()
    {
        GWTMockUtilities.disarm();
        dialogMock = context.mock(Dialog.class);
        factoryMock = context.mock(WidgetFactory.class);

        sut = new DialogController(dialogMock, factoryMock);

        final DialogContent mockContent = context.mock(DialogContent.class);

        /* set the view expections */
        context.checking(new Expectations()
        {
            {
                allowing(factoryMock).getCommand("hideModalDialog");
                will(returnValue(closeCommand));

                oneOf(dialogMock).addCloseButtonListener(
                        with(any(ClickListener.class)));
                will(closeClickInt);

                oneOf(dialogMock).setEscapeCommand(closeCommand);
            }
        });
    }

    /**
     * Tests initializing the controller.
     */
    @Test
    public final void testInit()
    {
        sut.init();

        context.assertIsSatisfied();
    }

    /**
     * Tests the close click.
     */
    @Test
    public final void testClose()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(closeCommand).execute();
            }
        });

        sut.init();

        ClickListener closeListener = closeClickInt.getObject();
        closeListener.onClick(null);

        context.assertIsSatisfied();
    }

    /**
     * Tests getCloseCommand.
     */
    @Test
    public void testGetCloseCommand()
    {
        sut.init();

        WidgetCommand result = sut.getCloseCommand();

        context.assertIsSatisfied();
        assertSame(result, closeCommand);
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
