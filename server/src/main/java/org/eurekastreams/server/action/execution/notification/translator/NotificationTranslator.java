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

import org.eurekastreams.server.action.execution.notification.NotificationBatch;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;

/**
 * Interface for strategies which convert a notification request into a list of notifications to send.
 *
 * @param <T>
 *            Type of request translator uses.
 */
public interface NotificationTranslator<T extends CreateNotificationsRequest>
{
    /**
     * Creates a list of notifications for the given event.
     * 
     * @param request
     *            The request containing information about the event that occurred.
     * @return Notification batch (includes notification types, recipients, and properties).
     */
    NotificationBatch translate(T request);
}
