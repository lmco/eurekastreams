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
package org.eurekastreams.server.action.execution.profile;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.DomainGroupShortNameRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.action.request.profile.RequestForGroupMembershipRequest;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.mappers.db.InsertRequestForGroupMembership;

/**
 * Submits a request for access to a private group.
 */
public class SendGroupAccessRequestExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /** Class logger. */
    private Log log = LogFactory.make();

    /** The GroupMapper used for looking up the coordinators of the group. */
    private DomainGroupMapper groupMapper;

    /** Mapper to insert membership requests. */
    private InsertRequestForGroupMembership insertMembershipRequestMapper;

    /**
     * Constructor.
     *
     * @param inGroupMapper
     *            injecting the GroupMapper.
     * @param inInsertMembershipRequestMapper
     *            Mapper to save the request.
     */
    public SendGroupAccessRequestExecution(final DomainGroupMapper inGroupMapper,
            final InsertRequestForGroupMembership inInsertMembershipRequestMapper)
    {
        groupMapper = inGroupMapper;
        insertMembershipRequestMapper = inInsertMembershipRequestMapper;
    }

    /**
     * Sends Email Requesting Access.
     *
     * @param inActionContext
     *            the principal context with request
     * @return Nothing.
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
    {
        DomainGroupShortNameRequest request =
                (DomainGroupShortNameRequest) inActionContext.getActionContext().getParams();
        DomainGroup target = groupMapper.findByShortName(request.getGroupShortName());
        if (null == target)
        {
            throw new IllegalArgumentException("Tried to send a request for access to an invalid group"
                    + request.getGroupShortName());
        }

        long personId = inActionContext.getActionContext().getPrincipal().getId();

        // save the request
        insertMembershipRequestMapper.execute(new RequestForGroupMembershipRequest(target.getId(), personId));

        // send notification
        // Note: doesn't check whether the request was freshly added or whether it was already found, thus allowing a
        // user to "nag" the coordinators for access
        inActionContext.getUserActionRequests().add(
                new UserActionRequest("createNotificationsAction", null, new CreateNotificationsRequest(
                        RequestType.REQUEST_GROUP_ACCESS, personId, target.getId(), 0L)));

        return null;
    }
}
