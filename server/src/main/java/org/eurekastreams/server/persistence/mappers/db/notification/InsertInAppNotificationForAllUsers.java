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

import java.io.Serializable;

import javax.persistence.Query;

import org.eurekastreams.server.action.request.notification.SendPrebuiltNotificationRequest;
import org.eurekastreams.server.domain.InAppNotificationEntity;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Inserts an in-app notification into the table for all non-locked users.
 */
public class InsertInAppNotificationForAllUsers extends
        BaseArgDomainMapper<SendPrebuiltNotificationRequest, Serializable>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable execute(final SendPrebuiltNotificationRequest inRequest)
    {
        // insert a notification like the one we want into the DB, but without a recipient
        InAppNotificationEntity dbNotif = new InAppNotificationEntity();
        dbNotif.setNotificationType(NotificationType.PASS_THROUGH);
        dbNotif.setMessage(inRequest.getMessage());
        dbNotif.setUrl(inRequest.getUrl());
        dbNotif.setHighPriority(inRequest.isHighPriority());

        getEntityManager().persist(dbNotif);

        // use a bulk insert to duplicate it for all non-locked users
        String q = "insert into InAppNotification (recipient, notificationType, notificationDate, message, url, "
                + "highPriority, isRead, sourceType, avatarOwnerType) "
                + "select p, n.notificationType, n.notificationDate, n.message, n.url, n.highPriority, n.isRead, "
                + "n.sourceType, n.avatarOwnerType from Person p, InAppNotification n "
                + "where n.id = :id and p.accountLocked = false";
        Query query = getEntityManager().createQuery(q).setParameter("id", dbNotif.getId());
        int count = query.executeUpdate();

        // delete the template notification
        getEntityManager().remove(dbNotif);

        return count;
    }
}
