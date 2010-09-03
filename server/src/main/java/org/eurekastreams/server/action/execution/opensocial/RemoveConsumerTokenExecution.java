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
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.opensocial.RemoveConsumerTokenRequest;
import org.eurekastreams.server.domain.OAuthConsumer;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.opensocial.OAuthConsumerRequest;
import org.eurekastreams.server.persistence.mappers.requests.opensocial.OAuthTokenRequest;

/**
 * Execution Strategy to Remove an OAuth consumer Token during proxied requests to oauth providers.
 */
public class RemoveConsumerTokenExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Instance of OAuth domain mapper injected by spring.
     */
    private final DomainMapper<OAuthConsumerRequest, OAuthConsumer> consumerMapper;

    /**
     * Instance of OAuth token mapper injected by spring.
     */
    private final DomainMapper<OAuthTokenRequest, Boolean> tokenDeleteMapper;

    /**
     * Constructor.
     * 
     * @param inConsumerMapper
     *            instance of the {@link DomainMapper} class.
     * @param inTokenDeleteMapper
     *            instance of the {@link DomainMapper} class.
     */
    public RemoveConsumerTokenExecution(final DomainMapper<OAuthConsumerRequest, OAuthConsumer> inConsumerMapper, // \n
            final DomainMapper<OAuthTokenRequest, Boolean> inTokenDeleteMapper)
    {
        consumerMapper = inConsumerMapper;
        tokenDeleteMapper = inTokenDeleteMapper;
    }

    /**
     * {@inheritDoc}. Remove the OAuth Token based on the supplied token.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        RemoveConsumerTokenRequest request = (RemoveConsumerTokenRequest) inActionContext.getParams();

        SecurityToken securityToken = request.getSecurityToken();
        OAuthConsumer consumer = consumerMapper.execute(new OAuthConsumerRequest(request.getServiceName(),
                securityToken.getAppUrl()));
        if (consumer != null)
        {
            tokenDeleteMapper.execute(new OAuthTokenRequest(consumer, securityToken.getViewerId(), securityToken
                    .getOwnerId()));
        }
        else
        {
            throw new ExecutionException("OAuth Consumer was not found.");
        }

        return null;
    }
}
