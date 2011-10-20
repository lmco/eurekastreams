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
    /** Test data. */
    private static final String ACTION_KEY = "actionKey";

    /**
     * Context for building mock objects.
     */
    private final Mockery mockery = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: mock of Spring bean factory. */
    private final BeanFactory beanFactory = mockery.mock(BeanFactory.class);

    /**
     * The asnyc action to mock.
     */
    private final AsyncAction asyncActionMock = mockery.mock(AsyncAction.class);

    /**
     * The AsyncSubmitterAsyncAction to mock.
     */
    private final TaskHandlerAsyncAction asyncSubmitterAsyncActionMock = mockery.mock(TaskHandlerAsyncAction.class);

    /**
     * The user action request mock object.
     */
    private final UserActionRequest userActionRequestMock = mockery.mock(UserActionRequest.class);

    /**
     * Mocked instance of the AsyncActionController for this test suite.
     */
    private final AsyncActionController asyncActionControllerMock = mockery.mock(AsyncActionController.class);

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

        mockery.checking(new Expectations()
        {
            {
                allowing(userActionRequestMock).getActionKey();
                will(returnValue(ACTION_KEY));
            }
        });
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

        mockery.checking(new Expectations()
        {
            {
                oneOf(beanFactory).getBean(ACTION_KEY);
                will(returnValue(asyncActionMock));

                oneOf(userActionRequestMock).getParams();
                will(returnValue(param1));

                oneOf(asyncActionControllerMock).execute(with(any(AsyncActionContext.class)),
                        with(any(AsyncAction.class)));
            }
        });

        sut.handleTask(userActionRequestMock);

        mockery.assertIsSatisfied();
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

        mockery.checking(new Expectations()
        {
            {
                oneOf(beanFactory).getBean(ACTION_KEY);
                will(returnValue(asyncSubmitterAsyncActionMock));

                oneOf(userActionRequestMock).getParams();
                will(returnValue(param1));

                oneOf(asyncActionControllerMock).execute(with(any(AsyncActionContext.class)),
                        with(any(TaskHandlerAsyncAction.class)));
            }
        });

        sut.handleTask(userActionRequestMock);

        mockery.assertIsSatisfied();
    }
}
