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
package org.eurekastreams.server.action.execution.opensocial;

import org.apache.shindig.auth.AuthenticationMode;
import org.apache.shindig.social.core.oauth.OAuthSecurityToken;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.response.opensocial.SecurityTokenResponse;

/**
 * This Execution performs the functionality for retrieving a security token during two-legged OAuth.
 * 
 */
public class GetSecurityTokenForConsumerRequestExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Domain for the security token.
     */
    private final String domain;

    /**
     * Container for the security token.
     */
    private final String container;

    /**
     * Constructor.
     * 
     * @param inDomain
     *            - domain name for this security token.
     * @param inContainer
     *            - container for this security token.
     */
    public GetSecurityTokenForConsumerRequestExecution(final String inDomain, final String inContainer)
    {
        domain = inDomain;
        container = inContainer;
    }

    /**
     * {@inheritDoc}.
     * Retrieve a security token for a two-legged OAuth consumer request.
     */
    @Override
    public SecurityTokenResponse execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        // TODO: put authorization in place to ensure this is a legit call. (i.e. supplied user has the supplied app
        // installed.)
        String userId = inActionContext.getPrincipal().getOpenSocialId();
        String consumerKey = (String) inActionContext.getParams();
        return new SecurityTokenResponse(new OAuthSecurityToken(userId, null, consumerKey, domain, container,
                null, AuthenticationMode.OAUTH_CONSUMER_REQUEST.name()));
    }

}
