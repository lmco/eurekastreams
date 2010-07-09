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
package org.eurekastreams.server.service.actions.strategies.links;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Test fixture for ConnectionFacade.
 */
public class ConnectionFacadeTest
{
    /**
     * Test getter and setter for redirectCodes property.
     */
    @Test
    public void testRedirectCodesProperty()
    {
        ConnectionFacade sut = new ConnectionFacade();
        List<Integer> redirectCodes = new ArrayList<Integer>();
        sut.setRedirectCodes(redirectCodes);
        assertSame(redirectCodes, sut.getRedirectCodes());
    }

    /**
     * Test the getter and setter for the proxyPort property.
     */
    @Test
    public void testProxyPortProperty()
    {
        ConnectionFacade sut = new ConnectionFacade();
        String proxyPort = "1234";
        sut.setProxyPort(proxyPort);
        assertEquals(proxyPort, sut.getProxyPort());
    }

    /**
     * Test the getter and setter for the proxyHost property.
     */
    @Test
    public void testProxyHostProperty()
    {
        ConnectionFacade sut = new ConnectionFacade();
        String proxyHost = "some.proxy.host";
        sut.setProxyHost(proxyHost);
        assertEquals(proxyHost, sut.getProxyHost());
    }

    /**
     * Test the getter and setter for the proxyHost property.
     */
    @Test
    public void testUserAgentProperty()
    {
        ConnectionFacade sut = new ConnectionFacade();
        String userAgent = "SOME USER/AGENT";
        sut.setUserAgent(userAgent);
        assertEquals(userAgent, sut.getUserAgent());
    }

    /**
     * Test the connection timeout property.
     */
    @Test
    public void testConnectionTimeoutProperty()
    {
        ConnectionFacade sut = new ConnectionFacade();
        final int connectionTimeout = 834;
        sut.setConnectionTimeOut(connectionTimeout);
        assertEquals(connectionTimeout, sut.getConnectionTimeOut());
    }

    /**
     * Test the connection timeout property.
     */
    @Test
    public void testConnectionTimeoutPropertyAtLowerBoundary()
    {
        ConnectionFacade sut = new ConnectionFacade();
        final int connectionTimeout = 0;
        sut.setConnectionTimeOut(connectionTimeout);
        assertEquals(connectionTimeout, sut.getConnectionTimeOut());
    }

    /**
     * Test the connection timeout property.
     */
    @Test
    public void testConnectionTimeoutPropertyAtMaxBoundary()
    {
        ConnectionFacade sut = new ConnectionFacade();
        final int connectionTimeout = 30000;
        sut.setConnectionTimeOut(connectionTimeout);
        assertEquals(connectionTimeout, sut.getConnectionTimeOut());
    }


    /**
     * Test the connection timeout property.
     */
    @Test(expected = InvalidParameterException.class)
    public void testConnectionTimeoutPropertyLessThanMinValue()
    {
        ConnectionFacade sut = new ConnectionFacade();
        final int connectionTimeout = -1;
        sut.setConnectionTimeOut(connectionTimeout);
    }

    /**
     * Test the connection timeout property.
     */
    @Test(expected = InvalidParameterException.class)
    public void testConnectionTimeoutPropertyMoreThanMaxValue()
    {
        ConnectionFacade sut = new ConnectionFacade();
        final int connectionTimeout = 300001;
        sut.setConnectionTimeOut(connectionTimeout);
    }
}
