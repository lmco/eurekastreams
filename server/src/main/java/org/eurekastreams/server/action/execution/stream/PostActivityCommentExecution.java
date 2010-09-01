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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
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
    private Log logger = LogFactory.make();

    /**
     * Comment insert mapper.
     */
    private InsertActivityComment insertCommentDAO;

    /**
     * Mapper to get activity dto.
     */
    private DomainMapper<List<Long>, List<ActivityDTO>>  activitiesMapper;

    /**
     * Constructor.
     *
     * @param inInsertCommentDAO
     *            The comment insert DAO.
     * @param inActivitiesMapper
     *            The activities mapper.
     */
    public PostActivityCommentExecution(final InsertActivityComment inInsertCommentDAO,
            final DomainMapper<List<Long>, List<ActivityDTO>>  inActivitiesMapper)
    {
        insertCommentDAO = inInsertCommentDAO;
        activitiesMapper = inActivitiesMapper;
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

        inActionContext.getUserActionRequests().addAll(getNotificationUserRequests(personId, activityId));

        return results;
    }

    /**
     * Creates and sets UserActionRequests based on comment action.
     *
     * @param personId
     *            current user id.
     * @param activityId
     *            id of activity being commented on.
     * @return List of UserActionRequests.
     */
    private List<UserActionRequest> getNotificationUserRequests(final long personId, final long activityId)
    {
        List<UserActionRequest> queuedRequests = null;

        // need to get activity info to decide about notifying
        ActivityDTO activityDTO = activitiesMapper.execute(Arrays.asList(activityId)).get(0);

        // Sends notifications for new personal stream comments.
        StreamEntityDTO destination = activityDTO.getDestinationStream();
        long destinationId = destination.getDestinationEntityId();
        EntityType destinationType = destination.getType();

        RequestType requestType = null;
        if (destinationType == EntityType.PERSON)
        {
            requestType = RequestType.COMMENT;
        }

        else if (destinationType == EntityType.GROUP)
        {
            requestType = RequestType.GROUP_COMMENT;
        }

        if (requestType != null)
        {
            CreateNotificationsRequest notificationRequest = new CreateNotificationsRequest(requestType, personId,
                    destinationId, activityId);

            // create list if it has not already been done.
            queuedRequests = queuedRequests == null ? new ArrayList<UserActionRequest>() : queuedRequests;

            // add UserRequest.
            queuedRequests.add(new UserActionRequest("createNotificationsAction", null, notificationRequest));
        }

        return queuedRequests;
    }

}
