/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
import java.util.List;

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.execution.notification.NotificationPropertyKeys;
import org.eurekastreams.server.action.request.notification.TargetEntityNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;

/**
 * Translates pending group approval events.
 */
public class PendingGroupApprovedTranslator implements NotificationTranslator<TargetEntityNotificationsRequest>
{
    /** Group DAO. */
    private final DomainMapper<Long, DomainGroupModelView> groupDAO;

    /** Group coordinator DAO. */
    private final DomainMapper<Long, List<Long>> groupCoordinatorDAO;

    /**
     * Constructor.
     *
     * @param inGroupDAO
     *            Group DAO.
     * @param inGroupCoordinatorDAO
     *            Group coordinator DAO.
     */
    public PendingGroupApprovedTranslator(final DomainMapper<Long, DomainGroupModelView> inGroupDAO,
            final DomainMapper<Long, List<Long>> inGroupCoordinatorDAO)
    {
        groupDAO = inGroupDAO;
        groupCoordinatorDAO = inGroupCoordinatorDAO;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationBatch translate(final TargetEntityNotificationsRequest inRequest)
    {
        DomainGroupModelView group = groupDAO.execute(inRequest.getTargetEntityId());
        Collection<Long> recipientIds = groupCoordinatorDAO.execute(inRequest.getTargetEntityId());

        NotificationBatch batch = new NotificationBatch(NotificationType.REQUEST_NEW_GROUP_APPROVED, recipientIds);
        batch.setProperty("group", group);
        batch.setProperty(NotificationPropertyKeys.HIGH_PRIORITY, true);
        return batch;
    }
}
