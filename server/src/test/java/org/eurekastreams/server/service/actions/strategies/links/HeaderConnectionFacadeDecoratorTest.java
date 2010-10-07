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
package org.eurekastreams.server.service.actions.strategies.links;

import java.net.HttpURLConnection;
import java.util.HashMap;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the {@link HeadersConnectionFacadeDecorator} class.
 *
 */
public class HeaderConnectionFacadeDecoratorTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    /**
     * Mocked {@link HttpURLConnection}.
     */
    private HttpURLConnection urlConnectionMock = context.mock(HttpURLConnection.class);
    
    /**
     * System under test.
     */
    private HeadersConnectionFacadeDecorator sut;

    /**
     * Test header values.
     */
    private HashMap<String, String> headerValues;
    
    /**
     * Prepare the systme under test.
     */
    @Before
    public void setup()
    {
        headerValues = new HashMap<String, String>();
        headerValues.put("User-Agent", "useragent");
        sut = new HeadersConnectionFacadeDecorator(headerValues);
    }
    
    /**
     * Test the successful addition of new headers.
     */
    @Test
    public void testAddHeaders()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(urlConnectionMock).addRequestProperty("User-Agent", "useragent");
            }
        });
        
        sut.decorate(urlConnectionMock, "testAccount");
        context.assertIsSatisfied();
    }
}
