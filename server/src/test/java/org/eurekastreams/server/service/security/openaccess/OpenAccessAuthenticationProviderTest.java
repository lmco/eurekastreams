/*
 * Copyright (c) 2009-2012 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.security.openaccess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsChecker;
import org.springframework.security.userdetails.UserDetailsService;

/**
 * Test for the OpenAccessAuthenticationProvider.
 *
 */
public class OpenAccessAuthenticationProviderTest
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
     * Test for the Authenticate method.
     */
    @Test
    public void testAuthenticate()
    {
        final UsernamePasswordAuthenticationToken auth =
            context.mock(UsernamePasswordAuthenticationToken.class);
        final UserDetailsService uds = context.mock(UserDetailsService.class);
        final UserDetails ud = context.mock(UserDetails.class);
        final UserDetailsChecker checker = context.mock(UserDetailsChecker.class);

        context.checking(new Expectations()
        {
            {
                oneOf(auth).getName();
                will(returnValue("homers"));

                oneOf(uds).loadUserByUsername("homers");
                will(returnValue(ud));

                oneOf(auth).getCredentials();
                will(returnValue(null));

                oneOf(ud).getAuthorities();

                oneOf(checker).check(ud);
            }
        });

        OpenAccessAuthenticationProvider sut = new OpenAccessAuthenticationProvider(uds, checker);
        assertEquals("User details are not correct for passed in authentication",
                ud, sut.authenticate(auth).getPrincipal());
        context.assertIsSatisfied();
    }

    /**
     * Test for the Supports method.
     */
    @Test
    public void testSuports()
    {
        final UserDetailsService uds = context.mock(UserDetailsService.class);
        final UserDetailsChecker checker = context.mock(UserDetailsChecker.class);

        OpenAccessAuthenticationProvider sut = new OpenAccessAuthenticationProvider(uds, checker);
        assertTrue(sut.supports(null));
    }
}
