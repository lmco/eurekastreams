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
package org.eurekastreams.server.action.execution.notification;

import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.ApplicationAlertNotification;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;
import org.eurekastreams.server.persistence.mappers.InsertMapper;
import org.eurekastreams.server.persistence.mappers.cache.SyncUnreadApplicationAlertCountCacheByUserId;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;

/**
 * Notifier which inserts an alert in the database for display in the UI.
 */
public class ApplicationAlertNotifier extends BaseDomainMapper implements Notifier
{
    /**
     * Mapper to persist the notification.
     */
    private InsertMapper<ApplicationAlertNotification> insertMapper;

    /**
     * Mapper to sync unread alert count in cache.
     */
    SyncUnreadApplicationAlertCountCacheByUserId syncMapper;

    /**
     * Constructor.
     *
     * @param inInsertMapper
     *            the insert mapper to set.
     * @param inSyncMapper
     *            the sync mapper to set.
     */
    public ApplicationAlertNotifier(final InsertMapper<ApplicationAlertNotification> inInsertMapper,
            final SyncUnreadApplicationAlertCountCacheByUserId inSyncMapper)
    {
        insertMapper = inInsertMapper;
        syncMapper = inSyncMapper;
    }

    /**
     * {@inheritDoc}
     *
     * This notifier does not submit async requests.
     */
    @Override
    public UserActionRequest notify(final NotificationDTO inNotification)
    {
        for (long recipient : inNotification.getRecipientIds())
        {
            Person person = (Person) getHibernateSession().get(Person.class, recipient);
            if (person != null)
            {
                ApplicationAlertNotification alert = new ApplicationAlertNotification(inNotification, person);
                insertMapper.execute(new PersistenceRequest<ApplicationAlertNotification>(alert));
                syncMapper.execute(recipient);
            }
        }
        return null;
    }
}
