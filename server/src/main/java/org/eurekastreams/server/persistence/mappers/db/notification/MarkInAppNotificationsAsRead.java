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

import java.io.Serializable;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * This mapper sets the specified alerts for the given user as read. (The user is provided so that someone cannot act on
 * other people's notifications.)
 */
public class MarkInAppNotificationsAsRead extends
        BaseArgDomainMapper<InAppNotificationsByUserMapperRequest, Serializable>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable execute(final InAppNotificationsByUserMapperRequest inRequest)
    {
        String q = "update InAppNotification set isRead = true where recipient.id = :userId and id in (:ids)";
        Query query = getEntityManager().createQuery(q).setParameter("userId", inRequest.getPersonId())
                .setParameter("ids", inRequest.getNotificationIds());
        query.executeUpdate();
        return null;
    }
}
