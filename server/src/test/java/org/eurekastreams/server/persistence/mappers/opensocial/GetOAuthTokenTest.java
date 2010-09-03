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
package org.eurekastreams.server.persistence.mappers.opensocial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.domain.OAuthConsumer;
import org.eurekastreams.server.domain.OAuthToken;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.opensocial.OAuthTokenRequest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link GetOAuthToken} mapper.
 */
public class GetOAuthTokenTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetOAuthToken sut;

    /**
     * Prep the sut.
     */
    @Before
    public void setup()
    {
        sut = new GetOAuthToken();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test finding a token.
     */
    @Test
    public void testFindTokenByServiceNameAndGadgetUrl()
    {
        OAuthConsumer consumer = new OAuthConsumer("provider1", "http://www.example.com/gadget2.xml", "key1",
                "secret1", "HMAC-SHA1");
        OAuthToken token = sut.execute(new OAuthTokenRequest(consumer, "123", "456"));

        assertTrue("No Token found for consumer 101 and viewer 123 and owner 456", token != null);

        // verify loaded attributes of token
        assertEquals("Incorrect access token returned", "accesstoken1", token.getAccessToken());
        assertEquals("Incorrect token secret returned", "accesssecret1", token.getTokenSecret());
    }

    /**
     * Test not finding a token.
     */
    @Test
    public void testFindNoTokenByServiceNameAndGadgetUrl()
    {
        OAuthConsumer consumer = new OAuthConsumer("provider1", "http://www.example.com/gadget2.xml", "key1",
                "secret1", "HMAC-SHA1");
        OAuthToken token = sut.execute(new OAuthTokenRequest(consumer, "111", "111"));

        assertTrue("Token found for consumer 101 and viewer 111 and owner 111", token == null);
    }

    /**
     * Test finding an expired token.
     */
    @Test
    public void testExpiredToken()
    {
        OAuthConsumer consumer = new OAuthConsumer("provider4", "http://www.example.com/gadget5.xml", "key4",
                "secret4", "HMAC-SHA1");
        OAuthToken token = sut.execute(new OAuthTokenRequest(consumer, "123", "456"));
        assertTrue("Non-expired Token found for consumer 104 and viewer 123 and owner 456", token == null);
    }
}
