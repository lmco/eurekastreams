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
package org.eurekastreams.web.client.ui.common.notification.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eurekastreams.server.domain.InAppNotificationDTO;
import org.eurekastreams.web.client.events.DialogLinkClickedEvent;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.NotificationClickedEvent;
import org.eurekastreams.web.client.events.NotificationDeleteRequestEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.UnreadNotificationClearedEvent;
import org.eurekastreams.web.client.events.data.GotNotificationListResponseEvent;
import org.eurekastreams.web.client.model.NotificationListModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.BaseDialogContent;
import org.eurekastreams.web.client.ui.pages.master.CoreCss;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog content (i.e. main panel) for showing notifications.
 */
public class NotificationsDialogContent extends BaseDialogContent
{
    /** Main content widget. */
    private final Widget main;

    /** To unwire the observer when done with dialog. */
    private Observer<DialogLinkClickedEvent> linkClickedObserver;

    /** Binder for building UI. */
    private static LocalUiBinder binder = GWT.create(LocalUiBinder.class);

    /** Local styles. */
    @UiField
    LocalStyle style;

    /** Global CSS. */
    @UiField(provided = true)
    CoreCss coreCss;

    /** The list of sources. */
    @UiField
    FlowPanel sourceFiltersPanel;

    /** Scroll panel holding the notification list. */
    @UiField
    ScrollPanel notificationListScrollPanel;

    /** The displayed list of notifications. */
    @UiField
    FlowPanel notificationListPanel;

    /** Element to indicate no notifications. */
    @UiField
    DivElement noNotificationsUi;

    /** Selector for all (read+unread) notifications. */
    @UiField
    Label allFilterUi;

    /** Selector for unread only notifications. */
    @UiField
    Label unreadFilterUi;

    /** Button to mark all notifications as read. */
    @UiField
    Label markAllReadUi;

    /** Current all/unread selector. */
    private Widget currentReadFilterWidget;

    /** Notifications. */
    private List<InAppNotificationDTO> allNotifications;

    /** The notifications currently being displayed. */
    private final Collection<InAppNotificationDTO> notifsShowing = new ArrayList<InAppNotificationDTO>();

    /** Source representing all notifications. */
    private Source rootSource;

    /** Index of actual sources. */
    private Map<String, Source> sourceIndex;

    /** Currently-selected source. */
    private Source currentSource;

    /** Currently selected show read option. */
    private boolean currentShowRead = false;

    /** Observer (allow unlinking). */
    private final Observer<UnreadNotificationClearedEvent> unreadNotificationClearedObserver = // \n
    new Observer<UnreadNotificationClearedEvent>()
    {
        public void update(final UnreadNotificationClearedEvent ev)
        {
            reduceUnreadCount(ev.getResponse());
        }
    };
    /** Observer (allow unlinking). */
    private final Observer<NotificationClickedEvent> notificationClickedObserver = // \n
    new Observer<NotificationClickedEvent>()
    {
        public void update(final NotificationClickedEvent ev)
        {
            handleNotificationClicked(ev.getResponse());
        }
    };
    /** Observer (allow unlinking). */
    private final Observer<NotificationDeleteRequestEvent> notificationDeleteRequestObserver = // \n
    new Observer<NotificationDeleteRequestEvent>()
    {
        public void update(final NotificationDeleteRequestEvent ev)
        {
            handleNotificationDeleteRequest(ev.getResponse());
        }
    };

