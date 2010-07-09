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
package org.eurekastreams.server.service.security.userdetails;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.GrantedAuthority;

/**
 * Test for AuthorityProviderImp.
 *
 */
public class AuthorityProviderImplTest
{
    /**
     * sut.
     */
    private AuthorityProviderImpl sut;
    
    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new AuthorityProviderImpl();
    }
    
    /**
     * Test loadAuthoritiesByUsername method.
     */
    @Test
    public void testLoadAuthoritiesByUsername()
    {
        List<GrantedAuthority> result = sut.loadAuthoritiesByUsername("testUser");
        assertNotNull(result);
        assertEquals("Expected list size of 1.", 1, result.size());
    }
}
