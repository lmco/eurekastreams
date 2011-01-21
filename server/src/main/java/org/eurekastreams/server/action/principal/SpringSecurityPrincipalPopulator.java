/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.principal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.context.DefaultPrincipal;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalPopulator;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.service.security.userdetails.ExtendedUserDetails;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;

/**
 * This class is a Spring based implementation of the {@link PrincipalPopulator} interface for Eureka Streams.
 * 
 */
public class SpringSecurityPrincipalPopulator implements PrincipalPopulator
{
    /**
     * Local logger instance.
     */
    private final Log logger = LogFactory.getLog(SpringSecurityPrincipalPopulator.class);

    /**
     * Get Sessionless Principal.
     * 
     * @param inAccountId
     *            the account id.
     * @return the principal.
     */
    public Principal getPrincipal(final String inAccountId)
    {
        return getPrincipal(inAccountId, "");
    }

    /**
     * Retrieve the {@link Principal} object based on the security context loaded by Spring.
     * 
     * TODO: Refactor this when all references to {@link ExtendedUserDetails} are removed to just pull a Principal
     * object directly out of the Spring Context.
     * 
     * {@inheritDoc}.
     */
    @Override
    public Principal getPrincipal(final String inAccountId, final String inSessionId)
    {
        UserDetails user = null;
        Principal currentPrincipal = null;
        try
        {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (null != auth)
            {
                Object obj = auth.getPrincipal();
                if (obj instanceof UserDetails)
                {
                    user = (UserDetails) obj;
                }
            }
            ExtendedUserDetails extUser = (ExtendedUserDetails) user;
            currentPrincipal = new DefaultPrincipal(extUser.getUsername(), extUser.getPerson().getOpenSocialId(),
                    extUser.getPerson().getId(), inSessionId);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred populating Principal object for current request.", ex);
            throw new AuthorizationException("Error occurred populating Principal object for current request.");
        }
        return currentPrincipal;
    }
}
