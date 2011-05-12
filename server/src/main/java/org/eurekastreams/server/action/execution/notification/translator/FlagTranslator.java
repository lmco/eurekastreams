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

import java.io.Serializable;
import java.util.Arrays;
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
 * Translates the event of someone flagging an activity to appropriate notifications.
 */
public class FlagTranslator implements NotificationTranslator
{
    /** For getting activity details. */
    private DomainMapper<List<Long>, List<ActivityDTO>> activitiesMapper;

    /**
     * Mapper to get a list of system admin ids.
     */
    private DomainMapper<Serializable, List<Long>> systemAdminMapper;

    /**
     * Constructor.
     * 
     * @param inActivitiesMapper
     *            For getting activity details.
     * @param inSystemAdminMapper
     *            mapper to get all the system admin ids
     */
    public FlagTranslator(final DomainMapper<List<Long>, List<ActivityDTO>> inActivitiesMapper,
            final DomainMapper<Serializable, List<Long>> inSystemAdminMapper)
    {
        activitiesMapper = inActivitiesMapper;
        systemAdminMapper = inSystemAdminMapper;
    }

    /**
     * This method takes the activity and gets a list of all the org coordinators who are responsible for the person or
     * group to whose stream the activity was posted. Those will be the recipients.
     * 
     * {@inheritDoc}
     */
    @Override
    public Collection<NotificationDTO> translate(final long inActorId, final long inDestinationId,
            final long inActivityId)
    {
        // Get the activity
        ActivityDTO activity = activitiesMapper.execute(Arrays.asList(inActivityId)).get(0);
        StreamEntityDTO stream = activity.getDestinationStream();
        NotificationType type = EntityType.PERSON == stream.getType() ? NotificationType.FLAG_PERSONAL_ACTIVITY
                : NotificationType.FLAG_GROUP_ACTIVITY;

        // Get the list of admins
        List<Long> adminIds = systemAdminMapper.execute(null);

        NotificationDTO notif = new NotificationDTO(adminIds, type, inActorId);
        notif.setDestination(activity.getRecipientParentOrgId(), EntityType.ORGANIZATION);
        notif.setActivity(inActivityId, activity.getBaseObjectType());

        return Collections.singletonList(notif);
    }
}
