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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.jaas.JaasAuthenticationProvider;
import org.springframework.security.providers.jaas.JaasAuthenticationToken;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;

/**
 * Tests for JaasAuthenticationProviderWrapper class.
 *
 */
public class JaasAuthenticationProviderWrapperTest
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
     * Test constructor rejects null UserDetailsService.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullUserDetailsService()
    {           
        final JaasAuthenticationProvider provider = context.mock(JaasAuthenticationProvider.class);
        new JaasAuthenticationProviderWrapper(provider, null);              
    }
    
    /**
     * Test constructor rejects null JaasAuthenticationProvider.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullJaasAuthenticationProvider()
    {           
        final UserDetailsService uds = context.mock(UserDetailsService.class);
        new JaasAuthenticationProviderWrapper(null, uds);              
    }
    
    /**
     * This test will force parent class's authenticate to return null,
     * verifying that we check for this avoiding null pointers
     * and return null also.
     */
    @Test
    public void testAuthenticateParentAuthenticateReturnsNull()
    {
        final JaasAuthenticationProvider provider = context.mock(JaasAuthenticationProvider.class);
        final UsernamePasswordAuthenticationToken auth = context.mock(UsernamePasswordAuthenticationToken.class);
        final UserDetailsService uds = context.mock(UserDetailsService.class);
        
        context.checking(new Expectations()
        {
            {
                //start parent class mock
                oneOf(provider).authenticate(auth);
                will(returnValue(null));                
            }
        });
        
        JaasAuthenticationProviderWrapper sut = new JaasAuthenticationProviderWrapper(provider, uds);
        assertNull("authenticate() should return null if wrapped provider returns null",
                sut.authenticate(auth));  
        context.assertIsSatisfied();
    }
    
    /**
     * Test that returned authentication object .getPrincipal() method returns
     * contains the userDetails object loaded from the passed in UserDetailsService.
     */
    @Test
    public void testAuthenticate()
    {
        final JaasAuthenticationProvider provider = context.mock(JaasAuthenticationProvider.class);
        final JaasAuthenticationToken token = context.mock(JaasAuthenticationToken.class);
        final UsernamePasswordAuthenticationToken auth = context.mock(UsernamePasswordAuthenticationToken.class);
        final UserDetailsService uds = context.mock(UserDetailsService.class);
        final UserDetails ud = context.mock(UserDetails.class);
        
        context.checking(new Expectations()
        {
            {
                //start parent class mock
                oneOf(provider).authenticate(auth);
                will(returnValue(token));    
                
                exactly(2).of(token).getName();
                will(returnValue("homers"));
                
                oneOf(uds).loadUserByUsername("homers");
                will(returnValue(ud));
                
                oneOf(ud).isAccountNonLocked();
                will(returnValue(true));
                
                oneOf(ud).isEnabled();
                will(returnValue(true));
                
                oneOf(ud).isAccountNonExpired();
                will(returnValue(true));
                
                oneOf(ud).isCredentialsNonExpired();
                will(returnValue(true));
                
                oneOf(token).getCredentials();
                will(returnValue(null));
                
                oneOf(token).getAuthorities();
                will(returnValue(new GrantedAuthority[0]));                               
            }
        });
        
        JaasAuthenticationProviderWrapper sut = new JaasAuthenticationProviderWrapper(provider, uds);
        assertEquals("Authentication.getPrincipal() doesn't return expected userDetailsObject ",
                ud, sut.authenticate(auth).getPrincipal());
        context.assertIsSatisfied();
    }
    
    /**
     * Test that the supports method is just a pass-through.
     */
    @Test
    public void testSupports()
    {
        final JaasAuthenticationProvider provider = context.mock(JaasAuthenticationProvider.class);        
        final UserDetailsService uds = context.mock(UserDetailsService.class);
        final Sequence executeSequence = context.sequence("executeSequence");
        
        context.checking(new Expectations()
        {
            {                
                oneOf(provider).supports(null);
                will(returnValue(true)); 
                inSequence(executeSequence);
                
                oneOf(provider).supports(null);
                will(returnValue(false));
                inSequence(executeSequence);
            }
        });
        
        JaasAuthenticationProviderWrapper sut = new JaasAuthenticationProviderWrapper(provider, uds);
        assertTrue(sut.supports(null));        
        assertFalse(sut.supports(null));
        context.assertIsSatisfied();
    }
    
 

}
