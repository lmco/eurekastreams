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

import java.util.Collection;

import javax.persistence.Query;

import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Retrieves a user's notification filter preferences.
 *
 */
public class GetNotificationFilterPreferencesByPersonId extends
        BaseArgDomainMapper<Long, Collection<NotificationFilterPreferenceDTO>>
{
    /**
     * Retrieves the list of notification filter preferences for a given user.
     *
     * @param personId
     *            User's person id.
     * @return List of filters (notifications which should not be sent).
     */
    @SuppressWarnings("unchecked")
    public Collection<NotificationFilterPreferenceDTO> execute(final Long personId)
    {
        Query query =
                getEntityManager()
                        .createQuery(
                                "select new org.eurekastreams.server.domain.NotificationFilterPreferenceDTO"
                                + "(person.id,notifierType,notificationCategory) "
                                + "from NotificationFilterPreference where person.id = :personId");
        query.setParameter("personId", personId);
        return query.getResultList();
    }
}
