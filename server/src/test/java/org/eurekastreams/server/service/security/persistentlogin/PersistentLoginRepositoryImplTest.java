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
package org.eurekastreams.server.service.security.persistentlogin;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.eurekastreams.server.domain.PersistentLogin;
import org.eurekastreams.server.persistence.PersistentLoginMapper;

/**
 * Tests for PersistentLoginRepositoryImpl class.
 *
 */
public class PersistentLoginRepositoryImplTest
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
     * Test constructor sets Person.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullMapper()
    {                
        new PersistentLoginRepositoryImpl(null);              
    }
    
    /**
     * Test correct method on mapper is called.
     */
    @Test
    public void testCreateOrUpdatePersistentLogin()
    {
        final PersistentLoginMapper loginMapper = context.mock(PersistentLoginMapper.class);
        final PersistentLogin login = context.mock(PersistentLogin.class);
        
        context.checking(new Expectations()
        {
            {
                oneOf(loginMapper).createOrUpdate(with(login));                 
            }
        });
        
        PersistentLoginRepositoryImpl sut = new PersistentLoginRepositoryImpl(loginMapper);
        sut.createOrUpdatePersistentLogin(login);
        context.assertIsSatisfied();
    }
    
    /**
     * Test correct method on mapper is called.
     */
    @Test
    public void testRemovePersistentLogin()
    {
        final PersistentLoginMapper loginMapper = context.mock(PersistentLoginMapper.class);
        
        context.checking(new Expectations()
        {
            {
                oneOf(loginMapper).deletePersistentLogin(with("theuser"));                
            }
        });
        
        PersistentLoginRepositoryImpl sut = new PersistentLoginRepositoryImpl(loginMapper);
        sut.removePersistentLogin("theuser");
        context.assertIsSatisfied();
    }
    
    /**
     * Test correct method on mapper is called and that PersistentLogin is returned
     * to caller.
     */
    @Test
    public void testGetPersistentLoginDataSuccess()
    {
        final PersistentLoginMapper loginMapper = context.mock(PersistentLoginMapper.class);
        final PersistentLogin login = context.mock(PersistentLogin.class);
        
        context.checking(new Expectations()
        {
            {
                oneOf(loginMapper).findByAccountId(with("theuser")); 
                will(returnValue(login));
            }
        });
        
        PersistentLoginRepositoryImpl sut = new PersistentLoginRepositoryImpl(loginMapper);
        assertNotNull("Did not return PersistentLogin retreived from DB", 
                sut.getPersistentLogin("theuser"));
        context.assertIsSatisfied();
    }
    
    /**
     * Test that exception is swallowed and null is returned if mapper
     * throws exception.
     */
    @Test
    public void testGetPersistentLoginDataFail()
    {
        final PersistentLoginMapper loginMapper = context.mock(PersistentLoginMapper.class);
        
        context.checking(new Expectations()
        {
            {
                oneOf(loginMapper).findByAccountId(with("theuser")); 
                will(throwException(new Exception()));                
            }
        });
        
        PersistentLoginRepositoryImpl sut = new PersistentLoginRepositoryImpl(loginMapper);        
        assertNull("Should return null if mapper throws exception.", sut.getPersistentLogin("theuser"));
        context.assertIsSatisfied();
    }

}
