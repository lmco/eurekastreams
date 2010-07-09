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
package org.eurekastreams.server.action.execution.stream;

import java.io.Serializable;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.persistence.mappers.requests.UpdateActivityFlagRequest;
import org.eurekastreams.server.persistence.mappers.stream.UpdateActivityFlag;

/**
 * Marks an activity as flagged or unflagged.
 */
public class UpdateActivityFlagExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /** Desired state. */
    private boolean toFlag;

    /** Update mapper. */
    private UpdateActivityFlag setFlagMapper;

    /**
     * Constructor.
     *
     * @param inToFlag
     *            Desired state.
     * @param inSetFlagMapper
     *            Update mapper.
     */
    public UpdateActivityFlagExecution(final UpdateActivityFlag inSetFlagMapper, final boolean inToFlag)
    {
        toFlag = inToFlag;
        setFlagMapper = inSetFlagMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
            throws ExecutionException
    {
        Long activityId = (Long) inActionContext.getActionContext().getParams();

        boolean updated = setFlagMapper.execute(new UpdateActivityFlagRequest(activityId, toFlag));

        // trigger notification on activity being flagged
        if (toFlag && updated)
        {
            CreateNotificationsRequest notificationRequest =
                    new CreateNotificationsRequest(RequestType.FLAG_ACTIVITY, inActionContext.getActionContext()
                            .getPrincipal().getId(), 0L /*
                                                         * TODO: determine what to use for destination ID (if anything)
                                                         */,
                            activityId);
            inActionContext.getUserActionRequests().add(
                    new UserActionRequest("createNotificationsAction", null, notificationRequest));
        }

        return toFlag;
    }
}
