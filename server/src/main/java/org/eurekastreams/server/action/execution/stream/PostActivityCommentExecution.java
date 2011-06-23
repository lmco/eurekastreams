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
package org.eurekastreams.server.action.execution.stream;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.notification.CommentNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.InsertActivityCommentRequest;
import org.eurekastreams.server.persistence.mappers.stream.InsertActivityComment;
import org.eurekastreams.server.search.modelview.CommentDTO;

/**
 * Execution strategy for posting a comment to an activity.
 *
 */
public class PostActivityCommentExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /**
     * Local logger instance.
     */
    private final Log logger = LogFactory.make();

    /**
     * Comment insert DAO.
     */
    private final InsertActivityComment insertCommentDAO;

    /** DAO to get activity details. */
    private final DomainMapper<Long, ActivityDTO> activityDAO;


    /**
     * Constructor.
     *
     * @param inInsertCommentDAO
     *            Comment insert DAO.
     * @param inActivityDAO
     *            DAO to get activity details.
     */
    public PostActivityCommentExecution(final InsertActivityComment inInsertCommentDAO,
            final DomainMapper<Long, ActivityDTO> inActivityDAO)
    {
        insertCommentDAO = inInsertCommentDAO;
        activityDAO = inActivityDAO;
    }

    /**
     * Posts a comment to an activity.
     *
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     * @return {@link CommentDTO}.
     *
     */
    @Override
    public CommentDTO execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
    {
        CommentDTO inRequest = (CommentDTO) inActionContext.getActionContext().getParams();

        long personId = inActionContext.getActionContext().getPrincipal().getId();
        long activityId = inRequest.getActivityId();
        CommentDTO results = insertCommentDAO.execute(new InsertActivityCommentRequest(personId, activityId, inRequest
                .getBody()));

        getNotificationUserRequests(personId, activityId, results.getId(), inActionContext.getUserActionRequests());

        return results;
    }

    /**
     * Creates and sets UserActionRequests based on comment action.
     *
     * @param personId
     *            current user id.
     * @param activityId
     *            id of activity being commented on.
     * @param commentId
     *            ID of new comment.
     * @param queuedRequests
     *            List to receive any queued requests generated.
     */
    private void getNotificationUserRequests(final long personId, final long activityId, final long commentId,
            final Collection<UserActionRequest> queuedRequests)
    {
        // need to get activity info to decide about notifying
        ActivityDTO activityDTO = activityDAO.execute(activityId);

        // Sends notifications for new personal stream comments.
        StreamEntityDTO destination = activityDTO.getDestinationStream();
        long destinationId = destination.getDestinationEntityId();
        EntityType destinationType = destination.getType();

        RequestType requestType = null;
        switch (destinationType)
        {
        case PERSON:
            requestType = RequestType.COMMENT;
            break;
        case GROUP:
            requestType = RequestType.GROUP_COMMENT;
            break;
        case RESOURCE:
            // TODO: Determine correct action for notifications here.
            return;
        default:
            return;
        }

        CreateNotificationsRequest notificationRequest = new CommentNotificationsRequest(requestType, personId,
                destinationId, activityId, commentId);

        // add request to queued request list
        queuedRequests.add(new UserActionRequest(CreateNotificationsRequest.ACTION_NAME, null, notificationRequest));
    }
}
