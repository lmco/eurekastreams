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
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Execution Strategy to Remove an OAuth Token by the string token.
 * 
 */
public class RemoveOAuthTokenExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Instance of domain mapper injected by spring that will delete an entry.
     */
    private final DomainMapper<String, Boolean> deleteMapper;

    /**
     * Constructor.
     * 
     * @param inDomainMapper
     *            - instance of the {@link DomainMapper} class.
     */
    public RemoveOAuthTokenExecution(final DomainMapper<String, Boolean> inDomainMapper)
    {
        deleteMapper = inDomainMapper;
    }

    /**
     * {@inheritDoc}. Remove the OAuth Token based on the supplied token.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        String token = (String) inActionContext.getParams();
        deleteMapper.execute(token);
        return null;
    }

}
