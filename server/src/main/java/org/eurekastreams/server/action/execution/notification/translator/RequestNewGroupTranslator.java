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
import org.eurekastreams.server.action.execution.notification.NotificationPropertyKeys;
import org.eurekastreams.server.action.request.notification.TargetEntityNotificationsRequest;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.utility.ui.UiUrlBuilder;

/**
 * Translates the event of someone requesting creation of a group to appropriate notifications.
 */
public class RequestNewGroupTranslator implements NotificationTranslator<TargetEntityNotificationsRequest>
{
    /** For getting the group. Need to use this mapper, since the bulk group mapper excludes pending groups. */
    // TODO: Should use GetDomainGroupsByIds, but it excludes pending groups, which are the only ones we want.
    private final FindByIdMapper<DomainGroup> groupDAO;

    /** For getting the org coordinators. */
    private final DomainMapper<Serializable, List<Long>> systemAdminIdsDAO;

    /**
     * Constructor.
     *
     * @param inGroupDAO
     *            For getting the group.
     * @param inSystemAdminIdsDAO
     *            Mapper for getting system admin ids
     */
    public RequestNewGroupTranslator(final FindByIdMapper<DomainGroup> inGroupDAO,
            final DomainMapper<Serializable, List<Long>> inSystemAdminIdsDAO)
    {
        groupDAO = inGroupDAO;
        systemAdminIdsDAO = inSystemAdminIdsDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationBatch translate(final TargetEntityNotificationsRequest inRequest)
    {
        DomainGroup group = groupDAO.execute(new FindByIdRequest("DomainGroup", inRequest.getTargetEntityId()));

        List<Long> admins = systemAdminIdsDAO.execute(null);

        NotificationBatch batch = new NotificationBatch(NotificationType.REQUEST_NEW_GROUP, admins);
        batch.setProperty(NotificationPropertyKeys.ACTOR, PersonModelView.class, inRequest.getActorId());
        batch.setProperty("group", group);
        batch.setPropertyAlias(NotificationPropertyKeys.SOURCE, "group");
        batch.setProperty(NotificationPropertyKeys.HIGH_PRIORITY, true);
        batch.setProperty(NotificationPropertyKeys.URL, UiUrlBuilder.relativeUrlForPendingGroupRequest());
        return batch;
    }
}
