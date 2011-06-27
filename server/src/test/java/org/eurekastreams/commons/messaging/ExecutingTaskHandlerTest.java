/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.commons.messaging;

import java.io.Serializable;

import org.eurekastreams.commons.actions.async.AsyncAction;
import org.eurekastreams.commons.actions.async.TaskHandlerAsyncAction;
import org.eurekastreams.commons.actions.context.async.AsyncActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.server.async.AsyncActionController;
import org.eurekastreams.commons.task.ExecutingTaskHandler;
import org.eurekastreams.commons.task.TaskHandler;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;

/**
 * Tests ExecutingTaskHandler.
 */
public class ExecutingTaskHandlerTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: mock of Spring bean factory. */
    private final BeanFactory beanFactory = context.mock(BeanFactory.class);

    /**
     * The asnyc action to mock.
     */
    private final AsyncAction asyncActionMock = context.mock(AsyncAction.class);

    /**
     * The AsyncSubmitterAsyncAction to mock.
     */
    private final TaskHandlerAsyncAction asyncSubmitterAsyncActionMock = context.mock(TaskHandlerAsyncAction.class);

    /**
     * The user action request mock object.
     */
    private final UserActionRequest userActionRequestMock = context.mock(UserActionRequest.class);

    /**
     * Mocked instance of the AsyncActionController for this test suite.
     */
    private final AsyncActionController asyncActionControllerMock = context.mock(AsyncActionController.class);

    /**
     * SUT.
     */
    private TaskHandler sut = null;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new ExecutingTaskHandler(asyncActionControllerMock);
        ((ExecutingTaskHandler) sut).setBeanFactory(beanFactory);
    }

    /**
     * Test submit when the user request is pointing to an AsyncAction.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void testSubmitForAsyncAction() throws Exception
    {
        final Serializable param1 = null;

        final String actionKey = "TestAsyncAction";

        context.checking(new Expectations()
        {
            {
                oneOf(userActionRequestMock).getActionKey();
                will(returnValue(actionKey));

                oneOf(beanFactory).getBean(actionKey);
                will(returnValue(asyncActionMock));

                oneOf(userActionRequestMock).getParams();
                will(returnValue(param1));

                oneOf(asyncActionControllerMock).execute(with(any(AsyncActionContext.class)),
                        with(any(AsyncAction.class)));

                oneOf(userActionRequestMock).getActionKey();
                will(returnValue(actionKey));
            }
        });

        sut.handleTask(userActionRequestMock);

        context.assertIsSatisfied();
    }

    /**
     * Test submit when the user request is pointing to an AsyncSubmitterAsyncAction.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void testSubmitForAsyncSubmitterAsyncAction() throws Exception
    {
        final Serializable param1 = null;

        final String actionKey = "TestAsyncAction";

        context.checking(new Expectations()
        {
            {
                oneOf(userActionRequestMock).getActionKey();
                will(returnValue(actionKey));

                oneOf(beanFactory).getBean(actionKey);
                will(returnValue(asyncSubmitterAsyncActionMock));

                oneOf(userActionRequestMock).getParams();
                will(returnValue(param1));

                oneOf(asyncActionControllerMock).execute(with(any(AsyncActionContext.class)),
                        with(any(TaskHandlerAsyncAction.class)));

                oneOf(userActionRequestMock).getActionKey();
                will(returnValue(actionKey));
            }
        });

        sut.handleTask(userActionRequestMock);

        context.assertIsSatisfied();
    }
}
