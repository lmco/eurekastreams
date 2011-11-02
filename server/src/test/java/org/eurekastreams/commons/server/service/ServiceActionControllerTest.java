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
package org.eurekastreams.commons.server.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.TaskHandlerAction;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.actions.service.TaskHandlerServiceAction;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.exceptions.GeneralException;
import org.eurekastreams.commons.exceptions.InvalidActionException;
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
 * Test class for the ServiceActionController.
 */
@SuppressWarnings("unchecked")
public class ServiceActionControllerTest
{
    /**
     * Instance of {@link ServiceAction} System Under Test.
     */
    private ServiceActionController sut;

    /**
     * Context for building mock objects.
     */
    private final Mockery mockery = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mock of the TransactionManagerFake for testing.
     */
    private final TransactionManagerFake transMgrMock = mockery.mock(TransactionManagerFake.class);

    /**
     * instance of a TransactionDefinition.
     */
    private DefaultTransactionDefinition transDef = null;

    /**
     * Mocked instance of a TransactionStatus.
     */
    private final TransactionStatus transStatus = mockery.mock(TransactionStatus.class);

    /**
     * Mocked instance of the ValidationStrategy for testing.
     */
    private final ValidationStrategy<ServiceActionContext> validationStrategy = mockery.mock(ValidationStrategy.class);

    /**
     * Mocked instance of the {@link AuthorizationStrategy for testing.
     */
    private final AuthorizationStrategy<PrincipalActionContext> authorizationStrategy = mockery
            .mock(AuthorizationStrategy.class);

    /**
     * Mocked instance of the {@link ExecutionStrategy} for testing.
     */
    private final ExecutionStrategy<ServiceActionContext> executionStrategy = mockery.mock(ExecutionStrategy.class);

    /**
     * Mocked instance of the {@link TaskHandlerExecutionStrategy} for testing.
     */
    private final TaskHandlerExecutionStrategy<ActionContext> taskHandlerExecutionStrategy = mockery
            .mock(TaskHandlerExecutionStrategy.class);

    /**
     * Mocked instance of the {@link ServiceAction} class for testing.
     */
    private final ServiceAction serviceActionMock = mockery.mock(ServiceAction.class);

    /**
     * Mocked instance of the {@link TaskHandlerAction} class for testing.
     */
    private final TaskHandlerAction queueSubmitterActionMock = mockery.mock(TaskHandlerAction.class);

    /**
     * Mocked instance of the {@link TaskHandler} class.
     */
    private final TaskHandler taskHandlerMock = mockery.mock(TaskHandler.class);

    /**
     * Mocked instance of the {@link Principal} class for testing.
     */
    private final Principal principalMock = mockery.mock(Principal.class);

    /**
     * Mocked instance of the {@link UserActionRequest} class for testing.
     */
    private final UserActionRequest userActionRequest = mockery.mock(UserActionRequest.class);

    /**
     * Setup the test fixture.
     */
    @Before
    public void setUp()
    {
        sut = new ServiceActionController(transMgrMock);
        transDef = new DefaultTransactionDefinition();
        mockery.checking(new Expectations()
        {
            {
                allowing(principalMock).getAccountId();
                will(returnValue("someuser"));
            }
        });
    }

    /**
     * Sets up expectations for service actions.
     */
    private void expectServiceAction()
    {
        mockery.checking(new Expectations()
        {
            {
                allowing(serviceActionMock).getValidationStrategy();
                will(returnValue(validationStrategy));
                allowing(serviceActionMock).getAuthorizationStrategy();
                will(returnValue(authorizationStrategy));
                allowing(serviceActionMock).getExecutionStrategy();
                will(returnValue(executionStrategy));
            }
        });
    }

    /**
     * Sets up expectations for task handler service actions.
     */
    private void expectTaskHandlerServiceAction()
    {
        mockery.checking(new Expectations()
        {
            {
                allowing(queueSubmitterActionMock).getValidationStrategy();
                will(returnValue(validationStrategy));
                allowing(queueSubmitterActionMock).getAuthorizationStrategy();
                will(returnValue(authorizationStrategy));
                allowing(queueSubmitterActionMock).getExecutionStrategy();
                will(returnValue(taskHandlerExecutionStrategy));
                allowing(queueSubmitterActionMock).getTaskHandler();
                will(returnValue(taskHandlerMock));
            }
        });
    }

