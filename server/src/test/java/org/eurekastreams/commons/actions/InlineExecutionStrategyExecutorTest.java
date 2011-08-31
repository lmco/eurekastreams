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
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.test.EasyMatcher;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;


/**
 * Tests InlineExecutionStrategyExecutor. All 16 combinations of context needed vs. context provided are tested.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class InlineExecutionStrategyExecutorTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: Execution strategy to execute which needs to queue async tasks. */
    private final TaskHandlerExecutionStrategy taskHandlerExecution = context.mock(TaskHandlerExecutionStrategy.class);

    /** Fixture: Execution strategy to execute which does not need to queue async tasks. */
    private final ExecutionStrategy plainExecution = context.mock(ExecutionStrategy.class);

    /** Fixture: parameters. */
    private final Serializable parameters = context.mock(Serializable.class, "parameters");

    /** Fixture: results. */
    private final Serializable results = context.mock(Serializable.class, "results");

    /** Fixture: User action request list. */
    private final List<UserActionRequest> userActionRequests = context.mock(List.class, "userActionRequests");

    /** Fixture: principal. */
    private final Principal principal = context.mock(Principal.class);

    /** SUT. */
    private InlineExecutionStrategyExecutor sut;

    /**
     * Test.
     */
    @Test
    public void testNeedPHasTHP()
    {
        expectPlain();
        invokeWithTaskHandlerAndPrincipal();
    }

    /**
     * Test.
     */
    @Test
    public void testNeedPHasTH()
    {
        expectPlain();
        invokeWithTaskHandler();
    }

    /**
     * Test.
     */
    @Test
    public void testNeedPPHasTHP()
    {
        expectPlainAndPrincipal();
        invokeWithTaskHandlerAndPrincipal();
    }

    /**
     * Test.
     */
    @Test(expected = ExecutionException.class)
    public void testNeedPPHasTH()
    {
        expectPlainAndPrincipal();
        invokeWithTaskHandler();
    }

    /**
     * Test.
     */
    @Test
    public void testNeedTHPHasTHP()
    {
        expectTaskHandlerAndPrincipal();
        invokeWithTaskHandlerAndPrincipal();
    }

    /**
     * Test.
     */
    @Test(expected = ExecutionException.class)
    public void testNeedTHPHasTH()
    {
        expectTaskHandlerAndPrincipal();
        invokeWithTaskHandler();
    }

    /**
     * Test.
     */
    @Test
    public void testNeedTHHasTHP()
    {
        expectTaskHandler();
        invokeWithTaskHandlerAndPrincipal();
    }

    /**
     * Test.
     */
    @Test
    public void testNeedTHHasTH()
    {
        expectTaskHandler();
        invokeWithTaskHandler();
    }

    /**
     * Test.
     */
    @Test
    public void testNeedPHasPP()
    {
        expectPlain();
        invokeWithPlainAndPrincipal();
    }

    /**
     * Test.
     */
    @Test
    public void testNeedPHasP()
    {
        expectPlain();
        invokeWithPlain();
    }

    /**
     * Test.
     */
    @Test
    public void testNeedPPHasPP()
    {
        expectPlainAndPrincipal();
        invokeWithPlainAndPrincipal();
    }

    /**
     * Test.
     */
    @Test(expected = ExecutionException.class)
    public void testNeedPPHasP()
    {
        expectPlainAndPrincipal();
        invokeWithPlain();
    }

    /**
     * Test.
     */
    @Test(expected = ExecutionException.class)
    public void testNeedTHPHasPP()
    {
        expectTaskHandlerAndPrincipal();
        invokeWithPlainAndPrincipal();
    }

    /**
     * Test.
     */
    @Test(expected = ExecutionException.class)
    public void testNeedTHPHasP()
    {
        expectTaskHandlerAndPrincipal();
        invokeWithPlain();
    }

    /**
     * Test.
     */
    @Test(expected = ExecutionException.class)
    public void testNeedTHHasPP()
    {
        expectTaskHandler();
        invokeWithPlainAndPrincipal();
    }

    /**
     * Test.
     */
    @Test(expected = ExecutionException.class)
    public void testNeedTHHasP()
    {
        expectTaskHandler();
        invokeWithPlain();
    }

    // ----- Methods to set up the SUT with different execution strategies and expect to be invoked properly -----

    /**
     * Sets up SUT with an execution strategy needing only an ActionContext.
     */
    private void expectPlain()
    {
        sut = new InlineExecutionStrategyExecutor(false, plainExecution);
        context.checking(new Expectations()
        {
            {
                oneOf(plainExecution).execute(with(new EasyMatcher<ActionContext>()
                {
                    @Override
                    protected boolean isMatch(final ActionContext innerContext)
                    {
                        assertSame(parameters, innerContext.getParams());
                        return true;
                    }
                }));
                will(returnValue(results));
            }
        });
    }

    /**
     * Sets up SUT with an execution strategy needing a PrincipalActionContext.
     */
    private void expectPlainAndPrincipal()
    {
        sut = new InlineExecutionStrategyExecutor(true, plainExecution);
        context.checking(new Expectations()
        {
            {
                oneOf(plainExecution).execute(with(new EasyMatcher<ActionContext>()
                {
                    @Override
                    protected boolean isMatch(final ActionContext innerContext)
                    {
                        assertTrue(innerContext instanceof PrincipalActionContext);
                        assertSame(principal, ((PrincipalActionContext) innerContext).getPrincipal());
                        assertSame(parameters, innerContext.getParams());
                        return true;
                    }
                }));
                will(returnValue(results));
            }
        });
    }

    /**
     * Sets up SUT with an execution strategy needing a TaskHandlerActionContext.
     */
    private void expectTaskHandler()
    {
        sut = new InlineExecutionStrategyExecutor(false, taskHandlerExecution);
        context.checking(new Expectations()
        {
            {
                oneOf(taskHandlerExecution).execute(with(new EasyMatcher<TaskHandlerActionContext>()
                {
                    @Override
                    protected boolean isMatch(final TaskHandlerActionContext inTestObject)
                    {
                        assertSame(userActionRequests, inTestObject.getUserActionRequests());
                        ActionContext innerContext = inTestObject.getActionContext();
                        assertSame(parameters, innerContext.getParams());
                        return true;
                    }
                }));
                will(returnValue(results));
            }
        });
    }

    /**
     * Sets up SUT with an execution strategy needing a TaskHandlerActionContext containing a principal.
     */
    private void expectTaskHandlerAndPrincipal()
    {
        sut = new InlineExecutionStrategyExecutor(true, taskHandlerExecution);
        context.checking(new Expectations()
        {
            {
                oneOf(taskHandlerExecution).execute(with(new EasyMatcher<TaskHandlerActionContext>()
                {
                    @Override
                    protected boolean isMatch(final TaskHandlerActionContext inTestObject)
                    {
                        assertSame(userActionRequests, inTestObject.getUserActionRequests());
                        ActionContext innerContext = inTestObject.getActionContext();
                        assertTrue(innerContext instanceof PrincipalActionContext);
                        assertSame(principal, ((PrincipalActionContext) innerContext).getPrincipal());
                        assertSame(parameters, innerContext.getParams());
                        return true;
                    }
                }));
                will(returnValue(results));
            }
        });
    }

    // ----- Methods to invoke the SUT with different action contexts -----

    /**
     * Invokes the SUT with a PrincipalActionContext.
     */
    private void invokeWithPlainAndPrincipal()
    {
        final PrincipalActionContext originalInnerContext = context.mock(PrincipalActionContext.class,
                "originalInnerContext");

        context.checking(new Expectations()
        {
            {
                allowing(originalInnerContext).getPrincipal();
                will(returnValue(principal));
            }
        });

        Serializable result = sut.execute(originalInnerContext, parameters);
        assertSame(results, result);
    }

    /**
     * Invokes the SUT with a plain ActionContext.
     */
    private void invokeWithPlain()
    {
        final ActionContext originalInnerContext = context.mock(ActionContext.class, "originalInnerContext");
        Serializable result = sut.execute(originalInnerContext, parameters);
        assertSame(results, result);
    }

    /**
     * Invokes the SUT with a TaskHandlerActionContext containing a principal.
     */
    private void invokeWithTaskHandlerAndPrincipal()
    {
        final PrincipalActionContext originalInnerContext = context.mock(PrincipalActionContext.class,
                "originalInnerContext");
        TaskHandlerActionContext originalOuterContext = new TaskHandlerActionContext(originalInnerContext,
                userActionRequests);

        context.checking(new Expectations()
        {
            {
                allowing(originalInnerContext).getPrincipal();
                will(returnValue(principal));
            }
        });

        Serializable result = sut.execute(originalOuterContext, parameters);
        assertSame(results, result);
    }

    /**
     * Invokes the SUT with a TaskHandlerActionContext.
     */
    private void invokeWithTaskHandler()
    {
        final ActionContext originalInnerContext = context.mock(ActionContext.class, "originalInnerContext");
        TaskHandlerActionContext originalOuterContext = new TaskHandlerActionContext(originalInnerContext,
                userActionRequests);

        Serializable result = sut.execute(originalOuterContext, parameters);
        assertSame(results, result);
    }
}
