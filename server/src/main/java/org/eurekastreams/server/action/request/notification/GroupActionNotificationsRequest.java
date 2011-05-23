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
 * Request to generate notifications for an action on a group.
 */
public class GroupActionNotificationsRequest extends CreateNotificationsRequest
{
    /** Group acted on. */
    private final long groupId;

    /**
     * Constructor.
     *
     * @param inType
     *            Event type.
     * @param inActorId
     *            Actor entity id.
     * @param inGroupId
     *            Group id.
     */
    public GroupActionNotificationsRequest(final RequestType inType, final long inActorId, final long inGroupId)
    {
        super(inType, inActorId, 0L, 0L);
        groupId = inGroupId;
    }

    /**
     * @return the groupId
     */
    public long getGroupId()
    {
        return groupId;
    }
}
