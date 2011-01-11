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
package org.eurekastreams.server.action.execution.notification.translator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.db.GetCommentorIdsByActivityId;
import org.eurekastreams.server.search.modelview.CommentDTO;

/**
 * Translates the event of someone commenting on a post to appropriate notifications.
 */
public class GroupCommentTranslator implements NotificationTranslator
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
     * @param inActorId
     *            ID of actor that made the comment.
     * @param inDestinationId
     *            ID of group whose stream contains the activity commented on.
     * @param inCommentId
     *            the comment id.
     * @return List of notifications generated.
     */
    @Override
    public Collection<NotificationDTO> translate(final long inActorId, final long inDestinationId,
            final long inCommentId)
    {
        // get activity ID from comment
        List<CommentDTO> commentList = commentsMapper.execute(Collections.singletonList(inCommentId));
        if (commentList.isEmpty())
        {
            return Collections.EMPTY_LIST;
        }
        long activityId = commentList.get(0).getActivityId();
        List<ActivityDTO> activities = activitiesMapper.execute(Collections.singletonList(activityId));
        if (activities.isEmpty())
        {
            return Collections.EMPTY_LIST;
        }
        ActivityDTO activity = activities.get(0);

        Map<NotificationType, List<Long>> recipientsByType = new HashMap<NotificationType, List<Long>>();
        List<Long> allRecipients = new ArrayList<Long>();

        // Adds post author as recipient
        long postAuthor = activity.getActor().getId();
        if (postAuthor != inActorId)
        {
            recipientsByType.put(NotificationType.COMMENT_TO_PERSONAL_POST, Collections.singletonList(postAuthor));
            allRecipients.add(postAuthor);
        }

        // Adds group coordinators as recipients (if enabled)
        List<Long> coordinatorIds = Collections.EMPTY_LIST;
        if (coordinatorMapper != null)
        {
            coordinatorIds = coordinatorMapper.execute(inDestinationId);
            List<Long> coordinatorsToNotify = new ArrayList<Long>();
            for (long coordinatorId : coordinatorIds)
            {
                if (coordinatorId != postAuthor && coordinatorId != inActorId)
                {
                    allRecipients.add(coordinatorId);
                    coordinatorsToNotify.add(coordinatorId);
                    // this recipient list will keep replacing the old value in the map when new recipients are
                    // found
                    recipientsByType.put(NotificationType.COMMENT_TO_GROUP_STREAM, coordinatorsToNotify);
                }
            }
        }

        // Adds recipient who previously commented on this post
        List<Long> commentToCommentedRecipients = new ArrayList<Long>();
        for (long commentorId : commentorsMapper.execute(activityId))
        {
            if (commentorId != postAuthor && !coordinatorIds.contains(commentorId) && commentorId != inActorId)
            {
                allRecipients.add(commentorId);
                commentToCommentedRecipients.add(commentorId);

                // this recipient list will keep replacing the old value in the map when new recipients are found
                recipientsByType.put(NotificationType.COMMENT_TO_COMMENTED_POST, commentToCommentedRecipients);
            }
        }

        // Add people who saved post as recipients
        List<Long> commentToSaversRecipients = new ArrayList<Long>();
        for (long saverId : saversMapper.execute(activityId))
        {
            if (saverId != inActorId && !allRecipients.contains(saverId))
            {
                commentToSaversRecipients.add(saverId);
                allRecipients.add(saverId);

                // this recipient list will keep replacing the old value in the map when new recipients are found
                recipientsByType.put(NotificationType.COMMENT_TO_SAVED_POST, commentToSaversRecipients);
            }
        }

        // Build notifications
        List<NotificationDTO> notifications = new ArrayList<NotificationDTO>();
        for (NotificationType notificationType : recipientsByType.keySet())
        {
            NotificationDTO notif = new NotificationDTO(recipientsByType.get(notificationType), notificationType,
                    inActorId);
            notif.setActivity(activityId, activity.getBaseObjectType());
            StreamEntityDTO dest = activity.getDestinationStream();
            notif.setDestination(dest.getId(), dest.getType(), dest.getUniqueIdentifier(), dest.getDisplayName());
            notif.setCommentId(inCommentId);
            if (notif.getType().equals(NotificationType.COMMENT_TO_COMMENTED_POST)
                    || notif.getType().equals(NotificationType.COMMENT_TO_SAVED_POST))
            {
                StreamEntityDTO author = activity.getActor();
                notif.setAuxiliary(author.getType(), author.getUniqueIdentifier(), author.getDisplayName());
            }
            notifications.add(notif);
        }

        return notifications;
    }
}
