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
package org.eurekastreams.server.action.execution.opensocial;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry.Type;
import org.eurekastreams.server.domain.OAuthConsumer;
import org.eurekastreams.server.domain.OAuthDomainEntry;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for {@link OAuthEntryConversionStrategy}.
 *
 */
@SuppressWarnings("unchecked")
public class OAuthEntryConversionStrategyTest
{
    /**
     * System under test.
     */
    private OAuthEntryConversionStrategy sut;
    
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    /**
     * Instance of the {@link DomainMapper}.
     */
    private DomainMapper<String, OAuthConsumer> mapper = context.mock(DomainMapper.class);
    
    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new OAuthEntryConversionStrategy(mapper);
    }
    
    /**
     * Test successful conversion of an {@link OAuthDomainEntry} to an {@link OAuthEntry}.
     */
    @Test
    public void testSuccessfulDtoToEntry()
    {
        OAuthDomainEntry dto = new OAuthDomainEntry();
        dto.setAppId("appId");
        dto.setAuthorized(true);
        dto.setCallbackToken("callbacktoken");
        dto.setCallbackTokenAttempts(0);
        dto.setCallbackUrl("callbackurl");
        dto.setCallbackUrlSigned(true);
        dto.setConsumer(new OAuthConsumer(null, null, "key", null, null));
        dto.setContainer("container");
        dto.setDomain("domain");
        dto.setIssueTime(new Date());
        dto.setOauthVersion("1.0");
        dto.setToken("token");
        dto.setTokenSecret("tokensecret");
        dto.setType(Type.ACCESS.toString());
        dto.setUserId("userid");
                
        OAuthEntry entry = sut.convertToEntry(dto);
        
        assertEquals(entry.appId, dto.getAppId());
        assertEquals(entry.authorized, dto.isAuthorized());
        assertEquals(entry.callbackToken, dto.getCallbackToken());
        assertEquals(entry.callbackTokenAttempts, dto.getCallbackTokenAttempts());
        assertEquals(entry.callbackUrl, dto.getCallbackUrl());
        assertEquals(entry.callbackUrlSigned, dto.isCallbackUrlSigned());
        assertEquals(entry.consumerKey, dto.getConsumer().getConsumerKey());
        assertEquals(entry.container, dto.getContainer());
        assertEquals(entry.domain, dto.getDomain());
        assertEquals(entry.issueTime, dto.getIssueTime());
        assertEquals(entry.oauthVersion, dto.getOauthVersion());
        assertEquals(entry.token, dto.getToken());
        assertEquals(entry.tokenSecret, dto.getTokenSecret());
        assertEquals(entry.type, Type.valueOf(dto.getType()));
        assertEquals(entry.userId, dto.getUserId());

    }
    
    /**
     * Test successful conversion of an {@link OAuthEntry} to an {@link OAuthDomainEntry}.
     */
    @Test
    public void testSuccessfulEntryToDto()
    {
        OAuthEntry entry = new OAuthEntry();
        entry.appId = "appid";
        entry.authorized = true;
        entry.callbackToken = "callbacktoken";
        entry.callbackTokenAttempts = 0;
        entry.callbackUrl = "callbackurl";
        entry.callbackUrlSigned = true;
        entry.consumerKey = "testkey";
        entry.container = "container";
        entry.domain = "domain";
        entry.issueTime = new Date();
        entry.oauthVersion = "1.0";
        entry.token = "token";
        entry.tokenSecret = "secret";
        entry.type = Type.ACCESS;
        entry.userId = "userId";
        
        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute("testkey");
                will(returnValue(new OAuthConsumer(null, null, null, null, null)));
            }
        });

        OAuthDomainEntry dto = sut.convertToEntryDTO(entry);
        assertEquals(entry.appId, dto.getAppId());
        assertEquals(entry.authorized, dto.isAuthorized());
        assertEquals(entry.callbackToken, dto.getCallbackToken());
        assertEquals(entry.callbackTokenAttempts, dto.getCallbackTokenAttempts());
        assertEquals(entry.callbackUrl, dto.getCallbackUrl());
        assertEquals(entry.callbackUrlSigned, dto.isCallbackUrlSigned());
        assertEquals(entry.container, dto.getContainer());
        assertEquals(entry.domain, dto.getDomain());
        assertEquals(entry.issueTime, dto.getIssueTime());
        assertEquals(entry.oauthVersion, dto.getOauthVersion());
        assertEquals(entry.token, dto.getToken());
        assertEquals(entry.tokenSecret, dto.getTokenSecret());
        assertEquals(entry.type.toString(), dto.getType());
        assertEquals(entry.userId, dto.getUserId());
        context.assertIsSatisfied();
    }
}
