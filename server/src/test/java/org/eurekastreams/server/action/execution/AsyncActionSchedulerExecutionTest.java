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
package org.eurekastreams.server.action.execution;

import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link AsyncActionSchedulerExecution} class.
 *
 */
public class AsyncActionSchedulerExecutionTest
{
    /**
     * System under test.
     */
    private AsyncActionSchedulerExecution sut;

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
     * Mocked instance of TaskHandlerActionContext.
     */
    private final TaskHandlerActionContext taskHandlerActionContextMock = context.mock(TaskHandlerActionContext.class);

    /**
     * Mocked instance of ServiceActionContext.
     */
    private final ServiceActionContext serviceActionContextMock = context.mock(ServiceActionContext.class);

    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new AsyncActionSchedulerExecution("testaction");
    }

    /**
     * Test the execute method.
     */
    @Test
    public void testExecute()
    {
        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContextMock).getActionContext();
                will(returnValue(serviceActionContextMock));

                allowing(serviceActionContextMock).getParams();
                will(returnValue(null));

                oneOf(taskHandlerActionContextMock).getUserActionRequests();
            }
        });

        sut.execute(taskHandlerActionContextMock);
    }
}
