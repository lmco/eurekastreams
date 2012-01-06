/*
 * Copyright (c) 2010-2012 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.notification;

import org.eurekastreams.web.client.events.NotificationCountsAvailableEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.model.NotificationCountModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.notification.dialog.NotificationsDialogContent;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;

/**
 * Widget displaying the notification count in the nav bar.
 */
public class NotificationCountWidget extends Label
{
    /** Polling time in minutes. */
    private static final int POLL_TIME_MINUTES = 1;

    /**
     * Constructor.
     */
    public NotificationCountWidget()
    {
        addStyleName(StaticResourceBundle.INSTANCE.coreCss().notifCount());
        setTitle("View Notifications");

        addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent inEvent)
            {
                showDialog();
            }
        });

        Session.getInstance().getEventBus()
        .addObserver(NotificationCountsAvailableEvent.class, new Observer<NotificationCountsAvailableEvent>()
                {
            public void update(final NotificationCountsAvailableEvent ev)
            {
                int total = ev.getNormalCount() + ev.getHighPriorityCount();
                setText(Integer.toString(total));
                if (total > 0)
                {
                    if (ev.getHighPriorityCount() > 0)
                    {
                        addStyleName(StaticResourceBundle.INSTANCE.coreCss().notifCountHighPriority());
                        removeStyleName(StaticResourceBundle.INSTANCE.coreCss().notifCountNormalPriority());
                    }
                    else
                    {
                        addStyleName(StaticResourceBundle.INSTANCE.coreCss().notifCountNormalPriority());
                        removeStyleName(StaticResourceBundle.INSTANCE.coreCss().notifCountHighPriority());
                    }
                }
                else
                {
                    removeStyleName(StaticResourceBundle.INSTANCE.coreCss().notifCountNormalPriority());
                    removeStyleName(StaticResourceBundle.INSTANCE.coreCss().notifCountHighPriority());
                }
            }
                });
    }

    /**
     * Init the data.
     */
    public void init()
    {
        Session.getInstance().getTimer()
        .addTimerJob("getNotificationCountTimerJob", POLL_TIME_MINUTES, NotificationCountModel.getInstance(),
                null, true);

        NotificationCountModel.getInstance().fetch(null, true);
    }

    /**
     * Shows the notifications list dialog.
     */
    public void showDialog()
    {
        Dialog.showCentered(new NotificationsDialogContent());
    }
}
