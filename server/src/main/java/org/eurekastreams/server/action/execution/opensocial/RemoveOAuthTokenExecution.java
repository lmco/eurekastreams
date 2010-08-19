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

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.persistence.OAuthEntryMapper;

/**
 * Execution Strategy to Remove an OAuth Token by the string token.
 * 
 */
public class RemoveOAuthTokenExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Instance of OAuth entry mapper injected by spring.
     */
    private final OAuthEntryMapper entryMapper;

    /**
     * Constructor.
     * 
     * @param inEntryMapper
     *            - instance of the {@link OAuthEntryMapper} class.
     */
    public RemoveOAuthTokenExecution(final OAuthEntryMapper inEntryMapper)
    {
        entryMapper = inEntryMapper;
    }

    /**
     * {@inheritDoc}. Remove the OAuth Token based on the supplied token.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        String token = (String) inActionContext.getParams();
        entryMapper.delete(token);
        return null;
    }

}
