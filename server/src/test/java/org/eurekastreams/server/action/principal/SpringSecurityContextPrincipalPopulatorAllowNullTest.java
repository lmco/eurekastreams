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

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.service.security.userdetails.ExtendedUserDetails;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
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
     * System under test.
     */
    private SpringSecurityContextPrincipalPopulatorAllowNull sut;

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
     * Setup for test Suite.
     */
    @Before
    public void setup()
    {
        sut = new SpringSecurityContextPrincipalPopulatorAllowNull(false, "JSESSIONID", "sessionid");
    }

    /**
     * Test.
     */
    @Test
    public void testNullAuthentication()
    {
        final SecurityContext securityContext = context.mock(SecurityContext.class);

        // Save off the current security context, so that it can be reset when this test is complete.
        SecurityContext originalSecurityContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(securityContext);

        context.checking(new Expectations()
        {
            {
                oneOf(securityContext).getAuthentication();
                will(returnValue(null));
            }
        });

        assertNull(sut.getPrincipal(null, null));
        context.assertIsSatisfied();

        SecurityContextHolder.setContext(originalSecurityContext);
    }

    /**
     * Test.
     */
    @Test
    public void testNullPrincipal()
    {
        final SecurityContext securityContext = context.mock(SecurityContext.class);
        final Authentication authentication = context.mock(Authentication.class);

        // Save off the current security context, so that it can be reset when this test is complete.
        SecurityContext originalSecurityContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(securityContext);

        context.checking(new Expectations()
        {
            {
                oneOf(securityContext).getAuthentication();
                will(returnValue(authentication));

                oneOf(authentication).getPrincipal();
                will(returnValue(null));
            }
        });

        assertNull(sut.getPrincipal(null, null));
        context.assertIsSatisfied();

        SecurityContextHolder.setContext(originalSecurityContext);
    }

    /**
     * Test.
     */
    @Test
    public void test()
    {
        final SecurityContext securityContext = context.mock(SecurityContext.class);
        final Authentication authentication = context.mock(Authentication.class);
        final ExtendedUserDetails extUserDetails = context.mock(ExtendedUserDetails.class);

        // Save off the current security context, so that it can be reset when this test is complete.
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
                will(returnValue("username"));

                allowing(extUserDetails).getPerson();
                will(returnValue(personMock));

                oneOf(personMock).getId();

                oneOf(personMock).getOpenSocialId();
            }
        });

        assertNotNull(sut.getPrincipal(null, null));
        context.assertIsSatisfied();

        SecurityContextHolder.setContext(originalSecurityContext);
    }

    /**
     * Test and verify session.
     */
    @Test
    public void testVerifySession()
    {
        SpringSecurityContextPrincipalPopulatorAllowNull sutVerify =
        //
        new SpringSecurityContextPrincipalPopulatorAllowNull(true, "JSESSIONID", "sessionid");

        final SecurityContext securityContext = context.mock(SecurityContext.class);
        final Authentication authentication = context.mock(Authentication.class);
        final ExtendedUserDetails extUserDetails = context.mock(ExtendedUserDetails.class);

        // Save off the current security context, so that it can be reset when this test is complete.
        SecurityContext originalSecurityContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(securityContext);

        final Request request = context.mock(Request.class);
        final Series<Cookie> cookies = context.mock(Series.class);
        final Cookie cookie = context.mock(Cookie.class);
        final String sessionId = "123";

        final Form headers = context.mock(Form.class);
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

                allowing(headers).getFirstValue("sessionid");
                will(returnValue(sessionId));

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

        assertNotNull(sutVerify.transform(request));
        context.assertIsSatisfied();

        SecurityContextHolder.setContext(originalSecurityContext);
    }

    /**
     * Test and verify session.
     */
    @Test
    public void testVerifySessionWrongSession()
    {
        SpringSecurityContextPrincipalPopulatorAllowNull sutVerify =
        //
        new SpringSecurityContextPrincipalPopulatorAllowNull(true, "JSESSIONID", "sessionid");

        final SecurityContext securityContext = context.mock(SecurityContext.class);
        final Authentication authentication = context.mock(Authentication.class);
        final ExtendedUserDetails extUserDetails = context.mock(ExtendedUserDetails.class);

        // Save off the current security context, so that it can be reset when this test is complete.
        SecurityContext originalSecurityContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(securityContext);

        final Request request = context.mock(Request.class);
        final Series<Cookie> cookies = context.mock(Series.class);
        final Cookie cookie = context.mock(Cookie.class);
        final String sessionId = "123";

        final Form headers = context.mock(Form.class);
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

                allowing(headers).getFirstValue("sessionid");
                will(returnValue("adsf"));
            }
        });

        assertNull(sutVerify.transform(request));
        context.assertIsSatisfied();

        SecurityContextHolder.setContext(originalSecurityContext);
    }

    /**
     * Test and verify session.
     */
    @Test
    public void testVerifySessionNoHeader()
    {
        SpringSecurityContextPrincipalPopulatorAllowNull sutVerify =
        //
        new SpringSecurityContextPrincipalPopulatorAllowNull(true, "JSESSIONID", "sessionid");

        final SecurityContext securityContext = context.mock(SecurityContext.class);
        final Authentication authentication = context.mock(Authentication.class);
        final ExtendedUserDetails extUserDetails = context.mock(ExtendedUserDetails.class);

        // Save off the current security context, so that it can be reset when this test is complete.
        SecurityContext originalSecurityContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(securityContext);

        final Request request = context.mock(Request.class);
        final Series<Cookie> cookies = context.mock(Series.class);
        final Cookie cookie = context.mock(Cookie.class);
        final String sessionId = "123";

        final Form headers = context.mock(Form.class);
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

        assertNull(sutVerify.transform(request));
        context.assertIsSatisfied();

        SecurityContextHolder.setContext(originalSecurityContext);
    }

    /**
     * Test.
     */
    @Test
    public void testExceptionCatch()
    {
        final SecurityContext securityContext = context.mock(SecurityContext.class);

        // Save off the current security context, so that it can be reset when this test is complete.
        SecurityContext originalSecurityContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(securityContext);

        context.checking(new Expectations()
        {
            {
                oneOf(securityContext).getAuthentication();
                will(throwException(new Exception()));
            }
        });

        assertNull(sut.getPrincipal(null, null));
        context.assertIsSatisfied();

        SecurityContextHolder.setContext(originalSecurityContext);
    }
}
