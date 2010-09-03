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
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.opensocial.OAuthConsumerRequest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link GetOAuthConsumerByServiceName} mapper.
 * 
 */
public class GetOAuthConsumerByServiceNameTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetOAuthConsumerByServiceName sut;

    /**
     * Prep the sut.
     */
    @Before
    public void setup()
    {
        sut = new GetOAuthConsumerByServiceName();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test finding a consumer.
     */
    @Test
    public void testFindConsumerByServiceNameAndGadgetUrl()
    {
        OAuthConsumer consumer = sut
                .execute(new OAuthConsumerRequest("provider1", "http://www.example.com/gadget2.xml"));

        assertTrue(
                "No Consumer found for serviceName of 'provider1' and gadget url of http://www.example.com/gadget1.xml",
                consumer != null);

        // verify loaded attributes of consumer
        assertEquals("Incorrect consumer key returned", "key1", consumer.getConsumerKey());
        assertEquals("Incorrect consumer secret returned", "secret1", consumer.getConsumerSecret());
        assertEquals("Incorrect signature method", "HMAC-SHA1", consumer.getSignatureMethod());
    }

    /**
     * Test not finding a consumer.
     */
    @Test
    public void testNullFindConsumerByServiceNameAndGadgetUrl()
    {
        OAuthConsumer consumer = sut.execute(new OAuthConsumerRequest("providerX",
                "http://localhost:4040/some/path/gadgetX.xml"));

        assertTrue("Consumer found for serviceName of 'providerX' and gadget url of /some/path/gadgetX.xml",
                consumer == null);

    }
}
