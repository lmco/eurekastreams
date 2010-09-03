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
import org.eurekastreams.server.action.request.opensocial.SetConsumerTokenInfoRequest;
import org.eurekastreams.server.domain.OAuthConsumer;
import org.eurekastreams.server.domain.OAuthToken;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.InsertMapper;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.eurekastreams.server.persistence.mappers.requests.opensocial.OAuthConsumerRequest;

/**
 * Execution Strategy to set OAuth consumer Token info during proxied requests to oauth providers.
 */
public class SetConsumerTokenInfoExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Instance of OAuth domain mapper injected by spring.
     */
    private final DomainMapper<OAuthConsumerRequest, OAuthConsumer> consumerMapper;

    /**
     * Instance of OAuth token mapper injected by spring.
     */
    private final InsertMapper<OAuthToken> tokenInsertMapper;

    /**
     * Constructor.
     * 
     * @param inConsumerMapper
     *            instance of the {@link DomainMapper} class.
     * @param inTokenInsertMapper
     *            instance of the {@link DomainMapper} class.
     */
    public SetConsumerTokenInfoExecution(final DomainMapper<OAuthConsumerRequest, OAuthConsumer> inConsumerMapper,
            final InsertMapper<OAuthToken> inTokenInsertMapper)
    {
        consumerMapper = inConsumerMapper;
        tokenInsertMapper = inTokenInsertMapper;
    }

    /**
     * {@inheritDoc}. Set the OAuth Token info.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        SetConsumerTokenInfoRequest request = (SetConsumerTokenInfoRequest) inActionContext.getParams();

        SecurityToken securityToken = request.getSecurityToken();
        TokenInfo tokenInfo = request.getTokenInfo();

        OAuthConsumer consumer = consumerMapper.execute(new OAuthConsumerRequest(request.getServiceName(),
                securityToken.getAppUrl()));
        if (consumer != null)
        {
            OAuthToken token = new OAuthToken(consumer, securityToken.getViewerId(), securityToken.getOwnerId(),
                    tokenInfo.getAccessToken(), tokenInfo.getTokenSecret());
            token.setTokenExpireMillis(tokenInfo.getTokenExpireMillis());
            tokenInsertMapper.execute(new PersistenceRequest<OAuthToken>(token));
        }
        else
        {
            throw new ExecutionException("OAuth Consumer was not found");
        }

        return null;
    }
}
