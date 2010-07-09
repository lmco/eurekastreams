/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.security.persistentlogin;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eurekastreams.server.service.security.userdetails.ExtendedUserDetails;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.security.Authentication;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.eurekastreams.server.domain.PersistentLogin;
import org.springframework.security.ui.rememberme.InvalidCookieException;

/**
 * Test class for PersistentLoginService class.
 * 
 */
public class PersistentLoginServiceTest
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
     * Test constructor rejects null key.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullKey()
    {
        final PersistentLoginRepository loginRepo = context
                .mock(PersistentLoginRepository.class);
        final UserDetailsService userDetailsService = context
                .mock(UserDetailsService.class);
        new PersistentLoginService(null, userDetailsService, loginRepo);
    }

    /**
     * Test constructor rejects null UserDetailsService.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullUserDetailsService()
    {
        final PersistentLoginRepository loginRepo = context
                .mock(PersistentLoginRepository.class);
        new PersistentLoginService("blah", null, loginRepo);
    }

    /**
     * Test constructor rejects null PersistentLoginRepository.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullPersistentLoginRepository()
    {
        final UserDetailsService userDetailsService = context
                .mock(UserDetailsService.class);
        new PersistentLoginService("blah", userDetailsService, null);
    }

    /**
     * Test wrong number of cookie tokens causes exception.
     */
    @Test(expected = InvalidCookieException.class)
    public void testProcessAutoLoginCookieWrongNumTokens()
    {
        final PersistentLoginRepository loginRepo = context
                .mock(PersistentLoginRepository.class);
        final UserDetailsService userDetailsService = context
                .mock(UserDetailsService.class);
        final HttpServletRequest request = context
                .mock(HttpServletRequest.class);
        final HttpServletResponse response = context
                .mock(HttpServletResponse.class);
        final String[] cookieTokens = new String[] { "blah", "foo" };

        PersistentLoginService svc = new PersistentLoginService("key",
                userDetailsService, loginRepo);
        svc.processAutoLoginCookie(cookieTokens, request, response);
        context.assertIsSatisfied();
    }

    /**
     * Test wrong number format causes exception.
     */
    @Test(expected = InvalidCookieException.class)
    public void testProcessAutoLoginCookieWrongNumberFormat()
    {
        final PersistentLoginRepository loginRepo = context
                .mock(PersistentLoginRepository.class);
        final UserDetailsService userDetailsService = context
                .mock(UserDetailsService.class);
        final HttpServletRequest request = context
                .mock(HttpServletRequest.class);
        final HttpServletResponse response = context
                .mock(HttpServletResponse.class);
        final String[] cookieTokens = new String[] { "blah", "foo", "whatever" };

        PersistentLoginService svc = new PersistentLoginService("key",
                userDetailsService, loginRepo);
        svc.processAutoLoginCookie(cookieTokens, request, response);
        context.assertIsSatisfied();
    }

    /**
     * Test expired token causes exception.
     */
    @Test(expected = InvalidCookieException.class)
    public void testProcessAutoLoginCookieTokenExpired()
    {
        final int cookieDelay = 1000;
        
        final PersistentLoginRepository loginRepo = context
                .mock(PersistentLoginRepository.class);
        final UserDetailsService userDetailsService = context
                .mock(UserDetailsService.class);
        final HttpServletRequest request = context
                .mock(HttpServletRequest.class);
        final HttpServletResponse response = context
                .mock(HttpServletResponse.class);
        final String[] cookieTokens = new String[] { "blah",
                String.valueOf(System.currentTimeMillis() - cookieDelay), "whatever" };

        PersistentLoginService svc = new PersistentLoginService("key",
                userDetailsService, loginRepo);
        svc.processAutoLoginCookie(cookieTokens, request, response);
        context.assertIsSatisfied();
    }

    /**
     * Test no persistentLogin information found causes exception.
     */
    @Test(expected = InvalidCookieException.class)
    public void testProcessAutoLoginNoDataRecord()
    {
        final int cookieDelay = 30000;
        
        final PersistentLoginRepository loginRepo = context
                .mock(PersistentLoginRepository.class);
        final UserDetailsService userDetailsService = context
                .mock(UserDetailsService.class);
        final HttpServletRequest request = context
                .mock(HttpServletRequest.class);
        final HttpServletResponse response = context
                .mock(HttpServletResponse.class);
        final String[] cookieTokens = new String[] { "blah",
                String.valueOf(System.currentTimeMillis() + cookieDelay), "whatever" };
        final ExtendedUserDetails userDetails = context
                .mock(ExtendedUserDetails.class);

        context.checking(new Expectations()
        {
            {
                oneOf(userDetailsService).loadUserByUsername("blah");
                will(returnValue(userDetails));

                oneOf(userDetails).getPersistentLogin();
                will(returnValue(null));
            }
        });

        PersistentLoginService svc = new PersistentLoginService("key",
                userDetailsService, loginRepo);
        svc.processAutoLoginCookie(cookieTokens, request, response);
        context.assertIsSatisfied();
    }

    /**
     * Test expiration time mismatch causes exception.
     */
    @Test(expected = InvalidCookieException.class)
    public void testProcessAutoLoginExpiryTimeMismatch()
    {
        final long cookieDelay = 30000L;
                
        final PersistentLoginRepository loginRepo = context
                .mock(PersistentLoginRepository.class);
        final UserDetailsService userDetailsService = context
                .mock(UserDetailsService.class);
        final HttpServletRequest request = context
                .mock(HttpServletRequest.class);
        final HttpServletResponse response = context
                .mock(HttpServletResponse.class);
        final String[] cookieTokens = new String[] { "blah", "", "whatever" };
        final ExtendedUserDetails userDetails = context
                .mock(ExtendedUserDetails.class);
        final PersistentLogin login = context.mock(PersistentLogin.class);

        final long cookieTime = System.currentTimeMillis() + cookieDelay;
        cookieTokens[1] = String.valueOf(cookieTime);

        context.checking(new Expectations()
        {
            {
                oneOf(userDetailsService).loadUserByUsername("blah");
                will(returnValue(userDetails));

                oneOf(userDetails).getPersistentLogin();
                will(returnValue(login));

                oneOf(login).getTokenValue();
                will(returnValue("abc123"));

                oneOf(login).getTokenExpirationDate();
                will(returnValue((cookieTime + 4L)));
            }
        });

        PersistentLoginService svc = new PersistentLoginService("key",
                userDetailsService, loginRepo);
        svc.processAutoLoginCookie(cookieTokens, request, response);
        context.assertIsSatisfied();
    }

    /**
     * Test token signature mismatch causes exception.
     */
    @Test(expected = InvalidCookieException.class)
    public void testProcessAutoLoginTokenValueMismatch()
    {
        final long cookieDelay = 30000L;
        
        final PersistentLoginRepository loginRepo = context
                .mock(PersistentLoginRepository.class);
        final UserDetailsService userDetailsService = context
                .mock(UserDetailsService.class);
        final HttpServletRequest request = context
                .mock(HttpServletRequest.class);
        final HttpServletResponse response = context
                .mock(HttpServletResponse.class);
        final String[] cookieTokens = new String[] { "blah", "", "whatever" };
        final ExtendedUserDetails userDetails = context
                .mock(ExtendedUserDetails.class);
        final PersistentLogin login = context.mock(PersistentLogin.class);

        final long cookieTime = System.currentTimeMillis() + cookieDelay;
        cookieTokens[1] = String.valueOf(cookieTime);

        context.checking(new Expectations()
        {
            {
                oneOf(userDetailsService).loadUserByUsername("blah");
                will(returnValue(userDetails));

                oneOf(userDetails).getPersistentLogin();
                will(returnValue(login));

                oneOf(login).getTokenValue();
                will(returnValue("abc123"));

                oneOf(login).getTokenExpirationDate();
                will(returnValue((cookieTime)));
            }
        });

        PersistentLoginService svc = new PersistentLoginService("key",
                userDetailsService, loginRepo);
        svc.processAutoLoginCookie(cookieTokens, request, response);
        context.assertIsSatisfied();
    }

    /**
     * Test mapper is called correctly.
     */
    @Test
    public void testOnLoginSuccess()
    {
        final UserDetails userDetails = context.mock(UserDetails.class);
        final PersistentLoginRepository loginRepo = context
                .mock(PersistentLoginRepository.class);
        final UserDetailsService userDetailsService = context
                .mock(UserDetailsService.class);
        final HttpServletRequest request = context
                .mock(HttpServletRequest.class);
        final HttpServletResponse response = context
                .mock(HttpServletResponse.class);
        final Authentication auth = context.mock(Authentication.class);

        context.checking(new Expectations()
        {
            {
                exactly(2).of(auth).getPrincipal();
                will(returnValue(userDetails));

                oneOf(userDetails).getUsername();
                will(returnValue("username"));

                oneOf(loginRepo).createOrUpdatePersistentLogin(
                        with(any(PersistentLogin.class)));

                oneOf(request).getContextPath();
                will(returnValue(""));

                oneOf(response).addCookie(with(any(Cookie.class)));
            }
        });

        PersistentLoginService svc = new PersistentLoginService("key",
                userDetailsService, loginRepo);
        svc.onLoginSuccess(request, response, auth);
        context.assertIsSatisfied();
    }

    /**
     * Test mapper not called with empty username.
     */
    @Test
    public void testOnLoginSuccessEmptyUsername()
    {
        final UserDetails userDetails = context.mock(UserDetails.class);
        final PersistentLoginRepository loginRepo = context
                .mock(PersistentLoginRepository.class);
        final UserDetailsService userDetailsService = context
                .mock(UserDetailsService.class);
        final HttpServletRequest request = context
                .mock(HttpServletRequest.class);
        final HttpServletResponse response = context
                .mock(HttpServletResponse.class);
        final Authentication auth = context.mock(Authentication.class);

        context.checking(new Expectations()
        {
            {
                exactly(2).of(auth).getPrincipal();
                will(returnValue(userDetails));

                oneOf(userDetails).getUsername();
                will(returnValue(""));
            }
        });

        PersistentLoginService svc = new PersistentLoginService("key",
                userDetailsService, loginRepo);
        svc.onLoginSuccess(request, response, auth);
        context.assertIsSatisfied();
    }

    /**
     * Test exception is swallowed and setCookie is not called on mapper
     * exception.
     */
    @Test
    public void testOnLoginSuccessRepositoryException()
    {
        final UserDetails userDetails = context.mock(UserDetails.class);
        final PersistentLoginRepository loginRepo = context
                .mock(PersistentLoginRepository.class);
        final UserDetailsService userDetailsService = context
                .mock(UserDetailsService.class);
        final HttpServletRequest request = context
                .mock(HttpServletRequest.class);
        final HttpServletResponse response = context
                .mock(HttpServletResponse.class);
        final Authentication auth = context.mock(Authentication.class);

        context.checking(new Expectations()
        {
            {
                exactly(2).of(auth).getPrincipal();
                will(returnValue(userDetails));

                oneOf(userDetails).getUsername();
                will(returnValue("username"));

                oneOf(loginRepo).createOrUpdatePersistentLogin(
                        with(any(PersistentLogin.class)));
                will(throwException(new Exception()));
            }
        });

        PersistentLoginService svc = new PersistentLoginService("key",
                userDetailsService, loginRepo);
        svc.onLoginSuccess(request, response, auth);
        context.assertIsSatisfied();
    }

    /**
     * Test cancelCookie called and mapper called to remove data.
     */
    @Test
    public void testLogout()
    {
        final HttpServletRequest request = context
                .mock(HttpServletRequest.class);
        final HttpServletResponse response = context
                .mock(HttpServletResponse.class);
        final Authentication auth = context.mock(Authentication.class);
        final UserDetails userDetails = context.mock(UserDetails.class);
        final PersistentLoginRepository loginRepo = context
                .mock(PersistentLoginRepository.class);
        final UserDetailsService userDetailsService = context
                .mock(UserDetailsService.class);

        context.checking(new Expectations()
        {
            {
                oneOf(auth).getName();
                will(returnValue("foo"));

                oneOf(request).getContextPath();
                will(returnValue(""));

                oneOf(response).addCookie(with(any(Cookie.class)));

                oneOf(auth).getPrincipal();
                will(returnValue(userDetails));

                oneOf(userDetails).getUsername();
                will(returnValue("username"));

                oneOf(loginRepo).removePersistentLogin("username");
            }
        });

        PersistentLoginService svc = new PersistentLoginService("key",
                userDetailsService, loginRepo);
        svc.logout(request, response, auth);
        context.assertIsSatisfied();
    }

}