    /**
     * Test the execution of the ServiceActionController with just a service Action.
     */
    @Test
    public void testExecuteWithServiceAction()
    {
        Serializable params = null;

        final ServiceActionContext serviceActionContext = new ServiceActionContext(params, principalMock);
        transDef.setReadOnly(true);

        setupTransactionContext(true, serviceActionContext, false);

        sut.execute(serviceActionContext, serviceActionMock);

        mockery.assertIsSatisfied();
    }

    /**
     * Test the execution of the ServiceActionController with just a service Action.
     */
    @Test(expected = GeneralException.class)
    public void testExecuteWithServiceActionFailure()
    {
        Serializable params = null;

        final ServiceActionContext serviceActionContext = new ServiceActionContext(params, principalMock);
        transDef.setReadOnly(true);

        setupTransactionContext(true, serviceActionContext, true);

        mockery.checking(new Expectations()
        {
            {
                oneOf(transStatus).isCompleted();
                will(returnValue(false));

                oneOf(transMgrMock).rollback(transStatus);
            }
        });

        sut.execute(serviceActionContext, serviceActionMock);

        mockery.assertIsSatisfied();
    }

    /**
     * Test the execution of the ServiceActionController with just a service Action and fail on validation.
     */
    @Test(expected = ValidationException.class)
    public void testExecuteWithServiceActionValidationFailure()
    {
        Serializable params = null;

        final ServiceActionContext serviceActionContext = new ServiceActionContext(params, principalMock);
        transDef.setReadOnly(true);

        expectServiceAction();
        mockery.checking(new Expectations()
        {
            {
                allowing(serviceActionMock).isReadOnly();
                will(returnValue(true));

                oneOf(transMgrMock).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(validationStrategy).validate(serviceActionContext);
                will(throwException(new ValidationException()));

                oneOf(transStatus).isCompleted();
                will(returnValue(false));

                oneOf(transMgrMock).rollback(transStatus);
            }
        });

        sut.execute(serviceActionContext, serviceActionMock);

        mockery.assertIsSatisfied();
    }

    /**
     * Test the execution of the ServiceActionController with just a service Action and fail on authorization.
     */
    @Test(expected = AuthorizationException.class)
    public void testExecuteWithServiceActionAuthorizationFailure()
    {
        Serializable params = null;

        final ServiceActionContext serviceActionContext = new ServiceActionContext(params, principalMock);
        transDef.setReadOnly(true);

        expectServiceAction();
        mockery.checking(new Expectations()
        {
            {
                allowing(serviceActionMock).isReadOnly();
                will(returnValue(true));

                oneOf(transMgrMock).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(validationStrategy).validate(serviceActionContext);

                oneOf(authorizationStrategy).authorize(serviceActionContext);
                will(throwException(new AuthorizationException()));

                oneOf(transStatus).isCompleted();
                will(returnValue(false));

                oneOf(transMgrMock).rollback(transStatus);
            }
        });

        sut.execute(serviceActionContext, serviceActionMock);

        mockery.assertIsSatisfied();
    }

    /**
     * Test the execution of the ServiceActionController with just a service Action and fail on execution.
     */
    @Test(expected = ExecutionException.class)
    public void testExecuteWithServiceActionExecutionFailure()
    {
        Serializable params = null;

        final ServiceActionContext serviceActionContext = new ServiceActionContext(params, principalMock);
        transDef.setReadOnly(true);

        expectServiceAction();
        mockery.checking(new Expectations()
        {
            {
                allowing(serviceActionMock).isReadOnly();
                will(returnValue(true));

                oneOf(transMgrMock).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(validationStrategy).validate(serviceActionContext);

                oneOf(authorizationStrategy).authorize(serviceActionContext);

                oneOf(executionStrategy).execute(serviceActionContext);
                will(throwException(new ExecutionException()));

                oneOf(transStatus).isCompleted();
                will(returnValue(false));

                oneOf(transMgrMock).rollback(transStatus);
            }
        });

        sut.execute(serviceActionContext, serviceActionMock);

        mockery.assertIsSatisfied();
    }

    /**
     * Test the failure path of the {@link TaskHandlerAction}.
     *
     * @throws Exception
     *             on error.
     */
    @Test(expected = GeneralException.class)
    public void testExecuteWithQueueServiceActionFailure() throws Exception
    {
        final ServiceActionContext serviceActionContext = mockery.mock(ServiceActionContext.class);
        transDef.setReadOnly(false);

        final List<UserActionRequest> requests = new ArrayList<UserActionRequest>();
        requests.add(userActionRequest);

        final TaskHandlerActionContext<ServiceActionContext> taskHandlerActionContextMock = mockery
                .mock(TaskHandlerActionContext.class);

        setupTaskHandlerTransactionContext(false, taskHandlerActionContextMock, true);

        mockery.checking(new Expectations()
        {
            {
                oneOf(transStatus).isCompleted();
                will(returnValue(false));

                oneOf(transMgrMock).rollback(transStatus);
            }
        });

        sut.execute(serviceActionContext, queueSubmitterActionMock);

        mockery.assertIsSatisfied();
    }

