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
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Translates the event of someone beginning to follow a group stream to appropriate notifications.
 */
public class GroupFollowerTranslator implements NotificationTranslator
{
    /**
     * Mapper to get group coordinator ids.
     */
    private final DomainMapper<Long, List<Long>> coordinatorMapper;

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
    public NotificationBatch translate(final CreateNotificationsRequest inRequest)
    {
        List<Long> coordinatorIds = coordinatorMapper.execute(inRequest.getDestinationId());
        if (coordinatorIds.contains(inRequest.getActorId()))
        {
            // Don't sent notification to the actor (if a group coordinator follows their own group).
            // Clone the list, since the mapper contract doesn't specify if the caller owns the list and thus can alter
            // it, or whether it belongs to the mapper.
            coordinatorIds = new ArrayList<Long>(coordinatorIds);
            coordinatorIds.remove(inRequest.getActorId());
        }

        NotificationBatch batch = new NotificationBatch(NotificationType.FOLLOW_GROUP, coordinatorIds);
        batch.setProperty("actor", PersonModelView.class, inRequest.getActorId());
        batch.setProperty("streamEntity", DomainGroupModelView.class, inRequest.getDestinationId());
        // TODO: add appropriate properties
        return batch;
    }
}
