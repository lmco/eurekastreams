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

import java.util.Collection;

/**
 * Request to generate notifications when a group is removed (such as creation denied). Any needed data must be captured
 * in the request, since the group will be deleted and thus cannot be looked up by the notifiers.
 */
public class GroupRemovedNotificationsRequest extends CreateNotificationsRequest
{
    /** Group acted on. */
    private final String groupName;

    /** Group coordinators. */
    private final Collection<Long> coordinatorIds;

    /**
     * Constructor.
     * 
     * @param inType
     *            Event type.
     * @param inActorId
     *            Actor entity ID.
     * @param inGroupName
     *            Name of group.
     * @param inCoordinatorIds
     *            Group coordinators.
     */
    public GroupRemovedNotificationsRequest(final RequestType inType, final long inActorId, final String inGroupName,
            final Collection<Long> inCoordinatorIds)
    {
        super(inType, inActorId, 0L, 0L);
        groupName = inGroupName;
        coordinatorIds = inCoordinatorIds;
    }

    /**
     * @return the groupName
     */
    public String getGroupName()
    {
        return groupName;
    }

    /**
     * @return the coordinatorIds
     */
    public Collection<Long> getCoordinatorIds()
    {
        return coordinatorIds;
    }
}
