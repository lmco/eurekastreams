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
package org.eurekastreams.server.service.restlets;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.Cookie;
import org.restlet.data.Request;
import org.restlet.resource.ResourceException;
import org.restlet.util.Series;

/**
 * Test class for EstablishSessionResource.
 */
public class EstablishSessionResourceTest
{
    /**
     * Subject under test.
     */
    private EstablishSessionResource sut;

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
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new EstablishSessionResource("JSESSIONID");
    }

    /**
     * Test getting the session.
     * 
     * @throws ResourceException
     *             not expected
     */
    @Test
    public void testBackgroundTypeGetSet() throws ResourceException
    {
        final Request request = context.mock(Request.class);
        final Series<Cookie> cookies = context.mock(Series.class);
        final Cookie cookie = context.mock(Cookie.class);
        final String sessionId = "123";

        context.checking(new Expectations()
        {
            {
                oneOf(request).getCookies();
                will(returnValue(cookies));

                oneOf(cookies).getFirst("JSESSIONID", true);
                will(returnValue(cookie));

                oneOf(cookie).getValue();
                will(returnValue(sessionId));
            }
        });

        sut.initParams(request);
        sut.represent(null);

        context.assertIsSatisfied();
    }
}
