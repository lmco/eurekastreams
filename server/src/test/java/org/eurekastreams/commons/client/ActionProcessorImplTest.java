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
package org.eurekastreams.commons.client;

import static org.eurekastreams.commons.client.ActionProcessorMockSupport.setupActionService;
import static org.junit.Assert.assertEquals;

import java.io.Serializable;

import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.action.CustomAction;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Tests for ActionProcessorImplTest class.
 *
 */
public class ActionProcessorImplTest
{
    /**
     * Context for mocking.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * ActionRPCServiceAsync.
     */
    private final ActionRPCServiceAsync service = context.mock(ActionRPCServiceAsync.class);

    /**
     * ActionRequest result array.
     */
    @SuppressWarnings("unchecked")
    private ActionRequest[] results = null;

    /**
     * String params array.
     */
    private final String[] paramsRet = { "a", "b", "c" };

    /**
     * The AsyncCallback mock.
     */
    @SuppressWarnings("unchecked")
    private final AsyncCallback widgetCallback = context.mock(AsyncCallback.class);

    /** List of intercepted requests. */
    private ActionRequest[] interceptedRequestList;

    /** Intercepted master callback. */
    private AsyncCallback interceptedMasterCallback;


    /**
     * Test setup.
     */
    @Before
    public final void setUp()
    {
    }

    /**
     * Test makeRequest with queue off.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testMakeRequestWithQueueOff()
    {
        AsyncCallback<ActionRequest[]> rpcCallback = setupActionService(context, service);
        final ActionRequest requestMock = context.mock(ActionRequest.class);

        results = new ActionRequestImpl[1];
        results[0] = new ActionRequestImpl("SampleMethod", paramsRet);
        results[0].setResponse("Result");
        results[0].setId(0);

        context.checking(new Expectations()
        {
            {
                one(requestMock).setSessionId("session");
                one(requestMock).addCallback(widgetCallback);
                one(requestMock).setId(0);
                one(requestMock).executeCallbacks("Result");
            }
        });
        // Create the SUT
        ActionProcessor sut = new ActionProcessorImpl(service);

        // Exercise
        sut.setSessionId("session");
        sut.setQueueRequests(false);
        sut.makeRequest(requestMock, widgetCallback);

        rpcCallback.onSuccess(results);

        // Verification
        context.assertIsSatisfied();
    }

    /**
     * Test makeRequest (alternate convenience form) with queue off.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testMakeRequestAltWithQueueOff()
    {
        final Serializable param = "ThisIsMyParameter";
        final String action = "ThisIsMyActionName";
        final Serializable result = "ThisIsMyResult";
        final int idZero = 0;


        final States state = context.states("phase").startsAs("init");
        context.checking(new Expectations()
        {
            {
                // Note: The custom action could be replaced with two invocations of the AnonymousClassInterceptor if
                // it were accessible (in the same or a dependent jar).
                oneOf(service).execute(with(any(ActionRequest[].class)), with(any(AsyncCallback.class)));
                will(new CustomAction("Intercept the parameters of the call.")
                {
                    @Override
                    public Object invoke(final Invocation invocation) throws Throwable
                    {
                        final Object[] arguments = invocation.getParametersAsArray();
                        interceptedRequestList = (ActionRequest[]) arguments[0];
                        interceptedMasterCallback = (AsyncCallback) arguments[1];
                        return null;
                    }
                });
                when(state.is("makeRequest"));

                oneOf(widgetCallback).onSuccess(with(same(result)));
                when(state.is("runCallback"));
            }
        });

        // setup
        ActionProcessor sut = new ActionProcessorImpl(service);
        sut.setSessionId("session");
        sut.setQueueRequests(false);

        // run makeRequest and validate
        state.become("makeRequest");
        sut.makeRequest(action, param, widgetCallback);


        ActionRequest rqst = interceptedRequestList[0];
        assertEquals(action, rqst.getActionKey());
        assertEquals(0, (int) rqst.getId());
        assertEquals(param, rqst.getParam());
        assertEquals("session", rqst.getSessionId());

        // invoke callback and validate
        ActionRequest response = new ActionRequestImpl();
        response.setId(idZero);
        response.setResponse(result);
        ActionRequest[] masterResponse = { response };

        state.become("runCallback");
        interceptedMasterCallback.onSuccess(masterResponse);

        // Verification
        context.assertIsSatisfied();
    }

    /**
     * test fireQueuedRequests.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testFireQueuedRequestsWithQueueOn()
    {
        AsyncCallback<ActionRequest[]> rpcCallback = setupActionService(context, service);
        final ActionRequest requestMock1 = context.mock(ActionRequest.class);
        final ActionRequest requestMock2 = context.mock(ActionRequest.class, "actionrequest2");

        results = new ActionRequestImpl[2];
        results[0] = new ActionRequestImpl("SampleMethod", paramsRet);
        results[0].setResponse("Result");
        results[0].setId(0);
        results[1] = new ActionRequestImpl("SampleMethod2", paramsRet);
        results[1].setResponse("Result2");
        results[1].setId(1);

        context.checking(new Expectations()
        {
            {
                one(requestMock1).setSessionId("session");
                one(requestMock2).setSessionId("session");
                one(requestMock1).addCallback(widgetCallback);
                one(requestMock2).addCallback(widgetCallback);
                one(requestMock1).setId(0);
                one(requestMock2).setId(1);
                oneOf(requestMock1).executeCallbacks("Result");
                oneOf(requestMock2).executeCallbacks("Result2");
            }
        });
        // Create the SUT
        ActionProcessor sut = new ActionProcessorImpl(service);

        // Exercise
        sut.setSessionId("session");
        sut.setQueueRequests(true);
        sut.makeRequest(requestMock1, widgetCallback);
        sut.makeRequest(requestMock2, widgetCallback);
        sut.fireQueuedRequests();
        rpcCallback.onSuccess(results);

        // Verification
        context.assertIsSatisfied();
    }

    /**
     * Tests fireQueuedRequests with an empty queue.
     */
    @Test
    public void testFireQueuedRequestsEmptyQueue()
    {
        ActionProcessor sut = new ActionProcessorImpl(service);

        sut.fireQueuedRequests();

        context.assertIsSatisfied();
    }
}
