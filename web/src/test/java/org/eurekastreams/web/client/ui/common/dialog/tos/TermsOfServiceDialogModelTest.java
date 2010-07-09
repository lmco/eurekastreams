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
package org.eurekastreams.web.client.ui.common.dialog.tos;

import java.io.Serializable;

import junit.framework.Assert;

import org.eurekastreams.web.client.AnonymousClassInterceptor;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.TermsOfServiceAcceptedEvent;
import org.eurekastreams.web.client.ui.Session;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ActionRequest;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Model test.
 */
public class TermsOfServiceDialogModelTest
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
     * The session mock.
     */
    Session sessionMock = context.mock(Session.class);

    /**
     * The event bus mock.
     */
    EventBus eventBusMock = context.mock(EventBus.class);

    /**
     * System under test.
     */
    private TermsOfServiceDialogModel sut = new TermsOfServiceDialogModel(sessionMock, eventBusMock);

    /**
     * Tests the agree property.
     */
    @Test
    public final void agreePropertyTest()
    {
        sut.setAgreeValue(true);
        Assert.assertTrue(sut.getAgreeValue());

        sut.setAgreeValue(false);
        Assert.assertFalse(sut.getAgreeValue());
    }

    /**
     * Tests accepting the terms of service with success.
     */
    @Test
    public final void acceptTermsOfServiceSuccessTest()
    {
        final ActionProcessor processorMock = context.mock(ActionProcessor.class);

        final AnonymousClassInterceptor<AsyncCallback<Serializable>> acceptCallBackInt = 
            new AnonymousClassInterceptor<AsyncCallback<Serializable>>();

        context.checking(new Expectations()
        {
            {
                oneOf(sessionMock).getActionProcessor();
                will(returnValue(processorMock));

                oneOf(processorMock).makeRequest(with(any(ActionRequest.class)), with(any(AsyncCallback.class)));
                will(acceptCallBackInt);
                
                oneOf(eventBusMock).notifyObservers(with(any(TermsOfServiceAcceptedEvent.class)));
            }
        });

        sut.acceptTermsOfService();
        
        acceptCallBackInt.getObject().onSuccess(null);

        context.assertIsSatisfied();
    }
    
    /**
     * Tests accepting the terms of service with failure.
     */
    @Test
    public final void acceptTermsOfServiceFailureTest()
    {
        final ActionProcessor processorMock = context.mock(ActionProcessor.class);

        final AnonymousClassInterceptor<AsyncCallback<Serializable>> acceptCallBackInt = 
            new AnonymousClassInterceptor<AsyncCallback<Serializable>>();

        context.checking(new Expectations()
        {
            {
                oneOf(sessionMock).getActionProcessor();
                will(returnValue(processorMock));

                oneOf(processorMock).makeRequest(with(any(ActionRequest.class)), with(any(AsyncCallback.class)));
                will(acceptCallBackInt);
            }
        });

        sut.acceptTermsOfService();
        
        acceptCallBackInt.getObject().onFailure(null);

        context.assertIsSatisfied();
    }
}
