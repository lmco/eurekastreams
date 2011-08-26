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
package org.eurekastreams.web.client.events.data;

import org.eurekastreams.server.domain.stream.ActivityDTO;

/**
 * Event when a group's sticky activity is updated.
 */
public class UpdatedGroupStickyActivityEvent
{
    /** The sticky activity (null if none). */
    private final ActivityDTO activity;

    /** The group. */
    private final long groupId;

    /**
     * Constructor.
     *
     * @param inGroupId
     *            The group.
     * @param inActivity
     *            The sticky activity (null if none).
     */
    public UpdatedGroupStickyActivityEvent(final long inGroupId, final ActivityDTO inActivity)
    {
        groupId = inGroupId;
        activity = inActivity;
    }

    /**
     * @return the activity
     */
    public ActivityDTO getActivity()
    {
        return activity;
    }

    /**
     * @return the groupId
     */
    public long getGroupId()
    {
        return groupId;
    }
}
