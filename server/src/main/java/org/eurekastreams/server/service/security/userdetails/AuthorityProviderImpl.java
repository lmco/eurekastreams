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

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;

/**
 * Implementation of AuthorityProvider interface used to map a given username
 * to a list of GrantedAuthorities.
 *
 */
public class AuthorityProviderImpl implements AuthorityProvider
{
    /**
     * Returns a list of granted authorities for a given username.
     * @param username The username.
     * @return A list of granted authorities for a given username.
     */
    public List<GrantedAuthority> loadAuthoritiesByUsername(final String username)
    {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(1);
        authorities.add(new GrantedAuthorityImpl("ROLE_USER"));
        return authorities;
    }
}
