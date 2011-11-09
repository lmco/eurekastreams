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
import org.eurekastreams.commons.exceptions.GeneralException;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
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
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ActionExecutorTest
{
    /** Subject under test. */
    private ActionExecutor sut = null;

    /** Context for building mock objects. */
    private final Mockery mockery = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Mocked Application Spring context. */
    private final BeanFactory springContextMock = mockery.mock(BeanFactory.class);

    /** Mocked UserDetails that is a valid user details object. */
    private final UserDetails validUserDetailsMock = mockery.mock(UserDetails.class);

    /** Used for GWT traffic while serializing persistent objects. */
    private final PersistentBeanManager persistentBeanManager = mockery.mock(PersistentBeanManager.class);

    /** Mocked PrincipalPopulator used to populate a principal object for the ServiceActionContext. */
    private final PrincipalPopulator principalPopulator = mockery.mock(PrincipalPopulator.class);

    /** Mocked ServiceActionController used to execute ServiceActions. */
    private final ServiceActionController serviceActionController = mockery.mock(ServiceActionController.class);

    /** The params array of Strings. */
    private String[] params = null;

    /** The service target. */
    private static final String ACTION_KEY = "actionToExecute";

    /** Test username for the suite. */
    private static final String USERNAME = "testuser";

    /** Action request object. */
    private ActionRequestImpl<String> defaultRequest = null;

    /** Action request object. */
    private ActionRequestImpl<String> nullParmRequest = null;

    /** Fixture: action bean to execute. */
    private final ServiceAction serviceAction = mockery.mock(ServiceAction.class, "serviceAction");

    /** Fixture: action bean to execute. */
    private final TaskHandlerServiceAction taskHandlerAction = mockery.mock(TaskHandlerServiceAction.class,
            "taskHandlerAction");

    /** Fixture: generic action result. */
    private final Serializable genericResult = mockery.mock(Serializable.class, "genericResult");

    /** Fixture: generic action result. */
    private final Serializable genericClonedResult = mockery.mock(Serializable.class, "genericClonedResult");

    /** Fixture: exception sanitizer. */
    private final Transformer exceptionSanitizer = mockery.mock(Transformer.class, "name");

    /** Fixture: returnException. */
    private final Exception returnException = mockery.mock(Exception.class, "returnException");

    /**
     * Setup before each test.
     */
    @SuppressWarnings("unchecked")
    @Before
    public void setUp()
    {
        sut = new ActionExecutor(springContextMock, serviceActionController, principalPopulator,
                persistentBeanManager, exceptionSanitizer);

        params = new String[] { "echo me" };

        // set up requests
        defaultRequest = new ActionRequestImpl<String>(ACTION_KEY, params);
        nullParmRequest = new ActionRequestImpl<String>(ACTION_KEY, null);

        mockery.checking(new Expectations()
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
        mockery.checking(new Expectations()
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
        mockery.checking(new Expectations()
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

        mockery.assertIsSatisfied();
    }

    /**
     * Test execution with a TaskHandlerServiceAction.
     */
    @Test
    public final void testExecuteAsyncSubmitterServiceAction()
    {
        setupSuccess(taskHandlerAction);
        mockery.checking(new Expectations()
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

        mockery.assertIsSatisfied();
    }

    /**
     * Test the scenario where the bean supplied for the action is not a valid Action type.
     */
    @Test
    public final void testInvalidActionExecution()
    {
        mockery.checking(new Expectations()
        {
            {
                oneOf(springContextMock).getBean(ACTION_KEY);
                will(returnValue("String"));

                allowing(exceptionSanitizer).transform(with(any(Exception.class)));
                will(returnValue(returnException));
            }
        });

        // Execute the request.
        ActionRequest results = sut.execute(defaultRequest, validUserDetailsMock);

        mockery.assertIsSatisfied();
        assertSame(returnException, results.getResponse());
    }

    /* ---- Test with a null parameter ---- */

    /**
     * Test execution with a ServiceAction.
     */
    @Test
    public final void testExecuteServiceActionNullParam()
    {
        setupSuccess(serviceAction);
        mockery.checking(new Expectations()
        {
            {
                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(same(serviceAction)));
                will(returnValue(genericResult));
            }
        });

        ActionRequest results = sut.execute(nullParmRequest, validUserDetailsMock);

        mockery.assertIsSatisfied();
        assertSame(genericClonedResult, results.getResponse());
        assertNull(results.getParam());
    }

    /**
     * Test execution with a ServiceAction.
     */
    @Test
    public final void testExecuteServiceActionExceptionNullParam()
    {
        mockery.checking(new Expectations()
        {
            {
                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(same(serviceAction)));
                will(throwException(new ArithmeticException()));

                oneOf(springContextMock).getBean(ACTION_KEY);
                will(returnValue(serviceAction));

                oneOf(principalPopulator).getPrincipal(USERNAME, null);

                allowing(exceptionSanitizer).transform(with(any(ArithmeticException.class)));
                will(returnValue(returnException));
            }
        });

        ActionRequest results = sut.execute(nullParmRequest, validUserDetailsMock);

        mockery.assertIsSatisfied();
        assertSame(returnException, results.getResponse());
        assertNull(results.getParam());
    }

    /**
     * Test execution with a TaskHandlerServiceAction.
     */
    @Test
    public final void testExecuteTaskHandlerServiceActionNullParam()
    {
        setupSuccess(serviceAction);
        mockery.checking(new Expectations()
        {
            {
                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(same(serviceAction)));
                will(returnValue(genericResult));
            }
        });

        ActionRequest results = sut.execute(nullParmRequest, validUserDetailsMock);

        mockery.assertIsSatisfied();
        assertSame(genericClonedResult, results.getResponse());
        assertNull(results.getParam());
    }

    /**
     * Test the scenario where the bean supplied for the action is not a valid Action type.
     */
    @Test
    public final void testExecuteNonActionNullParam()
    {
        mockery.checking(new Expectations()
        {
            {
                oneOf(springContextMock).getBean(ACTION_KEY);
                will(returnValue("String"));

                allowing(exceptionSanitizer).transform(with(any(Exception.class)));
                will(returnValue(returnException));
            }
        });

        ActionRequest results = sut.execute(nullParmRequest, validUserDetailsMock);

        mockery.assertIsSatisfied();
        assertSame(returnException, results.getResponse());
        assertNull(results.getParam());
    }

    /**
     * Test the scenario where the toString of the parameter throws an exception.
     */
    public final void testExecuteWithFaultyToString()
    {
        setupSuccess(serviceAction);
        final Serializable requestParm = mockery.mock(Serializable.class, "requestParm");
        mockery.checking(new Expectations()
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

        mockery.assertIsSatisfied();
        assertSame(genericClonedResult, results.getResponse());
        assertNull(results.getParam());
    }

    /**
     * Test the scenario where the toString of the parameter throws an exception.
     */
    public final void testExecuteExceptionWithFaultyToString()
    {
        final Serializable requestParm = mockery.mock(Serializable.class, "requestParm");
        mockery.checking(new Expectations()
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

        mockery.assertIsSatisfied();
        assertTrue(results.getResponse() instanceof GeneralException);
        assertNull(results.getParam());
    }
}
