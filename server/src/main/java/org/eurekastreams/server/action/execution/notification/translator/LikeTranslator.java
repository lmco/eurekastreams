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

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.NotificationType;

/**
 * Translates the event of someone liking an activity to appropriate notifications.
 */
public class LikeTranslator implements NotificationTranslator
{
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<NotificationDTO> translate(final long inActorId, final long inDestinationId,
            final long inActivityId)
    {
        if (inActorId == inDestinationId)
        {
            return Collections.EMPTY_LIST;
        }

        return Collections.singletonList(new NotificationDTO(Collections.singletonList(inDestinationId),
                NotificationType.LIKE_ACTIVITY, inActorId, inDestinationId, EntityType.PERSON, inActivityId));
    }
}