    /**
     * Test the validation failure path of the {@link TaskHandlerAction}.
     *
     * @throws Exception
     *             on error.
     */
    @Test(expected = ValidationException.class)
    public void testExecuteWithQueueServiceActionValidationFailure() throws Exception
    {
        final ServiceActionContext serviceActionContext = mockery.mock(ServiceActionContext.class);
        transDef.setReadOnly(false);

        final List<UserActionRequest> requests = new ArrayList<UserActionRequest>();
        requests.add(userActionRequest);

        expectTaskHandlerServiceAction();
        mockery.checking(new Expectations()
        {
            {
                allowing(queueSubmitterActionMock).isReadOnly();
                will(returnValue(false));

                oneOf(transMgrMock).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(validationStrategy).validate(with(any(ServiceActionContext.class)));
                will(throwException(new ValidationException()));

                oneOf(transStatus).isCompleted();
                will(returnValue(false));

                oneOf(transMgrMock).rollback(transStatus);
            }
        });

        sut.execute(serviceActionContext, queueSubmitterActionMock);

        mockery.assertIsSatisfied();
    }

    /**
     * Test the validation failure path of the {@link TaskHandlerAction}.
     *
     * @throws Exception
     *             on error.
     */
    @Test(expected = AuthorizationException.class)
    public void testExecuteWithQueueServiceActionAuthorizationFailure() throws Exception
    {
        final ServiceActionContext serviceActionContext = mockery.mock(ServiceActionContext.class);
        transDef.setReadOnly(false);

        final List<UserActionRequest> requests = new ArrayList<UserActionRequest>();
        requests.add(userActionRequest);

        expectTaskHandlerServiceAction();
        mockery.checking(new Expectations()
        {
            {
                allowing(queueSubmitterActionMock).isReadOnly();
                will(returnValue(false));

                oneOf(transMgrMock).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(validationStrategy).validate(with(any(ServiceActionContext.class)));

                oneOf(authorizationStrategy).authorize(with(any(ServiceActionContext.class)));
                will(throwException(new AuthorizationException()));

                oneOf(transStatus).isCompleted();
                will(returnValue(false));

                oneOf(transMgrMock).rollback(transStatus);
            }
        });

        sut.execute(serviceActionContext, queueSubmitterActionMock);

        mockery.assertIsSatisfied();
    }

    /**
     * Test the validation failure path of the {@link TaskHandlerAction}.
     *
     * @throws Exception
     *             on error.
     */
    @Test(expected = ExecutionException.class)
    public void testExecuteWithQueueServiceActionExecutionFailure() throws Exception
    {
        final ServiceActionContext serviceActionContext = mockery.mock(ServiceActionContext.class);
        transDef.setReadOnly(false);

        final List<UserActionRequest> requests = new ArrayList<UserActionRequest>();
        requests.add(userActionRequest);

        expectTaskHandlerServiceAction();
        mockery.checking(new Expectations()
        {
            {
                allowing(queueSubmitterActionMock).isReadOnly();
                will(returnValue(false));

                oneOf(transMgrMock).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(validationStrategy).validate(with(any(ServiceActionContext.class)));

                oneOf(authorizationStrategy).authorize(with(any(ServiceActionContext.class)));

                oneOf(taskHandlerExecutionStrategy).execute(with(any(TaskHandlerActionContext.class)));
                will(throwException(new ExecutionException()));

                oneOf(transStatus).isCompleted();
                will(returnValue(false));

                oneOf(transMgrMock).rollback(transStatus);
            }
        });

        sut.execute(serviceActionContext, queueSubmitterActionMock);

        mockery.assertIsSatisfied();
    }

    /**
     * Test the sucessful path of the {@link TaskHandlerAction}.
     *
     * @throws Exception
     *             on error.
     */
    @Test
    public void testExecuteWithQueueServiceAction() throws Exception
    {
        final ServiceActionContext serviceActionContext = mockery.mock(ServiceActionContext.class);
        transDef.setReadOnly(false);

        final List<UserActionRequest> requests = new ArrayList<UserActionRequest>();
        requests.add(userActionRequest);

        final TaskHandlerActionContext<ServiceActionContext> taskHandlerActionContextMock = mockery
                .mock(TaskHandlerActionContext.class);

        setupTaskHandlerTransactionContext(false, taskHandlerActionContextMock, false);

        sut.execute(serviceActionContext, queueSubmitterActionMock);

        mockery.assertIsSatisfied();
    }

