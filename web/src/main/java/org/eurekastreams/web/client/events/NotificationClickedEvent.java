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
package org.eurekastreams.web.client.events;

import org.eurekastreams.server.domain.InAppNotificationDTO;

import com.google.gwt.user.client.ui.Widget;

/**
 * Raised when a notification is clicked.
 */
public class NotificationClickedEvent
{
    /** Notification. */
    private final InAppNotificationDTO notification;

    /** Widget displaying the notification. */
    private final Widget widget;

    /**
     * Constructor.
     * 
     * @param inNotification
     *            Notification.
     * @param inWidget
     *            Widget displaying the notification.
     */
    public NotificationClickedEvent(final InAppNotificationDTO inNotification, final Widget inWidget)
    {
        notification = inNotification;
        widget = inWidget;
    }

    /**
     * @return the notification
     */
    public InAppNotificationDTO getNotification()
    {
        return notification;
    }

    /**
     * @return the widget
     */
    public Widget getWidget()
    {
        return widget;
    }
}
