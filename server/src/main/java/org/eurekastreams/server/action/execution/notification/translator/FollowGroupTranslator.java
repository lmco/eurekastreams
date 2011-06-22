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
import org.eurekastreams.server.action.request.notification.TargetEntityNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Translates the event of someone beginning to follow a group stream to appropriate notifications.
 */
public class FollowGroupTranslator implements NotificationTranslator<TargetEntityNotificationsRequest>
{
    /** DAO to get group coordinator ids. */
    private final DomainMapper<Long, List<Long>> coordinatorDAO;

    /**
     * Constructor.
     *
     * @param inCoordinatorDAO
     *            DAO to get group coordinator ids.
     */
    public FollowGroupTranslator(final DomainMapper<Long, List<Long>> inCoordinatorDAO)
    {
        coordinatorDAO = inCoordinatorDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationBatch translate(final TargetEntityNotificationsRequest inRequest)
    {
        List<Long> coordinatorIds = coordinatorDAO.execute(inRequest.getTargetEntityId());

        // When a user is added as a group coordinator, they are also added as a group member. Filter out these
        // notifications.
        if (coordinatorIds.contains(inRequest.getActorId()))
        {
            return null;
        }

        NotificationBatch batch = new NotificationBatch(NotificationType.FOLLOW_GROUP, coordinatorIds);
        batch.setProperty(NotificationPropertyKeys.ACTOR, PersonModelView.class, inRequest.getActorId());
        batch.setProperty("stream", DomainGroupModelView.class, inRequest.getTargetEntityId());
        batch.setPropertyAlias(NotificationPropertyKeys.SOURCE, "stream");
        return batch;
    }
}
