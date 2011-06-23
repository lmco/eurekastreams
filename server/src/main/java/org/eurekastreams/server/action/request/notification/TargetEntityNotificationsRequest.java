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
 * Request to generate notifications for an action pertaining to a stream or stream's entity.
 */
public class TargetEntityNotificationsRequest extends CreateNotificationsRequest
{
    /** Fingerprint. */
    private static final long serialVersionUID = 7293258744162957736L;

    /** ID of the entity (person, group, etc.) who was acted upon or whose stream was affected. */
    private final long targetEntityId;

    /**
     * Constructor.
     *
     * @param inType
     *            Type of event that occurred.
     * @param inActorId
     *            ID of the entity (person) who performed the action which the notification is about.
     * @param inTargetEntityId
     *            ID of the entity (person, group, etc.) who was acted upon or whose stream was affected.
     */
    public TargetEntityNotificationsRequest(final RequestType inType, final long inActorId, final long inTargetEntityId)
    {
        super(inType, inActorId);
        targetEntityId = inTargetEntityId;
    }

    /**
     * @return ID of the entity (person, group, etc.) who was acted upon or whose stream was affected.
     */
    public long getTargetEntityId()
    {
        return targetEntityId;
    }
}
