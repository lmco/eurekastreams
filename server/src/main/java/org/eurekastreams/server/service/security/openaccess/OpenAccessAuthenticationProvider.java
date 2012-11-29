/*
 * Copyright (c) 2009-2012 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.security.openaccess;

import org.springframework.security.Authentication;
import org.springframework.security.providers.AuthenticationProvider;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsChecker;
import org.springframework.security.userdetails.UserDetailsService;

/**
 * This class provides an extremely basic Authentication Provider. This is NOT intended for production setup because
 * this will allow any user that is in the database to login by just supplying their username.
 *
 * This provider works well for quick startups/demos or development environments.
 *
 * WARNING - THIS IS NOT SECURE - DO NOT USE FOR PRODUCTION CONFIGURATION.
 */
public class OpenAccessAuthenticationProvider implements AuthenticationProvider
{
    /** For fetching user account information about the user being authenticated. */
    private final UserDetailsService userDetailsService;

    /** For validating the user's account. */
    private final UserDetailsChecker userDetailsChecker;

    /**
     * Constructor.
     *
     * @param inUserDetailsService
     *            For fetching user account information about the user being authenticated.
     * @param inUserDetailsChecker
     *            For validating the user's account.
     */
    public OpenAccessAuthenticationProvider(final UserDetailsService inUserDetailsService,
            final UserDetailsChecker inUserDetailsChecker)
    {
        userDetailsService = inUserDetailsService;
        userDetailsChecker = inUserDetailsChecker;
    }

    /**
     * {@inheritDoc}
     */
    public Authentication authenticate(final Authentication authentication)
    {
        UsernamePasswordAuthenticationToken userToken = (UsernamePasswordAuthenticationToken) authentication;
        UserDetails currentUserDets = userDetailsService.loadUserByUsername(userToken.getName());
        userDetailsChecker.check(currentUserDets);
        return new UsernamePasswordAuthenticationToken(currentUserDets, userToken.getCredentials(),
                currentUserDets.getAuthorities());
    }

    /**
     * {@inheritDoc}
     */
    public boolean supports(final Class authentication)
    {
        return true;
    }

}
