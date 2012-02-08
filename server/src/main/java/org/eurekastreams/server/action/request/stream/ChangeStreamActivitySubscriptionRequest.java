/*
 * Copyright (c) 2010-2012 Lockheed Martin Corporation
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

package org.eurekastreams.server.action.request.stream;

import java.io.Serializable;

import org.eurekastreams.server.domain.EntityType;

/**
 * Request for changing stream activity subscription notifications.
 */
public class ChangeStreamActivitySubscriptionRequest implements Serializable
{
    /** Serial version uid. */
    private static final long serialVersionUID = -3057266450773584546L;

    /** Entity type of stream. */
    private EntityType entityType;

    /** The unique ID of the stream entity. */
    private String streamEntityUniqueId;

    /** Whether the user wants to receive new activity notifications. */
    private boolean receiveNewActivityNotifications;

    /** If notifications should be generated for all posts, or just ones from coordinators. */
    private boolean coordinatorOnlyNotifications;

    /**
     * Default constructor.
     */
    public ChangeStreamActivitySubscriptionRequest()
    {
    }

    /**
     * Constructor.
     * 
     * @param inEntityType
     *            Entity type of stream.
     * @param inStreamEntityUniqueId
     *            The unique ID of the stream entity.
     * @param inReceiveNewActivityNotifications
     *            Whether the user wants to receive new activity notifications.
     * @param inCoordinatorOnlyNotifications
     *            Subscribe for coordinator posts only.
     */
    public ChangeStreamActivitySubscriptionRequest(final EntityType inEntityType, final String inStreamEntityUniqueId,
            final boolean inReceiveNewActivityNotifications, final boolean inCoordinatorOnlyNotifications)
    {
        entityType = inEntityType;
        streamEntityUniqueId = inStreamEntityUniqueId;
        receiveNewActivityNotifications = inReceiveNewActivityNotifications;
        coordinatorOnlyNotifications = inCoordinatorOnlyNotifications;
    }

    /**
     * @return the receiveNewActivityNotifications
     */
    public boolean getReceiveNewActivityNotifications()
    {
        return receiveNewActivityNotifications;
    }

    /**
     * @param inReceiveNewActivityNotifications
     *            the receiveNewActivityNotifications to set
     */
    public void setReceiveNewActivityNotifications(final boolean inReceiveNewActivityNotifications)
    {
        receiveNewActivityNotifications = inReceiveNewActivityNotifications;
    }

    /**
     * @return the streamEntityUniqueId
     */
    public String getStreamEntityUniqueId()
    {
        return streamEntityUniqueId;
    }

    /**
     * @param inStreamEntityUniqueId
     *            the streamEntityUniqueId to set
     */
    public void setStreamEntityUniqueId(final String inStreamEntityUniqueId)
    {
        streamEntityUniqueId = inStreamEntityUniqueId;
    }

    /**
     * @return the entityType
     */
    public EntityType getEntityType()
    {
        return entityType;
    }

    /**
     * @return If notifications should be generated for all posts, or just ones from coordinators.
     */
    public boolean getCoordinatorOnlyNotifications()
    {
        return coordinatorOnlyNotifications;
    }

    /**
     * @param inCoordinatorOnlyNotifications
     *            If notifications should be generated for all posts, or just ones from coordinators.
     */
    public void setCoordinatorOnlyNotifications(final boolean inCoordinatorOnlyNotifications)
    {
        coordinatorOnlyNotifications = inCoordinatorOnlyNotifications;
    }
}
