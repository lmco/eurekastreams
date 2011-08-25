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
package org.eurekastreams.server.action.request;

import java.io.Serializable;

import org.eurekastreams.server.domain.HasGroupId;

/**
 * Request to update which activity is sticky for a stream.
 */
public class UpdateStickyActivityRequest implements Serializable, HasGroupId
{
    /** ID for stream's entity. */
    private Long streamEntityId;

    /** Activity ID to be sticky (null for none). */
    private Long activityId;

    /**
     * Constructor.
     */
    @SuppressWarnings("unused")
    private UpdateStickyActivityRequest()
    {
    }

    /**
     * Constructor.
     *
     * @param inStreamEntityId
     *            ID for stream's entity.
     * @param inActivityId
     *            Activity ID to be sticky (null for none.)
     */
    public UpdateStickyActivityRequest(final Long inStreamEntityId, final Long inActivityId)
    {
        streamEntityId = inStreamEntityId;
        activityId = inActivityId;
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

    /**
     * @return the streamEntityId
     */
    public Long getStreamEntityId()
    {
        return streamEntityId;
    }

    /**
     * @param inStreamEntityId
     *            the streamEntityId to set
     */
    public void setStreamEntityId(final Long inStreamEntityId)
    {
        streamEntityId = inStreamEntityId;
    }

    /**
     * Returns the stream entity's ID, which if the stream entity happens to be a group, is the group id. Implemented as
     * part of HasGroupId for convenience with authorizers.
     * 
     * @return The stream entity's ID.
     */
    @Override
    public long getGroupId()
    {
        return streamEntityId;
    }
}
