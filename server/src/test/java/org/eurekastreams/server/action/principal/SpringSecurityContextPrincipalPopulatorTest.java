/*
 * Copyright (c) 2011-2012 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.principal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.service.security.userdetails.ExtendedUserDetails;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.restlet.data.Request;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;

/**
 * Test for SpringSecurityContextPrincipalPopulator.
 */
public class SpringSecurityContextPrincipalPopulatorTest
{

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
     * Test person mock.
     */
    private final Person personMock = context.mock(Person.class);

    /** Fixture: security context. */
    private final SecurityContext securityContext = context.mock(SecurityContext.class);

    /** Fixture: servlet request. */
    private final Request request = context.mock(Request.class);

    /** Fixture: authentication. */
    private final Authentication authentication = context.mock(Authentication.class);

    /** Fixture: user details. */
    private final ExtendedUserDetails extUserDetails = context.mock(ExtendedUserDetails.class);

    /**
     * Performs the core of running the test, including saving/restoring the security context.
     * 
     * @param exceptionOnError
     *            If the SUT should be configured to throw exceptions on error.
     * @return Result of invoking SUT.
     */
    private Principal runTest(final boolean exceptionOnError)
    {
        // Save off the current security context, so that it can be reset when this test is complete.
        SecurityContext originalSecurityContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(securityContext);

        SpringSecurityContextPrincipalPopulator sut = new SpringSecurityContextPrincipalPopulator(
exceptionOnError);

        Principal result;
        try
        {
            result = sut.transform(request);
        }
        finally
        {
            SecurityContextHolder.setContext(originalSecurityContext);
        }
        context.assertIsSatisfied();

        return result;
    }

    /**
     * Sets up expectations for a valid principal.
     */
    private void expectValidPrincipal()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(securityContext).getAuthentication();
                will(returnValue(authentication));

                oneOf(authentication).getPrincipal();
                will(returnValue(extUserDetails));

                allowing(extUserDetails).getUsername();
                will(returnValue("username"));

                allowing(extUserDetails).getPerson();
                will(returnValue(personMock));

                oneOf(personMock).getId();

                oneOf(personMock).getOpenSocialId();
            }
        });
    }

    /**
     * Test.
     */
    @Test
    public void testNullAuthentication()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(securityContext).getAuthentication();
                will(returnValue(null));
            }
        });

        assertNull(runTest(true));
    }

    /**
     * Test.
     */
    @Test
    public void testNullPrincipal()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(securityContext).getAuthentication();
                will(returnValue(authentication));

                oneOf(authentication).getPrincipal();
                will(returnValue(null));
            }
        });

        assertNull(runTest(true));
    }

    /**
     * Test.
     */
    @Test
    public void testNullAuthenticationVerifySession()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(securityContext).getAuthentication();
                will(returnValue(null));
            }
        });

        assertNull(runTest(true));
    }

    /**
     * Test.
     */
    @Test
    public void testNullPrincipalVerifySession()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(securityContext).getAuthentication();
                will(returnValue(authentication));

                oneOf(authentication).getPrincipal();
                will(returnValue(null));
            }
        });

        assertNull(runTest(true));
    }

    /**
     * Test.
     */
    @Test
    public void testSuccess()
    {
        expectValidPrincipal();
        assertNotNull(runTest(true));
    }

    /**
     * Test.
     */
    @Test
    public void testExceptionCatch()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(securityContext).getAuthentication();
                will(throwException(new Exception()));
            }
        });

        assertNull(runTest(false));
    }

    /**
     * Test.
     */
    @Test(expected = AuthorizationException.class)
    public void testExceptionThrow()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(securityContext).getAuthentication();
                will(throwException(new Exception()));
            }
        });

        assertNull(runTest(true));
    }
}
