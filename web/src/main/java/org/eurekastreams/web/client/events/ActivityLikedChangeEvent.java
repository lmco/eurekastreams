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
package org.eurekastreams.web.client.events;

import org.eurekastreams.server.action.request.stream.SetActivityLikeRequest.LikeActionType;

/**
 * Event fired when liked status changes.
 */
public class ActivityLikedChangeEvent
{
    /**
     * Action type.
     */
    private LikeActionType actionType;

    /**
     * Activity ID.
     */
    private Long activityId;

    /**
     * Constructor.
     * 
     * @param inActionType
     *            type of action.
     * @param inActivityId
     *            activity ID.
     */
    public ActivityLikedChangeEvent(final LikeActionType inActionType, final Long inActivityId)
    {
        actionType = inActionType;
        activityId = inActivityId;
    }

    /**
     * Get the action type.
     * 
     * @return the action type.
     */
    public LikeActionType getActionType()
    {
        return actionType;
    }

    /**
     * Get the activity ID.
     * 
     * @return the activity ID.
     */
    public Long getActivityId()
    {
        return activityId;
    }
}
