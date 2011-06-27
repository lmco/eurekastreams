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
package org.eurekastreams.server.persistence.mappers.requests.notification;

import java.util.Collection;

/**
 * Request for notification preferences for a specific set of users and notification types.
 */
public class GetNotificationFilterPreferenceRequest
{
    /** Persons to limit to. */
    private final Collection<Long> personIds;

    /** Notification type categories to limit to. */
    private final Collection<String> categories;

    /**
     * Constructor.
     *
     * @param inPersonIds
     *            Persons to limit to.
     * @param inCategories
     *            Notification type categories to limit to.
     */
    public GetNotificationFilterPreferenceRequest(final Collection<Long> inPersonIds,
            final Collection<String> inCategories)
    {
        personIds = inPersonIds;
        categories = inCategories;
    }

    /**
     * @return the personIds
     */
    public Collection<Long> getPersonIds()
    {
        return personIds;
    }

    /**
     * @return the categories
     */
    public Collection<String> getCategories()
    {
        return categories;
    }
}
