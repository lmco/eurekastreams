/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
package org.eurekastreams.commons.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;

import net.sf.gilead.core.PersistentBeanManager;

import org.eurekastreams.commons.actions.context.PrincipalPopulator;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.actions.service.TaskHandlerServiceAction;
import org.eurekastreams.commons.client.ActionRequest;
import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.exceptions.GeneralException;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.security.userdetails.UserDetails;

/**
 * Unit test for ActionExecutor.
 */
public class ActionExecutorTest
{
    /**
     * Subject under test.
     */
    private ActionExecutor sut = null;

    /**
     * Context for building mock objects.
     */
    private final Mockery mockContext = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mocked Application Spring context.
     */
    private final BeanFactory springContextMock = mockContext.mock(BeanFactory.class);

    /**
     * Mocked UserDetails that is a valid user details object.
     */
    private final UserDetails validUserDetailsMock = mockContext.mock(UserDetails.class);

    /**
     * Used for GWT traffic while serializing persistent objects.
     */
    private final PersistentBeanManager persistentBeanManager = mockContext.mock(PersistentBeanManager.class);

    /**
     * Mocked PrincipalPopulator used to populate a principal object for the ServiceActionContext.
     */
    private final PrincipalPopulator principalPopulator = mockContext.mock(PrincipalPopulator.class);

    /**
     * Mocked ServiceActionController used to execute ServiceActions.
     */
    private final ServiceActionController serviceActionController = mockContext.mock(ServiceActionController.class);

    /**
     * The params array of Strings.
     */
    private String[] params = null;

    /**
     * The service target.
     */
    private static final String ACTION_KEY = "actionToExecute";

    /**
     * Test username for the suite.
     */
    private static final String USERNAME = "testuser";

    /** Action request object. */
    private ActionRequestImpl<String> defaultRequest = null;

    /** Action request object. */
    private ActionRequestImpl<String> nullParmRequest = null;

    /** Fixture: action bean to execute. */
    private final ServiceAction serviceAction = mockContext.mock(ServiceAction.class, "serviceAction");

    /** Fixture: action bean to execute. */
    private final TaskHandlerServiceAction taskHandlerAction = mockContext.mock(TaskHandlerServiceAction.class,
            "taskHandlerAction");

    /** Fixture: generic action result. */
    private final Serializable genericResult = mockContext.mock(Serializable.class, "genericResult");

    /** Fixture: generic action result. */
    private final Serializable genericClonedResult = mockContext.mock(Serializable.class, "genericClonedResult");

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new ActionExecutor(springContextMock, serviceActionController, principalPopulator, persistentBeanManager);

        params = new String[] { "echo me" };

        // set up requests
        defaultRequest = new ActionRequestImpl<String>(ACTION_KEY, params);
        nullParmRequest = new ActionRequestImpl<String>(ACTION_KEY, null);

