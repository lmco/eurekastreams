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
import java.util.Date;
import java.util.List;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.notification.SendPrebuiltNotificationRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action to inject a pre-built notification into the system.
 */
public class SendMassPrebuiltNotificationExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /** Logger. */
    static Logger log = LoggerFactory.getLogger(LogFactory.getClassName());

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

        Date d = new Date();
        d = logTime(d, "START");

        // add the notification to the database
        int count = (Integer) createNotificationsMapper.execute((SendPrebuiltNotificationRequest) actionContext
                .getParams());

        d = logTime(d, "created notifications (" + count + ")");

        // get list of users and refresh their notification counts
        // Note: strategy here is to fetch the list within the action, then refresh each asynchronously. Some design
        // choices/alternatives are: 1) fetch list sync vs. async, 2) delete cache keys immediately also vs. not, 3)
        // refresh keys vs. just delete keys
        if (count > 0)
        {
            List<Long> userIds = unlockedUsersMapper.execute(false);

            d = logTime(d, "got unlocked users (" + userIds.size() + ")");

            List<UserActionRequest> actions = inWrapperContext.getUserActionRequests();
            for (Long userId : userIds)
            {
                actions.add(new UserActionRequest("refreshUserInAppNotificationCounts", null, userId));
            }

            d = logTime(d, "added async requests  [DONE]");
        }

        return count;
    }

    /**
     * REMOVE THIS.
     *
     * @param last
     *            last.
     * @param msg
     *            msg.
     * @return return.
     */
    private Date logTime(final Date last, final String msg)
    {
        Date now = new Date();
        log.info(msg + " (" + (now.getTime() - last.getTime()) + "ms)");

        return now;
    }
}
