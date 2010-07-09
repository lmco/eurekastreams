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

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.server.service.security.userdetails.AuthorityProvider;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.providers.jaas.AuthorityGranter;

/**
 * AuthorityGranter used to map a given principal to role names.
 * 
 */
public class JaasAuthorityGranter implements AuthorityGranter
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
    public JaasAuthorityGranter(final AuthorityProvider inAuthorityProvider)
    {
        authorityProvider = inAuthorityProvider;
    }

    /**
     * The grant method is called for each principal returned from the LoginContext subject. If the AuthorityGranter
     * wishes to grant any authorities, it should return a java.util.Set containing the role names it wishes to grant,
     * such as ROLE_USER. If the AuthrityGranter does not wish to grant any authorities it should return null. 
     * 
     * @param inPrincipal
     *            Principal representing authenticated user.
     * 
     * @return A java.util.Set of role names to grant, or null meaning no roles should be granted for the principal.
     */
    @SuppressWarnings("unchecked")
    public Set grant(final Principal inPrincipal)
    {
        List<GrantedAuthority> authorities = authorityProvider.loadAuthoritiesByUsername(inPrincipal.getName());
        return authorities.size() == 0 ? null : new HashSet(authorities);
    }

}
