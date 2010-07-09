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
package org.eurekastreams.server.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.domain.OAuthConsumer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is responsible for testing the JPA Implementation of the OAuthConsumer Mapper interface. The tests
 * contained in here ensure proper interaction with the database.
 */
public class OAuthConsumerMapperTest extends DomainEntityMapperTest
{
    /**
     * JpaOauthConsumerMapper - system under test.
     */
    @Autowired
    private OAuthConsumerMapper jpaOAuthConsumerMapper;

    /**
     * Test inserting a consumer.
     */
    @Test
    public void testInsert()
    {
        OAuthConsumer consumer =
                new OAuthConsumer("provider", "http://localhost:4040/some/path/gadget.xml", "key", "secret",
                        "HMCA-SHA1");
        jpaOAuthConsumerMapper.insert(consumer);
        long consumerId = consumer.getId();
        jpaOAuthConsumerMapper.getEntityManager().clear();

        assertTrue("Inserting an OAuthConsumer did not get a positive id.", jpaOAuthConsumerMapper
                .findById(consumerId).getId() > 0);
    }

    /**
     * Test finding a consumer.
     */
    @Test
    public void testFindConsumerByServiceNameAndGadgetUrl()
    {
        OAuthConsumer consumer =
                jpaOAuthConsumerMapper.findConsumerByServiceNameAndGadgetUrl("provider1",
                        "http://localhost:4040/some/path/gadget1.xml");

        assertTrue("No Consumer found for serviceName of 'provider1' and gadget url of /some/path/gadget1.xml",
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
        OAuthConsumer consumer =
                jpaOAuthConsumerMapper.findConsumerByServiceNameAndGadgetUrl("providerX",
                        "http://localhost:4040/some/path/gadgetX.xml");

        assertTrue("Consumer found for serviceName of 'providerX' and gadget url of /some/path/gadgetX.xml",
                consumer == null);

    }

    /**
     * Test finding a consumer by key.
     */
    @Test
    public void testFindConsumerByKey()
    {
        OAuthConsumer consumer = jpaOAuthConsumerMapper.findConsumerByConsumerKey("key1");

        assertTrue("No Consumer found for key of 'key1'", consumer != null);

        // verify loaded attributes of consumer
        assertEquals("Incorrect consumer secret returned", "secret1", consumer.getConsumerSecret());
        assertEquals("Incorrect signature method", "HMAC-SHA1", consumer.getSignatureMethod());
    }
}
