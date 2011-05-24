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
package org.eurekastreams.server.action.execution.notification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.actions.context.ClientPrincipalActionContext;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.test.IsEqualInternally;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.action.request.notification.PrebuiltNotificationsRequest;
import org.eurekastreams.server.action.request.notification.SendPrebuiltNotificationRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests SendPrebuiltNotificationExecution.
 */
public class SendPrebuiltNotificationExecutionTest
{
    /** Test data. */
    private static final String CLIENT_ID = "CLIENT_ID";

    /** Test data. */
    private static final String RECIPIENT_ACCOUNT_ID = "jdoe";

    /** Test data. */
    private static final long RECIPIENT_ID = 40L;

    /** Test data. */
    private static final String MESSAGE = "This is a message";

    /** Test data. */
    private static final String URL = "http://www.eurekastreams.org";

    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Mapper to get recipient id. */
    private final DomainMapper<String, Long> personIdMapper = context.mock(DomainMapper.class, "personIdMapper");

    /** SUT. */
    private SendPrebuiltNotificationExecution sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new SendPrebuiltNotificationExecution(personIdMapper);
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteNoState()
    {
        context.checking(new Expectations()
        {
            {
                allowing(personIdMapper).execute(RECIPIENT_ACCOUNT_ID);
                will(returnValue(RECIPIENT_ID));
            }
        });

        runTest(Collections.EMPTY_MAP);
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteWithState()
    {
        final PersonModelView recipient = context.mock(PersonModelView.class);

        context.checking(new Expectations()
        {
            {
                allowing(recipient).getId();
                will(returnValue(RECIPIENT_ID));
            }
        });

        runTest(Collections.singletonMap("recipient", (Object) recipient));
    }

    /**
     * Runs the test.
     *
     * @param state
     *            State bag to use.
     */
    private void runTest(final Map<String, Object> state)
    {
        TaskHandlerActionContext<ClientPrincipalActionContext> ctx = createContext(state);
        sut.execute(ctx);

        context.assertIsSatisfied();

        List<UserActionRequest> requests = ctx.getUserActionRequests();
        assertEquals(1, requests.size());
        UserActionRequest request = requests.get(0);
        assertEquals("createNotificationsAction", request.getActionKey());
        PrebuiltNotificationsRequest expected = new PrebuiltNotificationsRequest(RequestType.EXTERNAL_PRE_BUILT, true,
                CLIENT_ID, RECIPIENT_ID, MESSAGE, URL);
        assertTrue(IsEqualInternally.areEqualInternally(expected, request.getParams()));
    }

    /**
     * Creates the context for the action.
     *
     * @param state
     *            State bag to use.
     * @return The context.
     */
    private TaskHandlerActionContext<ClientPrincipalActionContext> createContext(final Map<String, Object> state)
    {
        ClientPrincipalActionContext ctx = new ClientPrincipalActionContext()
        {
            @Override
            public void setActionId(final String inActionId)
            {
            }

            @Override
            public Map<String, Object> getState()
            {
                return state;
            }

            @Override
            public Serializable getParams()
            {
                return new SendPrebuiltNotificationRequest(true, RECIPIENT_ACCOUNT_ID, MESSAGE, URL);
            }

            @Override
            public String getActionId()
            {
                return null;
            }

            @Override
            public Principal getPrincipal()
            {
                return null;
            }

            @Override
            public String getClientUniqueId()
            {
                return CLIENT_ID;
            }
        };
        return new TaskHandlerActionContext<ClientPrincipalActionContext>(ctx, new ArrayList<UserActionRequest>());
    }
}
