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
import java.util.Date;

import org.eurekastreams.server.domain.InAppNotificationDTO;
import org.eurekastreams.web.client.events.data.GotNotificationListResponseEvent;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.user.client.Window;

/**
 * Model for handling the list of application alert notifications.
 */
public class NotificationListModel extends BaseModel implements Fetchable<Serializable>, Updateable<Date>
{
    /**
     * Singleton.
     */
    private static NotificationListModel model = new NotificationListModel();

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
                        Session.getInstance().getEventBus().notifyObservers(
                                new GotNotificationListResponseEvent(response));
                    }
                }, useClientCacheIfAvailable);
    }

    /**
     * Marks all notifications up through the given date as read.
     *
     * @param inRequest
     *            Date of newest notification to mark as read (to avoid marking ones not shown to user).
     */
    public void update(final Date inRequest)
    {
        Window.alert("NotificationListModel.update needs to be replaced");
        // super.callWriteAction("setAllApplicationAlertsAsRead", inRequest, new OnSuccessCommand<Integer>()
        // {
        // public void onSuccess(final Integer inResponse)
        // {
        // // this affects the data held by the count model, so clear the count model's cache
        // NotificationCountModel.getInstance().clearCache();
        //
        // // announce the new count
        // // (Would be ideal if we could hand it in to the count model and let it do this + cache it.)
        // Session.getInstance().getEventBus().notifyObservers(new NotificationCountAvailableEvent(inResponse));
        // }
        // });
    }
}
