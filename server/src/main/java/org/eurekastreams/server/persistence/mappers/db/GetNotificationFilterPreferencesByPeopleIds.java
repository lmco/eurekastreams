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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;

/**
 * Mapper to get notification filter preferences for one or more peopleIds.
 */
public class GetNotificationFilterPreferencesByPeopleIds extends BaseDomainMapper
{
    /**
     * Makes the database call to get notification preferences.
     * 
     * @param allRecipients
     *            list of all users to retrive notifications for.
     * @return the list of notificationFilterPreferenceDTOs for the input users
     */
    @SuppressWarnings("unchecked")
    public List<NotificationFilterPreferenceDTO> execute(final List<Long> allRecipients)
    {
        if (allRecipients == null || allRecipients.isEmpty())
        {
            return new ArrayList<NotificationFilterPreferenceDTO>();
        }
        
        String q = "select new org.eurekastreams.server.domain.NotificationFilterPreferenceDTO "
                + "(person.id, notifierType, notificationCategory) "
                + "from NotificationFilterPreference where person.id in (:recipients)";

        Query query = getEntityManager().createQuery(q).setParameter("recipients", allRecipients);
        return query.getResultList();
    }
}
