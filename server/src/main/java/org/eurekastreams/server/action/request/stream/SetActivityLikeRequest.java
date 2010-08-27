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
 * Request for adding/removing like on an activity.
 *
 */
public class SetActivityLikeRequest implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 4598149072321803155L;

    /**
     * The LikeActionType.
     */
    private LikeActionType likeActionType;

    /**
     * The activity id.
     */
    private long activityId;

    /**
     * Default constructor for serialization.
     */
    public SetActivityLikeRequest()
    {
        //no op.
    }

    /**
     * Constructor.
     * @param inActivityId activity id.
     * @param inLikeActionType action type.
     */
    public SetActivityLikeRequest(final long inActivityId, final LikeActionType inLikeActionType)
    {
        likeActionType = inLikeActionType;
        activityId = inActivityId;
    }

    /**
     * @return the likeActionType
     */
    public LikeActionType getLikeActionType()
    {
        return likeActionType;
    }

    /**
     * @param inLikeActionType the likeActionType to set
     */
    public void setLikeActionType(final LikeActionType inLikeActionType)
    {
        this.likeActionType = inLikeActionType;
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
     * Enum describing type of like action to do.
     */
    public enum LikeActionType implements Serializable
    {
        /**
         * Add a like.
         */
        ADD_LIKE,

        /**
         * Remove a like.
         */
        REMOVE_LIKE
    }

}
