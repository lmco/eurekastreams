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
package org.eurekastreams.server.action.execution.notification.inapp;

import java.io.Serializable;
import java.util.Collection;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.UnreadInAppNotificationCountDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.db.notification.InAppNotificationsByUserMapperRequest;

/**
 * This action performs an action on a list of notifications for a user then syncs the unread counts. The specific
 * action depends on the mapper provided; delete or mark as read being two cases.
 */
public class ModifyInAppNotificationsExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /** Mapper to modify (update or delete) notifications. */
    private final DomainMapper<InAppNotificationsByUserMapperRequest, Serializable> modifyNotifsMapper;

    /** Mapper to sync database and cache unread alert count. */
    private final DomainMapper<Long, UnreadInAppNotificationCountDTO> syncMapper;

    /**
     * Constructor.
     *
     * @param inModifyNotifsMapper
     *            Mapper to modify (update or delete) notifications.
     * @param inSyncMapper
     *            the sync mapper to set.
     */
    public ModifyInAppNotificationsExecution(
            final DomainMapper<InAppNotificationsByUserMapperRequest, Serializable> inModifyNotifsMapper,
            final DomainMapper<Long, UnreadInAppNotificationCountDTO> inSyncMapper)
    {
        modifyNotifsMapper = inModifyNotifsMapper;
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
        Collection<Long> notifIds = (Collection<Long>) inActionContext.getParams();
        long userId = inActionContext.getPrincipal().getId();

        // modify notifs
        modifyNotifsMapper.execute(new InAppNotificationsByUserMapperRequest(notifIds, userId));

        // Sync count in cache with count in database
        UnreadInAppNotificationCountDTO newCounts = syncMapper.execute(userId);

        return newCounts;
    }
}
