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

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.persistence.mappers.stream.DeleteActivityComment;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for DeleteActivityCommentAction.
 *
 */
public class DeleteActivityCommentExecutionTest
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

    /**
     * ActionContext mock.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * TaskHandler action context mock.
     */
    private TaskHandlerActionContext<ActionContext> taskHandlerActionContext = context
            .mock(TaskHandlerActionContext.class);

    /**
     * The system under test.
     */
    private DeleteActivityCommentExecution sut;

    /**
     * {@link DeleteActivityComment} mock.
     */
    private DeleteActivityComment deleteActivityCommentMock = context.mock(DeleteActivityComment.class);

    /**
     * Setup sut before each test.
     */

    @Before
    public void setUp()
    {
        sut = new DeleteActivityCommentExecution(deleteActivityCommentMock);
    }

    /**
     * Test performAction method.
     *
     * @throws Exception
     *             not expected.
     */
    @Test
    public void testPerformAction() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getParams();
                will(returnValue(5L));

                oneOf(deleteActivityCommentMock).execute(5L);
            }
        });

        sut.execute(taskHandlerActionContext);
        context.assertIsSatisfied();
    }

}
