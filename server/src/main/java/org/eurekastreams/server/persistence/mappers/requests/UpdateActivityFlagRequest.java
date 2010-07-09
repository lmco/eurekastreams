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
package org.eurekastreams.server.persistence.mappers.requests;

/**
 * Request for changing the flagged state of an activity.
 */
public class UpdateActivityFlagRequest
{
    /** The id of activity. */
    private long activityId;

    /** New flag state. */
    private boolean toFlag;

    /**
     * Constructor.
     *
     * @param inActivityId
     *            ID the of activity.
     * @param inToFlag
     *            New flag state.
     */
    public UpdateActivityFlagRequest(final long inActivityId, final boolean inToFlag)
    {
        activityId = inActivityId;
        toFlag = inToFlag;
    }

    /**
     * @return the activityId
     */
    public long getActivityId()
    {
        return activityId;
    }

    /**
     * @return the toFlag
     */
    public boolean isToFlag()
    {
        return toFlag;
    }
}
