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
import org.eurekastreams.commons.exceptions.AuthorizationException;
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
 * Test class for the ServiceActionController.
 *
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
    private ValidationStrategy<ServiceActionContext> validationStrategy = context.mock(ValidationStrategy.class);

    /**
     * Mocked instance of the {@link AuthorizationStrategy for testing.
     */
    private AuthorizationStrategy<PrincipalActionContext> authorizationStrategy = context
            .mock(AuthorizationStrategy.class);

    /**
     * Mocked instance of the {@link ExecutionStrategy} for testing.
     */
    private ExecutionStrategy<ServiceActionContext> executionStrategy = context.mock(ExecutionStrategy.class);

    /**
     * Mocked instance of the {@link TaskHandlerExecutionStrategy} for testing.
     */
    private TaskHandlerExecutionStrategy<ActionContext> taskHandlerExecutionStrategy = context
            .mock(TaskHandlerExecutionStrategy.class);

    /**
     * Mocked instance of the {@link ServiceAction} class for testing.
     */
    private ServiceAction serviceActionMock = context.mock(ServiceAction.class);

    /**
     * Mocked instance of the {@link TaskHandlerAction} class for testing.
     */
    private TaskHandlerAction queueSubmitterActionMock = context.mock(TaskHandlerAction.class);

    /**
     * Mocked instance of the {@link TaskHandler} class.
     */
    private TaskHandler taskHandlerMock = context.mock(TaskHandler.class);

    /**
     * Mocked instance of the {@link Principal} class for testing.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Mocked instance of the {@link UserActionRequest} class for testing.
     */
    private UserActionRequest userActionRequest = context.mock(UserActionRequest.class);

    /**
     * Setup the test fixture.
     */
    @Before
    public void setUp()
    {
        sut = new ServiceActionController(transMgrMock);
        transDef = new DefaultTransactionDefinition();
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

        context.assertIsSatisfied();
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

        context.checking(new Expectations()
        {
            {
                oneOf(transStatus).isCompleted();
                will(returnValue(false));

                oneOf(transMgrMock).rollback(transStatus);
            }
        });

        sut.execute(serviceActionContext, serviceActionMock);

        context.assertIsSatisfied();
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

        context.checking(new Expectations()
        {
            {
                oneOf(serviceActionMock).isReadOnly();
                will(returnValue(true));

                oneOf(transMgrMock).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(serviceActionMock).getValidationStrategy();
                will(returnValue(validationStrategy));

                oneOf(validationStrategy).validate(serviceActionContext);
                will(throwException(new ValidationException()));

                oneOf(transStatus).isCompleted();
                will(returnValue(false));

                oneOf(transMgrMock).rollback(transStatus);
            }
        });

        sut.execute(serviceActionContext, serviceActionMock);

        context.assertIsSatisfied();
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

        context.checking(new Expectations()
        {
            {
                oneOf(serviceActionMock).isReadOnly();
                will(returnValue(true));

                oneOf(transMgrMock).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(serviceActionMock).getValidationStrategy();
                will(returnValue(validationStrategy));

                oneOf(validationStrategy).validate(serviceActionContext);

                oneOf(serviceActionMock).getAuthorizationStrategy();
                will(returnValue(authorizationStrategy));

                oneOf(authorizationStrategy).authorize(serviceActionContext);
                will(throwException(new AuthorizationException()));

                oneOf(transStatus).isCompleted();
                will(returnValue(false));

                oneOf(transMgrMock).rollback(transStatus);
            }
        });

        sut.execute(serviceActionContext, serviceActionMock);

        context.assertIsSatisfied();
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

        context.checking(new Expectations()
        {
            {
                oneOf(serviceActionMock).isReadOnly();
                will(returnValue(true));

                oneOf(transMgrMock).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(serviceActionMock).getValidationStrategy();
                will(returnValue(validationStrategy));

                oneOf(validationStrategy).validate(serviceActionContext);

                oneOf(serviceActionMock).getAuthorizationStrategy();
                will(returnValue(authorizationStrategy));

                oneOf(authorizationStrategy).authorize(serviceActionContext);

                oneOf(serviceActionMock).getExecutionStrategy();
                will(returnValue(executionStrategy));

                oneOf(executionStrategy).execute(serviceActionContext);
                will(throwException(new ExecutionException()));

                oneOf(transStatus).isCompleted();
                will(returnValue(false));

                oneOf(transMgrMock).rollback(transStatus);
            }
        });

        sut.execute(serviceActionContext, serviceActionMock);

        context.assertIsSatisfied();
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
        final ServiceActionContext serviceActionContext = context.mock(ServiceActionContext.class);
        transDef.setReadOnly(false);

        final List<UserActionRequest> requests = new ArrayList<UserActionRequest>();
        requests.add(userActionRequest);

        final TaskHandlerActionContext<ServiceActionContext> taskHandlerActionContextMock = context
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

        sut.execute(serviceActionContext, queueSubmitterActionMock);

        context.assertIsSatisfied();
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
        final ServiceActionContext serviceActionContext = context.mock(ServiceActionContext.class);
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

                oneOf(validationStrategy).validate(with(any(ServiceActionContext.class)));
                will(throwException(new ValidationException()));

                oneOf(transStatus).isCompleted();
                will(returnValue(false));

                oneOf(transMgrMock).rollback(transStatus);
            }
        });

        sut.execute(serviceActionContext, queueSubmitterActionMock);

        context.assertIsSatisfied();
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
        final ServiceActionContext serviceActionContext = context.mock(ServiceActionContext.class);
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

                oneOf(validationStrategy).validate(with(any(ServiceActionContext.class)));

                oneOf(queueSubmitterActionMock).getAuthorizationStrategy();
                will(returnValue(authorizationStrategy));

                oneOf(authorizationStrategy).authorize(with(any(ServiceActionContext.class)));
                will(throwException(new AuthorizationException()));

                oneOf(transStatus).isCompleted();
                will(returnValue(false));

                oneOf(transMgrMock).rollback(transStatus);
            }
        });

        sut.execute(serviceActionContext, queueSubmitterActionMock);

        context.assertIsSatisfied();
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
        final ServiceActionContext serviceActionContext = context.mock(ServiceActionContext.class);
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

                oneOf(validationStrategy).validate(with(any(ServiceActionContext.class)));

                oneOf(queueSubmitterActionMock).getAuthorizationStrategy();
                will(returnValue(authorizationStrategy));

                oneOf(authorizationStrategy).authorize(with(any(ServiceActionContext.class)));

                oneOf(queueSubmitterActionMock).getExecutionStrategy();
                will(returnValue(taskHandlerExecutionStrategy));

                oneOf(taskHandlerExecutionStrategy).execute(with(any(TaskHandlerActionContext.class)));
                will(throwException(new ExecutionException()));

                oneOf(transStatus).isCompleted();
                will(returnValue(false));

                oneOf(transMgrMock).rollback(transStatus);
            }
        });

        sut.execute(serviceActionContext, queueSubmitterActionMock);

        context.assertIsSatisfied();
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
        final ServiceActionContext serviceActionContext = context.mock(ServiceActionContext.class);
        transDef.setReadOnly(false);

        final List<UserActionRequest> requests = new ArrayList<UserActionRequest>();
        requests.add(userActionRequest);

        final TaskHandlerActionContext<ServiceActionContext> taskHandlerActionContextMock = context
                .mock(TaskHandlerActionContext.class);

        setupTaskHandlerTransactionContext(false, taskHandlerActionContextMock, false);

        sut.execute(serviceActionContext, queueSubmitterActionMock);

        context.assertIsSatisfied();
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
        context.checking(new Expectations()
        {
            {
                oneOf(serviceActionMock).isReadOnly();
                will(returnValue(isReadOnlyTransaction));

                oneOf(transMgrMock).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(serviceActionMock).getValidationStrategy();
                will(returnValue(validationStrategy));

                oneOf(validationStrategy).validate(inContext);

                oneOf(serviceActionMock).getAuthorizationStrategy();
                will(returnValue(authorizationStrategy));

                oneOf(authorizationStrategy).authorize(inContext);

                oneOf(serviceActionMock).getExecutionStrategy();
                will(returnValue(executionStrategy));

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
        context.checking(new Expectations()
        {
            {
                oneOf(queueSubmitterActionMock).isReadOnly();
                will(returnValue(isReadOnlyTransaction));

                oneOf(transMgrMock).getTransaction(transDef);
                will(returnValue(transStatus));

                oneOf(queueSubmitterActionMock).getValidationStrategy();
                will(returnValue(validationStrategy));

                oneOf(validationStrategy).validate(with(any(ServiceActionContext.class)));

                oneOf(queueSubmitterActionMock).getAuthorizationStrategy();
                will(returnValue(authorizationStrategy));

                oneOf(authorizationStrategy).authorize(with(any(ServiceActionContext.class)));

                oneOf(queueSubmitterActionMock).getExecutionStrategy();
                will(returnValue(taskHandlerExecutionStrategy));

                oneOf(taskHandlerExecutionStrategy).execute(with(any(TaskHandlerActionContext.class)));

                if (throwsException)
                {
                    oneOf(transMgrMock).commit(transStatus);
                    will(throwException(new Exception()));
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

}
