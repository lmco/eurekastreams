/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.commons.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.service.security.userdetails.ExtendedUserDetailsImpl;
import org.junit.Test;
import org.springframework.security.userdetails.UserDetails;

/**
 * Test fixture for UserActionRequest.
 */
public class UserActionRequestTest
{
    /**
     * Test the constructor and getters.
     */
    @Test
    public void testConstructorAndGetters()
    {
        UserDetails userDetails = null;
        String actionKey = "actionKey";
        UserActionRequest sut = new UserActionRequest(actionKey, userDetails, null);
        assertEquals(actionKey, sut.getActionKey());
        assertEquals(userDetails, sut.getUser());
        assertEquals(null, sut.getParams());
    }
    
    /**
     * Test the toString implementation.
     */
    @Test
    public void testToString()
    {
        Person testPerson = new Person("testAccountId", "firstname", "middlename", "lastname", "preferredname");
        UserDetails userDetails = new ExtendedUserDetailsImpl(testPerson, null, null, null);
        
        String actionKey = "actionKey";
        UserActionRequest sut = new UserActionRequest(actionKey, userDetails, null);
        assertNotNull(sut.toString());
    }

}
