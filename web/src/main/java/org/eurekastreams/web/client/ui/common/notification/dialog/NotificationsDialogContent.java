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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.server.domain.InAppNotificationDTO;
import org.eurekastreams.web.client.events.DialogLinkClickedEvent;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.NotificationClickedEvent;
import org.eurekastreams.web.client.events.NotificationDeleteRequestEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.UpdateRawHistoryEvent;
import org.eurekastreams.web.client.events.data.GotNotificationListResponseEvent;
import org.eurekastreams.web.client.model.NotificationListModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.BaseDialogContent;
import org.eurekastreams.web.client.ui.common.notification.NotificationSettingsWidget;
import org.eurekastreams.web.client.ui.pages.master.CoreCss;
import org.eurekastreams.web.client.ui.pages.master.MasterComposite;
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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
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

    /** Panel making a shadow on the source filter widgets. */
    @UiField
    SimplePanel shadowPanel;

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
    Label markAllReadButton;

    /** Button to switch show settings view. */
    @UiField
    Label settingsButton;

    /** Current all/unread selector. */
    private Widget currentReadFilterWidget;

    /** Notifications. */
    private List<InAppNotificationDTO> allNotifications;

    /** All notification widgets (cached to prevent re-creating when changing filters). */
    private final Map<Long, NotificationWidget> notifWidgetIndex = new HashMap<Long, NotificationWidget>();

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

    /** See explanation where this is used. */
    private final boolean manuallyHandleInternalLinks;

    /** Observer (allow unlinking). */
    private final Observer<NotificationClickedEvent> notificationClickedObserver = // \n
    new Observer<NotificationClickedEvent>()
    {
        public void update(final NotificationClickedEvent ev)
        {
            handleNotificationClicked(ev.getNotification(), ev.getWidget());
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
        // -- determine if IE workaround is needed (see explanation where used) --
        manuallyHandleInternalLinks = MasterComposite.getUserAgent().contains("msie");

        // -- build UI --
        coreCss = StaticResourceBundle.INSTANCE.coreCss();
        main = binder.createAndBindUi(this);
        currentReadFilterWidget = unreadFilterUi;

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
        eventBus.addObserver(NotificationClickedEvent.class, notificationClickedObserver);
        eventBus.addObserver(NotificationDeleteRequestEvent.class, notificationDeleteRequestObserver);

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
     * @param widget
     *            The notification's widget.
     */
    private void handleNotificationClicked(final InAppNotificationDTO item, final Widget widget)
    {
        // tell server notification is read
        if (!item.isRead())
        {
            NotificationListModel.getInstance().update(item.getId());
        }

        // dismiss the dialog when clicking on a notification with an internal URL. (Dialog will be discarded, so no
        // UI or local data updates are required.)
        final String url = item.getUrl();
        boolean hasInternalUrl = url != null && !url.isEmpty() && url.charAt(0) == '#';
        if (hasInternalUrl)
        {
            close();

            // For some reason, in IE (7 & 8), if the URL fragment is completely empty, then clicking one of the
            // notification links will update the URL in the address bar, but the HistoryHandler will never be notified
            // of it. So we need to force the app to go to the desired URL.
            // Also, IE seems to lose the history stack when clicking on a plain link, so we manually handle internal
            // links for all cases (not just the empty history token).
            if (manuallyHandleInternalLinks)
            {
                EventBus.getInstance().notifyObservers(new UpdateRawHistoryEvent(url.substring(1)));
            }
        }
        // not closing dialog, so update UI and local data if item was just read
        else if (!item.isRead())
        {
            item.setRead(true);

            // work up the source tree, reducing the unread count and hiding sources as applicable. (Note that the
            // starting source may not be the current source. This happens when the user is viewing "All" or
            // "Streams"/"Apps".)
            Source source = getSource(item);
            while (source != null)
            {
                source.decrementUnreadCount();
                updateDisplayString(source);
                if (source.getUnreadCount() == 0 && source != rootSource)
                {
                    source.getWidget().addStyleName(style.sourceFilterAllRead());
                }

                source = source.getParent();
            }

            // in unread view, insure no read items or empty sources are showing
            if (!currentShowRead)
            {
                if (currentSource.getUnreadCount() == 0)
                {
                    // Note: if already showing the root source, then this will redraw it with the "none" message.
                    selectSource(rootSource);
                }
                else
                {
                    widget.removeFromParent();
                }
                setShadowHeight();
            }
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
            notifWidgetIndex.remove(item.getId());

            // work up the source tree, updating/hiding/removing sources as applicable. (Note that the starting source
            // may not be the current source. This happens when the user is viewing "All" or "Streams"/"Apps".)
            Source source = getSource(item);
            while (source != null)
            {
                source.decrementTotalCount();
                if (source.getTotalCount() == 0 && source != rootSource)
                {
                    source.getWidget().removeFromParent();
                }
                else if (!item.isRead())
                {
                    source.decrementUnreadCount();
                    updateDisplayString(source);
                    if (source.getUnreadCount() == 0 && source != rootSource)
                    {
                        source.getWidget().addStyleName(style.sourceFilterAllRead());
                    }
                }

                source = source.getParent();
            }

            NotificationListModel.getInstance().delete(item.getId());

            // switch source if no notifications left to show. (If already showing the root source, this will just
            // redraw it with the "none" message.)
            if (currentSource.getTotalCount() == 0 || (!currentShowRead && currentSource.getUnreadCount() == 0))
            {
                selectSource(rootSource);
            }

            setShadowHeight();
        }
    }

    /**
     * Gets the source for a given notification.
     *
     * @param item
     *            Notification.
     * @return Source.
     */
    private Source getSource(final InAppNotificationDTO item)
    {
        Source source = sourceIndex.get(SourceListBuilder.buildSourceKey(item));
        return source == null ? rootSource : source;
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

        shadowPanel.setVisible(false);
        for (Source source : builder.getSourceList())
        {
            addSourceFilter(source, !source.isCategorySource());
        }

        currentSource = rootSource;

        // set up shadow
        setShadowHeight();
        shadowPanel.setVisible(true);
    }

    /**
     * Adjusts the height of the shadow to the displayed sources.
     */
    private void setShadowHeight()
    {
        shadowPanel.setHeight("0px");
        shadowPanel.setHeight(sourceFiltersPanel.getOffsetHeight() + "px");
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
        label.addStyleName(StaticResourceBundle.INSTANCE.coreCss().ellipsis());
        if (count == 0 && source != rootSource)
        {
            label.addStyleName(style.sourceFilterAllRead());
        }
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
        currentSource.getWidget().removeStyleName(style.filterSelected());

        currentSource = newSource;
        currentSource.getWidget().addStyleName(style.filterSelected());
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
                notificationListPanel.add(getNotificationWidget(item));
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
     * Gets/creates a notification widget for the given notification.
     *
     * @param item
     *            Notification.
     * @return Widget.
     */
    private NotificationWidget getNotificationWidget(final InAppNotificationDTO item)
    {
        NotificationWidget widget = notifWidgetIndex.get(item.getId());
        if (widget == null)
        {
            widget = new NotificationWidget(item, manuallyHandleInternalLinks);
            notifWidgetIndex.put(item.getId(), widget);
        }
        return widget;
    }

    /**
     * Shows all (unread+read) or just unread notifications.
     *
     * @param ev
     *            Event.
     */
    @UiHandler({ "allFilterUi", "unreadFilterUi" })
    void onFilterClick(final ClickEvent ev)
    {
        Widget selector = (Widget) ev.getSource();
        if (selector != currentReadFilterWidget)
        {
            currentReadFilterWidget.removeStyleName(style.filterSelected());
            currentReadFilterWidget = selector;
            currentReadFilterWidget.addStyleName(style.filterSelected());
            currentShowRead = !currentShowRead;

            if (currentShowRead)
            {
                sourceFiltersPanel.removeStyleName(style.sourceFilterListUnreadOnly());
            }
            else
            {
                sourceFiltersPanel.addStyleName(style.sourceFilterListUnreadOnly());
                if (currentSource != rootSource && currentSource.getUnreadCount() == 0)
                {
                    setShadowHeight();
                    selectSource(rootSource);
                    return;
                }
            }

            setShadowHeight();
            displayNotifications(currentSource.getFilter(), currentShowRead);
        }
    }

    /**
     * Marks all notifications read.
     *
     * @param ev
     *            Event.
     */
    @UiHandler("markAllReadButton")
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
        if (ids.isEmpty())
        {
            return;
        }
        NotificationListModel.getInstance().update(ids);

        // update the sources (unread counts and display)
        // This process needs to work upwards and downwards from the currently-displayed source. (e.g. marking all as
        // read on the Streams source needs to both decrement the appropriate number from "All"'s unread count as well
        // as zero out the unread count for every one of its child sources.) Given there are only three levels and very
        // few non-leaf sources, it is easiest to code for the specific cases.

        if (currentSource == rootSource)
        {
            // Root source. The root source contains everything, so set every source's unread count to zero.
            for (Source source : sourceIndex.values())
            {
                source.setUnreadCount(0);
                updateDisplayString(source);
                if (source != rootSource)
                {
                    source.getWidget().addStyleName(style.sourceFilterAllRead());
                }
            }
        }
        else if (currentSource.isCategorySource())
        {
            // Non-root category source. Set its and children's unread counts to zero; subtract from root's count.
            for (Source source : sourceIndex.values())
            {
                if (source == currentSource || source.getParent() == currentSource)
                {
                    source.setUnreadCount(0);
                    updateDisplayString(source);
                    source.getWidget().addStyleName(style.sourceFilterAllRead());
                }
            }
            rootSource.setUnreadCount(rootSource.getUnreadCount() - ids.size());
            updateDisplayString(rootSource);
        }
        else
        {
            // Leaf source. Work upwards.
            int number = ids.size();
            for (Source source = currentSource; source != null; source = source.getParent())
            {
                int unreadCount = source.getUnreadCount() - number;
                source.setUnreadCount(unreadCount);
                updateDisplayString(source);
                if (source != rootSource && unreadCount == 0)
                {
                    source.getWidget().addStyleName(style.sourceFilterAllRead());
                }
            }
        }

        // select a different source (or redraw root) if unread-only filter is active
        if (!currentShowRead)
        {
            setShadowHeight();
            selectSource(rootSource);
        }

        // add the already-read style to all the individual notification widgets (since the widgets are cached and
        // reused until the dialog is closed)
        for (long id : ids)
        {
            notifWidgetIndex.get(id).addReadStyle();
        }
    }

    /**
     * Shows the settings view.
     *
     * @param ev
     *            Event.
     */
    @UiHandler("settingsButton")
    void showSettings(final ClickEvent ev)
    {
        final NotificationSettingsWidget settings = new NotificationSettingsWidget(true);
        settings.setCloseCommand(new Command()
        {
            public void execute()
            {
                settings.removeFromParent();
                main.setVisible(true);
            }
        });

        // This assumes the dialog hosts the dialog content in a 1) dedicated panel which 2) allows multiple children.
        HasWidgets parent = (HasWidgets) main.getParent();
        main.setVisible(false);
        parent.add(settings);
    }

    /**
     * Causes the source to apply the current display string to the widget.
     *
     * @param source
     *            Source.
     */
    private void updateDisplayString(final Source source)
    {
        Label widget = source.getWidget();
        if (usingMozillaBinding(widget.getElement()))
        {
            int index = sourceFiltersPanel.getWidgetIndex(widget);
            if (index >= 0)
            {
                widget.removeFromParent();
                widget.setText(source.getDisplayString());
                sourceFiltersPanel.insert(widget, index);
            }
        }
        else
        {
            widget.setText(source.getDisplayString());
        }
    }

    /**
     * Determines if the source's widget is using a Mozilla binding. The purpose is to check for the XUL ellipsis
     * binding, since that binding causes text updates to fail, thus we must do some trickery to work around it.
     *
     * @param elem
     *            Element to check.
     * @return If using -moz-binding.
     */
    private static native boolean usingMozillaBinding(final Element elem)
    /*-{
            var v = $wnd.jQuery(elem).css('-moz-binding');
            return v ? v !== 'none' : false;
    }-*/;

    /**
     * Local styles.
     */
    interface LocalStyle extends CssResource
    {
        /** @return Extra style for entire modal. */
        @ClassName("modal")
        String modal();

        /** @return Style applied to the source list to only show sources with unread notifs. */
        @ClassName("source-filter-list-unread-only")
        String sourceFilterListUnreadOnly();

        /** @return Style applied to sources where all notifs are read. */
        @ClassName("source-filter-all-read")
        String sourceFilterAllRead();

        /** @return Style for sources. */
        @ClassName("source-filter")
        String sourceFilter();

        /** @return Added style for a selected filter (the selected source or unread/all). */
        @ClassName("filter-selected")
        String filterSelected();

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
