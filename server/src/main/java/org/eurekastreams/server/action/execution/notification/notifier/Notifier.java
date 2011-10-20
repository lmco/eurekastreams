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
package org.eurekastreams.server.action.execution.notification.notifier;

import java.util.Collection;
import java.util.Map;

import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Interface for notifiers -- strategies which send a notification via a particular medium (email, etc.).
 */
public interface Notifier
{
    /**
     * Sends the notification.
     * 
     * @param type
     *            Type of notification to send.
     * @param recipients
     *            Person IDs of notification recipients.
     * @param properties
     *            Properties of the notification.
     * @param recipientIndex
     *            Convenience index for looking up recipients. Generally will be a superset of the recipients.
     * @throws Exception
     *             If unable to notify user(s).
     * @return - If notifying requires queuing an action, then this returns a collection of {@link UserActionRequest}s
     *         which is/are the AsyncRequest(s) to perform. If no further action is required, then this returns null or
     *         an empty collection.
     */
    Collection<UserActionRequest> notify(NotificationType type, Collection<Long> recipients,
            Map<String, Object> properties,
            Map<Long, PersonModelView> recipientIndex) throws Exception;
}
