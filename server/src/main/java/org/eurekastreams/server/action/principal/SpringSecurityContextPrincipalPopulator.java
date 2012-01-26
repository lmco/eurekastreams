/*
 * Copyright (c) 2011-2012 Lockheed Martin Corporation
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
import org.eurekastreams.commons.actions.context.DefaultPrincipal;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalPopulator;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.eurekastreams.server.service.security.userdetails.ExtendedUserDetails;
import org.restlet.data.Request;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;

/**
 * Create/return Principal from Spring security context (if present; return null if not).
 */
public class SpringSecurityContextPrincipalPopulator implements PrincipalPopulator,
        Transformer<Request, Principal>
{
    /** Log. */
    private final Log logger = LogFactory.make();

    // /** If an exception should be thrown if no principal found (vs. just returning null). */
    // private boolean exceptionOnNone;

    /** If an exception should be thrown on error (vs. just returning null). */
    private final boolean exceptionOnError;

    /**
     * Constructor.
     *
     * @param inExceptionOnError
     *            If an exception should be thrown on error (vs. just returning null).
     */
    public SpringSecurityContextPrincipalPopulator(final boolean inExceptionOnError)
    {
        exceptionOnError = inExceptionOnError;
    }

    /**
     * Gets the principal from the Spring security context.
     *
     * @return Principal or null if none.
     */
    private Principal getPrincipal()
    {
        try
        {
            SecurityContext securityCtx = SecurityContextHolder.getContext();
            if (securityCtx == null)
            {
                throw new AuthorizationException("No security context available.");
            }

            Authentication auth = securityCtx.getAuthentication();
            if (auth == null)
            {
                return null;
            }

            Object obj = auth.getPrincipal();
            if (obj instanceof UserDetails)
            {
                UserDetails user = (UserDetails) obj;
                ExtendedUserDetails extUser = (ExtendedUserDetails) user;
                return new DefaultPrincipal(extUser.getUsername(), extUser.getPerson().getOpenSocialId(), extUser
                        .getPerson().getId());
            }
            else
            {
                return null;
            }
        }
        catch (Exception ex)
        {
            logger.error("Error occurred populating Principal object for current request.", ex);
            if (exceptionOnError)
            {
                throw new AuthorizationException("Error occurred populating Principal object for current request.");
            }
            else
            {
                return null;
            }
        }
    }

    /**
     * Retrieve the {@link Principal} object based on the security context loaded by Spring.
     *
     *
     * {@inheritDoc}.
     */
    @Override
    public Principal getPrincipal(final String inAccountId, final String inSessionId)
    {
        return getPrincipal();
    }

    /**
     * Return principal. This implementation ingnores params and returns principal based on Spring SecurityContext, or
     * null if SecurityContext is not populated.
     *
     * @param inTransformType
     *            Ignored.
     *
     * @return Principal from Spring security context if present, return null if not.
     */
    @Override
    public Principal transform(final Request inTransformType)
    {
        return getPrincipal();
    }
}
