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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.execution.notification.Notifier;
import org.eurekastreams.server.action.request.profile.ReviewPendingGroupRequest;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.mappers.cache.AddPrivateGroupIdToCachedCoordinatorAccessList;

/**
 * A coordinator decides the fate of a pending group.
 */
public class ReviewPendingGroupExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * The group mapper.
     */
    private DomainGroupMapper groupMapper;

    /**
     * Used to send email notifications to the group coordinators about whether the group was approved or denied.
     */
    private Notifier emailNotifier;

    /**
     * Used to update Cache when private group is approved.
     */
    private final AddPrivateGroupIdToCachedCoordinatorAccessList addPrivateGroupIdToCachedListMapper;

    /** Execution strategy for deleting a group. */
    private TaskHandlerExecutionStrategy deleteGroupExecution;

    /**
     * Constructor.
     * 
     * @param inGroupMapper
     *            the group mapper to use to find the domain group.
     * @param inEmailNotifier
     *            Email notifier.
     * @param inAddPrivateGroupIdToCachedListMapper
     *            mapper to update cache when a private group is approved
     * @param inDeleteGroupExecution
     *            Execution strategy for deleting a group.
     */
    public ReviewPendingGroupExecution(final DomainGroupMapper inGroupMapper, final Notifier inEmailNotifier,
            final AddPrivateGroupIdToCachedCoordinatorAccessList inAddPrivateGroupIdToCachedListMapper,
            final TaskHandlerExecutionStrategy inDeleteGroupExecution)
    {
        groupMapper = inGroupMapper;
        addPrivateGroupIdToCachedListMapper = inAddPrivateGroupIdToCachedListMapper;
        emailNotifier = inEmailNotifier;
        deleteGroupExecution = inDeleteGroupExecution;
    }

    /**
     * Execute the action, approving or declining the DomainGroup request.
     * 
     * @param inActionContext
     *            the action context containing the principal and request
     * @return true on success
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
    {
        try
        {
            ReviewPendingGroupRequest request = (ReviewPendingGroupRequest) inActionContext.getActionContext()
                    .getParams();
            DomainGroup group = groupMapper.findByShortName(request.getGroupShortName());
            final long groupId = group.getId();

            if (log.isInfoEnabled())
            {
                log.info((request.getApproved() ? "Approving" : "Disapproving") + " pending group '"
                        + request.getGroupShortName() + "' with id " + groupId);
            }

            notifyCoordinators(inActionContext, group, request);

            if (request.getApproved())
            {
                group.setPending(false);
                groupMapper.flush();
                if (!group.isPublicGroup())
                {
                    addPrivateGroupIdToCachedListMapper.execute(groupId);
                }
            }
            else
            {
                TaskHandlerActionContext<ActionContext> childContext = new TaskHandlerActionContext<ActionContext>(
                        new ActionContext()
                        {
                            public Serializable getParams()
                            {
                                return groupId;
                            }

                            public Map<String, Object> getState()
                            {
                                return null;
                            }

                            @Override
                            public String getActionId()
                            {
                                return null;
                            }

                            @Override
                            public void setActionId(final String inActionId)
                            {

                            }
                        }, inActionContext.getUserActionRequests());
                deleteGroupExecution.execute(childContext);
            }
        }
        catch (Exception e)
        {
            throw new ExecutionException(e);
        }

        return true;
    }

    /**
     * Notify the group's coordinators of the decision.
     * 
     * @param inActionContext
     *            Context (needed for async actions).
     * @param group
     *            The group.
     * @param request
     *            The group approve/deny request.
     * @throws Exception
     *             On notifier failure (shouldn't, since the notifier just queues an action).
     */
    private void notifyCoordinators(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final DomainGroup group, final ReviewPendingGroupRequest request) throws Exception
    {
        // This is using just the email backend of the notification subsystem. So it builds the notification object
        // (which is normally the notif engine's job) and hands it right to the email notifier.

        List<Long> recipients = new ArrayList<Long>();
        for (Person coord : group.getCoordinators())
        {
            recipients.add(coord.getId());
        }
        NotificationDTO notif = new NotificationDTO(recipients,
                request.getApproved() ? NotificationType.REQUEST_NEW_GROUP_APPROVED
                        : NotificationType.REQUEST_NEW_GROUP_DENIED, 0L);
        notif.setAuxiliary(EntityType.GROUP, group.getShortName(), group.getName());

        UserActionRequest asyncAction = emailNotifier.notify(notif);
        if (asyncAction != null)
        {
            inActionContext.getUserActionRequests().add(asyncAction);
        }
    }
}
