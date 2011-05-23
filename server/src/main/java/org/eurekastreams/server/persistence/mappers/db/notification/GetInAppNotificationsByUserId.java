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

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.InAppNotificationDTO;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * This mapper gets all application alerts for a given userId, up to a specified max count.
 */
public class GetInAppNotificationsByUserId extends BaseArgDomainMapper<Long, List<InAppNotificationDTO>>
{
    /**
     * Max items to return.
     */
    private final int count;

    /**
     * Constructor.
     *
     * @param inCount
     *            Max items to return.
     */
    public GetInAppNotificationsByUserId(final int inCount)
    {
        count = inCount;
    }

    /**
     * Query database for application alerts for this user.
     *
     * @param userId
     *            User's ID.
     *
     * @return the list of alerts for this user.
     */
    @Override
    public List<InAppNotificationDTO> execute(final Long userId)
    {
        String q = "select new org.eurekastreams.server.domain.InAppNotificationDTO (id,notificationType,"
                + "notificationDate,message,url,highPriority,isRead,sourceType,sourceUniqueId,sourceName,"
                + "avatarOwnerType,avatarOwnerUniqueId) from InAppNotification where recipient.id = :userId "
                + "order by highPriority desc, notificationDate desc";
        Query query = getEntityManager().createQuery(q).setParameter("userId", userId);
        query.setMaxResults(count);
        return query.getResultList();
    }
}
