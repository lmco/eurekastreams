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

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;

/**
 * This mapper returns a list of userIds where applicationAlerts exist for these users that are beyond a given date.
 */
public class GetUserIdsWithUnreadApplicationAlertsByDate extends BaseDomainMapper
{
    /**
     * Queries for users with unread alerts beyond a given date.
     * 
     * @param startDate
     *            Date at which an alert is considered "old".
     * @return the list of person ids.
     */
    @SuppressWarnings("unchecked")
    public List<Long> execute(final Date startDate)
    {
        String q = "select distinct recipient.id from ApplicationAlertNotification "
                + "where notificationDate <= :startDate and isread = false";
        Query query = getEntityManager().createQuery(q).setParameter("startDate", startDate);
        return query.getResultList();
    }
}
