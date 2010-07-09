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

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;

/**
 * A UserDetails implementation used when no user is logged in. It will throw an exception if any method is used but the
 * constructor.
 * 
 */
public class NoCurrentUserDetails implements UserDetails
{

    /**
     * Not important, since this class won't be sent across the wire.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The exception message.
     */
    private static final String EXCEPTION_MSG = "No user is logged in.";

    /**
     * Will throw exception.
     * 
     * @return nothing
     */
    public GrantedAuthority[] getAuthorities()
    {
        throw new SecurityException(EXCEPTION_MSG);
    }

    /**
     * Will throw exception.
     * 
     * @return nothing
     */
    public String getPassword()
    {
        throw new SecurityException(EXCEPTION_MSG);
    }

    /**
     * Will throw exception.
     * 
     * @return nothing
     */
    public String getUsername()
    {
        throw new SecurityException(EXCEPTION_MSG);
    }

    /**
     * Will throw exception.
     * 
     * @return nothing
     */
    public boolean isAccountNonExpired()
    {
        throw new SecurityException(EXCEPTION_MSG);
    }

    /**
     * Will throw exception.
     * 
     * @return nothing
     */
    public boolean isAccountNonLocked()
    {
        throw new SecurityException(EXCEPTION_MSG);
    }

    /**
     * Will throw exception.
     * 
     * @return nothing
     */
    public boolean isCredentialsNonExpired()
    {
        throw new SecurityException(EXCEPTION_MSG);
    }

    /**
     * Will throw exception.
     * 
     * @return nothing
     */
    public boolean isEnabled()
    {
        throw new SecurityException(EXCEPTION_MSG);
    }
}
