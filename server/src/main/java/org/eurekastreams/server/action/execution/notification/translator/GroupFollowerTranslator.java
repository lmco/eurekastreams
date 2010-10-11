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
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Translates the event of someone beginning to follow a group stream to appropriate notifications.
 */
public class GroupFollowerTranslator implements NotificationTranslator
{
    /**
     * Mapper to get group coordinator ids.
     */
    private DomainMapper<Long, List<Long>> coordinatorMapper;

    /**
     * Constructor.
     * 
     * @param inCoordinatorMapper
     *            coordinator mapper to set.
     */
    public GroupFollowerTranslator(final DomainMapper<Long, List<Long>> inCoordinatorMapper)
    {
        coordinatorMapper = inCoordinatorMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<NotificationDTO> translate(final long inActorId, final long inDestinationId,
            final long inActivityId)
    {
        List<Long> coordinatorIds = coordinatorMapper.execute(inDestinationId);
        if (coordinatorIds.contains(inActorId))
        {
            // Don't sent notification to the actor (if a group coordinator follows their own group).
            // Clone the list, since the mapper contract doesn't specify if the caller owns the list and thus can alter
            // it, or whether it belongs to the mapper.
            coordinatorIds = new ArrayList<Long>(coordinatorIds);
            coordinatorIds.remove(inActorId);
        }

        return (Collections.singletonList(new NotificationDTO(coordinatorIds, NotificationType.FOLLOW_GROUP, inActorId,
                inDestinationId, EntityType.GROUP, 0L)));
    }
}
