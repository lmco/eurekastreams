/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import org.eurekastreams.server.domain.EntityType;

/**
 * Event that's fired with whether the user is subscribed to new activity notifications for a given stream.
 */
public class GotStreamActivitySubscriptionResponseEvent
{
    /** Type of stream. */
    private final EntityType entityType;

    /** Unique ID of stream. */
    private final String uniqueId;

    /** If user is subscribed. */
    private final boolean isSubscribed;

    /**
     * Constructor.
     *
     * @param inEntityType
     *            Type of stream.
     * @param inUniqueId
     *            Unique ID of stream.
     * @param inIsSubscribed
     *            If user is subscribed.
     */
    public GotStreamActivitySubscriptionResponseEvent(final EntityType inEntityType, final String inUniqueId,
            final boolean inIsSubscribed)
    {
        entityType = inEntityType;
        uniqueId = inUniqueId;
        isSubscribed = inIsSubscribed;
    }

    /**
     * @return the entityType
     */
    public EntityType getEntityType()
    {
        return entityType;
    }

    /**
     * @return the uniqueId
     */
    public String getUniqueId()
    {
        return uniqueId;
    }

    /**
     * @return the isSubscribed
     */
    public boolean isSubscribed()
    {
        return isSubscribed;
    }
}
