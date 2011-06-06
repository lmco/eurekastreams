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
package org.eurekastreams.web.client.ui.common.notification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.InAppNotificationDTO;
import org.eurekastreams.web.client.events.DialogLinkClickedEvent;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotNotificationListResponseEvent;
import org.eurekastreams.web.client.model.NotificationListModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.BaseDialogContent;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog content (i.e. main panel) for showing notifications.
 */
public class NotificationsDialogContent extends BaseDialogContent
{
    /** Main content widget. */
    private final Widget main;

    // /** Notification list wrapper. */
    // @UiField
    // ScrollPanel scrollPanel;

    //
    // /** Notification list. */
    // private final Panel listPanel = new FlowPanel();

    /** To unwire the observer when done with dialog. */
    private Observer<DialogLinkClickedEvent> linkClickedObserver;

    /** Binder for building UI. */
    private static LocalUiBinder binder = GWT.create(LocalUiBinder.class);

    // /** Notification list wrapper. */
    // @UiField
    // ScrollPanel scrollPanel;

    /** The list of sources. */
    @UiField
    FlowPanel sourceFiltersPanel;

    /** The displayed list of notifications. */
    @UiField
    FlowPanel notificationListPanel;

    /** Element to indicate no notifications. */
    @UiField
    DivElement noNotificationsUi;

    /** Notifications. */
    private List<InAppNotificationDTO> allNotifications;

    /** The IDs of the notifications currently being displayed. */
    private final Collection<Long> idsShowing = new ArrayList<Long>();

    /**
     * Constructor.
     */
    public NotificationsDialogContent()
    {
        main = binder.createAndBindUi(this);

        // // -- build UI --
        // main.addStyleName(StaticResourceBundle.INSTANCE.coreCss().notifDialogMain());
        //
        // Hyperlink editSettings =
        // new Hyperlink("edit settings", Session.getInstance().generateUrl(
        // new CreateUrlRequest(Page.SETTINGS, null, "tab", "Notifications")));
        // editSettings.addStyleName(StaticResourceBundle.INSTANCE.coreCss().notifEditSettingsLink());
        // main.add(editSettings);
        //
        // scrollPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().notifScrollList());
        //
        // scrollPanel.add(listPanel);
        //
        // main.add(scrollPanel);
        // listPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().notifWait());

        // -- setup events --
        final EventBus eventBus = Session.getInstance().getEventBus();

        eventBus.addObserver(GotNotificationListResponseEvent.class, new Observer<GotNotificationListResponseEvent>()
        {
            public void update(final GotNotificationListResponseEvent ev)
            {
                eventBus.removeObserver(ev, this);
                storeReceivedNotifications(ev.getResponse());
                displayNotifications(null, null, false);
            }
        });

        // Since none of the links cause a full page load (which would annihilate the dialog), we must explicitly close
        // the dialog. We cannot count on a history change event (or any of the related events) because the user may
        // already be on the exact page to which the link would send them. (If clicking a link would cause no change to
        // the URL, the GWT does not raise the event.) So we close the dialog on a link being clicked. We directly
        // listen on the "edit settings" link, and have the links in notifications raise an event we listen to.

        // editSettings.addClickHandler(new ClickHandler()
        // {
        // public void onClick(final ClickEvent inArg0)
        // {
        // close();
        // }
        // });
        //
        // linkClickedObserver = new Observer<DialogLinkClickedEvent>()
        // {
        // public void update(final DialogLinkClickedEvent inArg1)
        // {
        // close();
        // }
        // };
        // Session.getInstance().getEventBus().addObserver(DialogLinkClickedEvent.class, linkClickedObserver);

        // -- request data --
        NotificationListModel.getInstance().fetch(null, false);
    }

    /**
     * Invoked on closing before the dialog is removed from screen.
     */
    @Override
    public void beforeHide()
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
    public Widget getBody()
    {
        return main;
    }

    /**
     * {@inheritDoc}
     */
    public String getCssName()
    {
        return StaticResourceBundle.INSTANCE.coreCss().notifModal();
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle()
    {
        return "Notifications";
    }

    /**
     * Handles the received list of notifications.
     *
     * @param list
     *            List of notifications.
     */
    private void storeReceivedNotifications(final List<InAppNotificationDTO> list)
    {
        allNotifications = list;

        // TODO: determine counts, build filter UI

    }

    /**
     * Displays the appropriate subset of notifications.
     *
     * @param desiredType
     *            Type of sources to display (null for no filtering).
     * @param desiredId
     *            Unique ID of source.
     * @param showRead
     *            If read notifications should be displayed (unread are always displayed).
     */
    private void displayNotifications(final EntityType desiredType, final String desiredId, final boolean showRead)
    {
        noNotificationsUi.getStyle().setDisplay(Display.NONE);

        notificationListPanel.clear();
        idsShowing.clear();

        for (InAppNotificationDTO item : allNotifications)
        {
            if (desiredType == null
                    || (desiredType == item.getSourceType() && (desiredId == null || desiredId.equals(item
                            .getSourceUniqueId()))) && (showRead || !item.isRead()))
            {
                idsShowing.add(item.getId());
                notificationListPanel.add(new NotificationWidget(item));

            }
        }
        if (idsShowing.isEmpty())
        {
            noNotificationsUi.getStyle().clearDisplay();
        }
    }

    /**
     * Binder for building UI.
     */
    interface LocalUiBinder extends UiBinder<Widget, NotificationsDialogContent>
    {
    }
}
