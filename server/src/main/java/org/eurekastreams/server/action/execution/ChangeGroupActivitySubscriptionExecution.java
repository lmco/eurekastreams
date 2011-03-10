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
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.stream.ChangeGroupActivitySubscriptionRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.ChangeGroupActivitySubscriptionMapperRequest;

/**
 * Execution strategy to set a person's group activity notification for a group.
 */
public class ChangeGroupActivitySubscriptionExecution implements ExecutionStrategy<PrincipalActionContext>,
        Serializable
{
    /**
     * Serial version uid.
     */
    private final long serialVersionUID = -3579855666241075285L;

    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Mapper to get a domain group id by its short name.
     */
    private DomainMapper<List<String>, List<Long>> groupIdsFromShortNamesMapper;

    /**
     * Mapper to change a person's group new activity notification preference for a specific group.
     */
    private DomainMapper<ChangeGroupActivitySubscriptionMapperRequest, Boolean> changeNotificationPreferenceMapper;

    /**
     * Constructor.
     * 
     * @param inGroupIdsFromShortNamesMapper
     *            mapper to get a domain group id by its short name.
     * @param inChangeNotificationPreferenceMapper
     *            mapper to change a person's group new activity notification preference for a specific group.
     */
    public ChangeGroupActivitySubscriptionExecution(
            final DomainMapper<List<String>, List<Long>> inGroupIdsFromShortNamesMapper,
            final DomainMapper<ChangeGroupActivitySubscriptionMapperRequest, Boolean> // 
            inChangeNotificationPreferenceMapper)
    {
        groupIdsFromShortNamesMapper = inGroupIdsFromShortNamesMapper;
        changeNotificationPreferenceMapper = inChangeNotificationPreferenceMapper;
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
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        if (!(inActionContext.getParams() instanceof ChangeGroupActivitySubscriptionRequest))
        {
            log.warn("Invalid request type");
            return new Boolean(false);
        }
        String accountId = inActionContext.getPrincipal().getAccountId();

        ChangeGroupActivitySubscriptionRequest request = //
        (ChangeGroupActivitySubscriptionRequest) inActionContext.getParams();

        if (log.isInfoEnabled())
        {
            log.info("Setting group email notification "
                    + (request.getReceiveNewActivityNotifications() ? "on" : "off") + " for " + accountId
                    + " and group with short name " + request.getGroupShortName());
        }

        // get the group id
        ArrayList<String> groupShortNames = new ArrayList<String>();
        groupShortNames.add(request.getGroupShortName());
        List<Long> groupIds = groupIdsFromShortNamesMapper.execute(groupShortNames);
        if (groupIds.size() != 1)
        {
            log.warn("Couldn't find group id from short name " + request.getGroupShortName());
            return new Boolean(false);
        }

        // update the user's preference
        changeNotificationPreferenceMapper.execute(new ChangeGroupActivitySubscriptionMapperRequest(inActionContext
                .getPrincipal().getId(), groupIds.get(0), request.getReceiveNewActivityNotifications()));

        return true;
    }
}
