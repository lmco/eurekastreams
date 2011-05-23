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
import org.eurekastreams.server.action.request.notification.PrebuiltNotificationsRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.OAuthConsumer;

/**
 * Translates a request for pre-built notifications.
 */
public class PreBuiltNotificationTranslator implements NotificationTranslator<PrebuiltNotificationsRequest>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationBatch translate(final PrebuiltNotificationsRequest inRequest)
    {
        NotificationBatch batch = new NotificationBatch(NotificationType.PASS_THROUGH, inRequest.getRecipientId());
        batch.setProperty(NotificationPropertyKeys.URL, inRequest.getUrl());
        batch.setProperty(NotificationPropertyKeys.HIGH_PRIORITY, inRequest.isHighPriority());
        batch.setProperty("message", inRequest.getMessage());
        batch.setProperty(NotificationPropertyKeys.SOURCE, OAuthConsumer.class, inRequest.getClientId());
        batch.setPropertyAlias(NotificationPropertyKeys.ACTOR, NotificationPropertyKeys.SOURCE);

        return batch;
    }
}
