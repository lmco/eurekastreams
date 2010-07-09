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
package org.eurekastreams.web.client.model.requests;

import java.io.Serializable;

/**
 * Request to mark an activity as flagged (inappropriate) or not. Implements Serializable so it can be passed to the
 * model's update method; it is not actually passed to the server though.
 */
@SuppressWarnings("serial")
public class UpdateActivityFlagRequest implements Serializable
{
    /** Activity ID. */
    private Long activityId;

    /** New state of activity. */
    private boolean toFlag;

    /**
     * Constructor.
     *
     * @param inActivityId
     *            Activity ID.
     * @param inToFlag
     *            Requested state of activity.
     */
    public UpdateActivityFlagRequest(final Long inActivityId, final boolean inToFlag)
    {
        activityId = inActivityId;
        toFlag = inToFlag;
    }

    /**
     * @return the activityId
     */
    public Long getActivityId()
    {
        return activityId;
    }

    /**
     * @return The requested state.
     */
    public boolean isFlagged()
    {
        return toFlag;
    }
}
