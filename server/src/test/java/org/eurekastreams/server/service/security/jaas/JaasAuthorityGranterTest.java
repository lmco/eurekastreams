/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.security.jaas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eurekastreams.server.service.security.userdetails.AuthorityProvider;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.GrantedAuthority;

/**
 * Test for JaasAuthorityGranter class.
 *
 */
public class JaasAuthorityGranterTest
{
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
     * mock.
     */
    private AuthorityProvider authorityProviderMock = context.mock(AuthorityProvider.class);
    
    /**
     * mock.
     */
    private GrantedAuthority authMock = context.mock(GrantedAuthority.class); 
    
    /**
     * mock.
     */
    private Principal principalMock = context.mock(Principal.class);
    
    /**
     * System under test.
     */
    private JaasAuthorityGranter sut = null;
    
    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new JaasAuthorityGranter(authorityProviderMock);
    }
    
    /**
     * Test grant Method when authorityProvider returns authority.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGrantHasAuthorities()
    {
        final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(1);
        authorities.add(authMock);
        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getName();
                
                oneOf(authorityProviderMock).loadAuthoritiesByUsername(with(any(String.class)));
                will(returnValue(authorities));                      
            }
        });
        
        Set results = sut.grant(principalMock);
        assertEquals(1, results.size());
        context.assertIsSatisfied();
    }
    
    /**
     * Test grant Method when authorityProvider returns no authorities.
     */
    @Test
    public void testGrantNoAuthorities()
    {
        final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getName();
                
                oneOf(authorityProviderMock).loadAuthoritiesByUsername(with(any(String.class)));
                will(returnValue(authorities));                      
            }
        });
        
        assertNull(sut.grant(principalMock));
        context.assertIsSatisfied();
    }

}
