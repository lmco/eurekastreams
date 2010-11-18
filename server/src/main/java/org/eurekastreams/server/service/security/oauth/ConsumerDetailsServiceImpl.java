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
package org.eurekastreams.server.service.security.oauth;

import net.oauth.OAuthConsumer;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.service.ActionController;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.oauth.common.OAuthException;
import org.springframework.security.oauth.common.signature.SharedConsumerSecret;
import org.springframework.security.oauth.provider.BaseConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetailsService;

/**
 * Interface between Spring Security OAuth and Eureka Streams for providing consumer data.
 */
public class ConsumerDetailsServiceImpl implements ConsumerDetailsService
{
    /** Local instance of logger. */
    private final Log log = LogFactory.make();

    /** Instance of the {@link ActionController} for this class. */
    private final ActionController actionController;

    /** Instance of the GetOauthConsumerByConsumerKeyAction Service Action. */
    private final ServiceAction getOAuthConsumerByConsumerKeyAction;

    /** Authorities granted to consumers. */
    private final GrantedAuthority[] grantedAuthorities;

    /**
     * Constructor.
     *
     * @param inActionController
     *            Action controller.
     * @param inGetOAuthConsumerByConsumerKeyAction
     *            Action to fetch consumer data.
     * @param inGrantedAuthorities
     *            Comma-separated list of roles all consumers will be given.
     */
    public ConsumerDetailsServiceImpl(final ActionController inActionController,
            final ServiceAction inGetOAuthConsumerByConsumerKeyAction, final String inGrantedAuthorities)
    {
        actionController = inActionController;
        getOAuthConsumerByConsumerKeyAction = inGetOAuthConsumerByConsumerKeyAction;

        String[] roles = inGrantedAuthorities.split(",\\s*");
        grantedAuthorities = new GrantedAuthority[roles.length];
        for (int i = 0; i < roles.length; i++)
        {
            grantedAuthorities[i] = new GrantedAuthorityImpl(roles[i]);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConsumerDetails loadConsumerByConsumerKey(final String inConsumerKey) throws OAuthException
    {
        try
        {
            ServiceActionContext currentContext = new ServiceActionContext(inConsumerKey, null);
            OAuthConsumer consumer =
                    (OAuthConsumer) actionController.execute(currentContext, getOAuthConsumerByConsumerKeyAction);

            BaseConsumerDetails details = new BaseConsumerDetails();
            details.setConsumerKey(inConsumerKey);
            details.setConsumerName(inConsumerKey);
            details.setSignatureSecret(new SharedConsumerSecret(consumer.consumerSecret));
            details.setAuthorities(grantedAuthorities);

            // NOTE: This line supports OAuth 2-legged only!
            details.setRequiredToObtainAuthenticatedToken(false);

            return details;
        }
        catch (Exception ex)
        {
            log.error("Error occurred retrieving consumer with provided key.", ex);
            throw new OAuthException("Unable to retrieve consumer with provided information.");
        }
    }
}
