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
 * Request to generate notifications for group membership approved or denied.
 */
public class GroupMembershipResponseNotificationsRequest extends TargetEntityNotificationsRequest
{
    /** Fingerprint. */
    private static final long serialVersionUID = 1209883127626523554L;

    /** ID of person whose request was dispositioned. */
    private final long requestorId;

    /**
     * Constructor.
     *
     * @param inType
     *            Type of event that occurred.
     * @param inActorId
     *            ID of the entity (person) who performed the action which the notification is about.
     * @param inTargetEntityId
     *            ID of group in which membership was requested.
     * @param inRequestorId
     *            ID of person whose request was dispositioned.
     */
    public GroupMembershipResponseNotificationsRequest(final RequestType inType, final long inActorId,
            final long inTargetEntityId, final long inRequestorId)
    {
        super(inType, inActorId, inTargetEntityId);
        requestorId = inRequestorId;
    }

    /**
     * @return the requestorId
     */
    public long getRequestorId()
    {
        return requestorId;
    }
}
