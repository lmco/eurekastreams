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
import org.eurekastreams.server.action.request.notification.TargetEntityNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Translates the event of someone requesting access to a private group to appropriate notifications.
 */
public class RequestGroupAccessTranslator implements NotificationTranslator<TargetEntityNotificationsRequest>
{
    /**
     * Mapper to get group coordinator ids.
     */
    private final DomainMapper<Long, List<Long>> coordinatorDAO;

    /**
     * Constructor.
     *
     * @param inCoordinatorDAO
     *            coordinator mapper to set.
     */
    public RequestGroupAccessTranslator(final DomainMapper<Long, List<Long>> inCoordinatorDAO)
    {
        coordinatorDAO = inCoordinatorDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationBatch translate(final TargetEntityNotificationsRequest inRequest)
    {
        List<Long> coordinatorIds = coordinatorDAO.execute(inRequest.getTargetEntityId());
        // actor cannot be a recipient - if they were a group coordinator, they wouldn't and couldn't be asking for
        // access, hence we don't need to filter

        NotificationBatch batch = new NotificationBatch(NotificationType.REQUEST_GROUP_ACCESS, coordinatorIds);
        batch.setProperty(NotificationPropertyKeys.ACTOR, PersonModelView.class, inRequest.getActorId());
        batch.setProperty("group", DomainGroupModelView.class, inRequest.getTargetEntityId());
        // TODO: add appropriate SOURCE property
        batch.setProperty(NotificationPropertyKeys.HIGH_PRIORITY, true);
        // TODO: add URL property for whatever page pending group access requests go on
        return batch;
    }
}
