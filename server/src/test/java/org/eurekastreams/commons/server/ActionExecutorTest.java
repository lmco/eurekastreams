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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import net.sf.gilead.core.PersistentBeanManager;

import org.eurekastreams.commons.actions.context.PrincipalPopulator;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.actions.service.TaskHandlerServiceAction;
import org.eurekastreams.commons.client.ActionRequest;
import org.eurekastreams.commons.client.ActionRequestImpl;
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
    private UserDetails validUserDetailsMock = context.mock(UserDetails.class);

    /**
     * Used for GWT traffic while serializing persistent objects.
     */
    private PersistentBeanManager persistentBeanManager = context.mock(PersistentBeanManager.class);

    /**
     * Mocked PrincipalPopulator used to populate a principal object for the ServiceActionContext.
     */
    private PrincipalPopulator principalPopulator = context.mock(PrincipalPopulator.class);

    /**
     * Mocked ServiceActionController used to execute ServiceActions.
     */
    private ServiceActionController serviceActionController = context.mock(ServiceActionController.class);

    /**
     * The params array of Strings.
     */
    private String[] params = null;

    /**
     * The response String.
     */
    private String response = null;

    /**
     * The service target.
     */
    private String serviceTarget = null;

    /**
     * Test username for the suite.
     */
    private static final String USERNAME = "testuser";

    /**
     * The request action request object.
     */
    ActionRequestImpl<String> request = null;

    /**
     * The action request object complete with response as the expected resulting object.
     */
    @SuppressWarnings("unchecked")
    ActionRequestImpl expected = null;

    /**
     * .
     */
    @SuppressWarnings("unchecked")
    @Before
    public final void setup()
    {
        params = new String[] { "echo me" };
        response = "echo me";

        serviceTarget = "echo";

        // set up request
        request = new ActionRequestImpl<String>(serviceTarget, params);

        // set up the expected response
        expected = new ActionRequestImpl<String>(serviceTarget, params);
        expected.setResponse(response);

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

        sut = new ActionExecutor(springContextMock, validUserDetailsMock, request);

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

        request = new ActionRequestImpl<String>("testkey", new String("testParam"));

        context.checking(new Expectations()
        {
            {
                oneOf(springContextMock).getBean(with(any(String.class)));
                will(returnValue(serviceActionMock));

                oneOf(principalPopulator).getPrincipal(USERNAME);

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

        request = new ActionRequestImpl<String>("testkey", new String("testParam"));

        context.checking(new Expectations()
        {
            {
                oneOf(springContextMock).getBean(with(any(String.class)));
                will(returnValue(serviceActionMock));

                oneOf(principalPopulator).getPrincipal(USERNAME);

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
                oneOf(springContextMock).getBean(with(any(String.class)));
                will(returnValue("String"));
            }
        });

        sut = new ActionExecutor(springContextMock, validUserDetailsMock, request);

        // Execute the request.
        ActionRequest results = sut.execute();

        Assert.assertTrue(results.getResponse() instanceof IllegalArgumentException);

        context.assertIsSatisfied();
    }

}
