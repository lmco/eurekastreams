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

import static org.junit.Assert.assertSame;

import java.io.Serializable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import junit.framework.Assert;
import net.sf.gilead.core.PersistentBeanManager;

import org.eurekastreams.commons.client.ActionRequest;
import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.commons.exceptions.NoCredentialsException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.web.context.WebApplicationContext;

/**
 * Unit test for MasterServiceImpl.
 */
public class ActionRPCServiceImplTest
{
    /**
     * Subject under test.
     */
    private ActionRPCServiceImpl sut = null;

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
     * Mocked ServletConfig so we can pretend to be Spring and inject it into the SUT.
     */
    private ServletConfig servletConfig = context.mock(ServletConfig.class);

    /**
     * Mocked ServletContext for the SUT to retrieve from the ServletConfig.
     */
    private ServletContext servletContext = context.mock(ServletContext.class);

    /**
     * Mocked application context loaded from the ServletContext.
     */
    private WebApplicationContext webContext = context.mock(WebApplicationContext.class);

    /**
     * Used for GWT traffic while serializing persistent objects.
     */
    private PersistentBeanManager persistentBeanManager = context.mock(PersistentBeanManager.class);

    /**
     * Mocked instance of action executor factory.
     */
    private ActionExecutorFactory actionExecutorFactory = context.mock(ActionExecutorFactory.class);

    /**
     * Mocked instance of the action executor.
     */
    private ActionExecutor actionExecutor = context.mock(ActionExecutor.class);

    /**
     * Security context.
     */
    private SecurityContext securityContext;

    /**
     * Authentication.
     */
    private Authentication authentication = context.mock(Authentication.class);

    /**
     * User details.
     */
    private UserDetails userDetails = context.mock(UserDetails.class);

    /**
     * original security context - so we clean up the global namespace after we're done.
     */
    private SecurityContext originalSecurityContext;

    /**
     * current user's username.
     */
    private String username = "username";

    /**
     * Wire up the SUT with the mocked servlet configuration.
     */
    @Before
    public final void setup()
    {
        originalSecurityContext = SecurityContextHolder.getContext();

        securityContext = context.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        context.checking(new Expectations()
        {
            {
                oneOf(securityContext).getAuthentication();
                will(returnValue(authentication));

                oneOf(authentication).getPrincipal();
                will(returnValue(userDetails));

                allowing(userDetails).getUsername();
                will(returnValue(username));

                oneOf(servletConfig).getServletContext();
                will(returnValue(servletContext));

                oneOf(servletContext).getAttribute("org.springframework.web.context.WebApplicationContext.ROOT");
                will(returnValue(webContext));

                oneOf(webContext).getBean("persistentBeanManager");
                will(returnValue(persistentBeanManager));

                oneOf(webContext).getBean("actionExecutorFactory");
                will(returnValue(actionExecutorFactory));
            }
        });

        sut = new ActionRPCServiceImpl();
        sut.init(servletConfig);
    }

    /**
     * Teardown.
     */
    @After
    public final void tearDown()
    {
        SecurityContextHolder.setContext(originalSecurityContext);
    }

    /**
     * Tests getters.
     */
    @Test
    public void testGetters()
    {
        assertSame(webContext, sut.getSpringContext());
    }

    /**
     * Tests that execute() calls what it should and handles the successful response on one request.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testExecuteOneSuccess()
    {

        final String[] params = new String[] { "echo me" };
        final String response = "echo me";

        /**
         * The action key to be tested.
         */
        String serviceTarget = "echo";

        // set up request
        final ActionRequestImpl<String> request = new ActionRequestImpl<String>(serviceTarget, params);

        // set up the expected response
        final ActionRequestImpl expected = new ActionRequestImpl<String>(serviceTarget, params);
        expected.setResponse(response);

        context.checking(new Expectations()
        {
            {
                oneOf(actionExecutorFactory).getActionExecutor(with(any(ApplicationContext.class)),
                        with(any(UserDetails.class)), with(any(ActionRequest.class)));
                will(returnValue(actionExecutor));

                oneOf(actionExecutor).execute();
                will(returnValue(expected));
            }
        });

        // Execute the request.
        ActionRequest actual = sut.execute(request);

        // verify
        Assert.assertTrue(actual.getResponse() instanceof String);
        Assert.assertEquals(response, actual.getResponse());

