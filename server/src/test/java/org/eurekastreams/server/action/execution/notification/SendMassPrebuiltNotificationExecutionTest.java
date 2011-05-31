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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.action.request.notification.SendPrebuiltNotificationRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.testing.TestContextCreator;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests SendMassPrebuiltNotificationExecution.
 */
public class SendMassPrebuiltNotificationExecutionTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Mapper to create notification for all (unlocked) users. */
    private final DomainMapper<SendPrebuiltNotificationRequest, Serializable> createNotificationsMapper = context
            .mock(DomainMapper.class, "createNotificationsMapper");

    /** Mapper to get list of unlocked persons - those who received the notification and need their counts reset. */
    private final DomainMapper<Boolean, List<Long>> unlockedUsersMapper = context.mock(DomainMapper.class,
            "unlockedUsersMapper");

    /** Request. */
    private final SendPrebuiltNotificationRequest request = context.mock(SendPrebuiltNotificationRequest.class);

    /** SUT. */
    private SendMassPrebuiltNotificationExecution sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new SendMassPrebuiltNotificationExecution(createNotificationsMapper, unlockedUsersMapper);
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecute()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(createNotificationsMapper).execute(request);
                will(returnValue(5));
                oneOf(unlockedUsersMapper).execute(false);
                will(returnValue(Collections.EMPTY_LIST));
            }
        });

        TaskHandlerActionContext<PrincipalActionContext> actionContext = TestContextCreator
                .createTaskHandlerContextWithPrincipal(request, null);

        Serializable result = sut.execute(actionContext);
        context.assertIsSatisfied();
        assertEquals(5, result);
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteNoPeople()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(createNotificationsMapper).execute(request);
                will(returnValue(0));
            }
        });

        TaskHandlerActionContext<PrincipalActionContext> actionContext = TestContextCreator
                .createTaskHandlerContextWithPrincipal(request, null);

        Serializable result = sut.execute(actionContext);
        context.assertIsSatisfied();
        assertEquals(0, result);
    }
}
