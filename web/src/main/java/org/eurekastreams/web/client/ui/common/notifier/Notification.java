/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.notifier;

import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Notifies the user of an event.
 *
 */
public class Notification
{
    /**
     * The message.
     */
    private String messageStr = "";

    /**
     * The widget inside the notification.
     */
    private Widget innerWidget = null;

    /**
     * Message constructor.
     *
     * @param message
     *            the message to notify about.
     */
    public Notification(final String message)
    {
        messageStr = message;
    }

    /**
     * Widget and message constructor.
     *
     * @param widget
     *            widget associated with the notification.
     * @param message
     *            the message.
     */
    public Notification(final Widget widget, final String message)
    {
        messageStr = message;
        innerWidget = widget;
    }

    /**
     * Get the widget associated with the notification.
     *
     * @return the widget.
     */
    public Widget getWidget()
    {
        if (innerWidget == null)
        {
            return new InlineLabel(messageStr);
        }
        return innerWidget;
    }

    /**
     * Get the notification as a string.
     *
     * @return the notification as a string.
     */
    public String toString()
    {
        return messageStr;
    }
}
