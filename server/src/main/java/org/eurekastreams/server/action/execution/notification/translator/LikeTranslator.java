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
import org.eurekastreams.server.action.request.notification.ActivityNotificationsRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.utility.ui.UiUrlBuilder;

/**
 * Translates the event of someone liking an activity to appropriate notifications.
 */
public class LikeTranslator implements NotificationTranslator<ActivityNotificationsRequest>
{
    /** For getting activity info. */
    private final DomainMapper<Long, ActivityDTO> activityDAO;

    /**
     * Constructor.
     *
     * @param inActivityDAO
     *            For getting activity info.
     */
    public LikeTranslator(final DomainMapper<Long, ActivityDTO> inActivityDAO)
    {
        activityDAO = inActivityDAO;
    }

    /**
     * Adds the person for the given entity to the recipient list, if ok to do so.
     *
     * @param entity
     *            Entity from activity (may be null).
     * @param actorId
     *            ID of person who liked activity.
     * @param recipients
     *            List of recipients.
     */
    private void addAuthorIfAppropriate(final StreamEntityDTO entity, final long actorId, final List<Long> recipients)
    {
        if (entity != null && EntityType.PERSON.equals(entity.getType()) && actorId != entity.getId())
        {
            recipients.add(entity.getId());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationBatch translate(final ActivityNotificationsRequest inRequest)
    {
        ActivityDTO activity = activityDAO.execute(inRequest.getActivityId());

        List<Long> recipients = new ArrayList<Long>();
        addAuthorIfAppropriate(activity.getActor(), inRequest.getActorId(), recipients);
        addAuthorIfAppropriate(activity.getOriginalActor(), inRequest.getActorId(), recipients);
        if (recipients.isEmpty())
        {
            return null;
        }

        NotificationBatch batch = new NotificationBatch(NotificationType.LIKE_ACTIVITY, recipients);
        batch.setProperty(NotificationPropertyKeys.ACTOR, PersonModelView.class, inRequest.getActorId());
        batch.setProperty("stream", activity.getDestinationStream());
        batch.setProperty("activity", activity);
        batch.setPropertyAlias(NotificationPropertyKeys.SOURCE, "stream");
        batch.setProperty(NotificationPropertyKeys.URL, UiUrlBuilder.relativeUrlForActivity(inRequest.getActivityId()));
        return batch;
    }
}
