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
package org.eurekastreams.server.service.security.ldap;

import org.eurekastreams.server.service.security.userdetails.UserDetailsServiceImpl;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsChecker;

/**
 * Test class for UserDetailsContextMapperImpl.
 *
 */
public class UserDetailsContextMapperImplTest
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
    public void testConstructorNullMapper()
    {                
        new UserDetailsContextMapperImpl(null);              
    }
    
    /**
     * Test method calls UserDetailsService correctly.
     */
    @Test
    public void testMapUserFromContext()
    {
        final UserDetailsServiceImpl svc = context.mock(UserDetailsServiceImpl.class);
        final UserDetailsChecker udc = context.mock(UserDetailsChecker.class);
        final UserDetails ud = context.mock(UserDetails.class);
                
        context.checking(new Expectations()
        {
            {
                oneOf(svc).loadUserByUsername(with("username")); 
                will(returnValue(ud));
                
                oneOf(udc).check(ud);
            }
        });
        
        UserDetailsContextMapperImpl sut  = new UserDetailsContextMapperImpl(svc); 
        sut.setDetailsChecker(udc);
        sut.mapUserFromContext(null, "username", null);
        context.assertIsSatisfied();
    }
    
    /**
     * Test that method throw UnsupportedOperationException.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testMapUserToContext()
    {
        final UserDetailsServiceImpl svc = context.mock(UserDetailsServiceImpl.class);
        UserDetailsContextMapperImpl sut  = new UserDetailsContextMapperImpl(svc); 
        sut.mapUserToContext(null, null);
    }

}
