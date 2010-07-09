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

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for NoCurrentUserDetails.
 */
public class NoCurrentUserDetailsTest
{
    /**
     * Subject under test.
     */
    private NoCurrentUserDetails sut = null;

    /**
     * Create the sut.
     */
    @Before
    public void setup()
    {
        sut = new NoCurrentUserDetails();
    }

    /**
     * Will throw exception.
     */
    @Test(expected = SecurityException.class)
    public void getAuthorities()
    {
        sut.getAuthorities();
    }

    /**
     * Will throw exception.
     */
    @Test(expected = SecurityException.class)
    public void getPassword()
    {
        sut.getPassword();
    }

    /**
     * Will throw exception.
     */
    @Test(expected = SecurityException.class)
    public void getUsername()
    {
        sut.getUsername();
    }

    /**
     * Will throw exception.
     */
    @Test(expected = SecurityException.class)
    public void isAccountNonExpired()
    {
        sut.isAccountNonExpired();
    }

    /**
     * Will throw exception.
     */
    @Test(expected = SecurityException.class)
    public void isAccountNonLocked()
    {
        sut.isAccountNonLocked();
    }

    /**
     * Will throw exception.
     */
    @Test(expected = SecurityException.class)
    public void isCredentialsNonExpired()
    {
        sut.isCredentialsNonExpired();
    }

    /**
     * Will throw exception.
     */
    @Test(expected = SecurityException.class)
    public void isEnabled()
    {
        sut.isEnabled();
    }
}
