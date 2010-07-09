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
package org.eurekastreams.server.persistence.mappers.requests;

import java.util.Collection;

import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;

/**
 * Request data for mapper.
 */
public class SetUserNotificationFilterPreferencesRequest
{
    /** List of notifications to suppress. */
    private Collection<NotificationFilterPreferenceDTO> prefList;

    /** Person whose preferences to update. */
    private long personId;

    /**
     * Constructor.
     *
     * @param inPrefList
     *            List of notifications to suppress.
     * @param inPersonId
     *            Person whose preferences to update.
     */
    public SetUserNotificationFilterPreferencesRequest(final long inPersonId,
            final Collection<NotificationFilterPreferenceDTO> inPrefList)
    {
        prefList = inPrefList;
        personId = inPersonId;
    }

    /**
     * @return the prefList
     */
    public Collection<NotificationFilterPreferenceDTO> getPrefList()
    {
        return prefList;
    }

    /**
     * @param inPrefList
     *            the prefList to set
     */
    public void setPrefList(final Collection<NotificationFilterPreferenceDTO> inPrefList)
    {
        prefList = inPrefList;
    }

    /**
     * @return the personId
     */
    public long getPersonId()
    {
        return personId;
    }

    /**
     * @param inPersonId
     *            the personId to set
     */
    public void setPersonId(final long inPersonId)
    {
        personId = inPersonId;
    }
}