    /**
     * Helper method.
     *
     * @param isReadOnlyTransaction
     *            - flag for setting the readonly transaction
     * @param inContext
     *            - {@link ServiceActionContext} for the current test.
     * @param throwsException
     *            - flag for whether or not to throw an exception within the trans.
     */
    private void setupTransactionContext(final boolean isReadOnlyTransaction, final ServiceActionContext inContext,
            final boolean throwsException)
    {
        expectServiceAction();
        mockery.checking(new Expectations()
        {
            {
                allowing(serviceActionMock).isReadOnly();
                will(returnValue(isReadOnlyTransaction));

                oneOf(transMgrMock).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(validationStrategy).validate(inContext);

                oneOf(authorizationStrategy).authorize(inContext);

                oneOf(executionStrategy).execute(inContext);

                if (throwsException)
                {
                    oneOf(transMgrMock).commit(transStatus);
                    will(throwException(new Exception()));
                }
                else
                {
                    oneOf(transMgrMock).commit(transStatus);
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
     *            - {@link TaskHandlerActionContext} for the current test.
     * @param throwsException
     *            - flag for whether or not to throw an exception within the trans.
     */
    private void setupTaskHandlerTransactionContext(final boolean isReadOnlyTransaction,
            final TaskHandlerActionContext inContext, final boolean throwsException)
    {
        expectTaskHandlerServiceAction();
        mockery.checking(new Expectations()
        {
            {
                allowing(queueSubmitterActionMock).isReadOnly();
                will(returnValue(isReadOnlyTransaction));

                oneOf(transMgrMock).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(validationStrategy).validate(with(any(ServiceActionContext.class)));

                oneOf(authorizationStrategy).authorize(with(any(ServiceActionContext.class)));

                oneOf(taskHandlerExecutionStrategy).execute(with(any(TaskHandlerActionContext.class)));

                if (throwsException)
                {
                    oneOf(transMgrMock).commit(transStatus);
                    will(throwException(new Exception()));
                }
                else
                {
                    oneOf(transMgrMock).commit(transStatus);
                }
            }
        });
    }

    // ===== Missing strategy tests =====

    /**
     * Test.
     */
    @Test(expected = InvalidActionException.class)
    public void testExecuteMissingValidation()
    {
        ServiceAction action = new ServiceAction(null, authorizationStrategy, executionStrategy, false);
        final ServiceActionContext context = new ServiceActionContext(null, principalMock);
        sut.execute(context, action);
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = InvalidActionException.class)
    public void testExecuteMissingAuthorization()
    {
        ServiceAction action = new ServiceAction(validationStrategy, null, executionStrategy, false);
        final ServiceActionContext context = new ServiceActionContext(null, principalMock);
        sut.execute(context, action);
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = InvalidActionException.class)
    public void testExecuteMissingExecution()
    {
        ServiceAction action = new ServiceAction(validationStrategy, authorizationStrategy, null, false);
        final ServiceActionContext context = new ServiceActionContext(null, principalMock);
        sut.execute(context, action);
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = InvalidActionException.class)
    public void testExecuteTHMissingValidation()
    {
        TaskHandlerAction action = new TaskHandlerServiceAction(null, authorizationStrategy,
                taskHandlerExecutionStrategy, taskHandlerMock, false);
        sut.execute(new ServiceActionContext(null, principalMock), action);
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = InvalidActionException.class)
    public void testExecuteTHMissingAuthorization()
    {
        TaskHandlerAction action = new TaskHandlerServiceAction(validationStrategy, null,
                taskHandlerExecutionStrategy, taskHandlerMock, false);
        sut.execute(new ServiceActionContext(null, principalMock), action);
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = InvalidActionException.class)
    public void testExecuteTHMissingExecution()
    {
        TaskHandlerAction action = new TaskHandlerServiceAction(validationStrategy, authorizationStrategy, null,
                taskHandlerMock, false);
        sut.execute(new ServiceActionContext(null, principalMock), action);
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = InvalidActionException.class)
    public void testExecuteTHMissingTaskHandler()
    {
        TaskHandlerAction action = new TaskHandlerServiceAction(validationStrategy, authorizationStrategy,
                taskHandlerExecutionStrategy, null, false);
        sut.execute(new ServiceActionContext(null, principalMock), action);
        mockery.assertIsSatisfied();
    }
}
