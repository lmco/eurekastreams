/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.events;

import org.eurekastreams.server.domain.Follower;

/**
 * Triggered when a follower changes status of following the group or person being viewed.
 */
public class FollowingStatusChangedEvent
{
    /**
     * Use to indicate the current user if you don't have the id. 
     */
    public static final long CURRENT_USER = -1;
    
    /**
     * Used when not specifying the affected user. 
     */
    public static final long NOT_SPECIFIED = -2;
    
    /**
     * The id of the follower whose status has changed. 
     */
    private long followerId = NOT_SPECIFIED;
    
    /**
     * The follower's following status for the group or person being viewed.
     */
    private Follower.FollowerStatus status = Follower.FollowerStatus.NOTSPECIFIED;

    /**
     * Constructor.
     * 
     * @param inFollowerId
     *            the follower whose status has changed
     * @param inStatus
     *            the new status
     */
    public FollowingStatusChangedEvent(final long inFollowerId, final Follower.FollowerStatus inStatus)
    {
        followerId = inFollowerId;
        if (null != inStatus)
        {
            status = inStatus;
        }
    }

    /**
     * Gets an instance of the event.
     * 
     * @return the event.
     */
    public static FollowingStatusChangedEvent getEvent()
    {
        return new FollowingStatusChangedEvent(NOT_SPECIFIED, null);
    }

    /**
     * @return the status
     */
    public Follower.FollowerStatus getStatus()
    {
        return status;
    }
    
    /**
     * @return the follower's id
     */
    public long getFollowerId()
    {
       return followerId; 
    }
}
