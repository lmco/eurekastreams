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

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.gadgets.oauth.OAuthStore.TokenInfo;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.opensocial.GetConsumerTokenInfoRequest;
import org.eurekastreams.server.action.response.opensocial.TokenInfoResponse;
import org.eurekastreams.server.domain.OAuthConsumer;
import org.eurekastreams.server.domain.OAuthToken;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.opensocial.OAuthConsumerRequest;
import org.eurekastreams.server.persistence.mappers.requests.opensocial.OAuthTokenRequest;

/**
 * Execution Strategy to get OAuth consumer Token info during proxied requests to oauth providers.
 */
public class GetConsumerTokenInfoExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Instance of OAuth domain mapper injected by spring.
     */
    private final DomainMapper<OAuthConsumerRequest, OAuthConsumer> consumerMapper;

    /**
     * Instance of OAuth domain mapper injected by spring.
     */
    private final DomainMapper<OAuthTokenRequest, OAuthToken> tokenMapper;

    /**
     * Constructor.
     * 
     * @param inConsumerMapper
     *            instance of the {@link DomainMapper} class.
     * @param inTokenMapper
     *            instance of the {@link DomainMapper} class.
     */
    public GetConsumerTokenInfoExecution(final DomainMapper<OAuthConsumerRequest, OAuthConsumer> inConsumerMapper,
            final DomainMapper<OAuthTokenRequest, OAuthToken> inTokenMapper)
    {
        consumerMapper = inConsumerMapper;
        tokenMapper = inTokenMapper;
    }

    /**
     * {@inheritDoc}. Get the OAuth consumer token info.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        GetConsumerTokenInfoRequest request = (GetConsumerTokenInfoRequest) inActionContext.getParams();
        SecurityToken securityToken = request.getSecurityToken();

        OAuthConsumer consumer = consumerMapper.execute(new OAuthConsumerRequest(request.getServiceName(),
                securityToken.getAppUrl()));
        if (consumer == null)
        {
            throw new ExecutionException("OAuth Consumer was not found");
        }
        OAuthToken token = tokenMapper.execute(new OAuthTokenRequest(consumer, securityToken.getViewerId(),
                securityToken.getOwnerId()));
        if (token != null)
        {
            TokenInfo tokenInfo = new TokenInfo(token.getAccessToken(), token.getTokenSecret(), null, token
                    .getTokenExpireMillis());
            return new TokenInfoResponse(tokenInfo);
        }
        return null;
    }
}
