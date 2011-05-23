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

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Translates the event of someone requesting creation of a group to appropriate notifications.
 */
public class RequestNewGroupTranslator implements NotificationTranslator<CreateNotificationsRequest>
{
    /** For getting the group. Need to use this mapper, since the bulk group mapper excludes pending groups. */
    // TODO: Should use GetDomainGroupsByIds, but it excludes pending groups, which are the only ones we want.
    private final FindByIdMapper<DomainGroup> groupMapper;

    /** For getting the org coordinators. */
    private final DomainMapper<Serializable, List<Long>> systemAdminIdsMapper;

    /**
     * Constructor.
     *
     * @param inGroupMapper
     *            For getting the group.
     * @param inSystemAdminIdsMapper
     *            Mapper for getting system admin ids
     */
    public RequestNewGroupTranslator(final FindByIdMapper<DomainGroup> inGroupMapper,
            final DomainMapper<Serializable, List<Long>> inSystemAdminIdsMapper)
    {
        groupMapper = inGroupMapper;
        systemAdminIdsMapper = inSystemAdminIdsMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationBatch translate(final CreateNotificationsRequest inRequest)
    {
        // TODO: The "activityId" is being used loosely as the "thing being acted on", hence the group being created.
        // Should refactor notifications for more flexible parameters.
        DomainGroup group = groupMapper.execute(new FindByIdRequest("DomainGroup", inRequest.getActivityId()));

        List<Long> admins = systemAdminIdsMapper.execute(null);

        NotificationBatch batch = new NotificationBatch(NotificationType.REQUEST_NEW_GROUP, admins);
        batch.setProperty("actor", PersonModelView.class, inRequest.getActorId());
        batch.setProperty("group", group);
        // TODO: add appropriate properties
        return batch;
    }
}
