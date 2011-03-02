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

import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.server.domain.ApplicationAlertNotification;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Panel;

/**
 * Builds the notification message.
 */
public class FlagActivityMessageRenderer extends NotificationMessageRenderer
{
    /**
     * {@inheritDoc}
     */
    @Override
    public String getStyleName()
    {
        return "notif-entry-flag";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void render(final Panel parent, final ApplicationAlertNotification notification)
    {
        appendActorLink(parent, notification);
        appendText(parent, " ");

        Map<String, String> params = new HashMap<String, String>();
        params.put(StaticResourceBundle.INSTANCE.coreCss().tab(), "Admin");
        params.put("adminFilter", "Flagged Activities");
        appendLink(parent, "flagged", new CreateUrlRequest(getEntityProfilePage(notification.getDestinationType()),
                notification.getDestinationUniqueId(), params));

        appendText(parent, " a " + getActivityTypeName(notification) + " in the ");
        appendDestinationLink(parent, notification);
        appendText(parent, " organization.");
    }
}
