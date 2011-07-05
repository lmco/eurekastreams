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
package org.eurekastreams.web.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import org.eurekastreams.server.domain.InAppNotificationDTO;
import org.eurekastreams.server.domain.UnreadInAppNotificationCountDTO;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.NotificationCountsAvailableEvent;
import org.eurekastreams.web.client.events.data.GotNotificationListResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Model for handling the list of application alert notifications.
 */
public class NotificationListModel extends BaseModel implements Fetchable<Serializable>, Updateable<ArrayList<Long>>,
        Deletable<Long>
{
    /** Singleton. */
    private static NotificationListModel model = new NotificationListModel();

    /** Action taken when an update succeeds - raises a count update event. */
    private final OnSuccessCommand<UnreadInAppNotificationCountDTO> updateSuccessCommand = // \n
    new OnSuccessCommand<UnreadInAppNotificationCountDTO>()
    {
        public void onSuccess(final UnreadInAppNotificationCountDTO response)
        {
            EventBus.getInstance().notifyObservers(
                    new NotificationCountsAvailableEvent(response.getNormalPriority(), response.getHighPriority()));
        }
    };

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static NotificationListModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final Serializable request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getInAppNotifications", request, new OnSuccessCommand<ArrayList<InAppNotificationDTO>>()
        {
            public void onSuccess(final ArrayList<InAppNotificationDTO> response)
            {
                Session.getInstance().getEventBus().notifyObservers(new GotNotificationListResponseEvent(response));
            }
        }, useClientCacheIfAvailable);
    }

    /**
     * Marks the given notifications as read.
     *
     * @param notifIds
     *            List of IDs of notifications to mark as read.
     */
    public void update(final ArrayList<Long> notifIds)
    {
        super.callWriteAction("markInAppNotificationsRead", notifIds, updateSuccessCommand);
    }

    /**
     * Marks a single notification as read.
     *
     * @param notifId
     *            ID of notification to mark as read.
     */
    public void update(final long notifId)
    {
        super.callWriteAction("markInAppNotificationsRead", (Serializable) Collections.singletonList(notifId),
                updateSuccessCommand);
    }

    /**
     * Deletes a notification.
     *
     * @param notifId
     *            ID of notification to delete.
     */
    public void delete(final Long notifId)
    {
        super.callWriteAction("deleteInAppNotifications", (Serializable) Collections.singletonList(notifId),
                updateSuccessCommand);
    }
}
