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

package org.eurekastreams.server.persistence.mappers.requests;

/**
 * Mapper request to change a activity subscription preference for a user's followed stream.
 */
public class ChangeStreamActivitySubscriptionMapperRequest
{
    /** The id of the person subscribing. */
    private long subscriberPersonId;

    /** The id of the stream's entity (person/group). */
    private long streamEntityId;

    /** Whether the user should receive notifications on new activity to this group. */
    private boolean receiveNewActivityNotifications;

    /** If notifications should be generated for all posts, or just ones from coordinators. */
    private boolean coordinatorOnlyNotifications;

    /**
     * Constructor.
     *
     * @param inSubscriberPersonId
     *            the person id
     * @param inStreamEntityId
     *            the group id
     * @param inReceiveNewActivityNotifications
     *            whether to receive notifications
     * @param inCoordinatorOnlyNotifications
     *            If notifications should be generated for all posts, or just ones from coordinators.
     */
    public ChangeStreamActivitySubscriptionMapperRequest(final long inSubscriberPersonId, final long inStreamEntityId,
            final boolean inReceiveNewActivityNotifications, final boolean inCoordinatorOnlyNotifications)
    {
        subscriberPersonId = inSubscriberPersonId;
        streamEntityId = inStreamEntityId;
        receiveNewActivityNotifications = inReceiveNewActivityNotifications;
        coordinatorOnlyNotifications = inCoordinatorOnlyNotifications;
    }

    /**
     * @return the personId
     */
    public long getSubscriberPersonId()
    {
        return subscriberPersonId;
    }

    /**
     * @param inPersonId
     *            the personId to set
     */
    public void setSubscriberPersonId(final long inPersonId)
    {
        subscriberPersonId = inPersonId;
    }

    /**
     * @return the StreamEntityId
     */
    public long getStreamEntityId()
    {
        return streamEntityId;
    }

    /**
     * @param inStreamEntityId
     *            the StreamEntityID to set
     */
    public void setStreamEntityId(final long inStreamEntityId)
    {
        streamEntityId = inStreamEntityId;
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
