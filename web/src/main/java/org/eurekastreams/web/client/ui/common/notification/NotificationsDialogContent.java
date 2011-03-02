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
package org.eurekastreams.web.client.ui.common.notification;

import java.util.List;

import org.eurekastreams.commons.client.ui.WidgetCommand;
import org.eurekastreams.server.domain.ApplicationAlertNotification;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.web.client.events.DialogLinkClickedEvent;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotNotificationListResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.NotificationListModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;
import org.eurekastreams.web.client.ui.common.notification.rendering.NotificationsRenderer;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog content (i.e. main panel) for showing notifications.
 */
public class NotificationsDialogContent implements DialogContent
{
    /** Main panel. */
    private Panel main = new FlowPanel();

    /** Notification list wrapper. */
    private ScrollPanel scrollPanel = new ScrollPanel();

    /** Notification list. */
    private Panel listPanel = new FlowPanel();

    /** Close command. */
    WidgetCommand closeCommand;

    /** To unwire the observer when done with dialog. */
    private Observer<DialogLinkClickedEvent> linkClickedObserver;

    /**
     * Constructor.
     */
    public NotificationsDialogContent()
    {
        // -- build UI --
        main.addStyleName(StaticResourceBundle.INSTANCE.coreCss().notifDialogMain());

        Hyperlink editSettings =
                new Hyperlink("edit settings", Session.getInstance().generateUrl(
                        new CreateUrlRequest(Page.SETTINGS, null, StaticResourceBundle.INSTANCE.coreCss().tab(), "Notifications")));
        editSettings.addStyleName(StaticResourceBundle.INSTANCE.coreCss().notifEditSettingsLink());
        main.add(editSettings);

        scrollPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().notifScrollList());

        scrollPanel.add(listPanel);

        main.add(scrollPanel);
        listPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().notifWait());

        // -- setup events --
        final EventBus eventBus = Session.getInstance().getEventBus();

        eventBus.addObserver(GotNotificationListResponseEvent.class, new Observer<GotNotificationListResponseEvent>()
        {
            public void update(final GotNotificationListResponseEvent ev)
            {
                eventBus.removeObserver(ev, this);
                displayNotifications(ev.getResponse());
            }
        });

        // Since none of the links cause a full page load (which would annihilate the dialog), we must explicitly close
        // the dialog. We cannot count on a history change event (or any of the related events) because the user may
        // already be on the exact page to which the link would send them. (If clicking a link would cause no change to
        // the URL, the GWT does not raise the event.) So we close the dialog on a link being clicked. We directly
        // listen on the "edit settings" link, and have the links in notifications raise an event we listen to.

        editSettings.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent inArg0)
            {
                close();
            }
        });

        linkClickedObserver = new Observer<DialogLinkClickedEvent>()
        {
            public void update(final DialogLinkClickedEvent inArg1)
            {
                close();
            }
        };
        Session.getInstance().getEventBus().addObserver(DialogLinkClickedEvent.class, linkClickedObserver);

        // -- request data --
        NotificationListModel.getInstance().fetch(null, false);
    }

    /**
     * Invoked on closing before the dialog is removed from screen.
     */
    public void beforeClose()
    {
        if (linkClickedObserver != null)
        {
            Session.getInstance().getEventBus().removeObserver(DialogLinkClickedEvent.class, linkClickedObserver);
            linkClickedObserver = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close()
    {
        closeCommand.execute();
    }

    /**
     * {@inheritDoc}
     */
    public Widget getBody()
    {
        return main;
    }

    /**
     * {@inheritDoc}
     */
    public String getCssName()
    {
        return "notif-modal";
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle()
    {
        return "Notifications";
    }

    /**
     * {@inheritDoc}
     */
    public void setCloseCommand(final WidgetCommand command)
    {
        closeCommand = command;
    }

    /**
     * {@inheritDoc}
     */
    public void show()
    {
    }

    /**
     * Handles the received list of notifications.
     *
     * @param list
     *            List of notifications.
     */
    private void displayNotifications(final List<ApplicationAlertNotification> list)
    {
        listPanel.clear();
        listPanel.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().notifWait());
        if (list.size() > 0)
        {
            NotificationsRenderer renderer = new NotificationsRenderer();
            for (ApplicationAlertNotification notif : list)
            {
                try
                {
                    listPanel.add(renderer.render(notif));
                }
                catch (Exception ex)
                {
                    Label label = new Label("Cannot display notification");
                    listPanel.add(label);
                }
            }

            // mark all as read since they've been displayed
            NotificationListModel.getInstance().update(list.get(0).getNotificationDate());
        }
        else
        {
            Label label = new Label("No notifications");
            label.addStyleName(StaticResourceBundle.INSTANCE.coreCss().notifNoNotifications());
            listPanel.add(label);
        }
    }
}
