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
package org.eurekastreams.commons.server.async;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.TaskHandlerAction;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.async.AsyncAction;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.async.AsyncActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.exceptions.GeneralException;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.commons.server.TransactionManagerFake;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.task.TaskHandler;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Test suite for the AsyncActionController.
 *
 */
@SuppressWarnings("unchecked")
public class AsyncActionControllerTest
{
    /**
     * Instance of {@link AsyncActionController} System Under Test.
     */
    private AsyncActionController sut;

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
     * Mock of the TransactionManagerFake for testing.
     */
    private final TransactionManagerFake transMgrMock = context.mock(TransactionManagerFake.class);

    /**
     * instance of a TransactionDefinition.
     */
    private DefaultTransactionDefinition transDef = null;

    /**
     * Mocked instance of a TransactionStatus.
     */
    private TransactionStatus transStatus = context.mock(TransactionStatus.class);

    /**
     * Mocked instance of the ValidationStrategy for testing.
     */
    private ValidationStrategy<ActionContext> validationStrategy = context.mock(ValidationStrategy.class);

    /**
     * Mocked instance of the {@link ExecutionStrategy} for testing.
     */
    private ExecutionStrategy<ActionContext> executionStrategy = context.mock(ExecutionStrategy.class);

    /**
     * Mocked instance of the {@link TaskHandlerExecutionStrategy} for testing.
     */
    private TaskHandlerExecutionStrategy<ActionContext> taskHandlerExecutionStrategy = context
            .mock(TaskHandlerExecutionStrategy.class);

    /**
     * Mocked instance of the {@link AsyncAction} class for testing.
     */
    private AsyncAction asyncActionMock = context.mock(AsyncAction.class);

    /**
     * Mocked instance of the {@link AsyncActionContext}.
     */
    private AsyncActionContext asyncActionContextMock = context.mock(AsyncActionContext.class);

    /**
     * Mocked instance of the {@link TaskHandlerAction} class for testing.
     */
    private TaskHandlerAction queueSubmitterActionMock = context.mock(TaskHandlerAction.class);

    /**
     * Mocked instance of the {@link TaskHandler} class.
     */
    private TaskHandler taskHandlerMock = context.mock(TaskHandler.class);

