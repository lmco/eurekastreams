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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Translates the event of someone posting to a group stream to appropriate notifications.
 */
public class GroupStreamPostTranslator implements NotificationTranslator
{
    /** Mapper to get list of members of a group. */
    private final DomainMapper<Long, List<Long>> memberMapper;

    /**
     * Constructor.
     *
     * @param inMemberMapper
     *            Mapper to get list of members of a group.
     */
    public GroupStreamPostTranslator(final DomainMapper<Long, List<Long>> inMemberMapper)
    {
        memberMapper = inMemberMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<NotificationDTO> translate(final long inActorId, final long inDestinationId,
            final long inActivityId)
    {
        List<Long> memberIdsToNotify = memberMapper.execute(inDestinationId);
        return memberIdsToNotify.isEmpty() ? Collections.EMPTY_LIST : Collections.singletonList(new NotificationDTO(
                memberIdsToNotify,
                NotificationType.POST_TO_JOINED_GROUP, inActorId, inDestinationId, EntityType.GROUP, inActivityId));
    }
}
