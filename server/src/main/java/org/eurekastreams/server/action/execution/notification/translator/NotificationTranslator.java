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

import java.util.Collection;

import org.eurekastreams.server.domain.NotificationDTO;

/**
 * Interface for strategies which convert a notification request into a list of notifications to send.
 */
public interface NotificationTranslator
{
    /**
     * Creates a list of notifications for the given event.
     * 
     * @param actorId
     *            ID of the entity (person) who performed the action which the notification is about.
     * @param destinationId
     *            ID of the entity (person or group) upon whom or upon whose stream that the action acted.
     * @param activityId
     *            ID of the activity the event pertained to.
     * @return List of notifications.
     */
    Collection<NotificationDTO> translate(final long actorId, final long destinationId, final long activityId);
}
