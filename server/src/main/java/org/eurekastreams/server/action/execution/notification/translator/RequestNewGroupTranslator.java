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
import java.util.Set;

import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.GetOrgCoordinators;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

/**
 * Translates the event of someone requesting creation of a group to appropriate notifications.
 */
public class RequestNewGroupTranslator implements NotificationTranslator
{
    /** For getting the group. Need to use this mapper, since the bulk group mapper excludes pending groups. */
    // TODO: Should use GetDomainGroupsByIds, but it excludes pending groups, which are the only ones we want.
    private FindByIdMapper<DomainGroup> groupMapper;

    /** For getting the org coordinators. */
    private GetOrgCoordinators orgCoordMapper;

    /**
     * Constructor.
     *
     * @param inGroupMapper
     *            For getting the group.
     * @param inOrgCoordMapper
     *            For getting the org coordinators.
     */
    public RequestNewGroupTranslator(final FindByIdMapper<DomainGroup> inGroupMapper,
            final GetOrgCoordinators inOrgCoordMapper)
    {
        groupMapper = inGroupMapper;
        orgCoordMapper = inOrgCoordMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<NotificationDTO> translate(final long inActorId, final long inDestinationId,
            final long inActivityId)
    {
        // TODO: The "activityId" is being used loosely as the "thing being acted on", hence the group being created.
        // Should refactor notifications for more flexible parameters.
        DomainGroup group = groupMapper.execute(new FindByIdRequest("DomainGroup", inActivityId));

        Set<Long> coordinators = orgCoordMapper.execute(inDestinationId);

        NotificationDTO notif =
                new NotificationDTO(new ArrayList<Long>(coordinators), NotificationType.REQUEST_NEW_GROUP, inActorId,
                        inDestinationId, EntityType.ORGANIZATION, 0L);
        notif.setAuxiliary(EntityType.GROUP, group.getShortName(), group.getName());
        return Collections.singletonList(notif);
    }
}
