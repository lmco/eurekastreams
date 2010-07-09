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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eurekastreams.server.action.response.settings.RetrieveSettingsResponse;
import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.persistence.mappers.db.GetNotificationFilterPreferencesByPersonId;

/**
 * Returns all data needed for the notification filters configuration screen.
 */
public class NotificationFilterPreferencesRetriever implements SettingsRetriever
{
    /** List of notifiers which can be disabled. */
    private Map<String, String> notifierTypes;

    /** Mapper. */
    private GetNotificationFilterPreferencesByPersonId mapper;


    /**
     * Constructor.
     *
     * @param inNotifierTypes
     *            List of notifiers which can be disabled.
     * @param inMapper
     *            Mapper.
     */
    public NotificationFilterPreferencesRetriever(final Map<String, String> inNotifierTypes,
            final GetNotificationFilterPreferencesByPersonId inMapper)
    {
        notifierTypes = inNotifierTypes;
        mapper = inMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void retrieve(final long userid, final RetrieveSettingsResponse response)
    {
        // fetch current preferences and filter by valid filterable notifiers
        // (just in case the list in the Spring config changed since the user last updated preferences in the db)
        List<NotificationFilterPreferenceDTO> list = new ArrayList<NotificationFilterPreferenceDTO>();
        for (NotificationFilterPreferenceDTO dto : mapper.execute(userid))
        {
            if (notifierTypes.containsKey(dto.getNotifierType()))
            {
                list.add(dto);
            }
        }

        response.getSettings().put(RetrieveSettingsResponse.SETTINGS_NOTIFICATION_FILTERS, list);
        response.getSupport().put(RetrieveSettingsResponse.SUPPORT_NOTIFIER_TYPES, notifierTypes);
    }
}
