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

import net.oauth.OAuth;

import org.apache.shindig.gadgets.oauth.OAuthStore.ConsumerInfo;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.opensocial.GetConsumerInfoRequest;
import org.eurekastreams.server.action.response.opensocial.ConsumerInfoResponse;
import org.eurekastreams.server.domain.OAuthConsumer;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.opensocial.OAuthConsumerRequest;

/**
 * Execution Strategy to get OAuth consumer info during proxied requests to oauth providers.
 */
public class GetConsumerInfoExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Instance of OAuth domain mapper injected by spring.
     */
    private final DomainMapper<OAuthConsumerRequest, OAuthConsumer> consumerMapper;

    /**
     * Constructor.
     * 
     * @param inConsumerMapper
     *            instance of the {@link DomainMapper} class.
     */
    public GetConsumerInfoExecution(final DomainMapper<OAuthConsumerRequest, OAuthConsumer> inConsumerMapper)
    {
        consumerMapper = inConsumerMapper;
    }

    /**
     * {@inheritDoc}. Get the OAuth consumer token info.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        GetConsumerInfoRequest request = (GetConsumerInfoRequest) inActionContext.getParams();
        OAuthConsumer oauthConsumer = consumerMapper.execute(new OAuthConsumerRequest(request.getServiceName(), request
                .getSecurityToken().getAppUrl()));
        if (oauthConsumer == null)
        {
            throw new ExecutionException("OAuth Consumer was not found");
        }
        net.oauth.OAuthConsumer consumer = new net.oauth.OAuthConsumer(oauthConsumer.getCallbackURL(), oauthConsumer
                .getConsumerKey(), oauthConsumer.getConsumerSecret(), request.getProvider());
        consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, oauthConsumer.getSignatureMethod());
        ConsumerInfo consumerInfo = new ConsumerInfo(consumer, null, oauthConsumer.getCallbackURL());
        return new ConsumerInfoResponse(consumerInfo);
    }
}
