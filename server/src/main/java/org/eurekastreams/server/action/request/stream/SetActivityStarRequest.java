/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

/**
 * Request for adding/removing star on an activity.
 *
 */
public class SetActivityStarRequest implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 4598149077421803155L;

    /**
     * The StarActionType.
     */
    private StarActionType starActionType;

    /**
     * The activity id.
     */
    private long activityId;

    /**
     * Default constructor for serialization.
     */
    public SetActivityStarRequest()
    {
        //no op.
    }

    /**
     * Constructor.
     * @param inActivityId activity id.
     * @param inStarActionType action type.
     */
    public SetActivityStarRequest(final long inActivityId, final StarActionType inStarActionType)
    {
        starActionType = inStarActionType;
        activityId = inActivityId;
    }

    /**
     * @return the starActionType
     */
    public StarActionType getStarActionType()
    {
        return starActionType;
    }

    /**
     * @param inStarActionType the starActionType to set
     */
    public void setStarActionType(final StarActionType inStarActionType)
    {
        this.starActionType = inStarActionType;
    }

    /**
     * @return the activityId
     */
    public long getActivityId()
    {
        return activityId;
    }

    /**
     * @param inActivityId the activityId to set
     */
    public void setActivityId(final long inActivityId)
    {
        this.activityId = inActivityId;
    }

    /**
     * Enum describing type of star action to do.
     */
    public enum StarActionType implements Serializable
    {
        /**
         * Add a star.
         */
        ADD_STAR,

        /**
         * Remove a star.
         */
        REMOVE_STAR
    }

}
