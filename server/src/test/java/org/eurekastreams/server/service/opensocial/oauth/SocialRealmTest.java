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
package org.eurekastreams.server.service.opensocial.oauth;

import static org.junit.Assert.*;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.jsecurity.authc.AuthenticationToken;
import org.jsecurity.subject.PrincipalCollection;
import org.junit.Before;
import org.junit.Test;

/**
 * This class will test the Eureka Streams OAuth Realm.
 *
 */
public class SocialRealmTest
{
    /**
     * The system under test.
     */
    private SocialRealm sut;

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
     * Mocked collection for testing.
     */
    private PrincipalCollection testPrincCollection = context.mock(PrincipalCollection.class);
    
    /**
     * Mocked token for testing.
     */
    private AuthenticationToken testAuthToken = context.mock(AuthenticationToken.class);
    
    /**
     * Setup the system under test.
     */
    @Before
    public void setUp()
    {
        sut = new SocialRealm();
    }
    
    /**
     * Simple stub test for Getting Authorization Info.
     */
    @Test
    public void testDoGetAuthorizationInfo()
    {
        sut.doGetAuthorizationInfo(testPrincCollection);
        assertTrue(true);
    }
    
    /**
     * Simple stub test for Getting Authentication Info.
     */
    @Test
    public void testDoGetAuthenticationInfo()
    {
        sut.doGetAuthenticationInfo(testAuthToken);
        assertTrue(true);
    }
}
