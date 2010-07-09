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

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.cache.SyncUnreadApplicationAlertCountCacheByUserId;
import org.eurekastreams.server.persistence.mappers.db.SetAllApplicationAlertsAsReadByUserId;

/**
 * This strategy marks all application alerts for the current user as read up to a given date. This date check ensures
 * that any new notifications that were not displayed to the user are not incorrectly marked as read.
 */
public class SetAllApplicationAlertsAsReadExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Mapper to set all application alerts as read.
     */
    private SetAllApplicationAlertsAsReadByUserId alertMapper;

    /**
     * Mapper to sync database and cache unread alert count.
     */
    private SyncUnreadApplicationAlertCountCacheByUserId syncMapper;

    /**
     * Constructor.
     * 
     * @param inAlertMapper
     *            the alert mapper to set.
     * @param inSyncMapper
     *            the sync mapper to set.
     */
    public SetAllApplicationAlertsAsReadExecution(final SetAllApplicationAlertsAsReadByUserId inAlertMapper,
            final SyncUnreadApplicationAlertCountCacheByUserId inSyncMapper)
    {
        alertMapper = inAlertMapper;
        syncMapper = inSyncMapper;
    }

    /**
     * {@inheritDoc} This method calls a database mapper to mark all application alerts as read for the user making the
     * action request. The method then makes a mapper call to sync the count of unread items for the current user with
     * the cached unread count.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext)
    {
        Date date = (Date) inActionContext.getParams();
        long userId = inActionContext.getPrincipal().getId();

        // Mark all as read
        alertMapper.execute(userId, date);

        // Sync count in cache with count in database
        int newCount = syncMapper.execute(userId);

        if (log.isTraceEnabled())
        {
            log.trace("Set all alerts as read for user " + userId);
        }

        return newCount;
    }
}
