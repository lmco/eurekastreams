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

import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsChecker;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.checker.AccountStatusUserDetailsChecker;
import org.springframework.security.userdetails.ldap.UserDetailsContextMapper;
import org.springframework.util.Assert;

/**
 * Implementation of Spring's UserDetailsContextMapper interface. This allows us
 * a hook to use our UserDetailsService implementation with LDAP authentication.
 */
public class UserDetailsContextMapperImpl implements UserDetailsContextMapper
{
    /**
     * The UserDetailsService implementation to use.
     */
    private UserDetailsService userDetailsService;
    
    /**
     * The UserDetailsChecker.
     */
    private UserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

    /**
     * Constructor.
     * 
     * @param inUserDetailsService
     *            The UserDetailsService implementation to use.
     */
    public UserDetailsContextMapperImpl(
            final UserDetailsService inUserDetailsService)
    {
        Assert.notNull(inUserDetailsService);
        userDetailsService = inUserDetailsService;
    }

    /**
     * Returns a populated UserDetails object. This is just a
     * pass-through to the UserDetailsService passed into this object.
     * 
     * @param context
     *            LDAP context.
     * @param username
     *            Username of user to map.
     * @param authority
     *            List of granted authorities (roles).
     * @return A populated UserDetails object.
     */
    public UserDetails mapUserFromContext(final DirContextOperations context,
            final String username, final GrantedAuthority[] authority)
    {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        detailsChecker.check(userDetails);
        return userDetails;
    }

    /**
     * Unsupported.
     * 
     * @param userDetails
     *            Unsupported.
     * @param context
     *            Unsupported.
     */
    public void mapUserToContext(final UserDetails userDetails,
            final DirContextAdapter context)
    {
        throw new UnsupportedOperationException("Not supported");
    }

    /**
     * @param inDetailsChecker the detailsChecker to set
     */
    public void setDetailsChecker(final UserDetailsChecker inDetailsChecker)
    {
        detailsChecker = inDetailsChecker;
    }

}
