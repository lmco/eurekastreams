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

import java.util.Date;

import javax.persistence.NoResultException;

import org.eurekastreams.server.domain.OAuthDomainEntry;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is responsible for testing the JPA Implementation of the OAuthEntry Mapper interface. The tests contained
 * in here ensure proper interaction with the database.
 */
public class OAuthEntryMapperTest extends DomainEntityMapperTest
{
    /**
     * JpaOauthEntryMapper - system under test.
     */
    @Autowired
    private OAuthEntryMapper jpaOAuthEntryMapper;

    /**
     * The consumer mapper.
     */
    @Autowired
    private OAuthConsumerMapper consumerMapper;

    /**
     * Test inserting an entry.
     */
    @Test
    public void testInsert()
    {
        OAuthDomainEntry entry = new OAuthDomainEntry();
        entry.setAppId("appId");
        entry.setAuthorized(false);
        entry.setCallbackToken("callbackToken");
        entry.setCallbackTokenAttempts(0);
        entry.setCallbackUrl("http://localhost:8080/gadgets/oauthcallback");
        entry.setCallbackUrlSigned(true);
        entry.setConsumer(consumerMapper.findConsumerByConsumerKey("key1"));
        entry.setContainer("container");
        entry.setDomain("domain");
        entry.setIssueTime(new Date());
        entry.setOauthVersion("1.0");
        entry.setToken("token");
        entry.setTokenSecret("tokenSecret");
        entry.setType("REQUEST");
        entry.setUserId("userId");

        jpaOAuthEntryMapper.insert(entry);

        long entryId = entry.getId();
        jpaOAuthEntryMapper.getEntityManager().clear();

        assertTrue("Inserting an OAuthEntry did not get a positive id.",
                jpaOAuthEntryMapper.findById(entryId).getId() > 0);
    }

    /**
     * Test finding an entry.
     */
    public void testFindEntry()
    {
        OAuthDomainEntry entry = jpaOAuthEntryMapper.findEntry("token1");

        assertTrue("No Entry found for token of 'token1'", entry != null);

        // verify loaded attributes of consumer
        assertEquals("Incorrect callback URL returned", "http://localhost:8080/gadgets/oauthcallback", entry
                .getCallbackUrl());
        assertEquals("Incorrect app id returned", "application1", entry.getAppId());
        assertEquals("Incorrect container returned", "container1", entry.getContainer());
    }

    /**
     * Test not finding an entry.
     */
    public void testFindNullEntry()
    {
        OAuthDomainEntry entry = jpaOAuthEntryMapper.findEntry("tokenX");
        assertTrue("Entry found for token of 'tokenX'", entry == null);
    }

    /**
     * Test deleting an entry.
     */
    @Test(expected = NoResultException.class)
    public void testDeleteEntry()
    {
        final long entryId = 102L;
        jpaOAuthEntryMapper.delete(entryId);

        // if deleted, this should throw NoResultsException
        jpaOAuthEntryMapper.findById(entryId);
    }

    /**
     * Test deleting an entry given the token.
     */
    @Test(expected = NoResultException.class)
    public void testDeleteEntryByToken()
    {
        final long entryId = 103L;
        jpaOAuthEntryMapper.delete("token3");

        // if deleted, this should throw NoResultsException
        jpaOAuthEntryMapper.findById(entryId);
    }
}
