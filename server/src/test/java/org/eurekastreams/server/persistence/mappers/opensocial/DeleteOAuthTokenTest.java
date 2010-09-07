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

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.OAuthConsumer;
import org.eurekastreams.server.domain.OAuthToken;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.opensocial.OAuthTokenRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link DeleteOAuthToken} mapper.
 * 
 */
public class DeleteOAuthTokenTest extends MapperTest
{
    /**
     * System under test.
     */
    private DeleteOAuthToken sut;

    /**
     * Prep the sut.
     */
    @Before
    public void setup()
    {
        sut = new DeleteOAuthToken();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test deleting a token by consumer, viewer, and owner.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testDeleteByConsumerAndViewerAndOwner()
    {
        OAuthConsumer consumer = new OAuthConsumer("provider3", "http://www.example.com/gadget4.xml", "key3",
                "secret3", "HMAC-SHA1");
        sut.execute(new OAuthTokenRequest(consumer, "123", "456"));

        Query q = getEntityManager().createQuery(
                "from OAuthToken t where t.consumer.serviceProviderName = :serviceName"
                        + " and t.consumer.gadgetUrl = :gadgetUrl and t.viewerId = :viewerId and t.ownerId = :ownerId")
                .setParameter("serviceName", "provider3").setParameter("gadgetUrl",
                        "http://www.example.com/gadget4.xml").setParameter("viewerId", "123").setParameter("ownerId",
                        "456");

        List<OAuthToken> results = q.getResultList();
        Assert.assertEquals(0, results.size());
    }

    /**
     * Test deleting a nonexistent token.
     */
    @Test
    public void testDeleteNonexistentToken()
    {
        OAuthConsumer consumer = new OAuthConsumer("providerX", "http://www.example.com/gadgetX.xml", "keyX",
                "secretX", "HMAC-SHA1");
        Boolean result = sut.execute(new OAuthTokenRequest(consumer, "X", "Y"));

        Assert.assertEquals(true, result);
    }

}
