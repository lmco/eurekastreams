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

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;

/**
 * This mapper sets all alerts for a user as read up to a specified date.
 */
public class SetAllApplicationAlertsAsReadByUserId extends BaseDomainMapper
{
    /**
     * Updates database rows for alerts to set then as read, up to a given date.
     * 
     * @param userId
     *            the user id.
     * @param startDate
     *            Rows newer than this date will not be marked as read.
     */
    public void execute(final long userId, final Date startDate)
    {
        String q = "update ApplicationAlertNotification set isRead = true "
                + "where recipient.id = :userId and notificationDate <= :startDate";
        Query query = getEntityManager().createQuery(q).setParameter("userId", userId).setParameter("startDate",
                startDate);
        query.executeUpdate();
    }
}
