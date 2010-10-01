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
        
        assertEquals(entry.getAppId(), dto.getAppId());
        assertEquals(entry.isAuthorized(), dto.isAuthorized());
        assertEquals(entry.getCallbackToken(), dto.getCallbackToken());
        assertEquals(entry.getCallbackTokenAttempts(), dto.getCallbackTokenAttempts());
        assertEquals(entry.getCallbackUrl(), dto.getCallbackUrl());
        assertEquals(entry.isCallbackUrlSigned(), dto.isCallbackUrlSigned());
        assertEquals(entry.getConsumerKey(), dto.getConsumer().getConsumerKey());
        assertEquals(entry.getContainer(), dto.getContainer());
        assertEquals(entry.getDomain(), dto.getDomain());
        assertEquals(entry.getIssueTime(), dto.getIssueTime());
        assertEquals(entry.getOauthVersion(), dto.getOauthVersion());
        assertEquals(entry.getToken(), dto.getToken());
        assertEquals(entry.getTokenSecret(), dto.getTokenSecret());
        assertEquals(entry.getType(), Type.valueOf(dto.getType()));
        assertEquals(entry.getUserId(), dto.getUserId());

    }
    
    /**
     * Test successful conversion of an {@link OAuthEntry} to an {@link OAuthDomainEntry}.
     */
    @Test
    public void testSuccessfulEntryToDto()
    {
        OAuthEntry entry = new OAuthEntry();
        entry.setAppId("appid");
        entry.setAuthorized(true);
        entry.setCallbackToken("callbacktoken");
        entry.setCallbackTokenAttempts(0);
        entry.setCallbackUrl("callbackurl");
        entry.setCallbackUrlSigned(true);
        entry.setConsumerKey("testkey");
        entry.setContainer("container");
        entry.setDomain("domain");
        entry.setIssueTime(new Date());
        entry.setOauthVersion("1.0");
        entry.setToken("token");
        entry.setTokenSecret("secret");
        entry.setType(Type.ACCESS);
        entry.setUserId("userId");
        
        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute("testkey");
                will(returnValue(new OAuthConsumer(null, null, "testkey", null, null)));
            }
        });

        OAuthDomainEntry dto = sut.convertToEntryDTO(entry);
        assertEquals(entry.getAppId(), dto.getAppId());
        assertEquals(entry.isAuthorized(), dto.isAuthorized());
        assertEquals(entry.getCallbackToken(), dto.getCallbackToken());
        assertEquals(entry.getCallbackTokenAttempts(), dto.getCallbackTokenAttempts());
        assertEquals(entry.getCallbackUrl(), dto.getCallbackUrl());
        assertEquals(entry.isCallbackUrlSigned(), dto.isCallbackUrlSigned());
        assertEquals(entry.getConsumerKey(), dto.getConsumer().getConsumerKey());
        assertEquals(entry.getContainer(), dto.getContainer());
        assertEquals(entry.getDomain(), dto.getDomain());
        assertEquals(entry.getIssueTime(), dto.getIssueTime());
        assertEquals(entry.getOauthVersion(), dto.getOauthVersion());
        assertEquals(entry.getToken(), dto.getToken());
        assertEquals(entry.getTokenSecret(), dto.getTokenSecret());
        assertEquals(entry.getType(), Type.valueOf(dto.getType()));
        assertEquals(entry.getUserId(), dto.getUserId());
        context.assertIsSatisfied();
    }
}
