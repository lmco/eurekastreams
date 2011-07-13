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

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.Serializable;
import java.util.logging.Logger;

import org.eurekastreams.commons.exceptions.SessionException;
import org.eurekastreams.server.testing.ParamInterceptor;
import org.eurekastreams.server.testing.TestHelper;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Tests ActionProcessorImpl.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ActionProcessorImplTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Test data: action key. */
    private static final String ACTION1 = "action1";

    /** Test data: action key. */
    private static final String ACTION2 = "action2";

    /** Test data: action key. */
    private static final String ACTION3 = "action3";

    /** Test data: action key. */
    private static final String ACTION4 = "action4";

    /** Test data: session ID. */
    private static final String SESSION_ID1 = "ABDEDB373";

    /** Test data: session ID. */
    private static final String SESSION_ID2 = "3983ABEDCFED";

    /** Action parameter. */
    private final Serializable param1 = context.mock(Serializable.class, "param1");

    /** Action parameter. */
    private final Serializable param2 = context.mock(Serializable.class, "param2");

    /** Action result. */
    private final Serializable result1 = context.mock(Serializable.class, "result1");

    /** Action result. */
    private final Serializable result2 = context.mock(Serializable.class, "result2");

    /** ActionRPCServiceAsync. */
    private final ActionRPCServiceAsync service = context.mock(ActionRPCServiceAsync.class);

    /** Fixture: session established callback. */
    private final AsyncCallback sessionCb = context.mock(AsyncCallback.class, "sessionCb");

    /** Fixture: action callback. */
    private final AsyncCallback actionCb1 = context.mock(AsyncCallback.class, "actionCb1");

    /** Fixture: action callback. */
    private final AsyncCallback actionCb2 = context.mock(AsyncCallback.class, "actionCb2");

    /** Parameter interceptor. */
    private final ParamInterceptor paramInt = new ParamInterceptor();

    /** Fixture: log. */
    private final Logger log = context.mock(Logger.class, "log");

    /** SUT. */
    private ActionProcessor sut;

    /*
     * Note: Many of the app callbacks are mocked to throw exceptions. This is done just to test the exception paths and
     * insure that a ill-behaved callback doesn't interfere with the proper behavior of the SUT.
     */

    /**
     * Test setup.
     * 
     * @throws NoSuchFieldException
     *             Only if SUT changes its log.
     * @throws IllegalAccessException
     *             Shouldn't.
     * @throws IllegalArgumentException
     *             Shouldn't.
     */
    @Before
    public final void setUp() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException
    {
        sut = new ActionProcessorImpl(service, sessionCb);
        TestHelper.setPrivateField(sut, "log", log);
        context.checking(new Expectations()
        {
            {
                // mock logger and ignore log calls to keep errors from spilling into the output and looking like
                // something actually went wrong. Would be ideal if the mock could be configured to ignore any
                // trace/debug logging, but have expectations that some other logging would be done (but without
                // specifying exactly which logging method is used) when a callback throws an exception.
                ignoring(log);
            }
        });
    }

    /**
     * Tests a normal startup sequence: send 2 messages before a session is established, everything succeeds.
     */
    @Test
    public void testNormalStartup()
    {
        sut.setQueueRequests(true);
        sut.makeRequest(ACTION1, param1, actionCb1);
        sut.makeRequest(ACTION2, param2, actionCb2);

        context.checking(new Expectations()
        {
            {
                oneOf(service).establishSession(with(any(AsyncCallback.class)));
                will(paramInt);
            }
        });
        sut.setQueueRequests(false);
        context.assertIsSatisfied();

        AsyncCallback<String> serviceSessionCb = (AsyncCallback<String>) paramInt.getParam(0);
        context.checking(new Expectations()
        {
            {
                oneOf(sessionCb).onSuccess(SESSION_ID1);
                will(throwException(new RuntimeException("Naughty callback")));

                oneOf(service).execute((ActionRequest[]) with(anything()), with(any(AsyncCallback.class)));
                will(paramInt);
            }
        });

        serviceSessionCb.onSuccess(SESSION_ID1);
        context.assertIsSatisfied();

        ActionRequest[] requests = (ActionRequest[]) paramInt.getParam(0);
        AsyncCallback serviceExecCb = (AsyncCallback) paramInt.getParam(1);

        assertEquals(2, requests.length);
        assertEquals(ACTION1, requests[0].getActionKey());
        assertSame(param1, requests[0].getParam());
        assertEquals(SESSION_ID1, requests[0].getSessionId());
        assertEquals(ACTION2, requests[1].getActionKey());
        assertSame(param2, requests[1].getParam());
        assertEquals(SESSION_ID1, requests[1].getSessionId());

        ActionRequest[] results = new ActionRequest[] { new ActionResponse(requests[0], result1),
                new ActionResponse(requests[1], result2) };

        context.checking(new Expectations()
        {
            {
                oneOf(actionCb1).onSuccess(with(same(result1)));
                will(throwException(new RuntimeException("Naughty callback")));
                oneOf(actionCb2).onSuccess(with(same(result2)));
                will(throwException(new RuntimeException("Naughty callback")));
            }
        });

        serviceExecCb.onSuccess(results);
        context.assertIsSatisfied();
    }

    /**
     * Tests losing a session and re-establishing it. Test adds an extra wrinkle by having the service reject the first
     * attempt to re-establish.
     *
     * @throws IllegalAccessException
     *             Shouldn't.
     * @throws IllegalArgumentException
     *             Shouldn't.
     * @throws NoSuchFieldException
     *             Only if test is out of date.
     * @throws SecurityException
     *             Shouldn't.
     */
    @Test
    public void testLoseSession() throws IllegalArgumentException, IllegalAccessException, SecurityException,
            NoSuchFieldException
    {
        // cheat and set the session ID directly in the SUT to set up the initial state
        TestHelper.setPrivateField(sut, "sessionId", SESSION_ID1);

        // -- try to make request, but service will throw session exception --
        context.checking(new Expectations()
        {
            {
                oneOf(service).execute((ActionRequest[]) with(anything()), with(any(AsyncCallback.class)));
                will(paramInt);
            }
        });
        sut.makeRequest(ACTION1, param1, actionCb1);
        context.assertIsSatisfied();

        // -- SUT should ask service to establish new session --
        ActionRequest[] requests = (ActionRequest[]) paramInt.getParam(0);
        AsyncCallback serviceExecCb = (AsyncCallback) paramInt.getParam(1);

        context.checking(new Expectations()
        {
            {
                oneOf(service).establishSession(with(any(AsyncCallback.class)));
                will(paramInt);
            }
        });
        serviceExecCb.onSuccess(new ActionRequest[] { new ActionResponse(requests[0], new SessionException()) });
        context.assertIsSatisfied();

        // -- service replies with error. SUT should notify app --
        AsyncCallback<String> serviceSessionCb = (AsyncCallback<String>) paramInt.getParam(0);
        final Exception ex = new Exception("Can it handle it?");
        context.checking(new Expectations()
        {
            {
                oneOf(sessionCb).onFailure(with(equal(ex)));
                will(throwException(new RuntimeException("Naughty callback")));
            }
        });
        serviceSessionCb.onFailure(ex);
        context.assertIsSatisfied();

        // -- app asks SUT to try again --
        context.checking(new Expectations()
        {
            {
                oneOf(service).establishSession(with(any(AsyncCallback.class)));
                will(paramInt);
            }
        });
        sut.fireQueuedRequests();
        context.assertIsSatisfied();

        // -- service replies with success. SUT should notify app and then send the messages --
        serviceSessionCb = (AsyncCallback<String>) paramInt.getParam(0);
        context.checking(new Expectations()
        {
            {
                oneOf(sessionCb).onSuccess(SESSION_ID2);
                will(throwException(new RuntimeException("Naughty callback")));

                oneOf(service).execute((ActionRequest[]) with(anything()), with(any(AsyncCallback.class)));
                will(paramInt);
            }
        });
        serviceSessionCb.onSuccess(SESSION_ID2);
        context.assertIsSatisfied();

        requests = (ActionRequest[]) paramInt.getParam(0);

        assertEquals(1, requests.length);
        assertEquals(ACTION1, requests[0].getActionKey());
        assertSame(param1, requests[0].getParam());
        assertEquals(SESSION_ID2, requests[0].getSessionId());
    }

    /**
     * Tests session loss when multiple requests are out at once.
     *
     * @throws NoSuchFieldException
     *             Shouldn't.
     * @throws IllegalAccessException
     *             Shouldn't.
     * @throws IllegalArgumentException
     *             Shouldn't.
     */
    @Test
    public void testMultipleOutstandingRequests() throws IllegalArgumentException, IllegalAccessException,
            NoSuchFieldException
    {
        // cheat and set the session ID directly in the SUT to set up the initial state
        TestHelper.setPrivateField(sut, "sessionId", SESSION_ID1);

        final Serializable param01 = context.mock(Serializable.class, "param01");
        final Serializable param02 = context.mock(Serializable.class, "param02");
        final Serializable param03 = context.mock(Serializable.class, "param03");
        final Serializable param04 = context.mock(Serializable.class, "param04");
        final Serializable result01 = context.mock(Serializable.class, "result01");
        final Serializable result02 = context.mock(Serializable.class, "result02");
        final Serializable result03 = context.mock(Serializable.class, "result03");
        final Serializable result04 = context.mock(Serializable.class, "result04");
        final AsyncCallback actionCb01 = context.mock(AsyncCallback.class, "actionCb01");
        final AsyncCallback actionCb02 = context.mock(AsyncCallback.class, "actionCb02");
        final AsyncCallback actionCb03 = context.mock(AsyncCallback.class, "actionCb03");
        final AsyncCallback actionCb04 = context.mock(AsyncCallback.class, "actionCb04");
        final ParamInterceptor paramInt01 = new ParamInterceptor();
        final ParamInterceptor paramInt02 = new ParamInterceptor();
        final ParamInterceptor paramInt03 = new ParamInterceptor();
        final ParamInterceptor paramInt04 = new ParamInterceptor();

        // -- send out a few requests --
        context.checking(new Expectations()
        {
            {
                oneOf(service).execute((ActionRequest[]) with(anything()), with(any(AsyncCallback.class)));
                will(paramInt01);
                oneOf(service).execute((ActionRequest[]) with(anything()), with(any(AsyncCallback.class)));
                will(paramInt02);
                oneOf(service).execute((ActionRequest[]) with(anything()), with(any(AsyncCallback.class)));
                will(paramInt03);
                oneOf(service).execute((ActionRequest[]) with(anything()), with(any(AsyncCallback.class)));
                will(paramInt04);
            }
        });
        sut.makeRequest(ACTION1, param01, actionCb01);
        sut.makeRequest(ACTION2, param02, actionCb02);
        sut.makeRequest(ACTION3, param03, actionCb03);
        sut.makeRequest(ACTION4, param04, actionCb04);
        context.assertIsSatisfied();

        // -- make 1, 2, 4 fail with a session exception; 3 will succeed. once all are back, SUT should request session.
        // out of order on purpose. --
        // 1 fails
        ((AsyncCallback) paramInt01.getParam(1)).onSuccess(new ActionRequest[] { new ActionResponse(
                ((ActionRequest[]) paramInt01.getParam(0))[0], new SessionException()) });
        // 4 fails
        ((AsyncCallback) paramInt04.getParam(1)).onSuccess(new ActionRequest[] { new ActionResponse(
                ((ActionRequest[]) paramInt04.getParam(0))[0], new SessionException()) });
        // 3 succeeds
        context.checking(new Expectations()
        {
            {
                oneOf(actionCb03).onSuccess(with(same(result03)));
            }
        });
        ((AsyncCallback) paramInt03.getParam(1)).onSuccess(new ActionRequest[] { new ActionResponse(
                ((ActionRequest[]) paramInt03.getParam(0))[0], result03) });
        context.assertIsSatisfied();
        // 2 fails. since it's the last outstanding, the return will trigger re-establishment
        context.checking(new Expectations()
        {
            {
                oneOf(service).establishSession(with(any(AsyncCallback.class)));
                will(paramInt);
            }
        });
        ((AsyncCallback) paramInt02.getParam(1)).onSuccess(new ActionRequest[] { new ActionResponse(
                ((ActionRequest[]) paramInt02.getParam(0))[0], new SessionException()) });
        context.assertIsSatisfied();

        // -- establishing new session should send request for all 3 that failed in one message --
        context.checking(new Expectations()
        {
            {
                oneOf(sessionCb).onSuccess(SESSION_ID2);
                oneOf(service).execute((ActionRequest[]) with(anything()), with(any(AsyncCallback.class)));
                will(paramInt);
            }
        });
        ((AsyncCallback) paramInt.getParam(0)).onSuccess(SESSION_ID2);
        context.assertIsSatisfied();

        ActionRequest[] requests = (ActionRequest[]) paramInt.getParam(0);

        assertEquals(3, requests.length);
        assertEquals(ACTION1, requests[0].getActionKey());
        assertSame(param01, requests[0].getParam());
        assertEquals(SESSION_ID2, requests[0].getSessionId());
        assertEquals(ACTION2, requests[1].getActionKey());
        assertSame(param02, requests[1].getParam());
        assertEquals(SESSION_ID2, requests[1].getSessionId());
        assertEquals(ACTION4, requests[2].getActionKey());
        assertSame(param04, requests[2].getParam());
        assertEquals(SESSION_ID2, requests[2].getSessionId());

        // -- reply to all messages to insure callbacks working --

        context.checking(new Expectations()
        {
            {
                oneOf(actionCb01).onSuccess(with(same(result01)));
                oneOf(actionCb02).onSuccess(with(same(result02)));
                oneOf(actionCb04).onSuccess(with(same(result04)));
            }
        });
        ActionRequest[] results = new ActionRequest[] { new ActionResponse(requests[0], result01),
                new ActionResponse(requests[1], result02), new ActionResponse(requests[2], result04) };
        ((AsyncCallback) paramInt.getParam(1)).onSuccess(results);
        context.assertIsSatisfied();
    }

    /**
     * Tests that callbacks are not required. Use same basic scenario as startup test.
     * 
     * @throws NoSuchFieldException
     *             Only if SUT changes its log.
     * @throws IllegalAccessException
     *             Shouldn't.
     * @throws IllegalArgumentException
     *             Shouldn't.
     */
    @Test
    public void testNoCallbacks() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException
    {
        sut = new ActionProcessorImpl(service, null);
        TestHelper.setPrivateField(sut, "log", log);

        // -- send over messages with no session set. SUT should request a session --
        sut.setQueueRequests(true);
        sut.makeRequest(ACTION1, param1, null);
        sut.makeRequest(ACTION2, param2, null);
        context.checking(new Expectations()
        {
            {
                oneOf(service).establishSession(with(any(AsyncCallback.class)));
                will(paramInt);
            }
        });
        sut.setQueueRequests(false);
        context.assertIsSatisfied();

        // -- have server reject the request --
        ((AsyncCallback) paramInt.getParam(0)).onFailure(new Exception());

        // -- make SUT try again --
        context.checking(new Expectations()
        {
            {
                oneOf(service).establishSession(with(any(AsyncCallback.class)));
                will(paramInt);
            }
        });
        sut.fireQueuedRequests();
        context.assertIsSatisfied();

        // -- accept this time. SUT should send messages --
        context.checking(new Expectations()
        {
            {
                oneOf(service).execute((ActionRequest[]) with(anything()), with(any(AsyncCallback.class)));
                will(paramInt);
            }
        });
        ((AsyncCallback) paramInt.getParam(0)).onSuccess(SESSION_ID1);
        context.assertIsSatisfied();

        // -- reply to messages - one success and one failure --
        ActionRequest[] requests = (ActionRequest[]) paramInt.getParam(0);
        ActionRequest[] results = new ActionRequest[] { new ActionResponse(requests[0], new Exception()),
                new ActionResponse(requests[1], result2) };
        ((AsyncCallback) paramInt.getParam(1)).onSuccess(results);

        // -- try to send messages; there should be none. --
        sut.fireQueuedRequests();
    }

    /**
     * Tests some additional session establishment request cases, such as exceptions trying to send a message, and
     * sending multiple session requests.
     *
     * @throws IllegalAccessException
     *             Shouldn't.
     * @throws IllegalArgumentException
     *             Shouldn't.
     * @throws NoSuchFieldException
     *             Only if test is out of date.
     * @throws SecurityException
     *             Shouldn't.
     */
    @Test
    public void testSomeAdditionalSessionRequestCases() throws IllegalArgumentException, IllegalAccessException,
            SecurityException, NoSuchFieldException
    {
        // cheat and set the session ID directly in the SUT to set up the initial state
        TestHelper.setPrivateField(sut, "sessionId", SESSION_ID1);

        // -- try to make request, but service will throw session exception --
        context.checking(new Expectations()
        {
            {
                oneOf(service).execute((ActionRequest[]) with(anything()), with(any(AsyncCallback.class)));
                will(paramInt);
            }
        });
        sut.makeRequest(ACTION1, param1, actionCb1);
        context.assertIsSatisfied();

        // -- return a session exception - SUT should ask service to establish new session, but have service throw an
        // exception - SUT should tell app --
        ActionRequest[] requests = (ActionRequest[]) paramInt.getParam(0);
        AsyncCallback serviceExecCb = (AsyncCallback) paramInt.getParam(1);

        final Exception ex1 = new RuntimeException("What will it do?");
        context.checking(new Expectations()
        {
            {
                oneOf(service).establishSession(with(any(AsyncCallback.class)));
                will(throwException(ex1));

                oneOf(sessionCb).onFailure(with(equal(ex1)));
            }
        });
        serviceExecCb.onSuccess(new ActionRequest[] { new ActionResponse(requests[0], new SessionException()) });
        context.assertIsSatisfied();

        // -- app asks SUT to try again --
        context.checking(new Expectations()
        {
            {
                oneOf(service).establishSession(with(any(AsyncCallback.class)));
                will(paramInt);
            }
        });
        sut.fireQueuedRequests();
        context.assertIsSatisfied();

        // -- app gets impatient and asks SUT to try again again - SUT should do nothing --
        sut.fireQueuedRequests();

        // -- service replies with success. SUT should notify app and then send the messages --
        AsyncCallback<String> serviceSessionCb = (AsyncCallback<String>) paramInt.getParam(0);
        context.checking(new Expectations()
        {
            {
                oneOf(sessionCb).onSuccess(SESSION_ID2);

                oneOf(service).execute((ActionRequest[]) with(anything()), with(any(AsyncCallback.class)));
                will(paramInt);
            }
        });
        serviceSessionCb.onSuccess(SESSION_ID2);
        context.assertIsSatisfied();

        requests = (ActionRequest[]) paramInt.getParam(0);

        assertEquals(1, requests.length);
        assertEquals(ACTION1, requests[0].getActionKey());
        assertSame(param1, requests[0].getParam());
        assertEquals(SESSION_ID2, requests[0].getSessionId());


        // -- server replies with wrong id - SUT should do nothing. --
        ActionResponse result = new ActionResponse(requests[0], "Ha ha!");
        result.setId((result.getId() + 9) * 9);
        ((AsyncCallback) paramInt.getParam(1)).onSuccess(new ActionRequest[] { result
                 });
    }

    /**
     * Concrete class for testing.
     */
    static class ActionResponse implements ActionRequest
    {
        /** id. */
        int id;
        /** actionKey. */
        String actionKey;
        /** param. */
        Serializable param;
        /** response. */
        Serializable response;
        /** sessionId. */
        String sessionId;

        /**
         * Quasi-copy constructor (behaves generally like the server).
         *
         * @param orig
         *            Request to "clone".
         * @param inResponse
         *            Response.
         */
        public ActionResponse(final ActionRequest orig, final Serializable inResponse)
        {
            id = orig.getId();
            actionKey = orig.getActionKey();
            response = inResponse;
            sessionId = orig.getSessionId();
        }

        /**
         * @return the id
         */
        public int getId()
        {
            return id;
        }

        /**
         * @param inId
         *            the id to set
         */
        public void setId(final int inId)
        {
            id = inId;
        }

        /**
         * @return the actionKey
         */
        public String getActionKey()
        {
            return actionKey;
        }

        /**
         * @param inActionKey
         *            the actionKey to set
         */
        public void setActionKey(final String inActionKey)
        {
            actionKey = inActionKey;
        }

        /**
         * @return the param
         */
        public Serializable getParam()
        {
            return param;
        }

        /**
         * @param inParam
         *            the param to set
         */
        public void setParam(final Serializable inParam)
        {
            param = inParam;
        }

        /**
         * @return the response
         */
        public Serializable getResponse()
        {
            return response;
        }

        /**
         * @param inResponse
         *            the response to set
         */
        public void setResponse(final Serializable inResponse)
        {
            response = inResponse;
        }

        /**
         * @return the sessionId
         */
        public String getSessionId()
        {
            return sessionId;
        }

        /**
         * @param inSessionId
         *            the sessionId to set
         */
        public void setSessionId(final String inSessionId)
        {
            sessionId = inSessionId;
        }
    }

}