    /**
     * Constructor.
     */
    public NotificationsDialogContent()
    {
        coreCss = StaticResourceBundle.INSTANCE.coreCss();
        main = binder.createAndBindUi(this);
        currentReadFilterWidget = unreadFilterUi;

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
                selectSource(currentSource);
            }
        });
        // eventBus.addObserver(CloseModalRequestEvent.class, new Observer<CloseModalRequestEvent>()
        // {
        // public void update(final CloseModalRequestEvent ev)
        // {
        // eventBus.removeObserver(ev, this);
        // close();
        // }
        // });

        eventBus.addObserver(UnreadNotificationClearedEvent.class, unreadNotificationClearedObserver);
        eventBus.addObserver(NotificationClickedEvent.class, notificationClickedObserver);
        eventBus.addObserver(NotificationDeleteRequestEvent.class, notificationDeleteRequestObserver);

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
        EventBus.getInstance().removeObserver(UnreadNotificationClearedEvent.class, unreadNotificationClearedObserver);
        EventBus.getInstance().removeObserver(NotificationClickedEvent.class, notificationClickedObserver);
        EventBus.getInstance().removeObserver(NotificationDeleteRequestEvent.class, notificationDeleteRequestObserver);
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
    public String getTitle()
    {
        return "Notifications";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCssName()
    {
        return style.modal();
    }

    /**
     * Responds appropriately to a notification being clicked.
     *
     * @param item
     *            The notification.
     */
    private void handleNotificationClicked(final InAppNotificationDTO item)
    {
        final String url = item.getUrl();
        boolean hasInternalUrl = url != null && !url.isEmpty() && url.charAt(0) == '#';
        if (!item.isRead())
        {
            item.setRead(true);

            NotificationListModel.getInstance().update(item.getId());

            // don't bother fixing up the counts if the notification has an internal URL, because we will close the
            // dialog momentarily
            if (!hasInternalUrl)
            {
                reduceUnreadCount(item);
            }
        }
        if (hasInternalUrl)
        {
            close();
        }
    }

    /**
     * Deletes a notification on request.
     *
     * @param item
     *            The notification.
     */
    private void handleNotificationDeleteRequest(final InAppNotificationDTO item)
    {
        if (notifsShowing.remove(item))
        {
            allNotifications.remove(item);
            if (!item.isRead())
            {
                reduceUnreadCount(item);
            }
            NotificationListModel.getInstance().delete(item.getId());
        }
    }

    /**
     * Reduces the unread count for all applicable sources.
     *
     * @param item
     *            Notification read / deleted.
     */
    private void reduceUnreadCount(final InAppNotificationDTO item)
    {
        // Source source = rootSource;
        //
        // if (item.getSourceType() != null && item.getSourceUniqueId() != null)
        // {
        // source = sourceIndex.get(item.getSourceType() + item.getSourceUniqueId());
        // }

        Source source = sourceIndex.get(item.getSourceType() + item.getSourceUniqueId());
        if (source == null)
        {
            source = rootSource;
        }

        // work from the specific source up, reducing the unread count
        while (source != null)
        {
            int count = source.getUnreadCount() - 1;
            source.setUnreadCount(count);
            String text = count > 0 ? source.getDisplayName() + " (" + count + ")" : source.getDisplayName();
            source.getWidget().setText(text);
            source = source.getParent();
        }
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

        SourceListBuilder builder = new SourceListBuilder(list, Session.getInstance().getCurrentPerson()
                .getAccountId());
        rootSource = builder.getRootSource();
        sourceIndex = builder.getSourceIndex();

        for (Source source : builder.getSourceList())
        {
            addSourceFilter(source, source.getParent() != null && source.getParent() != rootSource);
        }

        currentSource = rootSource;
    }

    /**
     * Creates and adds the widget for a source filter.
     *
     * @param source
     *            Source data.
     * @param indent
     *            If the label should be indented.
     */
    private void addSourceFilter(final Source source, final boolean indent)
    {
        int count = source.getUnreadCount();
        String text = count > 0 ? source.getDisplayName() + " (" + count + ")" : source.getDisplayName();

        final Label label = new Label(text);
        label.addStyleName(style.sourceFilter());
        label.addStyleName(StaticResourceBundle.INSTANCE.coreCss().buttonLabel());
        if (indent)
        {
            label.addStyleName(style.sourceFilterIndented());
        }
        label.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent inEvent)
            {
                selectSource(source);
            }
        });

        sourceFiltersPanel.add(label);

        source.setWidget(label);
    }

    /**
     * Updates the display to show a new source.
     *
     * @param newSource
     *            New source.
     */
    private void selectSource(final Source newSource)
    {
        currentSource.getWidget().removeStyleName(style.sourceFilterSelected());

        currentSource = newSource;
        currentSource.getWidget().addStyleName(style.sourceFilterSelected());
        displayNotifications(currentSource.getFilter(), currentShowRead);
    }

    /**
     * Displays the appropriate subset of notifications.
     *
     * @param filter
     *            Filter for notifications.
     * @param showRead
     *            If read notifications should be displayed (unread are always displayed).
     */
    private void displayNotifications(final Source.Filter filter, final boolean showRead)
    {
        noNotificationsUi.getStyle().setDisplay(Display.NONE);
        notificationListScrollPanel.setVisible(false);

        notificationListPanel.clear();
        notifsShowing.clear();

        for (InAppNotificationDTO item : allNotifications)
        {
            if (filter.shouldDisplay(item) && (showRead || !item.isRead()))
            {
                notifsShowing.add(item);
                notificationListPanel.add(new NotificationWidget(item));

            }
        }
        if (notifsShowing.isEmpty())
        {
            noNotificationsUi.getStyle().clearDisplay();
        }
        else
        {
            notificationListScrollPanel.scrollToTop();
            notificationListScrollPanel.setVisible(true);
        }
    }

    /**
     * Shows all (unread+read) notifications.
     *
     * @param ev
     *            Event.
     */
    @UiHandler({ "allFilterUi", "unreadFilterUi" })
    void onAllFilterClick(final ClickEvent ev)
    {
        Widget selector = (Widget) ev.getSource();
        if (selector != currentReadFilterWidget)
        {
            currentReadFilterWidget.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().active());
            currentReadFilterWidget = selector;
            currentReadFilterWidget.addStyleName(StaticResourceBundle.INSTANCE.coreCss().active());
            currentShowRead = !currentShowRead;
            displayNotifications(currentSource.getFilter(), currentShowRead);
        }
    }

    /**
     * Marks all notifications read.
     *
     * @param ev
     *            Event.
     */
    @UiHandler("markAllReadUi")
    void onMarkAllReadClick(final ClickEvent ev)
    {
        // update on server
        ArrayList<Long> ids = new ArrayList<Long>();
        for (InAppNotificationDTO item : notifsShowing)
        {
            if (!item.isRead())
            {
                item.setRead(true);
                ids.add(item.getId());
            }
        }
        if (!ids.isEmpty())
        {
            NotificationListModel.getInstance().update(ids);
        }

        // update UI
        // TODO: awaiting P.O. input
    }

    /**
     * Local styles.
     */
    interface LocalStyle extends CssResource
    {
        /** @return Extra style for entire modal. */
        @ClassName("modal")
        String modal();

        /** @return Style for sources. */
        @ClassName("source-filter")
        String sourceFilter();

        /** @return Added style for the selected source. */
        @ClassName("source-filter-selected")
        String sourceFilterSelected();

        /** @return Added style for indented sources. */
        @ClassName("source-filter-indented")
        String sourceFilterIndented();
    }

    /**
     * Binder for building UI.
     */
    interface LocalUiBinder extends UiBinder<Widget, NotificationsDialogContent>
    {
    }
}