        mockContext.checking(new Expectations()
        {
            {
                allowing(validUserDetailsMock).getUsername();
                will(returnValue(USERNAME));
            }
        });
    }

    /**
     * Performs additional setup for a successful action execution scenario.
     *
     * @param actionBean
     *            Object to be returned from Spring as the action bean.
     */
    private void setupSuccess(final Object actionBean)
    {
        mockContext.checking(new Expectations()
        {
            {
                oneOf(springContextMock).getBean(ACTION_KEY);
                will(returnValue(actionBean));

                oneOf(principalPopulator).getPrincipal(USERNAME, null);

                oneOf(persistentBeanManager).clone(with(same(genericResult)));
                will(returnValue(genericClonedResult));
            }
        });
    }

    /**
     * Test execution with a ServiceAction.
     */
    @Test
    public final void testExecuteServiceAction()
    {
        setupSuccess(serviceAction);
        mockContext.checking(new Expectations()
        {
            {
                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(same(serviceAction)));
                will(returnValue(genericResult));
            }
        });

        ActionRequestImpl<String> request = new ActionRequestImpl<String>(ACTION_KEY, new String("testParam"));

        // Test the setter on the ActionRequest class.
        Assert.assertEquals(request.getParam(), "testParam");

        request.setParam(new String("anotherTestParam"));

        Assert.assertEquals(request.getParam(), "anotherTestParam");

        // Execute the request.
        sut.execute(request, validUserDetailsMock);

        mockContext.assertIsSatisfied();
    }

    /**
     * Test execution with a TaskHandlerServiceAction.
     */
    @Test
    public final void testExecuteAsyncSubmitterServiceAction()
    {
        setupSuccess(taskHandlerAction);
        mockContext.checking(new Expectations()
        {
            {
                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(same(taskHandlerAction)));
                will(returnValue(genericResult));
            }
        });

        ActionRequestImpl<String> request = new ActionRequestImpl<String>(ACTION_KEY, new String("testParam"));

        // Test the setter on the ActionRequest class.
        Assert.assertEquals(request.getParam(), "testParam");

        request.setParam(new String("anotherTestParam"));

        Assert.assertEquals(request.getParam(), "anotherTestParam");

        // Execute the request.
        sut.execute(request, validUserDetailsMock);

        mockContext.assertIsSatisfied();
    }

    /**
     * Test the scenario where the bean supplied for the action is not a valid Action type.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testInvalidActionExecution()
    {
        mockContext.checking(new Expectations()
        {
            {
                oneOf(springContextMock).getBean(ACTION_KEY);
                will(returnValue("String"));
            }
        });

        // Execute the request.
        ActionRequest results = sut.execute(defaultRequest, validUserDetailsMock);

        mockContext.assertIsSatisfied();
        Assert.assertTrue(results.getResponse() instanceof GeneralException);
    }

    /* ---- Test with a null parameter ---- */

    /**
     * Test execution with a ServiceAction.
     */
    @Test
    public final void testExecuteServiceActionNullParam()
    {
        setupSuccess(serviceAction);
        mockContext.checking(new Expectations()
        {
            {
                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(same(serviceAction)));
                will(returnValue(genericResult));
            }
        });

        ActionRequest results = sut.execute(nullParmRequest, validUserDetailsMock);

        mockContext.assertIsSatisfied();
        assertSame(genericClonedResult, results.getResponse());
        assertNull(results.getParam());
    }

    /**
     * Test execution with a ServiceAction.
     */
    @Test
    public final void testExecuteServiceActionExceptionNullParam()
    {
        mockContext.checking(new Expectations()
        {
            {
                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(same(serviceAction)));
                will(throwException(new ArithmeticException()));

                oneOf(springContextMock).getBean(ACTION_KEY);
                will(returnValue(serviceAction));

                oneOf(principalPopulator).getPrincipal(USERNAME, null);
            }
        });

        ActionRequest results = sut.execute(nullParmRequest, validUserDetailsMock);

        mockContext.assertIsSatisfied();
        assertTrue(results.getResponse() instanceof GeneralException);
        assertNull(results.getParam());
    }

    /**
     * Test execution with a TaskHandlerServiceAction.
     */
    @Test
    public final void testExecuteTaskHandlerServiceActionNullParam()
    {
        setupSuccess(serviceAction);
        mockContext.checking(new Expectations()
        {
            {
                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(same(serviceAction)));
                will(returnValue(genericResult));
            }
        });

        ActionRequest results = sut.execute(nullParmRequest, validUserDetailsMock);

        mockContext.assertIsSatisfied();
        assertSame(genericClonedResult, results.getResponse());
        assertNull(results.getParam());
    }

    /**
     * Test the scenario where the bean supplied for the action is not a valid Action type.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testExecuteNonActionNullParam()
    {
        mockContext.checking(new Expectations()
        {
            {
                oneOf(springContextMock).getBean(ACTION_KEY);
                will(returnValue("String"));
            }
        });

        ActionRequest results = sut.execute(nullParmRequest, validUserDetailsMock);

        mockContext.assertIsSatisfied();
        assertTrue(results.getResponse() instanceof GeneralException);
        assertNull(results.getParam());
    }

    /**
     * Test the scenario where the toString of the parameter throws an exception.
     */
    @SuppressWarnings("unchecked")
    public final void testExecuteWithFaultyToString()
    {
        setupSuccess(serviceAction);
        final Serializable requestParm = mockContext.mock(Serializable.class, "requestParm");
        mockContext.checking(new Expectations()
        {
            {
                allowing(requestParm).toString();
                will(throwException(new ArithmeticException()));

                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(same(serviceAction)));
                will(returnValue(genericResult));
            }
        });

        ActionRequest results = sut.execute(new ActionRequestImpl(ACTION_KEY, requestParm), validUserDetailsMock);

        mockContext.assertIsSatisfied();
        assertSame(genericClonedResult, results.getResponse());
        assertNull(results.getParam());
    }

    /**
     * Test the scenario where the toString of the parameter throws an exception.
     */
    @SuppressWarnings("unchecked")
    public final void testExecuteExceptionWithFaultyToString()
    {
        final Serializable requestParm = mockContext.mock(Serializable.class, "requestParm");
        mockContext.checking(new Expectations()
        {
            {
                allowing(requestParm).toString();
                will(throwException(new ArithmeticException()));

                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(same(serviceAction)));
                will(throwException(new ArithmeticException()));

                oneOf(springContextMock).getBean(ACTION_KEY);
                will(returnValue(serviceAction));

                oneOf(principalPopulator).getPrincipal(USERNAME, null);
            }
        });

        ActionRequest results = sut.execute(new ActionRequestImpl(ACTION_KEY, requestParm), validUserDetailsMock);

        mockContext.assertIsSatisfied();
        assertTrue(results.getResponse() instanceof GeneralException);
        assertNull(results.getParam());
    }

    /* ---- Test action exception handling for various types of exceptions ---- */

    /**
     * Common parts of tests that insure exceptions do not contain a nested cause.
     *
     * @param inputException
     *            Exception to be thrown.
     * @return Exception returned by SUT.
     */
    private Throwable coreForbidNestingExceptionTest(final Throwable inputException)
    {
        mockContext.checking(new Expectations()
        {
            {
                oneOf(springContextMock).getBean(ACTION_KEY);
                will(throwException(inputException));
            }
        });

        ActionRequest result = sut.execute(defaultRequest, validUserDetailsMock);

        mockContext.assertIsSatisfied();
        Serializable response = result.getResponse();
        assertTrue(response instanceof Throwable);
        Throwable outputException = (Throwable) response;
        assertNull(outputException.getCause());
        return outputException;
    }

    /**
     * Tests how exceptions are returned to client.
     */
    @Test
    public void testExecutionExceptionNested()
    {
        Throwable exIn = new ExecutionException(new ArithmeticException());
        Throwable exOut = coreForbidNestingExceptionTest(exIn);
        assertTrue(exOut instanceof ExecutionException);
        assertEquals(exIn.getMessage(), exOut.getMessage());
    }

    /**
     * Tests how exceptions are returned to client.
     */
    @Test
    public void testExecutionExceptionNonNested()
    {
        Throwable exIn = new ExecutionException();
        Throwable exOut = coreForbidNestingExceptionTest(exIn);
        assertSame(exIn, exOut);
    }

    /**
     * Tests how exceptions are returned to client.
     */
    @Test
    public void testGeneralExceptionNested()
    {
        Throwable exIn = new GeneralException(new ArithmeticException());
        Throwable exOut = coreForbidNestingExceptionTest(exIn);
        assertTrue(exOut instanceof GeneralException);
        assertEquals(exIn.getMessage(), exOut.getMessage());
    }

    /**
     * Tests how exceptions are returned to client.
     */
    @Test
    public void testGeneralExceptionNonNested()
    {
        Throwable exIn = new GeneralException();
        Throwable exOut = coreForbidNestingExceptionTest(exIn);
        assertSame(exIn, exOut);
    }

    /**
     * Tests how exceptions are returned to client.
     */
    @Test
    public void testAuthorizationExceptionNested()
    {
        Throwable exIn = new AuthorizationException(new ArithmeticException());
        Throwable exOut = coreForbidNestingExceptionTest(exIn);
        assertTrue(exOut instanceof AuthorizationException);
        assertEquals(exIn.getMessage(), exOut.getMessage());
    }

    /**
     * Tests how exceptions are returned to client.
     */
    @Test
    public void testAuthorizationExceptionNonNested()
    {
        Throwable exIn = new AuthorizationException();
        Throwable exOut = coreForbidNestingExceptionTest(exIn);
        assertSame(exIn, exOut);
    }

    /**
     * Tests how exceptions are returned to client.
     */
    @Test
    public void testValidationExceptionNonNested()
    {
        Throwable exIn = new ValidationException();
        Throwable exOut = coreForbidNestingExceptionTest(exIn);
        assertSame(exIn, exOut);
    }

    /**
     * Tests how exceptions are returned to client.
     */
    @Test
    public void testOtherExceptionNonNested()
    {
        Throwable exIn = new IllegalArgumentException("bad");
        Throwable exOut = coreForbidNestingExceptionTest(exIn);
        assertTrue(exOut instanceof GeneralException);
        assertEquals(exIn.getMessage(), exOut.getMessage());
    }

    /**
     * Tests how exceptions are returned to client.
     */
    @Test
    public void testOtherExceptionNested()
    {
        Throwable exIn = new IllegalArgumentException(new IllegalStateException("really bad"));
        Throwable exOut = coreForbidNestingExceptionTest(exIn);
        assertTrue(exOut instanceof GeneralException);
        assertEquals(exIn.getMessage(), exOut.getMessage());
    }
}
