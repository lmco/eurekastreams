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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * OAuthConsumer test class.
 */
public class OAuthConsumerTest
{
    /**
     * Test provider.
     */
    private final String testServiceProviderName = "oauthProvider1";
    /**
     * Test gadget url.
     */
    private final String testGadgetUrl = "http://localhost:4040/some/path/gadget.xml";
    /**
     * Test consumer key.
     */
    private final String testConsumerKey = "key";
    /**
     * Test consumer secret.
     */
    private final String testConsumerSecret = "secret";
    /**
     * Test signature method.
     */
    private final String testSignatureMethod = "HMAC-SHA1";
    /**
     * Test callback url.
     */
    private final String testCallbackUrl = "http://localhost:4040/oauth/callback";
    /**
     * Test token collection.
     */
    private final List<OAuthToken> testTokens = new ArrayList<OAuthToken>();
    /**
     * Test consumer title.
     */
    private final String testTitle = "Some Title";
    /**
     * Object under test.
     */
    private OAuthConsumer sut;

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut =
                new OAuthConsumer(testServiceProviderName, testGadgetUrl, testConsumerKey, testConsumerSecret,
                        testSignatureMethod);
        testTokens.add(new OAuthToken(new OAuthConsumer(), "", "", "", ""));
    }

    /**
     * Test service provider name getter/setter.
     */
    @Test
    public void setAndGetServiceProviderName()
    {
        sut.setServiceProviderName(testServiceProviderName);
        assertEquals("property should be gotten", testServiceProviderName, sut.getServiceProviderName());
    }

    /**
     * Test gadget url getter/setter.
     */
    @Test
    public void setAndGetGadgetUrl()
    {
        sut.setGadgetUrl(testGadgetUrl);
        assertEquals("property should be gotten", testGadgetUrl, sut.getGadgetUrl());
    }

    /**
     * Test consumer key getter/setter.
     */
    @Test
    public void setAndGetConsumerKey()
    {
        sut.setConsumerKey(testConsumerKey);
        assertEquals("property should be gotten", testConsumerKey, sut.getConsumerKey());
    }

    /**
     * Test consumer secret getter/setter.
     */
    @Test
    public void setAndGetConsumerSecret()
    {
        sut.setConsumerSecret(testConsumerSecret);
        assertEquals("property should be gotten", testConsumerSecret, sut.getConsumerSecret());
    }

    /**
     * Test signature method getter/setter.
     */
    @Test
    public void setAndGetSignatureMethod()
    {
        sut.setSignatureMethod(testSignatureMethod);
        assertEquals("property should be gotten", testSignatureMethod, sut.getSignatureMethod());
    }

    /**
     * Test callback url getter/setter.
     */
    @Test
    public void setAndGetCallbackUrl()
    {
        sut.setCallbackURL(testCallbackUrl);
        assertEquals("property should be gotten", testCallbackUrl, sut.getCallbackURL());
    }

    /**
     * Test tokens getter/setter.
     */
    @Test
    public void setAndGetTokens()
    {
        sut.setTokens(testTokens);
        assertEquals("property should be gotten", testTokens.size(), sut.getTokens().size());
    }

    /**
     * Test title getter/setter.
     */
    @Test
    public void setAndGetTitle()
    {
        sut.setTitle(testTitle);
        assertEquals("property should be gotten", testTitle, sut.getTitle());
    }
}
