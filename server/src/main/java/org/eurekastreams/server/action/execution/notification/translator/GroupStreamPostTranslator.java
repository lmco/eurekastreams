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
import java.util.List;

import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

/**
 * Translates the event of someone posting to a group stream to appropriate notifications.
 */
public class GroupStreamPostTranslator implements NotificationTranslator
{
    /**
     * Mapper to get group coordinator ids.
     */
    private DomainMapper<Long, List<Long>> coordinatorMapper;

    /** Mapper to get list of members of a group. */
    private DomainMapper<Long, List<Long>> memberMapper;

    /** Group finder. */
    private FindByIdMapper<DomainGroup> groupFinder;

    /**
     * Constructor.
     * 
     * @param inCoordinatorMapper
     *            coordinator mapper to set.
     * @param inMemberMapper
     *            Mapper to get list of members of a group.
     * @param inGroupFinder
     *            Group finder.
     */
    public GroupStreamPostTranslator(final DomainMapper<Long, List<Long>> inCoordinatorMapper,
            final DomainMapper<Long, List<Long>> inMemberMapper, final FindByIdMapper<DomainGroup> inGroupFinder)
    {
        coordinatorMapper = inCoordinatorMapper;
        memberMapper = inMemberMapper;
        groupFinder = inGroupFinder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<NotificationDTO> translate(final long inActorId, final long inDestinationId,
            final long inActivityId)
    {
        // get group notification suppression settings
        DomainGroup group = groupFinder
                .execute(new FindByIdRequest(DomainGroup.getDomainEntityName(), inDestinationId));
        if (group.isSuppressPostNotifToCoordinator() && group.isSuppressPostNotifToMember())
        {
            return Collections.EMPTY_LIST;
        }

        Collection<NotificationDTO> notifications = new ArrayList<NotificationDTO>();

        // first, send notification to coordinators of the group
        // Need to fetch coordinators even if notif is disabled so that they will not be treated as members instead
        List<Long> coordinatorIds = coordinatorMapper.execute(inDestinationId);
        if (!group.isSuppressPostNotifToCoordinator())
        {
            List<Long> coordinatorsToNotify = new ArrayList<Long>();
            for (Long coordinatorId : coordinatorIds)
            {
                if (coordinatorId != inActorId)
                {
                    coordinatorsToNotify.add(coordinatorId);
                }
            }
            if (!coordinatorsToNotify.isEmpty())
            {
                notifications.add(new NotificationDTO(coordinatorsToNotify, NotificationType.POST_TO_GROUP_STREAM,
                        inActorId, inDestinationId, EntityType.GROUP, inActivityId));
            }
        }

        // second, send notification to members of the group
        if (!group.isSuppressPostNotifToMember())
        {
            List<Long> memberIds = memberMapper.execute(inDestinationId);
            List<Long> membersToNotify = new ArrayList<Long>();
            for (Long id : memberIds)
            {
                if (id != inActorId && !coordinatorIds.contains(id))
                {
                    membersToNotify.add(id);
                }
            }
            if (!membersToNotify.isEmpty())
            {
                notifications.add(new NotificationDTO(membersToNotify, NotificationType.POST_TO_JOINED_GROUP,
                        inActorId, inDestinationId, EntityType.GROUP, inActivityId));
            }
        }

        return notifications;
    }
}
