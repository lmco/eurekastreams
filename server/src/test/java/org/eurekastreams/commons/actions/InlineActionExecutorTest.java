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
package org.eurekastreams.commons.actions;

import static org.junit.Assert.assertSame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.async.AsyncAction;
import org.eurekastreams.commons.actions.async.TaskHandlerAsyncAction;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContextImpl;
import org.eurekastreams.commons.actions.context.async.AsyncActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.actions.service.TaskHandlerServiceAction;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.exceptions.InvalidActionException;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.task.TaskHandler;
import org.eurekastreams.commons.test.EasyMatcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.action.CustomAction;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests InlineActionExecutor.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class InlineActionExecutorTest
{
    /** Used for mocking objects. */
    private final Mockery mockery = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: resultValue. */
    private final Serializable resultValue = mockery.mock(Serializable.class, "resultValue");

    /** Fixture: Validation Strategy. */
    private final ValidationStrategy validationStrategy = mockery.mock(ValidationStrategy.class, "validationStrategy");

    /** Fixture: Authorization Strategy. */
    private final AuthorizationStrategy authorizationStrategy = mockery.mock(AuthorizationStrategy.class,
            "authorizationStrategy");

    /** Fixture: Execution Strategy. */
    private final ExecutionStrategy executionStrategy = mockery.mock(ExecutionStrategy.class, "executionStrategy");

    /** Fixture: Execution Strategy. */
    private final TaskHandlerExecutionStrategy executionStrategyTH = mockery.mock(TaskHandlerExecutionStrategy.class,
            "executionStrategyTH");

    /**
     * Fixture: task handler. There should be NO expectations defined on this mock so that any attempt to use it causes
     * a test error.
     */
    private final TaskHandler taskHandler = mockery.mock(TaskHandler.class, "taskHandler");

    /** Fixture: userActionRequests. */
    private final List<UserActionRequest> userActionRequests = new ArrayList<UserActionRequest>();

    /** Fixture: parent context parameter. */
    private final Serializable parentParam = mockery.mock(Serializable.class, "parentParam");

    /** Fixture: child context parameter. */
    private final Serializable childParam = mockery.mock(Serializable.class, "childParam");

    /** Fixture: principal. */
    private final Principal principal = mockery.mock(Principal.class, "principal");

    /** Fixture: user action request. */
    private final UserActionRequest userActionRequest = mockery.mock(UserActionRequest.class, "userActionRequest");

    /** Action. */
    private final Action asyncAction = new AsyncAction(validationStrategy, executionStrategy, false);

    /** Action. */
    private final Action serviceAction = new ServiceAction(validationStrategy, authorizationStrategy,
            executionStrategy, false);

    /** Action. */
    private final TaskHandlerAction taskHandlerServiceAction = new TaskHandlerServiceAction(validationStrategy,
            authorizationStrategy, executionStrategyTH, taskHandler, false);

    /** Action. */
    private final TaskHandlerAction taskHandlerAsyncAction = new TaskHandlerAsyncAction(validationStrategy,
            executionStrategyTH, taskHandler, false);

    /** Context. */
    private ActionContext asyncActionInnerContext;

    /** Context. */
    private ActionContext serviceActionInnerContext;

    /** Context. */
    private ActionContext serviceActionInnerContextNullPrincipal;

    /** Matcher for checking the context. */
    final EasyMatcher<ActionContext> contextMatcher = new EasyMatcher<ActionContext>()
    {
        @Override
        protected boolean isMatch(final ActionContext inTestObject)
        {
            return inTestObject.getParams() == childParam;
        }
    };

    /** Matcher for checking the context with a principal. */
    final EasyMatcher<ActionContext> contextWithPrincipalMatcher = new EasyMatcher<ActionContext>()
    {
        @Override
        protected boolean isMatch(final ActionContext inTestObject)
        {
            return inTestObject.getParams() == childParam && inTestObject instanceof PrincipalActionContext
                    && ((PrincipalActionContext) inTestObject).getPrincipal() == principal;
        }
    };

    /** Matcher for checking the context with a null principal. */
    final EasyMatcher<ActionContext> contextWithNullPrincipalMatcher = new EasyMatcher<ActionContext>()
    {
        @Override
        protected boolean isMatch(final ActionContext inTestObject)
        {
            return inTestObject.getParams() == childParam && inTestObject instanceof PrincipalActionContext
                    && ((PrincipalActionContext) inTestObject).getPrincipal() == null;
        }
    };

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        asyncActionInnerContext = new AsyncActionContext(parentParam);
        serviceActionInnerContext = new ServiceActionContext(parentParam, principal);
        serviceActionInnerContextNullPrincipal = new ServiceActionContext(parentParam, null);
        userActionRequests.clear();
        mockery.checking(new Expectations()
        {
            {
                allowing(principal).getAccountId();
                will(returnValue("somebody"));
            }
        });
    }

    // ========== TEST HELPERS ===========

    /**
     * Set up expectation for a validator being invoked.
     *
     * @param notNull
     *            If the principal should not be null.
     */
    private void expectValidatePrincipal(final boolean notNull)
    {
        mockery.checking(new Expectations()
        {
            {
                oneOf(validationStrategy).validate(
                        with(notNull ? contextWithPrincipalMatcher : contextWithNullPrincipalMatcher));
            }
        });
    }

    /**
     * Set up expectation for a validator being invoked.
     */
    private void expectValidate()
    {
        mockery.checking(new Expectations()
        {
            {
                oneOf(validationStrategy).validate(with(contextMatcher));
            }
        });
    }

    /**
     * Set up expectation for an authorizer being invoked.
     *
     * @param notNull
     *            If the principal should not be null.
     */
    private void expectAuthorizePrincipal(final boolean notNull)
    {
        mockery.checking(new Expectations()
        {
            {
                oneOf(authorizationStrategy).authorize(
                        (PrincipalActionContext) with(notNull ? contextWithPrincipalMatcher
                                : contextWithNullPrincipalMatcher));
            }
        });
    }

    /**
     * Set up expectation for an execution being invoked.
     */
    private void expectExecute()
    {
        mockery.checking(new Expectations()
        {
            {
                oneOf(executionStrategy).execute(with(contextMatcher));
                will(returnValue(resultValue));
            }
        });
    }

    /**
     * Set up expectation for an execution being invoked.
     *
     * @param notNull
     *            If the principal should not be null.
     */
    private void expectExecutePrincipal(final boolean notNull)
    {
        mockery.checking(new Expectations()
        {
            {
                oneOf(executionStrategy).execute(
                        with(notNull ? contextWithPrincipalMatcher : contextWithNullPrincipalMatcher));
                will(returnValue(resultValue));
            }
        });
    }

    /**
     * Set up expectation for an execution being invoked.
     *
     * @param notNull
     *            If the principal should not be null.
     */
    private void expectExecuteTaskHandlerPrincipal(final boolean notNull)
    {
        mockery.checking(new Expectations()
        {
            {
                oneOf(executionStrategyTH).execute(with(new EasyMatcher<TaskHandlerActionContext>()
                {
                    @Override
                    protected boolean isMatch(final TaskHandlerActionContext inTestObject)
                    {
                        return (notNull ? contextWithPrincipalMatcher : contextWithNullPrincipalMatcher)
                                .matches(inTestObject.getActionContext());
                    }
                }));
                will(new CustomAction("execute task handler execution")
                {
                    @Override
                    public Object invoke(final Invocation inInvocation) throws Throwable
                    {
                        userActionRequests.add(userActionRequest);
                        return resultValue;
                    }
                });
            }
        });
    }

    /**
     * Set up expectation for an execution being invoked.
     */
    private void expectExecuteTaskHandler()
    {
        mockery.checking(new Expectations()
        {
            {
                oneOf(executionStrategyTH).execute(with(new EasyMatcher<TaskHandlerActionContext>()
                {
                    @Override
                    protected boolean isMatch(final TaskHandlerActionContext inTestObject)
                    {
                        return contextMatcher.matches(inTestObject.getActionContext());
                    }
                }));
                will(new CustomAction("execute task handler execution")
                {
                    @Override
                    public Object invoke(final Invocation inInvocation) throws Throwable
                    {
                        userActionRequests.add(userActionRequest);
                        return resultValue;
                    }
                });
            }
        });
    }

    /**
     * Core of tests taking a TaskHandlerAction.
     * 
     * @param externalActionSelection
     *            Choice for creating SUT.
     * @param action
     *            Action to invoke SUT with.
     * @param innerContext
     *            Inner context of TaskHandlerActionContext to invoke SUT with.
     * @param param
     *            Param to invoke SUT with.
     */
    private void coreTest(final boolean externalActionSelection, final TaskHandlerAction action,
            final ActionContext innerContext, final Serializable param)
    {
        InlineActionExecutor sut = new InlineActionExecutor(externalActionSelection);
        Serializable result = sut.execute(action, new TaskHandlerActionContextImpl(innerContext, userActionRequests),
                param);
        mockery.assertIsSatisfied();
        assertSame(resultValue, result);
    }

    /**
     * Core of tests taking an Action (non-TaskHandlerAction).
     *
     * @param externalActionSelection
     *            Choice for creating SUT.
     * @param action
     *            Action to invoke SUT with.
     * @param innerContext
     *            Inner context of TaskHandlerActionContext to invoke SUT with.
     * @param param
     *            Param to invoke SUT with.
     */
    private void coreTest(final boolean externalActionSelection, final Action action,
            final ActionContext innerContext, final Serializable param)
    {
        InlineActionExecutor sut = new InlineActionExecutor(externalActionSelection);
        Serializable result = sut.execute(action, new TaskHandlerActionContextImpl(innerContext, userActionRequests),
                param);
        mockery.assertIsSatisfied();
        assertSame(resultValue, result);
    }

    // ========== EXECUTION COMBINATION TESTS ==========

    // Format for the combination test names: test_<action type>_<inner context type>_<external choice>
    // * action type: SA=ServiceAction, AA=AsyncAction, THSA=TaskHandlerServiceAction, THAA=TaskHandlerAsyncAction
    // * inner context type: SAC=ServiceActionContext, SACn=ServiceActionContext w/ null, AAC=AsyncActionContext
    // * external choice: E=externally selected action, I=internally selected action

    // ===== EXECUTION COMBINATION TESTS: for a non-task-handler action =====

    /**
     * Test.
     */
    @Test
    public void testwithSAwithSACwithE()
    {
        expectValidatePrincipal(true);
        expectAuthorizePrincipal(true);
        expectExecutePrincipal(true);
        coreTest(true, serviceAction, serviceActionInnerContext, childParam);
    }

    /**
     * Test.
     */
    @Test
    public void testwithSAwithSACnwithE()
    {
        expectValidatePrincipal(false);
        expectAuthorizePrincipal(false);
        expectExecutePrincipal(false);
        coreTest(true, serviceAction, serviceActionInnerContextNullPrincipal, childParam);
    }

    /**
     * Test.
     */
    @Test
    public void testwithSAwithAACwithE()
    {
        expectValidate();
        expectExecute();
        coreTest(true, serviceAction, asyncActionInnerContext, childParam);
    }

    /**
     * Test.
     */
    @Test
    public void testwithSAwithSACwithI()
    {
        expectValidatePrincipal(true);
        expectAuthorizePrincipal(true);
        expectExecutePrincipal(true);
        coreTest(false, serviceAction, serviceActionInnerContext, childParam);
    }

    /**
     * Test.
     */
    @Test
    public void testwithSAwithSACnwithI()
    {
        expectValidatePrincipal(false);
        expectAuthorizePrincipal(false);
        expectExecutePrincipal(false);
        coreTest(false, serviceAction, serviceActionInnerContextNullPrincipal, childParam);
    }

    /**
     * Test.
     */
    @Test
    public void testwithSAwithAACwithI()
    {
        expectValidate();
        expectExecute();
        coreTest(false, serviceAction, asyncActionInnerContext, childParam);
    }

    /**
     * Test.
     */
    @Test(expected = InvalidActionException.class)
    public void testwithAAwithSACwithE()
    {
        coreTest(true, asyncAction, serviceActionInnerContext, childParam);
    }

    /**
     * Test.
     */
    @Test(expected = InvalidActionException.class)
    public void testwithAAwithSACnwithE()
    {
        coreTest(true, asyncAction, serviceActionInnerContextNullPrincipal, childParam);
    }

    /**
     * Test.
     */
    @Test(expected = InvalidActionException.class)
    public void testwithAAwithAACwithE()
    {
        coreTest(true, asyncAction, asyncActionInnerContext, childParam);
    }

    /**
     * Test.
     */
    @Test
    public void testwithAAwithSACwithI()
    {
        expectValidate();
        expectExecute();
        coreTest(false, asyncAction, serviceActionInnerContext, childParam);
    }

    /**
     * Test.
     */
    @Test
    public void testwithAAwithSACnwithI()
    {
        expectValidate();
        expectExecute();
        coreTest(false, asyncAction, serviceActionInnerContextNullPrincipal, childParam);
    }

    /**
     * Test.
     */
    @Test
    public void testwithAAwithAACwithI()
    {
        expectValidate();
        expectExecute();
        coreTest(false, asyncAction, asyncActionInnerContext, childParam);
    }

    // ===== EXECUTION COMBINATION TESTS: for a task-handler action =====

    /**
     * Test.
     */
    @Test
    public void testwithTHSAwithSACwithE()
    {
        expectValidatePrincipal(true);
        expectAuthorizePrincipal(true);
        expectExecuteTaskHandlerPrincipal(true);
        coreTest(true, taskHandlerServiceAction, serviceActionInnerContext, childParam);
    }

    /**
     * Test.
     */
    @Test
    public void testwithTHSAwithSACnwithE()
    {
        expectValidatePrincipal(false);
        expectAuthorizePrincipal(false);
        expectExecuteTaskHandlerPrincipal(false);
        coreTest(true, taskHandlerServiceAction, serviceActionInnerContextNullPrincipal, childParam);
    }

    /**
     * Test.
     */
    @Test
    public void testwithTHSAwithAACwithE()
    {
        expectValidate();
        expectExecuteTaskHandler();
        coreTest(true, taskHandlerServiceAction, asyncActionInnerContext, childParam);
    }

    /**
     * Test.
     */
    @Test
    public void testwithTHSAwithSACwithI()
    {
        expectValidatePrincipal(true);
        expectAuthorizePrincipal(true);
        expectExecuteTaskHandlerPrincipal(true);
        coreTest(false, taskHandlerServiceAction, serviceActionInnerContext, childParam);
    }

    /**
     * Test.
     */
    @Test
    public void testwithTHSAwithSACnwithI()
    {
        expectValidatePrincipal(false);
        expectAuthorizePrincipal(false);
        expectExecuteTaskHandlerPrincipal(false);
        coreTest(false, taskHandlerServiceAction, serviceActionInnerContextNullPrincipal, childParam);
    }

    /**
     * Test.
     */
    @Test
    public void testwithTHSAwithAACwithI()
    {
        expectValidate();
        expectExecuteTaskHandler();
        coreTest(false, taskHandlerServiceAction, asyncActionInnerContext, childParam);
    }

    /**
     * Test.
     */
    @Test(expected = InvalidActionException.class)
    public void testwithTHAAwithSACwithE()
    {
        coreTest(true, taskHandlerAsyncAction, serviceActionInnerContext, childParam);
    }

    /**
     * Test.
     */
    @Test(expected = InvalidActionException.class)
    public void testwithTHAAwithSACnwithE()
    {
        coreTest(true, taskHandlerAsyncAction, serviceActionInnerContextNullPrincipal, childParam);
    }

    /**
     * Test.
     */
    @Test(expected = InvalidActionException.class)
    public void testwithTHAAwithAACwithE()
    {
        coreTest(true, taskHandlerAsyncAction, asyncActionInnerContext, childParam);
    }

    /**
     * Test.
     */
    @Test
    public void testwithTHAAwithSACwithI()
    {
        expectValidate();
        expectExecuteTaskHandler();
        coreTest(false, taskHandlerAsyncAction, serviceActionInnerContext, childParam);
    }

    /**
     * Test.
     */
    @Test
    public void testwithTHAAwithSACnwithI()
    {
        expectValidate();
        expectExecuteTaskHandler();
        coreTest(false, taskHandlerAsyncAction, serviceActionInnerContextNullPrincipal, childParam);
    }

    /**
     * Test.
     */
    @Test
    public void testwithTHAAwithAACwithI()
    {
        expectValidate();
        expectExecuteTaskHandler();
        coreTest(false, taskHandlerAsyncAction, asyncActionInnerContext, childParam);
    }

    // ========== EXCEPTION TESTS ==========

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testValidationException()
    {
        mockery.checking(new Expectations()
        {
            {
                oneOf(validationStrategy).validate(with(contextWithPrincipalMatcher));
                will(throwException(new ValidationException("BAD!")));
            }
        });
        coreTest(true, serviceAction, serviceActionInnerContext, childParam);
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizationException()
    {
        expectValidatePrincipal(true);
        mockery.checking(new Expectations()
        {
            {
                oneOf(authorizationStrategy).authorize((PrincipalActionContext) with(contextWithPrincipalMatcher));
                will(throwException(new AuthorizationException("BAD!")));
            }
        });
        coreTest(true, serviceAction, serviceActionInnerContext, childParam);
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = ExecutionException.class)
    public void testExecutionException()
    {
        expectValidatePrincipal(true);
        expectAuthorizePrincipal(true);
        mockery.checking(new Expectations()
        {
            {
                oneOf(executionStrategy).execute(with(contextWithPrincipalMatcher));
                will(throwException(new ExecutionException("BAD!")));
            }
        });
        coreTest(true, serviceAction, serviceActionInnerContext, childParam);
        mockery.assertIsSatisfied();
    }
}
