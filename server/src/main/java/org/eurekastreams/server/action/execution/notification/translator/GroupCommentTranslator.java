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
package org.eurekastreams.server.action.execution.notification.translator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.execution.notification.NotificationPropertyKeys;
import org.eurekastreams.server.action.request.notification.CommentNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.db.GetCommentorIdsByActivityId;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Translates the event of someone commenting on a post to appropriate notifications.
 */
public class GroupCommentTranslator implements NotificationTranslator<CommentNotificationsRequest>
{
    /**
     * Mapper to get commentors.
     */
    GetCommentorIdsByActivityId commentorsMapper;

    /**
     * Mapper to get activity details.
     */
    DomainMapper<List<Long>, List<ActivityDTO>> activitiesMapper;

    /**
     * Mapper to get group coordinator ids.
     */
    private final DomainMapper<Long, List<Long>> coordinatorMapper;

    /** Mapper to get the comment. */
    private final DomainMapper<List<Long>, List<CommentDTO>> commentsMapper;

    /** Mapper to get people who saved an activity. */
    private final DomainMapper<Long, List<Long>> saversMapper;

    /**
     * Constructor.
     *
     * @param inCommentorsMapper
     *            commentors mapper to set.
     * @param inActivitiesMapper
     *            activities mapper to set.
     * @param inCoordinatorMapper
     *            coordinator mapper to set.
     * @param inCommentsMapper
     *            Mapper to get the comment.
     * @param inSaversMapper
     *            Mapper to get people who saved an activity.
     */
    public GroupCommentTranslator(final GetCommentorIdsByActivityId inCommentorsMapper,
            final DomainMapper<List<Long>, List<ActivityDTO>> inActivitiesMapper,
            final DomainMapper<Long, List<Long>> inCoordinatorMapper,
            final DomainMapper<List<Long>, List<CommentDTO>> inCommentsMapper,
            final DomainMapper<Long, List<Long>> inSaversMapper)
    {
        commentorsMapper = inCommentorsMapper;
        activitiesMapper = inActivitiesMapper;
        coordinatorMapper = inCoordinatorMapper;
        commentsMapper = inCommentsMapper;
        saversMapper = inSaversMapper;
    }

    /**
     * Gets a list of people to notify when a new comment is added.
     *
     * @param inRequest
     *            Event data.
     * @return Notifications generated.
     */
    @Override
    public NotificationBatch translate(final CommentNotificationsRequest inRequest)
    {
        // get activity ID from comment
        List<CommentDTO> commentList = commentsMapper.execute(Collections.singletonList(inRequest.getCommentId()));
        if (commentList.isEmpty())
        {
            return null;
        }
        CommentDTO comment = commentList.get(0);
        long activityId = comment.getActivityId();
        List<ActivityDTO> activities = activitiesMapper.execute(Collections.singletonList(activityId));
        if (activities.isEmpty())
        {
            return null;
        }
        ActivityDTO activity = activities.get(0);

        NotificationBatch batch = new NotificationBatch();
        List<Long> allRecipients = new ArrayList<Long>();

        // Adds post author as recipient
        long postAuthor = activity.getActor().getId();
        if (postAuthor != inRequest.getActorId())
        {
            batch.getRecipients()
                    .put(NotificationType.COMMENT_TO_PERSONAL_POST, Collections.singletonList(postAuthor));
            allRecipients.add(postAuthor);
        }

        // Adds group coordinators as recipients (if enabled)
        List<Long> coordinatorIds = Collections.EMPTY_LIST;
        if (coordinatorMapper != null)
        {
            coordinatorIds = coordinatorMapper.execute(inRequest.getDestinationId());
            List<Long> coordinatorsToNotify = new ArrayList<Long>();
            for (long coordinatorId : coordinatorIds)
            {
                if (coordinatorId != postAuthor && coordinatorId != inRequest.getActorId())
                {
                    allRecipients.add(coordinatorId);
                    coordinatorsToNotify.add(coordinatorId);
                    // this recipient list will keep replacing the old value in the map when new recipients are
                    // found
                    batch.getRecipients().put(NotificationType.COMMENT_TO_GROUP_STREAM, coordinatorsToNotify);
                }
            }
        }

        // Adds recipient who previously commented on this post
        List<Long> commentToCommentedRecipients = new ArrayList<Long>();
        for (long commentorId : commentorsMapper.execute(activityId))
        {
            if (commentorId != postAuthor && !coordinatorIds.contains(commentorId)
                    && commentorId != inRequest.getActorId())
            {
                allRecipients.add(commentorId);
                commentToCommentedRecipients.add(commentorId);

                // this recipient list will keep replacing the old value in the map when new recipients are found
                batch.getRecipients().put(NotificationType.COMMENT_TO_COMMENTED_POST, commentToCommentedRecipients);
            }
        }

        // Add people who saved post as recipients
        List<Long> commentToSaversRecipients = new ArrayList<Long>();
        for (long saverId : saversMapper.execute(activityId))
        {
            if (saverId != inRequest.getActorId() && !allRecipients.contains(saverId))
            {
                commentToSaversRecipients.add(saverId);
                allRecipients.add(saverId);

                // this recipient list will keep replacing the old value in the map when new recipients are found
                batch.getRecipients().put(NotificationType.COMMENT_TO_SAVED_POST, commentToSaversRecipients);
            }
        }

        // Add properties
        batch.setProperty(NotificationPropertyKeys.ACTOR, PersonModelView.class, inRequest.getActorId());
        batch.setProperty("stream", activity.getDestinationStream());
        batch.setAlias(NotificationPropertyKeys.SOURCE, "stream");
        batch.setProperty("activity", activity);
        batch.setProperty("comment", comment);

        return batch;
    }

}
