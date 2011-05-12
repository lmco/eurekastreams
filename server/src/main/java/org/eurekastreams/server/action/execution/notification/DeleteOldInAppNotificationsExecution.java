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
package org.eurekastreams.server.action.execution.notification;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.db.notification.DeleteInAppNotificationsByDate;
import org.eurekastreams.server.persistence.mappers.db.notification.GetUserIdsWithUnreadInAppNotificationsByDate;

/**
 * Deletes application that are older (in days) than the configured ageInDays value.
 */
public class DeleteOldInAppNotificationsExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Mapper to delete alerts.
     */
    private final DeleteInAppNotificationsByDate deleteMapper;

    /**
     * Mapper to find user ids with old unread alerts.
     */
    private final GetUserIdsWithUnreadInAppNotificationsByDate unreadMapper;

    /**
     * Mapper to sync database and cache unread alert count.
     */
    private final DomainMapper<Long, Integer> syncMapper;

    /**
     * Age at which alerts can be deleted.
     */
    private final int ageInDays;

    /**
     * Constructor.
     *
     * @param inDeleteMapper
     *            The mapper that performs the application alert deletion from the database.
     * @param inUnreadMapper
     *            The mapper that retrieves the ids of users that have old unread alerts.
     * @param inSyncMapper
     *            The mapper that syncs the unread alert count from the database with the value in cache.
     * @param inAgeInDays
     *            The age in days when an alert is considered "old".
     */
    public DeleteOldInAppNotificationsExecution(final DeleteInAppNotificationsByDate inDeleteMapper,
            final GetUserIdsWithUnreadInAppNotificationsByDate inUnreadMapper,
            final DomainMapper<Long, Integer> inSyncMapper, final int inAgeInDays)
    {
        deleteMapper = inDeleteMapper;
        ageInDays = inAgeInDays;
        unreadMapper = inUnreadMapper;
        syncMapper = inSyncMapper;
    }

    /**
     * {@inheritDoc} This method calls the database mapper to delete application alerts that are older than ageInDays.
     * Before the delete, a mapper call is made to find which users had old unread alerts then after the actual
     * deletion, the cached count of unread alerts for these users is synced with the database.
     */
    @Override
    public Serializable execute(final ActionContext inActionContext)
    {
        if (log.isInfoEnabled())
        {
            log.info("Deleting application alerts older than " + ageInDays + " days");
        }

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(GregorianCalendar.DATE, ageInDays * -1);
        Date oldDate = calendar.getTime();

        List<Long> userIdsToSync = unreadMapper.execute(oldDate);

        deleteMapper.execute(oldDate);

        for (long userId : userIdsToSync)
        {
            syncMapper.execute(userId);
        }

        return true;
    }
}
