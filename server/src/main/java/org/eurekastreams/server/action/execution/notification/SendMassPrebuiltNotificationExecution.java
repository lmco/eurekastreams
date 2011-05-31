/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.notification;

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.action.request.notification.SendPrebuiltNotificationRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Action to inject a pre-built notification into the system.
 */
public class SendMassPrebuiltNotificationExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /** Mapper to create notification for all (unlocked) users. */
    private final DomainMapper<SendPrebuiltNotificationRequest, Serializable> createNotificationsMapper;

    /** Mapper to get list of unlocked persons - those who received the notification and need their counts reset. */
    private final DomainMapper<Boolean, List<Long>> unlockedUsersMapper;

    /**
     * Constructor.
     *
     * @param inCreateNotificationsMapper
     *            Mapper to create notification for all (unlocked) users.
     * @param inUnlockedUsersMapper
     *            Mapper to get list of unlocked persons.
     */
    public SendMassPrebuiltNotificationExecution(
            final DomainMapper<SendPrebuiltNotificationRequest, Serializable> inCreateNotificationsMapper,
            final DomainMapper<Boolean, List<Long>> inUnlockedUsersMapper)
    {
        createNotificationsMapper = inCreateNotificationsMapper;
        unlockedUsersMapper = inUnlockedUsersMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<PrincipalActionContext> inWrapperContext)
    {
        final PrincipalActionContext actionContext = inWrapperContext.getActionContext();

        // add the notification to the database
        int count = (Integer) createNotificationsMapper.execute((SendPrebuiltNotificationRequest) actionContext
                .getParams());

        // get list of users and refresh their notification counts
        if (count > 0)
        {
            List<Long> userIds = unlockedUsersMapper.execute(false);

            // TODO: refresh their counts
        }

        return count;
    }
}
