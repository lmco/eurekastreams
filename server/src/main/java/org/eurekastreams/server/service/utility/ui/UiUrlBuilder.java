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
package org.eurekastreams.server.service.utility.ui;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Identifiable;

/**
 * Generates URLs for items in the GWT UI. Used for building links in notifications, emails, etc.
 */
public final class UiUrlBuilder
{
    /** Forbid instantiation. */
    private UiUrlBuilder()
    {
    }

    /**
     * Returns the relative URL to a single activity.
     *
     * @param activityId
     *            The activity ID.
     * @return URL.
     */
    public static String relativeUrlForActivity(final long activityId)
    {
        return "#activity/" + activityId;
    }

    /**
     * Returns the relative URL to an entity's profile.
     *
     * @param entity
     *            Entity.
     * @return URL.
     */
    public static String relativeUrlForEntity(final Identifiable entity)
    {
        return relativeUrlForEntity(entity.getEntityType(), entity.getUniqueId());
    }

    /**
     * Returns the relative URL to an entity's profile.
     *
     * @param type
     *            Entity type.
     * @param uniqueId
     *            Entity unique id.
     * @return URL.
     */
    public static String relativeUrlForEntity(final EntityType type, final String uniqueId)
    {
        switch (type)
        {
        case PERSON:
            return "#activity/person/" + uniqueId;
        case GROUP:
            return "#activity/group/" + uniqueId;
        default:
            return null;
        }
    }

    /**
     * Returns the relative URL to the flagged activity review page.
     *
     * @return URL.
     */
    public static String relativeUrlForFlaggedActivity()
    {
        return "#settings?tab=Pending&listFilter=Flagged+Activities";
    }

    /**
     * Returns the relative URL to the pending group approval page.
     *
     * @return URL.
     */
    public static String relativeUrlForPendingGroupRequest()
    {
        return "#settings?tab=Pending&listFilter=Group+Requests";
    }

    /**
     * Returns the relative URL to the group access approval page.
     *
     * @param uniqueId
     *            Group unique id.
     * @return URL.
     */
    public static String relativeUrlForGroupAccessRequest(final String uniqueId)
    {
        // TODO: Need the correct page or tab on the page
        return "#groupsettings/" + uniqueId;
    }
}
