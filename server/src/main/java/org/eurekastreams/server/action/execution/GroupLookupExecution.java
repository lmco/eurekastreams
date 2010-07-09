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
package org.eurekastreams.server.action.execution;

import java.io.Serializable;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.GroupLookupRequest;
import org.eurekastreams.server.service.actions.strategies.ldap.LdapGroupLookup;

/**
 * Group lookup execution strategy.
 */
public class GroupLookupExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Strategy to use for lookup.
     */
    private LdapGroupLookup lookupStrategy;

    /**
     * Constructor.
     * 
     * @param inLookupStrategy
     *            lookup strategy.
     */
    public GroupLookupExecution(final LdapGroupLookup inLookupStrategy)
    {
        lookupStrategy = inLookupStrategy;
    }

    @Override
    public Serializable execute(final ActionContext inActionContext) throws ExecutionException
    {
        GroupLookupRequest params = (GroupLookupRequest) inActionContext.getParams();
        return (Serializable) lookupStrategy.groupExists(params.getQueryString());
    }
}
