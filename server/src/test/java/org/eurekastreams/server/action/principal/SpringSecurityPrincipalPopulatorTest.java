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
package org.eurekastreams.server.action.principal;

import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.service.security.userdetails.ExtendedUserDetails;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;

/**
 * This class contains the test suite for the {@link SpringSecurityPrincipalPopulator}.
 *
 */
public class SpringSecurityPrincipalPopulatorTest
{
    /**
     * System under test.
     */
    private SpringSecurityPrincipalPopulator sut;

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

    /**
     * Test account id.
     */
    private static final String ACCOUNT_ID = "testAccountId";

    /**
     * Setup for test Suite.
     */
    @Before
    public void setup()
    {
        sut = new SpringSecurityPrincipalPopulator();
    }

    /**
     * Test the successful retrieval of the principal object.
     */
    @Test
    public void testSuccessGetPrincipal()
    {
        final SecurityContext securityContext = context.mock(SecurityContext.class);
        final Authentication authentication = context.mock(Authentication.class);
        final ExtendedUserDetails extUserDetails = context.mock(ExtendedUserDetails.class);

        //Save off the current security context, so that it can be reset when this test is complete.
        SecurityContext originalSecurityContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(securityContext);

        context.checking(new Expectations()
        {
            {
                oneOf(securityContext).getAuthentication();
                will(returnValue(authentication));

                oneOf(authentication).getPrincipal();
                will(returnValue(extUserDetails));

                allowing(extUserDetails).getUsername();
                will(returnValue(ACCOUNT_ID));

                allowing(extUserDetails).getPerson();
                will(returnValue(personMock));

                oneOf(personMock).getId();

                oneOf(personMock).getOpenSocialId();
            }
        });

        sut.getPrincipal(ACCOUNT_ID);
        context.assertIsSatisfied();

        SecurityContextHolder.setContext(originalSecurityContext);
    }

    /**
     * Test the failure of retrieving the principal object.
     */
    @Test(expected = AuthorizationException.class)
    public void testFailureGetPrincipal()
    {
        final SecurityContext securityContext = context.mock(SecurityContext.class);
        final Authentication authentication = context.mock(Authentication.class);

        //Save off the current security context, so that it can be reset when this test is complete.
        SecurityContext originalSecurityContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(securityContext);

        context.checking(new Expectations()
        {
            {
                oneOf(securityContext).getAuthentication();
                will(returnValue(authentication));

                oneOf(authentication).getPrincipal();
                will(throwException(new Exception()));
            }
        });

        sut.getPrincipal(ACCOUNT_ID);
        context.assertIsSatisfied();

        SecurityContextHolder.setContext(originalSecurityContext);
    }
}
