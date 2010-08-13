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

import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.OAuthDomainEntry;
import org.eurekastreams.server.persistence.OAuthEntryMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link DisableOAuthTokenExecution} class.
 *
 */
public class DisableOAuthTokenExecutionTest
{
    /**
     * System under test.
     */
    private DisableOAuthTokenExecution sut;
    
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
     * Test token.
     */
    private static final String TEST_ENTRY_TOKEN = "testtoken";
    
    /**
     * Instance of OAuth entry mapper injected by spring.
     */
    private final OAuthEntryMapper entryMapper = context.mock(OAuthEntryMapper.class);
    
    /**
     * Mocked instance of the action context.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);
    
    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new DisableOAuthTokenExecution(entryMapper);
    }
    
    /**
     * Test disabling an oauth token.
     */
    @Test
    public void testSuccessfulDisableOauthToken()
    {
        final OAuthDomainEntry entry = new OAuthDomainEntry();
        entry.setCallbackTokenAttempts(1);
        entry.setCallbackUrlSigned(true);
        entry.setType(OAuthEntry.Type.ACCESS.toString());
        
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(TEST_ENTRY_TOKEN));
                
                oneOf(entryMapper).findEntry(TEST_ENTRY_TOKEN);
                will(returnValue(entry));
            }
        });
        
        sut.execute(actionContext);
        assertEquals(entry.getCallbackTokenAttempts(), 2);
        assertEquals(entry.getType(), OAuthEntry.Type.ACCESS.toString());
        context.assertIsSatisfied();
    }
    
    /**
     * Test disabling an oauth token.
     */
    @Test
    public void testSuccessfulDisableOauthTokenOverAttempts()
    {
        final OAuthDomainEntry entry = new OAuthDomainEntry();
        entry.setCallbackTokenAttempts(5);
        entry.setCallbackUrlSigned(true);
        entry.setType(OAuthEntry.Type.ACCESS.toString());
        
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(TEST_ENTRY_TOKEN));
                
                oneOf(entryMapper).findEntry(TEST_ENTRY_TOKEN);
                will(returnValue(entry));
            }
        });
        
        sut.execute(actionContext);
        assertEquals(entry.getCallbackTokenAttempts(), 6);
        assertEquals(entry.getType(), OAuthEntry.Type.DISABLED.toString());
        context.assertIsSatisfied();
    }
}
