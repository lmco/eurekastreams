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
package org.eurekastreams.server.persistence.mappers.db.notification;

import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.notification.GetNotificationFilterPreferenceRequest;

/**
 * Mapper to get notification filter preferences for one or more peopleIds.
 */
public class GetNotificationFilterPreferencesByPersonIdsAndCategories extends BaseDomainMapper implements
        DomainMapper<GetNotificationFilterPreferenceRequest, List<NotificationFilterPreferenceDTO>>
{
    /**
     * Makes the database call to get notification preferences.
     *
     * @param request
     *            Request with list of users and categories.
     * @return the list of notificationFilterPreferenceDTOs for the input users
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<NotificationFilterPreferenceDTO> execute(final GetNotificationFilterPreferenceRequest request)
    {
        if (request.getPersonIds() == null || request.getPersonIds().isEmpty() || request.getCategories() == null
                || request.getCategories().isEmpty())
        {
            return Collections.EMPTY_LIST;
        }

        String q = "SELECT new org.eurekastreams.server.domain.NotificationFilterPreferenceDTO "
                + "(person.id, notifierType, notificationCategory) FROM NotificationFilterPreference "
                + "WHERE person.id IN (:personIds) AND notificationCategory in (:categories)";

        Query query = getEntityManager().createQuery(q).setParameter("personIds", request.getPersonIds())
                .setParameter("categories", request.getCategories());
        return query.getResultList();
    }
}
