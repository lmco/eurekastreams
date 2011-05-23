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

import java.util.ArrayList;
import java.util.Collection;

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.request.notification.GroupActionNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Translates pending group approval events.
 */
public class PendingGroupApprovedTranslator implements NotificationTranslator<GroupActionNotificationsRequest>
{
    /** Group mapper. */
    private final DomainMapper<Long, DomainGroupModelView> groupMapper;

    /**
     * Constructor.
     *
     * @param inGroupMapper
     *            Group mapper.
     */
    public PendingGroupApprovedTranslator(final DomainMapper<Long, DomainGroupModelView> inGroupMapper)
    {
        groupMapper = inGroupMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationBatch translate(final GroupActionNotificationsRequest inRequest)
    {
        DomainGroupModelView group = groupMapper.execute(inRequest.getGroupId());

        Collection<Long> recipientIds = new ArrayList<Long>();
        for (PersonModelView coordinator : group.getCoordinators())
        {
            recipientIds.add(coordinator.getId());
        }

        NotificationBatch batch = new NotificationBatch(NotificationType.REQUEST_NEW_GROUP_APPROVED, recipientIds);
        batch.setProperty("group", group);
        return batch;
    }
}
