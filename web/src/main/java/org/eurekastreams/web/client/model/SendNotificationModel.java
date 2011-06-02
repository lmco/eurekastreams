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
package org.eurekastreams.web.client.model;

import java.io.Serializable;

import org.eurekastreams.server.action.request.notification.SendPrebuiltNotificationRequest;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.data.SendNotificationFailureEvent;
import org.eurekastreams.web.client.events.data.SendNotificationSuccessEvent;

/**
 * Model for sending pre-built notifications.
 */
public class SendNotificationModel extends BaseModel implements Insertable<SendPrebuiltNotificationRequest>
{
    /**
     * Singleton.
     */
    private static SendNotificationModel model = new SendNotificationModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static SendNotificationModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final SendPrebuiltNotificationRequest inRequest)
    {
        String action = (inRequest.getRecipientAccountId() == null || inRequest.getRecipientAccountId().isEmpty()) //
        ? "sendMassPrebuiltNotificationAction"
                : "sendPrebuiltNotificationAction";
        super.callWriteAction(action, inRequest, new OnSuccessCommand<Serializable>()
        {
            public void onSuccess(final Serializable inResponse)
            {
                EventBus.getInstance().notifyObservers(new SendNotificationSuccessEvent(inRequest));
            }
        }, new OnFailureCommand()
        {
            public void onFailure(final Throwable inEx)
            {
                EventBus.getInstance().notifyObservers(new SendNotificationFailureEvent(inRequest, inEx));
            }
        });
    }
}
