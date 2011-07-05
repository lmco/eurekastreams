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
package org.eurekastreams.server.action.response.notification;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;

/**
 * User Notification Filter Preferences.
 */
public class GetUserNotificationFilterPreferencesResponse implements Serializable
{
    /** User's preferences (suppressed notifications). */
    private List<NotificationFilterPreferenceDTO> preferences;

    /** List of notifiers which can be disabled with their display names. */
    private Map<String, String> notifierTypes;

    /**
     * Constructor.
     *
     * @param inPreferences
     *            User's preferences (suppressed notifications).
     * @param inNotifierTypes
     *            List of notifiers which can be disabled with their display names.
     */
    public GetUserNotificationFilterPreferencesResponse(final List<NotificationFilterPreferenceDTO> inPreferences,
            final Map<String, String> inNotifierTypes)
    {
        preferences = inPreferences;
        notifierTypes = inNotifierTypes;
    }

    /**
     * Constructor for serialization.
     */
    @SuppressWarnings("unused")
    private GetUserNotificationFilterPreferencesResponse()
    {
    }

    /**
     * @return the preferences
     */
    public List<NotificationFilterPreferenceDTO> getPreferences()
    {
        return preferences;
    }

    /**
     * @return the notifierTypes
     */
    public Map<String, String> getNotifierTypes()
    {
        return notifierTypes;
    }
}
