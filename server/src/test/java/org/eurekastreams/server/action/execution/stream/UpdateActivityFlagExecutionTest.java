/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.stream;

import static junit.framework.Assert.assertEquals;
import static org.eurekastreams.commons.test.IsEqualInternally.areEqualInternally;
import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.persistence.mappers.requests.UpdateActivityFlagRequest;
import org.eurekastreams.server.persistence.mappers.stream.UpdateActivityFlag;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests UpdateActivityFlagExecution.
 */
@SuppressWarnings("unchecked")
public class UpdateActivityFlagExecutionTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Test data. */
    private static final long ACTIVITY_ID = 123456L;
    /** Test data. */
    private static final long USER_ID = 4321L;

    /** Fixture: mapper mock. */
    private UpdateActivityFlag setFlagMapper = context.mock(UpdateActivityFlag.class);

    /** Fixture: action context. */
    private TaskHandlerActionContext contextOuter = context.mock(TaskHandlerActionContext.class, "contextOuter");

    /** Fixture: action context. */
    private PrincipalActionContext contextInner = context.mock(PrincipalActionContext.class, "contextInner");

    /** Fixture: request list. */
    private List<UserActionRequest> userActionRequests = new ArrayList<UserActionRequest>();

    /** Fixture: principal. */
    private Principal principal = context.mock(Principal.class);

    /** SUT. */
    private UpdateActivityFlagExecution sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        userActionRequests.clear();
        context.checking(new Expectations()
        {
            {
                allowing(contextOuter).getActionContext();
                will(returnValue(contextInner));
                allowing(contextInner).getParams();
                will(returnValue(ACTIVITY_ID));
                allowing(contextInner).getPrincipal();
                will(returnValue(principal));
                allowing(principal).getId();
                will(returnValue(USER_ID));
                allowing(contextOuter).getUserActionRequests();
                will(returnValue(userActionRequests));
            }
        });
    }

    /**
     * Tests setting flag.
     */
    @Test
    public void testExecuteSet()
    {
        sut = new UpdateActivityFlagExecution(setFlagMapper, true);

        final UpdateActivityFlagRequest rqst = new UpdateActivityFlagRequest(ACTIVITY_ID, true);

        context.checking(new Expectations()
        {
            {
                oneOf(setFlagMapper).execute(with(equalInternally(rqst)));
                will(returnValue(true));
            }
        });

        sut.execute(contextOuter);
        context.assertIsSatisfied();

        assertEquals(1, userActionRequests.size());
        UserActionRequest actualQueueRqst = userActionRequests.get(0);
        assertEquals("createNotificationsAction", actualQueueRqst.getActionKey());
        assertTrue(areEqualInternally(new CreateNotificationsRequest(RequestType.FLAG_ACTIVITY, USER_ID, 0L,
                ACTIVITY_ID), actualQueueRqst.getParams()));
    }

    /**
     * Tests setting flag, but already set.
     */
    @Test
    public void testExecuteSetAlready()
    {
        sut = new UpdateActivityFlagExecution(setFlagMapper, true);

        final UpdateActivityFlagRequest rqst = new UpdateActivityFlagRequest(ACTIVITY_ID, true);

        context.checking(new Expectations()
        {
            {
                oneOf(setFlagMapper).execute(with(equalInternally(rqst)));
                will(returnValue(false));
            }
        });

        sut.execute(contextOuter);
        context.assertIsSatisfied();
        assertTrue(userActionRequests.isEmpty());
    }

    /**
     * Tests clearing flag.
     */
    @Test
    public void testExecuteClear()
    {
        sut = new UpdateActivityFlagExecution(setFlagMapper, false);

        final UpdateActivityFlagRequest rqst = new UpdateActivityFlagRequest(ACTIVITY_ID, false);

        context.checking(new Expectations()
        {
            {
                oneOf(setFlagMapper).execute(with(equalInternally(rqst)));
                will(returnValue(true));
            }
        });

        sut.execute(contextOuter);
        context.assertIsSatisfied();
        assertTrue(userActionRequests.isEmpty());
    }

    /**
     * Tests clearing flag, but already clear.
     */
    @Test
    public void testExecuteClearAlready()
    {
        sut = new UpdateActivityFlagExecution(setFlagMapper, false);

        final UpdateActivityFlagRequest rqst = new UpdateActivityFlagRequest(ACTIVITY_ID, false);

        context.checking(new Expectations()
        {
            {
                oneOf(setFlagMapper).execute(with(equalInternally(rqst)));
                will(returnValue(false));
            }
        });

        sut.execute(contextOuter);
        context.assertIsSatisfied();
        assertTrue(userActionRequests.isEmpty());
    }
}
