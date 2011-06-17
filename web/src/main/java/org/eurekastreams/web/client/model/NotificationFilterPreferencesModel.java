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

import org.eurekastreams.server.action.response.notification.GetUserNotificationFilterPreferencesResponse;
import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.data.GotNotificationFilterPreferencesResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedNotificationFilterPreferencesResponseEvent;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.notifier.Notification;

/**
 * Notification filter preferences model.
 */
public class NotificationFilterPreferencesModel extends BaseModel implements
        Updateable<ArrayList<NotificationFilterPreferenceDTO>>, Fetchable<Serializable>
{
    /**
     * Singleton.
     */
    private static NotificationFilterPreferencesModel model = new NotificationFilterPreferencesModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static NotificationFilterPreferencesModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void update(final ArrayList<NotificationFilterPreferenceDTO> request)
    {
        super.callWriteAction("setCurrentUserNotificationPreferences", request, new OnSuccessCommand<Serializable>()
        {
            public void onSuccess(final Serializable response)
            {
                Session.getInstance().getEventBus()
                        .notifyObservers(new UpdatedNotificationFilterPreferencesResponseEvent());
            }
        });
    }

    /**
     * Disables a category of notifications.
     *
     * @param category
     *            Category to disable.
     */
    public void disable(final String category)
    {
        super.callWriteAction("disableCurrentUserNotificationCategory", category, new OnSuccessCommand<Serializable>()
        {
            public void onSuccess(final Serializable response)
            {
                Session.getInstance().getEventBus()
                        .notifyObservers(new ShowNotificationEvent(new Notification("Notification category stopped")));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final Serializable request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getCurrentUserNotificationPreferences", request,
                new OnSuccessCommand<GetUserNotificationFilterPreferencesResponse>()
                {
                    public void onSuccess(final GetUserNotificationFilterPreferencesResponse response)
                    {
                        Session.getInstance().getEventBus()
                                .notifyObservers(new GotNotificationFilterPreferencesResponseEvent(response));
                    }
                }, useClientCacheIfAvailable);
    }
}
