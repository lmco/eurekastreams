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

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.execution.notification.NotificationPropertyKeys;
import org.eurekastreams.server.action.request.notification.GroupRemovedNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;

/**
 * Translates pending group denial events.
 */
public class PendingGroupDeniedTranslator implements NotificationTranslator<GroupRemovedNotificationsRequest>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationBatch translate(final GroupRemovedNotificationsRequest inRequest)
    {
        NotificationBatch batch = new NotificationBatch(NotificationType.REQUEST_NEW_GROUP_DENIED,
                inRequest.getCoordinatorIds());
        batch.setProperty("groupName", inRequest.getGroupName());
        batch.setProperty(NotificationPropertyKeys.HIGH_PRIORITY, true);
        return batch;
    }
}
