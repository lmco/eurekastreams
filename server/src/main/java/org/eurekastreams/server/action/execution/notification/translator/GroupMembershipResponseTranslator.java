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

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.execution.notification.NotificationPropertyKeys;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.action.request.notification.GroupMembershipResponseNotificationsRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.service.utility.ui.UiUrlBuilder;

/**
 * Translates the event of a coordinator approving or denying a private group membership request to to the appropriate
 * notification.
 */
public class GroupMembershipResponseTranslator implements
        NotificationTranslator<GroupMembershipResponseNotificationsRequest>
{
    /** Notification type to generate. */
    private final NotificationType type;

    /** DAO to get the group's unique id. */
    private final DomainMapper<Long, String> idToUniqueIdDAO;

    /**
     * Constructor.
     *
     * @param inType
     *            Notification type to generate.
     * @param inIdToUniqueIdDAO
     *            DAO to get the group's unique id.
     */
    public GroupMembershipResponseTranslator(final NotificationType inType,
            final DomainMapper<Long, String> inIdToUniqueIdDAO)
    {
        type = inType;
        idToUniqueIdDAO = inIdToUniqueIdDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationBatch translate(final GroupMembershipResponseNotificationsRequest inRequest)
    {
        NotificationBatch batch = new NotificationBatch(type, inRequest.getRequestorId());
        batch.setProperty("group", DomainGroupModelView.class, inRequest.getTargetEntityId());
        batch.setPropertyAlias(NotificationPropertyKeys.ACTOR, "group");
        batch.setPropertyAlias(NotificationPropertyKeys.SOURCE, "group");

        if (inRequest.getType() == RequestType.REQUEST_GROUP_ACCESS_APPROVED)
        {
            batch.setProperty(
                    NotificationPropertyKeys.URL,
                    UiUrlBuilder.relativeUrlForEntity(EntityType.GROUP,
                            idToUniqueIdDAO.execute(inRequest.getTargetEntityId())));
        }

        return batch;
    }
}
