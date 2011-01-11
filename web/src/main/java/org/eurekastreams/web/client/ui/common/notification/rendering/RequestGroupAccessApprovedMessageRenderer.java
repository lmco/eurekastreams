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
package org.eurekastreams.web.client.ui.common.notification.rendering;

import org.eurekastreams.server.domain.ApplicationAlertNotification;

import com.google.gwt.user.client.ui.Panel;

/**
 * Builds the notification message.
 */
public class RequestGroupAccessApprovedMessageRenderer extends NotificationMessageRenderer
{
    /**
     * {@inheritDoc}
     */
    @Override
    public String getStyleName()
    {
        return "notif-entry-follow";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void render(final Panel parent, final ApplicationAlertNotification notification)
    {
        appendText(parent, "Your request to join the ");
        appendDestinationLink(parent, notification);
        appendText(parent, " group has been approved.");
    }
}
