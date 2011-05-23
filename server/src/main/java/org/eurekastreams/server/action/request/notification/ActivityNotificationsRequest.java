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
package org.eurekastreams.server.action.request.notification;

/**
 * Request to generate notifications for an action on an activity.
 */
public class ActivityNotificationsRequest extends CreateNotificationsRequest
{
    // NOTE: Currently empty, but several of the fields will move down from CreateNotificationsRequest eventually.

    /**
     * Constructor.
     * 
     * @param inType
     *            Type of event that occurred.
     * @param inActorId
     *            ID of the entity (person) who performed the action which the notification is about.
     * @param inDestinationId
     *            ID of the entity (person or group) who owns the stream containing the activity.
     * @param inActivityId
     *            ID of the activity the event pertained to.
     */
    public ActivityNotificationsRequest(final RequestType inType, final long inActorId, final long inDestinationId,
            final long inActivityId)
    {
        super(inType, inActorId, inDestinationId, inActivityId);
    }
}
