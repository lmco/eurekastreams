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

import java.util.List;

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.execution.notification.NotificationPropertyKeys;
import org.eurekastreams.server.action.request.notification.ActivityNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.utility.ui.UiUrlBuilder;

/**
 * Translates the event of someone posting to a stream to appropriate notifications.
 */
public class PostPersonStreamTranslator implements NotificationTranslator<ActivityNotificationsRequest>
{
    /** DAO to get list of followers of a stream. */
    private final DomainMapper<Long, List<Long>> followersDAO;

    /**
     * Constructor.
     *
     * @param inFollowersDAO
     *            DAO to get list of followers of a stream.
     */
    public PostPersonStreamTranslator(final DomainMapper<Long, List<Long>> inFollowersDAO)
    {
        followersDAO = inFollowersDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationBatch translate(final ActivityNotificationsRequest inRequest)
    {
        // NOTE: This code assumes that the DAO returns a list which can be safely altered, specifically that it
        // supports removing elements and is not used elsewhere (e.g. stored off).
        List<Long> followerIdsToNotify = followersDAO.execute(inRequest.getTargetEntityId());
        followerIdsToNotify.remove(inRequest.getActorId());
        followerIdsToNotify.remove(inRequest.getTargetEntityId());

        NotificationBatch batch = new NotificationBatch();
        if (inRequest.getActorId() != inRequest.getTargetEntityId())
        {
            batch.setRecipient(NotificationType.POST_TO_PERSONAL_STREAM, inRequest.getTargetEntityId());
        }
        if (!followerIdsToNotify.isEmpty())
        {
            batch.getRecipients().put(NotificationType.POST_TO_FOLLOWED_STREAM, followerIdsToNotify);
        }
        if (batch.getRecipients().isEmpty())
        {
            return null;
        }

        batch.setProperty(NotificationPropertyKeys.ACTOR, PersonModelView.class, inRequest.getActorId());
        batch.setProperty("stream", PersonModelView.class, inRequest.getTargetEntityId());
        batch.setProperty("activity", ActivityDTO.class, inRequest.getActivityId());
        batch.setPropertyAlias(NotificationPropertyKeys.SOURCE, "stream");
        batch.setProperty(NotificationPropertyKeys.URL, UiUrlBuilder.relativeUrlForActivity(inRequest.getActivityId()));
        return batch;
    }
}
