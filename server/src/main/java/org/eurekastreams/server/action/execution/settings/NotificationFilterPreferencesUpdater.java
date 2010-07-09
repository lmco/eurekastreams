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
package org.eurekastreams.server.action.execution.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.persistence.mappers.db.SetUserNotificationFilterPreferences;
import org.eurekastreams.server.persistence.mappers.requests.SetUserNotificationFilterPreferencesRequest;

/**
 * Updates the requesting user's notification filter preferences.
 *
 */
public class NotificationFilterPreferencesUpdater implements SettingsUpdater
{
    /** List of notifiers which can be disabled. */
    private Map<String, String> notifierTypes;

    /** Mapper. */
    SetUserNotificationFilterPreferences mapper;

    /**
     * Constructor.
     *
     * @param inNotifierTypes
     *            List of notifiers which can be disabled.
     * @param inMapper
     *            Mapper.
     */
    public NotificationFilterPreferencesUpdater(final Map<String, String> inNotifierTypes,
            final SetUserNotificationFilterPreferences inMapper)
    {
        notifierTypes = inNotifierTypes;
        mapper = inMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final Map<String, Serializable> inSettings, final Principal inUser)
    {
        List<NotificationFilterPreferenceDTO> notifPrefs = new ArrayList<NotificationFilterPreferenceDTO>();
        for (Map.Entry<String, Serializable> entry : inSettings.entrySet())
        {
            if (entry.getKey().startsWith("notif-")
                    && entry.getValue() != null
                    && notifierTypes
                            .containsKey(((NotificationFilterPreferenceDTO) entry.getValue()).getNotifierType()))
            {
                notifPrefs.add((NotificationFilterPreferenceDTO) entry.getValue());
            }
        }
        mapper.execute(new SetUserNotificationFilterPreferencesRequest(inUser.getId(), notifPrefs));
    }
}
