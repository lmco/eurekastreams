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
import java.util.List;

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.execution.notification.NotificationPropertyKeys;
import org.eurekastreams.server.action.request.notification.CommentNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.utility.ui.UiUrlBuilder;

/**
 * Translates the event of someone commenting on a post to appropriate notifications.
 */
public class CommentTranslator implements NotificationTranslator<CommentNotificationsRequest>
{
    /** DAO to get commentors. */
    private final DomainMapper<Long, List<Long>> commentorsDAO;

    /** DAO to get activity details. */
    private final DomainMapper<Long, ActivityDTO> activityDAO;

    /**
     * Constructor.
     *
     * @param inCommentorsDAO
     *            DAO to get commentors.
     * @param inActivityDAO
     *            DAO to get activity details.
     */
    public CommentTranslator(final DomainMapper<Long, List<Long>> inCommentorsDAO,
            final DomainMapper<Long, ActivityDTO> inActivityDAO)
    {
        commentorsDAO = inCommentorsDAO;
        activityDAO = inActivityDAO;
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
        final long activityId = inRequest.getActivityId();
        ActivityDTO activity = activityDAO.execute(activityId);
        if (activity == null)
        {
            return null;
        }
        final long actorId = inRequest.getActorId();

        NotificationBatch batch = new NotificationBatch();

        // Adds post author as recipient
        long postAuthor = activity.getActor().getId();
        if (postAuthor != actorId)
        {
            batch.setRecipient(NotificationType.COMMENT_TO_PERSONAL_POST, postAuthor);
        }

        // Adds recipient who previously commented on this post
        List<Long> commentToCommentedRecipients = new ArrayList<Long>();
        for (long commentorId : commentorsDAO.execute(activityId))
        {
            if (commentorId != postAuthor && commentorId != actorId)
            {
                commentToCommentedRecipients.add(commentorId);

                // this recipient list will keep replacing the old value in the map when new recipients are found
                batch.getRecipients().put(NotificationType.COMMENT_TO_COMMENTED_POST, commentToCommentedRecipients);
            }
        }

        // Add properties
        batch.setProperty(NotificationPropertyKeys.ACTOR, PersonModelView.class, actorId);
        batch.setProperty("stream", activity.getDestinationStream());
        batch.setPropertyAlias(NotificationPropertyKeys.SOURCE, "stream");
        batch.setProperty("activity", activity);
        batch.setProperty("comment", CommentDTO.class, inRequest.getCommentId());
        batch.setProperty(NotificationPropertyKeys.URL, UiUrlBuilder.relativeUrlForActivity(activityId));

        return batch;
    }
}
