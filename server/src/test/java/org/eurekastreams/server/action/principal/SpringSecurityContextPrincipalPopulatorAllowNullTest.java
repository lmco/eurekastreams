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
package org.eurekastreams.server.action.principal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.service.security.userdetails.ExtendedUserDetails;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.restlet.data.Cookie;
import org.restlet.data.Form;
import org.restlet.data.Request;
import org.restlet.util.Series;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;

/**
 * Test for SpringSecurityContextPrincipalPopulatorAllowNull.
 *
 */
public class SpringSecurityContextPrincipalPopulatorAllowNullTest
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

    /** Fixture: cookie list. */
    private final Series<Cookie> cookies = context.mock(Series.class);

    /** Fixture: cookie. */
    private final Cookie cookie = context.mock(Cookie.class);

    /** Fixture: headers. */
    private final Form headers = context.mock(Form.class);

    /**
     * Performs the core of running the test, including saving/restoring the security context.
     *
     * @param verifySession
     *            If the SUT should be configured to verify the session.
     * @return Result of invoking SUT.
     */
    private Principal runTest(final boolean verifySession)
    {
        // Save off the current security context, so that it can be reset when this test is complete.
        SecurityContext originalSecurityContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(securityContext);

        SpringSecurityContextPrincipalPopulatorAllowNull sut = new SpringSecurityContextPrincipalPopulatorAllowNull(
                verifySession, "JSESSIONID", "sessionid");

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

        assertNull(runTest(false));
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

        assertNull(runTest(false));
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
        assertNotNull(runTest(false));
    }

    /**
     * Test and verify session.
     */
    @Test
    public void testSuccessVerifySession()
    {
        final String sessionId = "123";
        final Map<String, Object> attribs = new HashMap<String, Object>();
        attribs.put("org.restlet.http.headers", headers);

        context.checking(new Expectations()
        {
            {
                oneOf(request).getCookies();
                will(returnValue(cookies));

                oneOf(cookies).getFirst("JSESSIONID");
                will(returnValue(cookie));

                oneOf(cookie).getValue();
                will(returnValue(sessionId));

                allowing(request).getAttributes();
                will(returnValue(attribs));

                allowing(headers).getFirstValue("sessionid", true);
                will(returnValue(sessionId));
            }
        });

        expectValidPrincipal();
        assertNotNull(runTest(true));
    }

    /**
     * Test and verify session.
     */
    @Test
    public void testVerifySessionWrongSession()
    {
        final String sessionId = "123";
        final Map<String, Object> attribs = new HashMap<String, Object>();
        attribs.put("org.restlet.http.headers", headers);

        context.checking(new Expectations()
        {
            {
                oneOf(request).getCookies();
                will(returnValue(cookies));

                oneOf(cookies).getFirst("JSESSIONID");
                will(returnValue(cookie));

                oneOf(cookie).getValue();
                will(returnValue(sessionId));

                allowing(request).getAttributes();
                will(returnValue(attribs));

                allowing(headers).getFirstValue("sessionid", true);
                will(returnValue("adsf"));
            }
        });

        expectValidPrincipal();
        assertNull(runTest(true));
    }

    /**
     * Test and verify session.
     */
    @Test
    public void testVerifySessionNoHeader()
    {
        final String sessionId = "123";
        final Map<String, Object> attribs = new HashMap<String, Object>();

        context.checking(new Expectations()
        {
            {
                oneOf(request).getCookies();
                will(returnValue(cookies));

                oneOf(cookies).getFirst("JSESSIONID");
                will(returnValue(cookie));

                oneOf(cookie).getValue();
                will(returnValue(sessionId));

                allowing(request).getAttributes();
                will(returnValue(attribs));
            }
        });

        expectValidPrincipal();
        assertNull(runTest(true));
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
}
