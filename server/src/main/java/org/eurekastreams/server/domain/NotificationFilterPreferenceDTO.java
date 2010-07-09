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
package org.eurekastreams.server.domain;

import java.io.Serializable;


/**
 * Represents a kind of notification that a user wishes not to see via a given transport.
 */
public class NotificationFilterPreferenceDTO implements Serializable
{
    /** For serialization. */
    private static final long serialVersionUID = -499261593625191183L;

    /** ID of person whose preference it is. */
    private long personId = 0L;

    /** Which notification method should be suppressed for the notification. */
    private String notifierType;

    /** Category of notifications to suppress. */
    private NotificationFilterPreference.Category notificationCategory;

    /**
     * Constructor for serialization only.
     */
    private NotificationFilterPreferenceDTO()
    {
    }

    /**
     * Constructor.
     * 
     * @param inPersonId
     *            ID of person whose preference it is.
     * @param inNotifierType
     *            Which notification method should be suppressed for the notification.
     * @param inNotificationCategory
     *            Category of notifications to suppress.
     */
    public NotificationFilterPreferenceDTO(final long inPersonId, final String inNotifierType,
            final NotificationFilterPreference.Category inNotificationCategory)
    {
        personId = inPersonId;
        notifierType = inNotifierType;
        notificationCategory = inNotificationCategory;
    }

    /**
     * Constructor.
     *
     * @param inNotifierType
     *            Which notification method should be suppressed for the notification.
     * @param inNotificationCategory
     *            Category of notifications to suppress.
     */
    public NotificationFilterPreferenceDTO(final String inNotifierType,
            final NotificationFilterPreference.Category inNotificationCategory)
    {
        notifierType = inNotifierType;
        notificationCategory = inNotificationCategory;
    }

    /**
     * @return the person ID
     */
    public long getPersonId()
    {
        return personId;
    }

    /**
     * @param inPersonId
     *            the person ID to set
     */
    public void setPersonId(final long inPersonId)
    {
        personId = inPersonId;
    }

    /**
     * @return the notifier type
     */
    public String getNotifierType()
    {
        return notifierType;
    }

    /**
     * @param inNotifierType
     *            the notifier type to set
     */
    public void setNotifierType(final String inNotifierType)
    {
        notifierType = inNotifierType;
    }

    /**
     * @return the notification category
     */
    public NotificationFilterPreference.Category getNotificationCategory()
    {
        return notificationCategory;
    }

    /**
     * @param inNotificationCategory
     *            the notification category to set
     */
    public void setNotificationCategory(final NotificationFilterPreference.Category inNotificationCategory)
    {
        notificationCategory = inNotificationCategory;
    }
}
