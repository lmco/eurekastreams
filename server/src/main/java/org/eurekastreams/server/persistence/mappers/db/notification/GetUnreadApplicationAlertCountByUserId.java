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
package org.eurekastreams.server.persistence.mappers.db.notification;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.ReadMapper;

/**
 * This mapper queries for the current count of unread notifications for a user.
 */
public class GetUnreadApplicationAlertCountByUserId extends ReadMapper<Long, Integer>
{
    /**
     * Makes the database call to get notification count and sets it in cache.
     *
     * @param userId
     *            user to get notification count.
     * @return the count of notifications for this user.
     */
    @Override
    public Integer execute(final Long userId)
    {
        String q = "select count(id) from ApplicationAlertNotification "
                + "where recipient.id = :userId and isRead = false";
        Query query = getEntityManager().createQuery(q).setParameter("userId", userId);
        Integer count = ((Long) query.getSingleResult()).intValue();
        return count;
    }
}