    /**
     * Mocked instance of the {@link UserActionRequest} class for testing.
     */
    private UserActionRequest userActionRequest = context.mock(UserActionRequest.class);

    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new AsyncActionController(transMgrMock);
        transDef = new DefaultTransactionDefinition();
    }

    /**
     * Test the execution of the AsyncActionController with just an AsyncAction.
     *
     * @throws Throwable
     *             - on error.
     */
    @Test
    public void testExecuteWithAsycAction() throws Throwable
    {
        Serializable params = null;

        final AsyncActionContext asyncActionContext = new AsyncActionContext(params);
        transDef.setReadOnly(true);

        setupTransactionContext(true, asyncActionContext, false);

        sut.execute(asyncActionContext, asyncActionMock);

        context.assertIsSatisfied();
    }

    /**
     * Test the execution of the AsyncActionController with just an AsyncAction.
     *
     * @throws Throwable
     *             - on error.
     */
    @Test(expected = GeneralException.class)
    public void testExecuteWithAsyncActionFailure() throws Throwable
    {
        Serializable params = null;

        final AsyncActionContext asyncActionContext = new AsyncActionContext(params);
        transDef.setReadOnly(true);

        setupTransactionContext(true, asyncActionContext, true);

        context.checking(new Expectations()
        {
            {
                oneOf(transStatus).isCompleted();
                will(returnValue(false));

                oneOf(transMgrMock).rollback(transStatus);
            }
        });

        sut.execute(asyncActionContext, asyncActionMock);

        context.assertIsSatisfied();
    }

    /**
     * Test the execution of the AsyncActionController with just an async Action and fail on validation.
     *
     * @throws Throwable
     *             - on error.
     */
    @Test(expected = ValidationException.class)
    public void testExecuteWithAsyncActionValidationFailure() throws Throwable
    {
        Serializable params = null;

        final AsyncActionContext asyncActionContext = new AsyncActionContext(params);
        transDef.setReadOnly(true);

        context.checking(new Expectations()
        {
            {
                oneOf(asyncActionMock).isReadOnly();
                will(returnValue(true));

                oneOf(transMgrMock).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(asyncActionMock).getValidationStrategy();
                will(returnValue(validationStrategy));

                oneOf(validationStrategy).validate(asyncActionContext);
                will(throwException(new ValidationException()));

                oneOf(transStatus).isCompleted();
                will(returnValue(false));

                oneOf(transMgrMock).rollback(transStatus);
            }
        });

        sut.execute(asyncActionContext, asyncActionMock);

        context.assertIsSatisfied();
    }

    /**
     * Test the execution of the AsyncActionController with just an async Action and fail on execution.
     *
     * @throws Throwable
     *             - on error.
     */
    @Test(expected = ExecutionException.class)
    public void testExecuteWithAsyncActionExecutionFailure() throws Throwable
    {
        Serializable params = null;

        final AsyncActionContext asyncActionContext = new AsyncActionContext(params);
        transDef.setReadOnly(true);

        context.checking(new Expectations()
        {
            {
                oneOf(asyncActionMock).isReadOnly();
                will(returnValue(true));

                oneOf(transMgrMock).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(asyncActionMock).getValidationStrategy();
                will(returnValue(validationStrategy));

                oneOf(validationStrategy).validate(asyncActionContext);

                oneOf(asyncActionMock).getExecutionStrategy();
                will(returnValue(executionStrategy));

                oneOf(executionStrategy).execute(asyncActionContext);
                will(throwException(new ExecutionException()));

                oneOf(transStatus).isCompleted();
                will(returnValue(false));

                oneOf(transMgrMock).rollback(transStatus);
            }
        });

        sut.execute(asyncActionContext, asyncActionMock);

        context.assertIsSatisfied();
    }

    /**
     * Test the sucessful path of the {@link TaskHandlerAction}.
     *
     * @throws Throwable
     *             - on error.
     */
    @Test
    public void testExecuteWithQueueAsyncAction() throws Throwable
    {
        transDef.setReadOnly(false);

        final List<UserActionRequest> requests = new ArrayList<UserActionRequest>();
        requests.add(userActionRequest);

        final TaskHandlerActionContext<ActionContext> taskHandlerActionContextMock = context
                .mock(TaskHandlerActionContext.class);

        setupTaskHandlerTransactionContext(false, taskHandlerActionContextMock, false);

        sut.execute(asyncActionContextMock, queueSubmitterActionMock);

        context.assertIsSatisfied();
    }

    /**
     * Test the failure path of the {@link TaskHandlerAction}.
     *
     * @throws Throwable
     *             - on error.
     */
    @Test(expected = GeneralException.class)
    public void testExecuteWithQueueAsyncActionFailure() throws Throwable
    {
        transDef.setReadOnly(false);

        final List<UserActionRequest> requests = new ArrayList<UserActionRequest>();
        requests.add(userActionRequest);

        final TaskHandlerActionContext<ActionContext> taskHandlerActionContextMock = context
                .mock(TaskHandlerActionContext.class);

        setupTaskHandlerTransactionContext(false, taskHandlerActionContextMock, true);

        context.checking(new Expectations()
        {
            {
                oneOf(transStatus).isCompleted();
                will(returnValue(false));

                oneOf(transMgrMock).rollback(transStatus);
            }
        });

        sut.execute(asyncActionContextMock, queueSubmitterActionMock);

        context.assertIsSatisfied();
    }

    /**
     * Test the failure path of the {@link TaskHandlerAction}.
     *
     * @throws Throwable
     *             - on error.
     */
    @Test(expected = ValidationException.class)
    public void testExecuteWithQueueAsyncActionValidationFailure() throws Throwable
    {
        transDef.setReadOnly(false);

        final List<UserActionRequest> requests = new ArrayList<UserActionRequest>();
        requests.add(userActionRequest);

        context.checking(new Expectations()
        {
            {
                oneOf(queueSubmitterActionMock).isReadOnly();
                will(returnValue(false));

                oneOf(transMgrMock).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(queueSubmitterActionMock).getValidationStrategy();
                will(returnValue(validationStrategy));

                oneOf(validationStrategy).validate(asyncActionContextMock);
                will(throwException(new ValidationException()));

                oneOf(transStatus).isCompleted();
                will(returnValue(false));

                oneOf(transMgrMock).rollback(transStatus);

            }
        });

        sut.execute(asyncActionContextMock, queueSubmitterActionMock);

        context.assertIsSatisfied();
    }

    /**
     * Test the failure path of the {@link TaskHandlerAction}.
     *
     * @throws Throwable
     *             - on error.
     */
    @Test(expected = ExecutionException.class)
    public void testExecuteWithQueueAsyncActionExecutionFailure() throws Throwable
    {
        transDef.setReadOnly(false);

        final List<UserActionRequest> requests = new ArrayList<UserActionRequest>();
        requests.add(userActionRequest);

        context.checking(new Expectations()
        {
            {
                oneOf(queueSubmitterActionMock).isReadOnly();
                will(returnValue(false));

                oneOf(transMgrMock).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(queueSubmitterActionMock).getValidationStrategy();
                will(returnValue(validationStrategy));

                oneOf(validationStrategy).validate(asyncActionContextMock);

                oneOf(queueSubmitterActionMock).getExecutionStrategy();
                will(returnValue(taskHandlerExecutionStrategy));

                oneOf(taskHandlerExecutionStrategy).execute(with(any(TaskHandlerActionContext.class)));
                will(throwException(new ExecutionException()));

                oneOf(transStatus).isCompleted();
                will(returnValue(false));

                oneOf(transMgrMock).rollback(transStatus);

            }
        });

        sut.execute(asyncActionContextMock, queueSubmitterActionMock);

        context.assertIsSatisfied();
    }

    /**
     * Helper method.
     *
     * @param isReadOnlyTransaction
     *            - flag for setting the readonly transaction
     * @param inContext
     *            - {@link AsyncActionContext} for the current test.
     * @param throwsException
     *            - flag for whether or not to throw an exception within the trans.
     * @throws Throwable
     *             on error.
     */
    private void setupTaskHandlerTransactionContext(final boolean isReadOnlyTransaction,
            final TaskHandlerActionContext inContext, final boolean throwsException) throws Throwable
    {
        context.checking(new Expectations()
        {
            {
                oneOf(queueSubmitterActionMock).isReadOnly();
                will(returnValue(isReadOnlyTransaction));

                oneOf(transMgrMock).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(queueSubmitterActionMock).getValidationStrategy();
                will(returnValue(validationStrategy));

                oneOf(validationStrategy).validate(asyncActionContextMock);

                oneOf(queueSubmitterActionMock).getExecutionStrategy();
                will(returnValue(taskHandlerExecutionStrategy));

                oneOf(taskHandlerExecutionStrategy).execute(with(any(TaskHandlerActionContext.class)));

                if (throwsException)
                {
                    oneOf(transMgrMock).commit(transStatus);
                    will(throwException(new GeneralException()));
                }
                else
                {
                    oneOf(transMgrMock).commit(transStatus);

                    oneOf(queueSubmitterActionMock).getTaskHandler();
                    will(returnValue(taskHandlerMock));
                }
            }
        });

    }

    /**
     * Helper method.
     *
     * @param isReadOnlyTransaction
     *            - flag for setting the readonly transaction
     * @param inContext
     *            - {@link AsyncActionContext} for the current test.
     * @param throwsException
     *            - flag for whether or not to throw an exception within the trans.
     * @throws Throwable
     *             on error.
     */
    private void setupTransactionContext(final boolean isReadOnlyTransaction, final AsyncActionContext inContext,
            final boolean throwsException) throws Throwable
    {
        context.checking(new Expectations()
        {
            {
                oneOf(asyncActionMock).isReadOnly();
                will(returnValue(isReadOnlyTransaction));

                oneOf(transMgrMock).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(asyncActionMock).getValidationStrategy();
                will(returnValue(validationStrategy));

                oneOf(validationStrategy).validate(inContext);

                oneOf(asyncActionMock).getExecutionStrategy();
                will(returnValue(executionStrategy));

                oneOf(executionStrategy).execute(inContext);

                if (throwsException)
                {
                    oneOf(transMgrMock).commit(transStatus);
                    will(throwException(new GeneralException()));
                }
                else
                {
                    oneOf(transMgrMock).commit(transStatus);
                }
            }
        });

    }
}
