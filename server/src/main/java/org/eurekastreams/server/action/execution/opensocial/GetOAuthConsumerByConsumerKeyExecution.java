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

import java.io.Serializable;

import net.oauth.OAuthConsumer;
import net.oauth.OAuthServiceProvider;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.persistence.OAuthConsumerMapper;

/**
 * Retrieve the {@link OAuthConsumer} based on the Consumer Key provided.
 * 
 */
public class GetOAuthConsumerByConsumerKeyExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Instance of the {@link OAuthConsumerMapper}.
     */
    private final OAuthConsumerMapper mapper;

    /**
     * The local service provider.
     */
    private final OAuthServiceProvider serviceProvider;

    /**
     * Constructor.
     * 
     * @param inMapper
     *            instance of the {@link OAuthConsumerMapper} for this class.
     * @param inRequestTokenUrl
     *            - Url for retrieving the request token
     * @param inAuthorizeUrl
     *            - Url for authorizing the user.
     * @param inAccessTokenUrl
     *            - Url for retrieving the access token.
     */
    public GetOAuthConsumerByConsumerKeyExecution(final OAuthConsumerMapper inMapper, final String inRequestTokenUrl,
            final String inAuthorizeUrl, final String inAccessTokenUrl)
    {
        mapper = inMapper;
        serviceProvider = new OAuthServiceProvider(inRequestTokenUrl, inAuthorizeUrl, inAccessTokenUrl);
    }

    /**
     * {@inheritDoc}. Retrieve the {@link OAuthConsumer} based on the supplied Consumer Key.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        String consumerKey = (String) inActionContext.getParams();
        org.eurekastreams.server.domain.OAuthConsumer mappedConsumer = mapper.findConsumerByConsumerKey(consumerKey);
        OAuthConsumer consumer = new OAuthConsumer(mappedConsumer.getCallbackURL(), consumerKey, mappedConsumer
                .getConsumerSecret(), serviceProvider);
        return consumer;
    }
}
