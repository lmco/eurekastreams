/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.eurekastreams.server.service.security.userdetails.ExtendedUserDetails;
import org.restlet.data.Form;
import org.restlet.data.Request;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;

/**
 * Create/return Principal from Spring security context if present, return null if not.
 */
public class SpringSecurityContextPrincipalPopulatorAllowNull implements PrincipalPopulator,
        Transformer<Request, Principal>
{
    /**
     * If the session should be verified.
     */
    private boolean verifySession = true;

    /**
     * Session cookie name.
     */
    private final String sessionCookieName;

    /**
     * Session Header name.
     */
    private final String sessionHeaderName;

    /**
     * Local logger instance.
     */
    private final Log logger = LogFactory.make();

    /**
     * Constructor.
     *
     * @param inVerifySession
     *            if the session should be verified from the header.
     * @param inSessionCookieName
     *            the session cookie name.
     * @param inSessionHeaderName
     *            the session header name.
     */
    public SpringSecurityContextPrincipalPopulatorAllowNull(final boolean inVerifySession,
            final String inSessionCookieName, final String inSessionHeaderName)
    {
        verifySession = inVerifySession;
        sessionCookieName = inSessionCookieName;
        sessionHeaderName = inSessionHeaderName;
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
        Principal currentPrincipal = null;
        try
        {
            UserDetails user = null;

            SecurityContext securityCtx = SecurityContextHolder.getContext();
            if (securityCtx == null)
            {
                return null;
            }

            Authentication auth = securityCtx.getAuthentication();
            if (auth == null)
            {
                return null;
            }

            Object obj = auth.getPrincipal();
            if (obj instanceof UserDetails)
            {
                user = (UserDetails) obj;
            }
            else
            {
                return null;
            }

            ExtendedUserDetails extUser = (ExtendedUserDetails) user;
            currentPrincipal = new DefaultPrincipal(extUser.getUsername(), extUser.getPerson().getOpenSocialId(),
                    extUser.getPerson().getId());
        }
        catch (Exception ex)
        {
            logger.info("Unable to populate principal from SecurityContext.", ex);
        }
        return currentPrincipal;
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
        final Principal principal = getPrincipal(null, "");
        if (principal != null && verifySession)
        {
            String sessionId = inTransformType.getCookies().getFirst(sessionCookieName).getValue();

            boolean sessionMatch = false;

            logger.debug("Session id from cookie: " + sessionId);

            if (inTransformType.getAttributes().containsKey("org.restlet.http.headers"))
            {
                Form httpHeaders = (Form) inTransformType.getAttributes().get("org.restlet.http.headers");

                if (httpHeaders.getFirstValue(sessionHeaderName) != null)
                {
                    String headerSessionId = httpHeaders.getFirstValue(sessionHeaderName);
                    logger.debug("Session id from header: " + headerSessionId);
                    sessionMatch = sessionId.equals(headerSessionId);
                }
            }

            if (!sessionMatch)
            {
                return null;
            }
        }
        return principal;
    }
}
