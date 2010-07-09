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
package org.eurekastreams.web.client.events;

import org.eurekastreams.web.client.ui.common.notifier.Notification;

/**
 * Event to trigger a new notification message to the user.
 */
public class ShowNotificationEvent
{
    /**
     * The notification.
     */
    private Notification notification;

    /**
     * Constructor.
     * 
     * @param inNotification
     *            the notification to set.
     */
    public ShowNotificationEvent(final Notification inNotification)
    {
        notification = inNotification;
    }

    /**
     * Gets an instance of the event. Used by jsni for making a new event from a gadget.
     * 
     * @param notificationText
     *            the text of the notification to create.
     * @return the new notification.
     */
    public static ShowNotificationEvent getInstance(final String notificationText)
    {
        return new ShowNotificationEvent(new Notification(notificationText));
    }

    /**
     * @return the notification.
     */
    public Notification getNotification()
    {
        return notification;
    }
}
