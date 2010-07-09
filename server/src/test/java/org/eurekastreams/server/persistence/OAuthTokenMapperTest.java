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

import javax.persistence.NoResultException;

import org.eurekastreams.server.domain.OAuthConsumer;
import org.eurekastreams.server.domain.OAuthToken;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is responsible for testing the JPA Implementation of the OAuthToken Mapper interface. The tests contained
 * here ensure proper interaction with the database.
 */
public class OAuthTokenMapperTest extends DomainEntityMapperTest
{
    /**
     * JpaOauthTokenMapper - system under test.
     */
    @Autowired
    private OAuthTokenMapper jpaOAuthTokenMapper;

    /**
     * The consumer mapper.
     */
    @Autowired
    private OAuthConsumerMapper jpaOAuthConsumerMapper;

    /**
     * Test inserting a token.
     */
    @Test
    public void testInsert()
    {
        final long consumerId = 101L;
        OAuthConsumer consumer = jpaOAuthConsumerMapper.findById(consumerId);

        OAuthToken token = new OAuthToken(consumer, "123", "456", "accesstoken", "accesssecret");
        jpaOAuthTokenMapper.insert(token);
        long tokenId = token.getId();
        jpaOAuthTokenMapper.getEntityManager().clear();

        assertTrue("Inserting an OAuthToken did not get a positive id.",
                jpaOAuthTokenMapper.findById(tokenId).getId() > 0);
    }

    /**
     * Test deleting a token.
     */
    @Test(expected = NoResultException.class)
    public void testDelete()
    {
        final long tokenId = 105L;
        jpaOAuthTokenMapper.delete(tokenId);

        // if deleted, this should throw NoResultsException
        jpaOAuthTokenMapper.findById(tokenId);
    }

    /**
     * Test deleting a token by consumer, viewer, and owner.
     */
    @Test(expected = NoResultException.class)
    public void testDeleteByConsumerAndViewerAndOwner()
    {
        final long consumerId = 103L;
        OAuthConsumer consumer = jpaOAuthConsumerMapper.findById(consumerId);
        jpaOAuthTokenMapper.delete(consumer, "123", "456");

        // if deleted, this should throw NoResultsException
        final long tokenId = 106L;
        jpaOAuthTokenMapper.findById(tokenId);
    }

    /**
     * Test finding a token.
     */
    @Test
    public void testFindTokenByServiceNameAndGadgetUrl()
    {
        final long consumerId = 101L;
        OAuthConsumer consumer = jpaOAuthConsumerMapper.findById(consumerId);
        OAuthToken token = jpaOAuthTokenMapper.findToken(consumer, "123", "456");

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
        final long consumerId = 101L;
        OAuthConsumer consumer = jpaOAuthConsumerMapper.findById(consumerId);
        OAuthToken token = jpaOAuthTokenMapper.findToken(consumer, "111", "111");

        assertTrue("Token found for consumer 101 and viewer 111 and owner 111", token == null);
    }

    /**
     * Test finding an expired token.
     */
    @Test
    public void testExpiredToken()
    {
        final long consumerId = 104L;
        OAuthConsumer consumer = jpaOAuthConsumerMapper.findById(consumerId);
        OAuthToken token = jpaOAuthTokenMapper.findToken(consumer, "123", "456");
        assertTrue("Non-expired Token found for consumer 104 and viewer 123 and owner 456", token == null);
    }
}
