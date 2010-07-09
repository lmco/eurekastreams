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

import java.util.List;

import org.eurekastreams.server.service.security.userdetails.AuthorityProvider;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.ldap.LdapAuthoritiesPopulator;

/**
 * Implementation of Spring's LdapAuthoritiesPopulator interface.
 *
 */
public class LdapAuthoritiesPopulatorImpl implements LdapAuthoritiesPopulator
{

    /**
     * The provided authorityProvider implementation to used to 
     * retrieve authorities by username.
     */
    private AuthorityProvider authorityProvider = null;

    /**
     * Constructor.
     * 
     * @param inAuthorityProvider
     *            The authorityProvider used by this class.
     */
    public LdapAuthoritiesPopulatorImpl(final AuthorityProvider inAuthorityProvider)
    {
        authorityProvider = inAuthorityProvider;
    }

    /**
     * Returns GrantedAuthorities mapped to provided user info.
     * 
     * @param userData
     *            Not used.
     * @param username
     *            The username of the user to map authorities for.
     * @return GrantedAuthorities mapped to provided user info.
     */
    public GrantedAuthority[] getGrantedAuthorities(final DirContextOperations userData, final String username)
    {
        List<GrantedAuthority> authorities = authorityProvider.loadAuthoritiesByUsername(username);
        return authorities.toArray(new GrantedAuthority[authorities.size()]);
    }

}
