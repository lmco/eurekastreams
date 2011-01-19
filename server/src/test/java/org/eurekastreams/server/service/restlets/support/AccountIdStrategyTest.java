/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.restlets.support;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.Request;

/**
 * Test suite for the AccountIdStrategy interface implementations.
 * 
 * Combining both tests into this single test file for simplification.
 *
 */
public class AccountIdStrategyTest
{
    /**
     * {@link AccountIdOAuthParamStrategy} instance for testing.
     */
    private AccountIdOAuthParamStrategy sut1;
    
    /**
     * {@link AccountIdUrlAndOAuthParamStrategy} instance for testing.
     */
    private AccountIdUrlAndOAuthParamStrategy sut2;
    
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
     * The mocked web request.
     */
    private Request request = context.mock(Request.class);
    
    /**
     * Prepare the systems under test.
     */
    @Before
    public void setup()
    {
        sut1 = new AccountIdOAuthParamStrategy();
        
        sut2 = new AccountIdUrlAndOAuthParamStrategy(sut1);
    }
    
    /**
     * Test the simple flow through getAccountId.
     */
    @Test
    public void testAccountIdOAuthParamStrategyGetAccountId()
    {
        final Map<String, Object> requestAttributes = new HashMap<String, Object>();
        final Form headers = new Form();
        headers.add("accountid", "testaccountid");
        requestAttributes.put("org.restlet.http.headers", headers);
        
        context.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(requestAttributes));
            }
        });
        
        String accountId = sut1.getAccountId(request);
        assertEquals("testaccountid", accountId);
        
        context.assertIsSatisfied();
    }
    
    /**
     * Test the simple flow through getAccountId.
     */
    @Test
    public void testAccountIdOAuthParamStrategyGetAccountIdReturnsNull()
    {
        final Map<String, Object> requestAttributes = new HashMap<String, Object>();
        requestAttributes.put("org.restlet.http.headers", new Form());
        
        context.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(requestAttributes));
            }
        });
        
        String accountId = sut1.getAccountId(request);
        assertEquals(null, accountId);
        
        context.assertIsSatisfied();
    }
    
    /**
     * Test the simple flow through getAccountId.
     */
    @Test
    public void testAccountIdUrlParamStrategyGetAccountId()
    {
        final Map<String, Object> requestAttributes = new HashMap<String, Object>();
        requestAttributes.put("accountidFromUrl", "testaccountid");
        
        context.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(requestAttributes));
            }
        });
        
        String accountId = sut2.getAccountId(request);
        assertEquals("testaccountid", accountId);
        
        context.assertIsSatisfied();
    }
    
    /**
     * Test the simple flow through getAccountId retrieving from the OAuth header.
     */
    @Test
    public void testAccountIdUrlParamStrategyGetAccountIdFromOAuth()
    {
        final Map<String, Object> requestAttributes = new HashMap<String, Object>();
        final Form headers = new Form();
        headers.add("accountid", "testaccountid");
        requestAttributes.put("org.restlet.http.headers", headers);
        
        context.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(requestAttributes));
            }
        });
        
        String accountId = sut2.getAccountId(request);
        assertEquals("testaccountid", accountId);
        
        context.assertIsSatisfied();
    }
    
    /**
     * Test the simple flow through getAccountId.
     */
    @Test
    public void testAccountIdUrlParamStrategyGetAccountIdReturnsNull()
    {        
        final Map<String, Object> requestAttributes = new HashMap<String, Object>();
        
        context.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(requestAttributes));
            }
        });
        
        String accountId = sut2.getAccountId(request);
        assertEquals(null, accountId);
        
        context.assertIsSatisfied();
    }
}