        context.assertIsSatisfied();
    }

    /**
     * Tests that execute() works with a valid session.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testExecuteOneSuccessWithSession()
    {
        final String[] params = new String[] { "echo me" };
        final String response = "echo me";

        /**
         * The action key to be tested.
         */
        String serviceTarget = "echo";

        // set up request
        ActionRequestImpl<String> request = new ActionRequestImpl<String>(serviceTarget, params);

        // set up the expected response
        final ActionRequestImpl expected = new ActionRequestImpl<String>(serviceTarget, params);
        expected.setResponse(response);

        context.checking(new Expectations()
        {
            {
                oneOf(actionExecutorFactory).getActionExecutor(with(any(ApplicationContext.class)),
                        with(any(UserDetails.class)), with(any(ActionRequest.class)));
                will(returnValue(actionExecutor));

                oneOf(actionExecutor).execute();
                will(returnValue(expected));

            }
        });

        // Execute the request.
        ActionRequest actual = sut.execute(request);

        // verify
        Assert.assertTrue(actual.getResponse() instanceof String);
        Assert.assertEquals(response, actual.getResponse());

        context.assertIsSatisfied();
    }

    /**
     * When running multiple actions, one failure should be isolated and let the others continue.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testExecuteMultipleWithOneFailure()
    {
        final String[] params = new String[] { "echo me" };
        final String response = "echo me";
        ActionRequestImpl[] requests = new ActionRequestImpl[2];

        // set up invalid request
        requests[0] = new ActionRequestImpl<String>("fail", params);

        requests[1] = new ActionRequestImpl<String>("echo", params);
        final ActionRequestImpl response1 = new ActionRequestImpl<Serializable>("fail", null);
        response1.setResponse(new Exception());
        final ActionRequestImpl response2 = new ActionRequestImpl<Serializable>("echo", null);
        response2.setResponse(response);

        final ActionRequest[] responses = new ActionRequestImpl[2];
        responses[0] = response1;
        responses[1] = response2;

        context.checking(new Expectations()
        {
            {
                oneOf(actionExecutorFactory).getActionExecutor(with(any(ApplicationContext.class)),
                        with(any(UserDetails.class)), with(any(ActionRequest.class)));
                will(returnValue(actionExecutor));

                oneOf(actionExecutor).execute();
                will(returnValue(response1));

                // Multiple requests call the actionExecutorFactory
                // more than once to return multiple ActionExectors,
                // This seems inefficient, but the reason why is because the
                // ActionRPCServiceImpl checks each request in a batch submission and
                // allows for different session id's. I don't know if this is intentional
                // or not, but it would seem to me that the client shouldn't be able to submit
                // a batch with multiple session ids.
                // TODO: check on this behavior.
                oneOf(actionExecutorFactory).getActionExecutor(with(any(ApplicationContext.class)),
                        with(any(UserDetails.class)), with(any(ActionRequest.class)));
                will(returnValue(actionExecutor));

                oneOf(actionExecutor).execute();
                will(returnValue(response2));
            }
        });

        // Execute the request.
        ActionRequest[] actual = sut.execute(requests);

        // verify
        Assert.assertTrue("Invalid request should have responded with a Throwable.",
                actual[0].getResponse() instanceof Exception);

        Assert.assertTrue(actual[1].getResponse() instanceof String);
        Assert.assertEquals(response, actual[1].getResponse());

        context.assertIsSatisfied();
    }

    /**
     * Test execute of action with @RequiresCredentials annotation with no user logged in.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testExecuteSecuredActionNoLogin()
    {
        /**
         * The action key to be tested.
         */
        String serviceTarget = "echo";

        final String[] params = new String[] { "echo me" };

        // set up request
        ActionRequestImpl<String> request = new ActionRequestImpl<String>(serviceTarget, params);

        final ActionRequestImpl<Serializable> response = new ActionRequestImpl<Serializable>(serviceTarget, null);
        response.setResponse(new NoCredentialsException());

        context.checking(new Expectations()
        {
            {
                oneOf(actionExecutorFactory).getActionExecutor(with(any(ApplicationContext.class)),
                        with(any(UserDetails.class)), with(any(ActionRequest.class)));
                will(returnValue(actionExecutor));

                oneOf(actionExecutor).execute();
                will(returnValue(response));
            }
        });

        // Execute the request.
        ActionRequest actual = sut.execute(request);

        // verify
        Assert.assertTrue(actual.getResponse() instanceof NoCredentialsException);

        context.assertIsSatisfied();
    }

    /**
     * Test execute of action with @RequiresCredentials annotation with no user logged in.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testExecuteSecuredWithSession()
    {
        final String[] params = new String[] { "echo me" };
        final String response = "echo me";

        /**
         * The action key to be tested.
         */
        String serviceTarget = "echo";

        // set up request
        ActionRequestImpl<String> request = new ActionRequestImpl<String>(serviceTarget, params);

        // set up the expected response
        final ActionRequestImpl<String> expected = new ActionRequestImpl<String>(serviceTarget, params);
        expected.setResponse(response);

        context.checking(new Expectations()
        {
            {
                oneOf(actionExecutorFactory).getActionExecutor(with(any(ApplicationContext.class)),
                        with(any(UserDetails.class)), with(any(ActionRequest.class)));
                will(returnValue(actionExecutor));

                oneOf(actionExecutor).execute();
                will(returnValue(expected));
            }
        });

        // Execute the request.
        ActionRequest actual = sut.execute(request);

        // verify
        Assert.assertTrue(actual.getResponse() instanceof String);
        Assert.assertEquals(response, actual.getResponse());

        context.assertIsSatisfied();
    }
}
