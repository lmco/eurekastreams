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
package org.eurekastreams.server.service.actions.requests;

import java.io.Serializable;

/**
 * Gets a single activity.
 */
public class GetActivityByIdRequest implements Serializable
{
    /**
     * Serialization ID.
     */
    private static final long serialVersionUID = -1567732216242221989L;

    /**
     * The ActivityId.
     */
    private Long activityId;

    /**
     * Constructor.
     * 
     * @param inActivityId
     *            the activity Id
     */
    public GetActivityByIdRequest(final Long inActivityId)
    {
        activityId = inActivityId;
    }

    /**
     * Used for Serialization.
     */
    @SuppressWarnings("unused")
    private GetActivityByIdRequest()
    {
    }

    /**
     * @return the activityId
     */
    public Long getActivityId()
    {
        return activityId;
    }

    /**
     * @param inActivityId
     *            the activityId to set
     */
    public void setActivityId(final Long inActivityId)
    {
        activityId = inActivityId;
    }
}
