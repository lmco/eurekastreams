/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
import static org.junit.Assert.assertNotNull;
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
import org.springframework.context.ApplicationContext;
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
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mocked Application Spring context.
     */
    ApplicationContext springContextMock = context.mock(ApplicationContext.class);

    /**
     * Mocked UserDetails that is a valid user details object.
     */
    private final UserDetails validUserDetailsMock = context.mock(UserDetails.class);

    /**
     * Used for GWT traffic while serializing persistent objects.
     */
    private final PersistentBeanManager persistentBeanManager = context.mock(PersistentBeanManager.class);

    /**
     * Mocked PrincipalPopulator used to populate a principal object for the ServiceActionContext.
     */
    private final PrincipalPopulator principalPopulator = context.mock(PrincipalPopulator.class);

    /**
     * Mocked ServiceActionController used to execute ServiceActions.
     */
    private final ServiceActionController serviceActionController = context.mock(ServiceActionController.class);

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

    /**
     * The request action request object.
     */
    private ActionRequestImpl<String> defaultRequest = null;

    /**
     * .
     */
    @SuppressWarnings("unchecked")
    @Before
    public final void setup()
    {
        params = new String[] { "echo me" };

        // set up request
        defaultRequest = new ActionRequestImpl<String>(ACTION_KEY, params);

        context.checking(new Expectations()
        {
            {
                allowing(springContextMock).getBean("persistentBeanManager");
                will(returnValue(persistentBeanManager));

                allowing(springContextMock).getBean("principalPopulator");
                will(returnValue(principalPopulator));

                allowing(springContextMock).getBean("serviceActionController");
                will(returnValue(serviceActionController));

                allowing(validUserDetailsMock).getUsername();
                will(returnValue(USERNAME));
            }
        });

    }

    /**
     * Tests the getters.
     */
    @Test
    public void testGetters()
    {
        context.checking(new Expectations()
        {
            {
                ignoring(springContextMock);
            }
        });

        sut = new ActionExecutor(springContextMock, validUserDetailsMock, defaultRequest);

        assertNotNull("Log should not be null.", sut.getLog());
        assertSame(springContextMock, sut.getSpringContext());
        assertSame(validUserDetailsMock, sut.getUserDetails());

        context.assertIsSatisfied();
    }

    /**
     * Test execution with a ServiceAction.
     */
    @Test
    public final void testExecuteServiceAction()
    {
        final ServiceAction serviceActionMock = context.mock(ServiceAction.class);

        ActionRequestImpl<String> request = new ActionRequestImpl<String>("testkey", new String("testParam"));

        context.checking(new Expectations()
        {
            {
                oneOf(springContextMock).getBean("testkey");
                will(returnValue(serviceActionMock));

                oneOf(principalPopulator).getPrincipal(USERNAME, null);

                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));

                oneOf(persistentBeanManager).clone(with(any(String.class)));
            }
        });

        // Test the setter on the ActionRequest class.
        Assert.assertEquals(request.getParam(), "testParam");

        request.setParam(new String("anotherTestParam"));

        Assert.assertEquals(request.getParam(), "anotherTestParam");

        sut = new ActionExecutor(springContextMock, validUserDetailsMock, request);

        // Execute the request.
        sut.execute();

        context.assertIsSatisfied();
    }

    /**
     * Test execution with a ServiceAction.
     */
    @Test
    public final void testExecuteAsyncSubmitterServiceAction()
    {
        final TaskHandlerServiceAction serviceActionMock = context.mock(TaskHandlerServiceAction.class);

        ActionRequestImpl<String> request = new ActionRequestImpl<String>("testkey", new String("testParam"));

        context.checking(new Expectations()
        {
            {
                oneOf(springContextMock).getBean("testkey");
                will(returnValue(serviceActionMock));

                oneOf(principalPopulator).getPrincipal(USERNAME, null);

                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(any(TaskHandlerServiceAction.class)));

                oneOf(persistentBeanManager).clone(with(any(String.class)));
            }
        });

        // Test the setter on the ActionRequest class.
        Assert.assertEquals(request.getParam(), "testParam");

        request.setParam(new String("anotherTestParam"));

        Assert.assertEquals(request.getParam(), "anotherTestParam");

        sut = new ActionExecutor(springContextMock, validUserDetailsMock, request);

        // Execute the request.
        sut.execute();

        context.assertIsSatisfied();
    }

    /**
     * Test the scenario where the bean supplied for the action is not a valid Action type.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testInvalidActionExecution()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(springContextMock).getBean(ACTION_KEY);
                will(returnValue("String"));
            }
        });

        sut = new ActionExecutor(springContextMock, validUserDetailsMock, defaultRequest);

        // Execute the request.
        ActionRequest results = sut.execute();

        Assert.assertTrue(results.getResponse() instanceof GeneralException);

        context.assertIsSatisfied();
    }

    /**
     * Common parts of tests that insure exceptions do not contain a nested cause.
     * 
     * @param inputException
     *            Exception to be thrown.
     * @return Exception returned by SUT.
     */
    private Throwable coreForbidNestingExceptionTest(final Throwable inputException)
    {
        context.checking(new Expectations()
        {
            {
                oneOf(springContextMock).getBean(ACTION_KEY);
                will(throwException(inputException));
            }
        });

        sut = new ActionExecutor(springContextMock, validUserDetailsMock, defaultRequest);
        ActionRequest result = sut.execute();

        context.assertIsSatisfied();
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
        Throwable exIn = new ExecutionException(new NullPointerException());
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
        Throwable exIn = new GeneralException(new NullPointerException());
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
        Throwable exIn = new AuthorizationException(new NullPointerException());
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
