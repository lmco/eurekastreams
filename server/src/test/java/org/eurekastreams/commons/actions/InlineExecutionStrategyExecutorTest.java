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
import java.util.List;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContextImpl;
import org.eurekastreams.commons.actions.context.async.AsyncActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.test.EasyMatcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests InlineExecutionStrategyExecutor.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class InlineExecutionStrategyExecutorTest
{
    /** Used for mocking objects. */
    private final Mockery mockery = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: execution. */
    private final ExecutionStrategy execution = mockery.mock(ExecutionStrategy.class, "execution");

    /** Fixture: execution. */
    private final TaskHandlerExecutionStrategy executionTH = mockery.mock(TaskHandlerExecutionStrategy.class,
            "executionTH");

    /** Fixture: params. */
    private final Serializable params = mockery.mock(Serializable.class, "params");

    /** Fixture: other params. */
    private final Serializable otherParams = mockery.mock(Serializable.class, "otherParams");

    /** Fixture: result. */
    private final Serializable result = mockery.mock(Serializable.class, "result");

    /** Fixture: principal. */
    private final Principal principal = mockery.mock(Principal.class, "principal");

    /** Fixture: user action requests. */
    private final List<UserActionRequest> userActionRequests = mockery.mock(List.class, "userActionRequests");

    /** SUT. */
    private InlineExecutionStrategyExecutor sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new InlineExecutionStrategyExecutor();
    }

    /**
     * Test.
     */
    @Test
    public void testExecuteBasicParms()
    {
        mockery.checking(new Expectations()
        {
            {
                oneOf(execution).execute(with(new EasyMatcher<PrincipalActionContext>()
                {
                    @Override
                    protected boolean isMatch(final PrincipalActionContext inTestObject)
                    {
                        return inTestObject.getParams() == params && inTestObject.getPrincipal() == principal
                                && inTestObject.getState() != null;
                    }
                }));
                will(returnValue(result));
            }
        });

        assertSame(result, sut.execute(execution, params, principal));
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testExecuteBasicContextPrincipal()
    {
        final ActionContext parentContext = new ServiceActionContext(otherParams, principal);
        mockery.checking(new Expectations()
        {
            {
                oneOf(execution).execute(with(new EasyMatcher<PrincipalActionContext>()
                {
                    @Override
                    protected boolean isMatch(final PrincipalActionContext inTestObject)
                    {
                        return inTestObject.getParams() == params && inTestObject.getPrincipal() == principal
                                && inTestObject.getState() != null
                                && inTestObject.getState() != parentContext.getState();
                    }
                }));
                will(returnValue(result));
            }
        });

        assertSame(result, sut.execute(execution, params, parentContext));
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testExecuteBasicContextNonPrincipal()
    {
        final ActionContext parentContext = new AsyncActionContext(otherParams);
        mockery.checking(new Expectations()
        {
            {
                oneOf(execution).execute(with(new EasyMatcher<PrincipalActionContext>()
                {
                    @Override
                    protected boolean isMatch(final PrincipalActionContext inTestObject)
                    {
                        return inTestObject.getParams() == params && inTestObject.getPrincipal() == null
                                && inTestObject.getState() != null
                                && inTestObject.getState() != parentContext.getState();
                    }
                }));
                will(returnValue(result));
            }
        });

        assertSame(result, sut.execute(execution, params, parentContext));
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testExecuteTaskParms()
    {
        mockery.checking(new Expectations()
        {
            {
                oneOf(executionTH).execute(with(new EasyMatcher<TaskHandlerActionContext<PrincipalActionContext>>()
                {
                    @Override
                    protected boolean isMatch(final TaskHandlerActionContext<PrincipalActionContext> inTestObject)
                    {
                        PrincipalActionContext inner = inTestObject.getActionContext();
                        return inTestObject.getUserActionRequests() == userActionRequests
                                && inner.getParams() == params && inner.getPrincipal() == principal
                                && inner.getState() != null;
                    }
                }));
                will(returnValue(result));
            }
        });

        assertSame(result, sut.execute(executionTH, params, principal, userActionRequests));
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testExecuteTaskContextPrincipal()
    {
        final ActionContext parentInnerContext = new ServiceActionContext(otherParams, principal);
        final TaskHandlerActionContext parentContext = new TaskHandlerActionContextImpl(parentInnerContext,
                userActionRequests);
        mockery.checking(new Expectations()
        {
            {
                oneOf(executionTH).execute(with(new EasyMatcher<TaskHandlerActionContext<PrincipalActionContext>>()
                {
                    @Override
                    protected boolean isMatch(final TaskHandlerActionContext<PrincipalActionContext> inTestObject)
                    {
                        PrincipalActionContext inner = inTestObject.getActionContext();
                        return inTestObject.getUserActionRequests() == userActionRequests
                                && inner.getParams() == params && inner.getPrincipal() == principal
                                && inner.getState() != null && inner.getState() != parentInnerContext.getState();
                    }
                }));
                will(returnValue(result));
            }
        });

        assertSame(result, sut.execute(executionTH, params, parentContext));
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testExecuteTaskContextNonPrincipal()
    {
        final ActionContext parentInnerContext = new AsyncActionContext(otherParams);
        final TaskHandlerActionContext parentContext = new TaskHandlerActionContextImpl(parentInnerContext,
                userActionRequests);
        mockery.checking(new Expectations()
        {
            {
                oneOf(executionTH).execute(with(new EasyMatcher<TaskHandlerActionContext<PrincipalActionContext>>()
                {
                    @Override
                    protected boolean isMatch(final TaskHandlerActionContext<PrincipalActionContext> inTestObject)
                    {
                        PrincipalActionContext inner = inTestObject.getActionContext();
                        return inTestObject.getUserActionRequests() == userActionRequests
                                && inner.getParams() == params && inner.getPrincipal() == null
                                && inner.getState() != null && inner.getState() != parentInnerContext.getState();
                    }
                }));
                will(returnValue(result));
            }
        });

        assertSame(result, sut.execute(executionTH, params, parentContext));
        mockery.assertIsSatisfied();
    }
}
