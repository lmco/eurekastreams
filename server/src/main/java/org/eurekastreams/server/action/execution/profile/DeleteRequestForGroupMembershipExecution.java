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

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.action.request.profile.RequestForGroupMembershipRequest;
import org.eurekastreams.server.persistence.mappers.db.DeleteRequestForGroupMembership;

/**
 * Execution to delete a group membership request.
 */
public class DeleteRequestForGroupMembershipExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /** Mapper to delete request. */
    private final DeleteRequestForGroupMembership mapper;

    /**
     * Constructor.
     *
     * @param inMapper
     *            Mapper.
     */
    public DeleteRequestForGroupMembershipExecution(final DeleteRequestForGroupMembership inMapper)
    {
        mapper = inMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
    {
        RequestForGroupMembershipRequest request = (RequestForGroupMembershipRequest) inActionContext
                .getActionContext().getParams();

        mapper.execute(request);

        // send notification
        inActionContext.getUserActionRequests().add(
                new UserActionRequest("createNotificationsAction", null, new CreateNotificationsRequest(
                        RequestType.REQUEST_GROUP_ACCESS_DENIED, inActionContext.getActionContext().getPrincipal()
                                .getId(), request.getGroupId(), request.getPersonId())));

        return null;
    }

}
