/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.pages.master;

import static org.eurekastreams.web.client.CommandActionProcessorMockSupport.setupActionProcessor;

import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacade;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.eurekastreams.commons.client.ActionProcessor;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Tests the master page controller.
 */
public class MasterCompositeControllerImplTest
{
    /**
     * Subject under test.
     */
    private MasterCompositeController sut = null;

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
     * Mock of the view facade.
     */
    private MasterComposite viewFacadeMock;

    /**
     * Mock of the service.
     */
    private ActionProcessor actionProcessorMock = context.mock(ActionProcessor.class);

    /**
     * Call native JS methods.
     */
    private WidgetJSNIFacade jsniFacadeMock = context.mock(WidgetJSNIFacade.class);

    /**
     * Mock session.
     */
    private Session sessionMock = context.mock(Session.class);

    /**
     * Mock event bus.
     */
    private EventBus eventBusMock = context.mock(EventBus.class);

    /**
     * Pre-test initialization.
     */
    @Before
    public final void setUp()
    {
        GWTMockUtilities.disarm();
        viewFacadeMock = context.mock(MasterComposite.class);
    }

    /**
     * Test setTimer().
     */
    @Test
    public final void testSetTimer()
    {
        sut = new MasterCompositeController(viewFacadeMock, actionProcessorMock, jsniFacadeMock, sessionMock,
                eventBusMock);
        final Timer keepAliveTimer = context.mock(Timer.class);

        context.checking(new Expectations()
        {
            {
                final int tenMinutes = 600000;
                one(keepAliveTimer).scheduleRepeating(tenMinutes);
            }
        });

        sut.setTimer(keepAliveTimer);
        context.assertIsSatisfied();
    }

    /**
     * Test keepAlive().
     */
    @Test
    public final void testKeepAliveSuccess()
    {
        sut = new MasterCompositeController(viewFacadeMock, actionProcessorMock, jsniFacadeMock, sessionMock,
                eventBusMock);
        final Boolean expectedResponse = true;
        final Sequence executeSequence = context.sequence("executeSequence");
        AsyncCallback<Boolean> commandCallback = setupActionProcessor(context, actionProcessorMock, executeSequence);

        // Execute the command, which will invoke the rpc call
        sut.keepAlive();

        /*
         * Imitate the asynchronous nature of rpc by calling the async rpcCallback here
         */
        commandCallback.onSuccess(expectedResponse);

        /* Verify */
        context.assertIsSatisfied();
    }

    /**
     * Test keepAlive() on failure.
     */
    @Test
    public final void testKeepAliveFailure()
    {
        sut = new MasterCompositeController(viewFacadeMock, actionProcessorMock, jsniFacadeMock, sessionMock,
                eventBusMock);
        final Sequence executeSequence = context.sequence("executeSequence");
        AsyncCallback<Boolean> commandCallback = setupActionProcessor(context, actionProcessorMock, executeSequence);

        // Execute the command, which will invoke the rpc call
        sut.keepAlive();

        /*
         * Imitate the asynchronous nature of rpc by calling the async rpcCallback here
         */
        commandCallback.onFailure(new RuntimeException("FOO"));

        /* Verify */
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
