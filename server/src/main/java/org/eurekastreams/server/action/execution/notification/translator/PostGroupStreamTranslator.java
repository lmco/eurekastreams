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
import org.eurekastreams.server.action.request.notification.ActivityNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.utility.ui.UiUrlBuilder;

/**
 * Translates the event of someone posting to a group stream to appropriate notifications.
 */
public class PostGroupStreamTranslator implements NotificationTranslator<ActivityNotificationsRequest>
{
    /** DAO to get list of members of a group. */
    private final DomainMapper<Long, List<Long>> memberDAO;

    /**
     * Constructor.
     *
     * @param inMemberDAO
     *            DAO to get list of members of a group.
     */
    public PostGroupStreamTranslator(final DomainMapper<Long, List<Long>> inMemberDAO)
    {
        memberDAO = inMemberDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationBatch translate(final ActivityNotificationsRequest inRequest)
    {
        // NOTE: This code assumes that the DAO returns a list which can be safely altered, specifically that it
        // supports removing elements and is not used elsewhere (e.g. stored off).
        List<Long> memberIdsToNotify = memberDAO.execute(inRequest.getTargetEntityId());
        memberIdsToNotify.remove(inRequest.getActorId());

        if (memberIdsToNotify.isEmpty())
        {
            return null;
        }

        NotificationBatch batch = new NotificationBatch(NotificationType.POST_TO_JOINED_GROUP, memberIdsToNotify);
        batch.setProperty(NotificationPropertyKeys.ACTOR, PersonModelView.class, inRequest.getActorId());
        batch.setProperty("stream", DomainGroupModelView.class, inRequest.getTargetEntityId());
        batch.setProperty("activity", ActivityDTO.class, inRequest.getActivityId());
        batch.setPropertyAlias(NotificationPropertyKeys.SOURCE, "stream");
        batch.setProperty(NotificationPropertyKeys.URL, UiUrlBuilder.relativeUrlForActivity(inRequest.getActivityId()));
        return batch;
    }
}
