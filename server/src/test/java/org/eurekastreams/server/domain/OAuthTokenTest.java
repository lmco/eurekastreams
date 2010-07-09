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
package org.eurekastreams.server.domain;

import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * OAuthToken test class.
 */
public class OAuthTokenTest
{
    /**
     * Test consumer.
     */
    private final OAuthConsumer testConsumer =
            new OAuthConsumer("provider", "http://localhost:4040/some/path/gadget.xml", "key", "secret", "HMAC-SHA1");
    /**
     * Test viewer id.
     */
    private final String testViewerId = "123";
    /**
     * Test owner id.
     */
    private final String testOwnerId = "456";
    /**
     * Test access token.
     */
    private final String testAccessToken = "accesstoken";
    /**
     * Test token secret.
     */
    private final String testTokenSecret = "accesssecret";
    /**
     * Test token expire milliseconds.
     */
    private final Long testTokenExpireMillis = new Long(0);

    /**
     * Object under test.
     */
    private OAuthToken sut;

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new OAuthToken(testConsumer, testViewerId, testOwnerId, testAccessToken, testTokenSecret);
    }

    /**
     * Test viewer id getter/setter.
     */
    @Test
    public void setAndGetViewerId()
    {
        sut.setViewerId(testViewerId);
        assertEquals("property should be gotten", testViewerId, sut.getViewerId());
    }

    /**
     * Test owner id getter/setter.
     */
    @Test
    public void setAndGetOwnerId()
    {
        sut.setOwnerId(testOwnerId);
        assertEquals("property should be gotten", testOwnerId, sut.getOwnerId());
    }

    /**
     * Test access token getter/setter.
     */
    @Test
    public void setAndGetAccessToken()
    {
        sut.setAccessToken(testAccessToken);
        assertEquals("property should be gotten", testAccessToken, sut.getAccessToken());
    }

    /**
     * Test token secret getter/setter.
     */
    @Test
    public void setAndGetTokenSecret()
    {
        sut.setTokenSecret(testTokenSecret);
        assertEquals("property should be gotten", testTokenSecret, sut.getTokenSecret());
    }

    /**
     * Test expire milliseconds getter/setter.
     */
    @Test
    public void setAndGetTokenExpireMillis()
    {
        sut.setTokenExpireMillis(testTokenExpireMillis);
        assertEquals("property should be gotten", testTokenExpireMillis, sut.getTokenExpireMillis());
    }

    /**
     * Test consumer getter/setter.
     */
    @Test
    public void setAndGetConsumer()
    {
        sut.setConsumer(testConsumer);
        assertEquals("property should be gotten", testConsumer, sut.getConsumer());
    }
}
