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

import org.eurekastreams.server.domain.UnreadInAppNotificationCountDTO;
import org.eurekastreams.web.client.events.NotificationCountsAvailableEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Notification Count Model.
 *
 */
public class NotificationCountModel extends BaseModel implements Fetchable<Serializable>
{
    /**
     * Singleton.
     */
    private static NotificationCountModel model = new NotificationCountModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static NotificationCountModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final Serializable request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getUnreadApplicationAlertCount", request,
                new OnSuccessCommand<UnreadInAppNotificationCountDTO>()
                {
                    public void onSuccess(final UnreadInAppNotificationCountDTO response)
                    {
                        Session.getInstance()
                                .getEventBus()
                                .notifyObservers(
                                        new NotificationCountsAvailableEvent(response.getNormalPriority(), response
                                                .getHighPriority()));
                    }
                }, useClientCacheIfAvailable);
    }
}
