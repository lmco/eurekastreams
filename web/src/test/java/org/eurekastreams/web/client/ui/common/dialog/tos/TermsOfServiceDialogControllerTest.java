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
package org.eurekastreams.web.client.ui.common.dialog.tos;

import org.eurekastreams.web.client.AnonymousClassInterceptor;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.TermsOfServiceAcceptedEvent;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.junit.GWTMockUtilities;

/**
 * Controller test.
 */
public class TermsOfServiceDialogControllerTest
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

    /**
     * The view mock.
     */
    TermsOfServiceDialogContent viewMock = context.mock(TermsOfServiceDialogContent.class);

    /**
     * The model mock.
     */
    TermsOfServiceDialogModel modelMock = context.mock(TermsOfServiceDialogModel.class);

    /**
     * The event bus mock.
     */
    EventBus eventBusMock = context.mock(EventBus.class);

    /**
     * The terms of service accepted event.
     */
    final AnonymousClassInterceptor<Observer<TermsOfServiceAcceptedEvent>> tosAcceptEventInt = 
        new AnonymousClassInterceptor<Observer<TermsOfServiceAcceptedEvent>>();

    /**
     * Agree click handler.
     */
    final AnonymousClassInterceptor<ClickHandler> agreeClickHandler = new AnonymousClassInterceptor<ClickHandler>();

    /**
     * Check box change handler.
     */
    final AnonymousClassInterceptor<ValueChangeHandler<Boolean>> checkBoxChangeHandler = 
        new AnonymousClassInterceptor<ValueChangeHandler<Boolean>>();

    /**
     * Setup the test fixtures.
     */
    @Before
    public final void setUp()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(eventBusMock).addObserver(with(equal(TermsOfServiceAcceptedEvent.class)),
                        with(any(Observer.class)));
                will(tosAcceptEventInt);

                oneOf(viewMock).addAgreeClickHandler(with(any(ClickHandler.class)));
                will(agreeClickHandler);

                oneOf(viewMock).addConfirmCheckBoxValueHandler(with(any(ValueChangeHandler.class)));
                will(checkBoxChangeHandler);
            }
        });

        new TermsOfServiceDialogController(viewMock, modelMock, eventBusMock);
    }

    /**
     * Accept event test.
     */
    @Test
    public final void acceptEventTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(viewMock).close();
            }
        });

        tosAcceptEventInt.getObject().update(null);

        context.assertIsSatisfied();
    }

    /**
     * Agree click test true.
     */
    @Test
    public final void agreeClickTrueTest()
    {
        context.checking(new Expectations()
        {
            {
                allowing(modelMock).getAgreeValue();
                will(returnValue(true));

                oneOf(modelMock).acceptTermsOfService();
            }
        });

        agreeClickHandler.getObject().onClick(null);

        context.assertIsSatisfied();
    }

    /**
     * Agree click test false.
     */
    @Test
    public final void agreeClickFalseTest()
    {
        context.checking(new Expectations()
        {
            {
                allowing(modelMock).getAgreeValue();
                will(returnValue(false));

                never(modelMock).acceptTermsOfService();
            }
        });

        agreeClickHandler.getObject().onClick(null);

        context.assertIsSatisfied();
    }

    /**
     * Checkbox click test true.
     */
    @Test
    public final void checkboxClickTrueTest()
    {
        final ValueChangeEvent<Boolean> event = context.mock(ValueChangeEvent.class);

        context.checking(new Expectations()
        {
            {
                allowing(event).getValue();
                will(returnValue(true));

                oneOf(modelMock).setAgreeValue(true);
                oneOf(viewMock).setAcceptEnabled(true);
            }
        });

        checkBoxChangeHandler.getObject().onValueChange(event);

        context.assertIsSatisfied();
    }

    /**
     * Checkbox click test false.
     */
    @Test
    public final void checkboxClickFalseTest()
    {
        final ValueChangeEvent<Boolean> event = context.mock(ValueChangeEvent.class);

        context.checking(new Expectations()
        {
            {
                allowing(event).getValue();
                will(returnValue(false));

                oneOf(modelMock).setAgreeValue(false);
                oneOf(viewMock).setAcceptEnabled(false);
            }
        });

        checkBoxChangeHandler.getObject().onValueChange(event);

        context.assertIsSatisfied();
    }

    /**
     * Initialization test.
     * Tests only expectations set in setUp().
     */
    @Test
    public final void initTest()
    {
        context.assertIsSatisfied();
    }
}
