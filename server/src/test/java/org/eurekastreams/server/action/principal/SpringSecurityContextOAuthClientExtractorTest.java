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

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.oauth.provider.ConsumerDetails;

/**
 * Test for SpringSecurityContextOAuthClientExtractor.
 *
 */
public class SpringSecurityContextOAuthClientExtractorTest
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

    /** Saved security context. */
    private SecurityContext originalSecurityContext;

    /** Security context. */
    private final SecurityContext securityContext = context.mock(SecurityContext.class);

    /** Authentication. */
    private final Authentication authentication = context.mock(Authentication.class);

    /** System under test. */
    private SpringSecurityContextOAuthClientExtractor sut;

    /**
     * Setup for test Suite.
     */
    @Before
    public void setup()
    {
        originalSecurityContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(securityContext);

        sut = new SpringSecurityContextOAuthClientExtractor();
    }

    /**
     * Clean up after test.
     */
    @After
    public void tearDown()
    {
        SecurityContextHolder.setContext(originalSecurityContext);
    }

    /**
     * Tests extracting.
     */
    @Test
    public void testTransformNullAuth()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(securityContext).getAuthentication();
                will(returnValue(null));
            }
        });

        assertNull(sut.transform(null));

        context.assertIsSatisfied();
    }


    /**
     * Tests extracting.
     */
    @Test
    public void testTransformNotOAuth()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(securityContext).getAuthentication();
                will(returnValue(authentication));

                oneOf(authentication).getPrincipal();
                will(returnValue("This is not the right type."));
            }
        });

        assertNull(sut.transform(null));

        context.assertIsSatisfied();
    }

    /**
     * Tests extracting.
     */
    @Test
    public void testTransform()
    {
        final ConsumerDetails details = context.mock(ConsumerDetails.class);
        final String key = "This is the key.";

        context.checking(new Expectations()
        {
            {
                oneOf(securityContext).getAuthentication();
                will(returnValue(authentication));

                oneOf(authentication).getPrincipal();
                will(returnValue(details));

                allowing(details).getConsumerKey();
                will(returnValue(key));
            }
        });

        assertEquals(key, sut.transform(null));

        context.assertIsSatisfied();
    }

    /**
     * Tests extracting.
     */
    @Test
    public void testTransformException()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(securityContext).getAuthentication();
                will(throwException(new Exception("BAD.")));
            }
        });

        assertNull(sut.transform(null));

        context.assertIsSatisfied();
    }
}
