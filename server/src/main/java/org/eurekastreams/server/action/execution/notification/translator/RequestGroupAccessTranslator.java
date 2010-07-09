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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.stream.GetCoordinatorIdsByGroupId;

/**
 * Translates the event of someone requesting access to a private group to appropriate notifications.
 */
public class RequestGroupAccessTranslator implements NotificationTranslator
{
    /**
     * Mapper to get group coordinator ids.
     */
    private GetCoordinatorIdsByGroupId coordinatorMapper;

    /**
     * Constructor.
     *
     * @param inCoordinatorMapper
     *            coordinator mapper to set.
     */
    public RequestGroupAccessTranslator(final GetCoordinatorIdsByGroupId inCoordinatorMapper)
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
        // actor cannot be a recipient - if they were a group coordinator, they wouldn't and couldn't be asking for
        // access, hence we don't need to filter

        return (Collections.singletonList(new NotificationDTO(coordinatorIds, NotificationType.REQUEST_GROUP_ACCESS,
                inActorId, inDestinationId, EntityType.GROUP, 0L)));
    }
}
