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

import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.GroupLookupRequest;
import org.eurekastreams.server.persistence.mappers.ldap.LdapGroup;
import org.eurekastreams.server.persistence.mappers.ldap.LdapLookup;
import org.eurekastreams.server.persistence.mappers.requests.LdapLookupRequest;

/**
 * Verify ldap group execution strategy.
 */
public class VerifyLdapGroupExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * mapper to use for ldap group lookup.
     */
    private LdapLookup<LdapGroup> groupMapper;

    /**
     * Constructor.
     * 
     * @param inGroupMapper
     *            lookup mapper.
     */
    public VerifyLdapGroupExecution(final LdapLookup<LdapGroup> inGroupMapper)
    {
        groupMapper = inGroupMapper;
    }

    @Override
    public Boolean execute(final ActionContext inActionContext) throws ExecutionException
    {
        GroupLookupRequest params = (GroupLookupRequest) inActionContext.getParams();
        List<LdapGroup> results = groupMapper.execute(new LdapLookupRequest(params.getQueryString()));

        return results.size() > 0;
    }
}
