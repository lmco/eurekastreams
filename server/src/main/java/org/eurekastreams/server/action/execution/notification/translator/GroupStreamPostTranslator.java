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
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Translates the event of someone posting to a group stream to appropriate notifications.
 */
public class GroupStreamPostTranslator implements NotificationTranslator<CreateNotificationsRequest>
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
    public NotificationBatch translate(final CreateNotificationsRequest inRequest)
    {
        // NOTE: This code assumes that the mapper returns a list which can be safely altered, specificially that it is
        // not used elsewhere (e.g. stored off) and supports removing elements.
        List<Long> memberIdsToNotify = memberMapper.execute(inRequest.getDestinationId());
        memberIdsToNotify.remove(inRequest.getActorId());

        if (memberIdsToNotify.isEmpty())
        {
            return null;
        }

        NotificationBatch batch = new NotificationBatch(NotificationType.POST_TO_JOINED_GROUP, memberIdsToNotify);
        batch.setProperty("actor", PersonModelView.class, inRequest.getActorId());
        batch.setProperty("streamEntity", DomainGroupModelView.class, inRequest.getDestinationId());
        batch.setProperty("activity", ActivityDTO.class, inRequest.getActivityId());
        // TODO: add appropriate properties
        return batch;
    }
}
