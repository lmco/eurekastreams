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
import java.util.Collection;

/**
 * Request used for mappers which act on a given Collection of notifications for a given user.
 */
public class InAppNotificationsByUserMapperRequest implements Serializable
{
    /** Fingerprint. */
    private static final long serialVersionUID = 5219572050253643696L;

    /** Collection of notifications. */
    private Collection<Long> notificationIds;

    /** Owner of the notifications. */
    private Long personId;

    /**
     * Constructor.
     *
     * @param inNotificationIds
     *            Collection of notifications.
     * @param inPersonId
     *            Owner of the notifications.
     */
    public InAppNotificationsByUserMapperRequest(final Collection<Long> inNotificationIds,
            final Long inPersonId)
    {
        notificationIds = inNotificationIds;
        personId = inPersonId;
    }

    /**
     * @return the notificationIds
     */
    public Collection<Long> getNotificationIds()
    {
        return notificationIds;
    }

    /**
     * @param inNotificationIds
     *            the notificationIds to set
     */
    public void setNotificationIds(final Collection<Long> inNotificationIds)
    {
        notificationIds = inNotificationIds;
    }

    /**
     * @return the personId
     */
    public Long getPersonId()
    {
        return personId;
    }

    /**
     * @param inPersonId
     *            the personId to set
     */
    public void setPersonId(final Long inPersonId)
    {
        personId = inPersonId;
    }
}
