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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Translates the event of someone liking an activity to appropriate notifications.
 */
public class LikeTranslator implements NotificationTranslator
{
    /** For getting activity info. */
    private final DomainMapper<List<Long>, List<ActivityDTO>> activitiesMapper;

    /**
     * Constructor.
     *
     * @param inActivitiesMapper
     *            Mapper for fetching activity info.
     */
    public LikeTranslator(final DomainMapper<List<Long>, List<ActivityDTO>> inActivitiesMapper)
    {
        activitiesMapper = inActivitiesMapper;
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
    public Collection<NotificationDTO> translate(final long inActorId, final long inDestinationId,
            final long inActivityId)
    {
        ActivityDTO activity = activitiesMapper.execute(Collections.singletonList(inActivityId)).get(0);
        List<Long> recipients = new ArrayList<Long>();

        addAuthorIfAppropriate(activity.getActor(), inActorId, recipients);
        addAuthorIfAppropriate(activity.getOriginalActor(), inActorId, recipients);
        if (recipients.isEmpty())
        {
            return Collections.EMPTY_LIST;
        }

        StreamEntityDTO destStream = activity.getDestinationStream();
        return Collections.singletonList(new NotificationDTO(recipients, NotificationType.LIKE_ACTIVITY, inActorId,
                destStream.getDestinationEntityId(), destStream.getType(), inActivityId));
    }
}
