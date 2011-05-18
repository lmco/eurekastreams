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
public class CommentTranslator implements NotificationTranslator<CommentNotificationsRequest>
{
    /** Mapper to get commentors. */
    private final GetCommentorIdsByActivityId commentorsMapper;

    /** Mapper to get activity details. */
    private final DomainMapper<List<Long>, List<ActivityDTO>> activitiesMapper;

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
     * @param inCommentsMapper
     *            Mapper to get the comment.
     * @param inSavedMapper
     *            Mapper to get people who saved an activity.
     */
    public CommentTranslator(final GetCommentorIdsByActivityId inCommentorsMapper,
            final DomainMapper<List<Long>, List<ActivityDTO>> inActivitiesMapper,
            final DomainMapper<List<Long>, List<CommentDTO>> inCommentsMapper,
            final DomainMapper<Long, List<Long>> inSavedMapper)
    {
        commentorsMapper = inCommentorsMapper;
        activitiesMapper = inActivitiesMapper;
        commentsMapper = inCommentsMapper;
        saversMapper = inSavedMapper;
    }

    /**
     * Gets a list of people to notify when a new comment is added.
     *
     * @param inRequest
     *            Event data.
     * @return List of notifications generated.
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
        final CommentDTO comment = commentList.get(0);
        final long activityId = comment.getActivityId();
        List<ActivityDTO> activities = activitiesMapper.execute(Collections.singletonList(activityId));
        if (activities.isEmpty())
        {
            return null;
        }
        final ActivityDTO activity = activities.get(0);
        final long actorId = inRequest.getActorId();
        final long destinationId = inRequest.getDestinationId();

        NotificationBatch batch = new NotificationBatch();
        List<Long> allRecipients = new ArrayList<Long>();

        // Adds post author as recipient
        long postAuthor = activity.getActor().getId();
        if (postAuthor != actorId)
        {
            batch.setRecipient(NotificationType.COMMENT_TO_PERSONAL_POST, postAuthor);
            allRecipients.add(postAuthor);
        }

        // Adds stream owner as a recipient
        if (destinationId != postAuthor && destinationId != actorId)
        {
            batch.setRecipient(NotificationType.COMMENT_TO_PERSONAL_STREAM, destinationId);
            allRecipients.add(destinationId);
        }

        // Adds recipient who previously commented on this post
        List<Long> commentToCommentedRecipients = new ArrayList<Long>();
        for (long commentorId : commentorsMapper.execute(activityId))
        {
            if (commentorId != postAuthor && commentorId != destinationId && commentorId != actorId)
            {
                commentToCommentedRecipients.add(commentorId);
                allRecipients.add(commentorId);

                // this recipient list will keep replacing the old value in the map when new recipients are found
                batch.getRecipients().put(NotificationType.COMMENT_TO_COMMENTED_POST, commentToCommentedRecipients);
            }
        }

        // Add people who saved post as recipients
        List<Long> commentToSaversRecipients = new ArrayList<Long>();
        for (long saverId : saversMapper.execute(activityId))
        {
            if (saverId != actorId && !allRecipients.contains(saverId))
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
