/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.ChangeGroupActivitySubscriptionMapperRequest;

/**
 * Execution strategy to get short names of domain groups subscribed to by the current user.
 */
public class GetSubscribedGroupsExecution implements ExecutionStrategy<PrincipalActionContext>, Serializable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 7520679650876671356L;

    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Mapper to get the subscribed groups for a person.
     */
    private DomainMapper<Long, ArrayList<String>> getSubscribedGroupsMapper;

    /**
     * Mapper to change a person's group new activity notification preference for a specific group.
     */
    private DomainMapper<ChangeGroupActivitySubscriptionMapperRequest, Boolean> changeNotificationPreferenceMapper;

    /**
     * Constructor.
     * 
     * @param inGetSubscribedGroupsMapper
     *            mapper to get the subscribed groups for a person.
     */
    public GetSubscribedGroupsExecution(final DomainMapper<Long, ArrayList<String>> inGetSubscribedGroupsMapper)
    {
        getSubscribedGroupsMapper = inGetSubscribedGroupsMapper;
    }

    /**
     * Set the current user's group new activity subscription.
     * 
     * @param inActionContext
     *            the action context with the ChangeGroupActivitySubscriptionRequest
     * @return false on error/failure, else true
     * @throws ExecutionException
     *             on error
     */
    @Override
    public ArrayList<String> execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        long personId = inActionContext.getPrincipal().getId();
        ArrayList<String> groups = getSubscribedGroupsMapper.execute(personId);
        if (log.isDebugEnabled())
        {
            log.debug("Person with accountId " + inActionContext.getPrincipal().getAccountId() + " is subscribed to "
                    + groups.toString() + " groups");
        }
        return groups;
    }
}
