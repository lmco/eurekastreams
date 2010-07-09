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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.Authentication;
import org.springframework.security.providers.AuthenticationProvider;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.jaas.JaasAuthenticationProvider;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsChecker;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.checker.AccountStatusUserDetailsChecker;
import org.springframework.util.Assert;

/**
 * This class wraps an instance of JaasAuthenticationProvider
 * and adds UserDetailsService functionality to populate authenticated
 * Authentication object with UserDetails object rather than just 
 * username.
 *
 */
public class JaasAuthenticationProviderWrapper implements
        AuthenticationProvider
{
    /**
     * Logger.
     */
    private static Log log = LogFactory
            .getLog(JaasAuthenticationProviderWrapper.class);
    
    /**
     * The JaasAuthenticationProvider to use for Authentication.
     */
    private JaasAuthenticationProvider authProvider;
    
    /**
     * The UserDetailsService.
     */
    private UserDetailsService userDetailsService;
    
    /**
     * The UserDetailsChecker for this AuthenticationProvider.
     */
    private UserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();
    
    /**
     * Constructor.
     * @param inAuthProvider The JaasAuthenticationProvider to wrap.
     * @param inUserDetailsService The UserDetailsService to use.
     */
    public JaasAuthenticationProviderWrapper(final JaasAuthenticationProvider inAuthProvider,
            final UserDetailsService inUserDetailsService)
    {
        Assert.notNull(inAuthProvider);
        Assert.notNull(inUserDetailsService);
        authProvider = inAuthProvider;        
        userDetailsService = inUserDetailsService;
    }

    /**
     * Attempts to authenticate the Authentication object.
     * 
     * @param authentication The object to authenticate.
     * @return Authenticated object populated with UserDetails
     * or null if wrapped AuthenticationProvider returns null.
     */
    public Authentication authenticate(final Authentication authentication)
    {
        // call parent.
        Authentication parentAuthResult = authProvider.authenticate(authentication);

        // if parent returns null, short circuit.
        if (parentAuthResult == null)
        {
            log.debug("Parent authentication provider returned null Authentication object.");
            return null;
        }

        // load user details.
        log.debug("loading userDetails for user: " + parentAuthResult.getName());
        UserDetails details = userDetailsService
                .loadUserByUsername((String) parentAuthResult.getName());
        
        detailsChecker.check(details);
        
        //return new Authentication object with UserDetails populated.
        return new UsernamePasswordAuthenticationToken(details, 
                parentAuthResult.getCredentials(), parentAuthResult.getAuthorities());

    }

    /**
     * Pass-through to wrapped AuthenticationProvider. No logic.
     * @param authentication The authentication class to check.
     * @return value from wrapped AuthenticationProvider's support method.
     */
    @SuppressWarnings("unchecked")
    public boolean supports(final Class authentication)
    {
        return authProvider.supports(authentication);
    }

}
