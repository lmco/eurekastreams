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
package org.eurekastreams.web.client.ui.common.notification.dialog;

import org.eurekastreams.commons.formatting.DateFormatter;
import org.eurekastreams.server.domain.AvatarUrlGenerator;
import org.eurekastreams.server.domain.InAppNotificationDTO;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.NotificationClickedEvent;
import org.eurekastreams.web.client.events.NotificationDeleteRequestEvent;
import org.eurekastreams.web.client.jsni.EffectsFacade;
import org.eurekastreams.web.client.model.NotificationFilterPreferencesModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Displays a single notification.
 */
public class NotificationWidget extends Composite
{
    /** Binder for building UI. */
    private static LocalUiBinder binder = GWT.create(LocalUiBinder.class);

    /** Date formatter. */
    private final DateFormatter dateFormatter = new DateFormatter();

    /** Local styles. */
    @UiField
    LocalStyle style;

    /** UI element for the main link that lets the notification be clicked on. */
    @UiField
    Anchor mainLinkUi;

    /** Avatar. */
    @UiField
    ImageElement avatarUi;

    /** UI element holding the message text. */
    @UiField
    Element messageTextUi;

    /** UI element holding the timestamp. */
    @UiField
    Element timestampUi;

    /** UI element acting as a delete button. */
    @UiField
    Label deleteUi;

    /** UI element acting as a disable button. */
    @UiField
    Label disableUi;

    /** UI element acting as the entire UI for the disable button. */
    @UiField
    DivElement disableToplevelUi;

    /** The notification to show. */
    private final InAppNotificationDTO item;

    /** Main widget. */
    private final Widget main;


    /**
     * Constructor.
     *
     * @param inItem
     *            Notification to display.
     * @param preventInternalLinks
     *            For internal destinations, simulate links instead of creating real ones (actually they'll still be 'a'
     *            tags, but they'll have no href attribute).
     */
    public NotificationWidget(final InAppNotificationDTO inItem, final boolean preventInternalLinks)
    {
        item = inItem;

        main = binder.createAndBindUi(this);
        initWidget(main);

        messageTextUi.setInnerText(item.getMessage());
        timestampUi.setInnerText(dateFormatter.timeAgo(item.getNotificationDate()));

        if (item.isHighPriority())
        {
            main.addStyleName(style.highPriority());
        }
        if (item.isRead())
        {
            main.addStyleName(style.read());
        }
        final String url = item.getUrl();
        if (url != null && !url.isEmpty())
        {
            if (url.charAt(0) != '#')
            {
                mainLinkUi.setTarget("_blank");
                mainLinkUi.setHref(url);
            }
            else if (preventInternalLinks)
            {
                // set style here to look like a link but don't set the href, then let the dialog handle actually
                // getting the user to the page
                mainLinkUi.addStyleName(style.fakeLink());
            }
            else
            {
                mainLinkUi.setHref(url);
            }
        }

        AvatarUrlGenerator urlGen = new AvatarUrlGenerator(item.getAvatarOwnerType());
        avatarUi.setSrc(urlGen.getSmallAvatarUrl(null, item.getAvatarId()));

        if (item.getFilterCategory() == null)
        {
            disableToplevelUi.removeFromParent();
        }
    }

    /**
     * Updates notification when clicked.
     *
     * @param ev
     *            Event.
     */
    @UiHandler("mainLinkUi")
    void onClick(final ClickEvent ev)
    {
        if (!item.isRead())
        {
            main.addStyleName(style.read());
        }
        EventBus.getInstance().notifyObservers(new NotificationClickedEvent(item, this));
    }

    /**
     * Deletes notification when button clicked.
     *
     * @param ev
     *            Event.
     */
    @UiHandler("deleteUi")
    void onDeleteClick(final ClickEvent ev)
    {
        new EffectsFacade().fadeOut(getElement(), true);
        EventBus.getInstance().notifyObservers(new NotificationDeleteRequestEvent(item));
    }

    /**
     * Disables the notification category when button clicked.
     *
     * @param ev
     *            Event.
     */
    @UiHandler("disableUi")
    void onDisableClick(final ClickEvent ev)
    {
        NotificationFilterPreferencesModel.getInstance().disable(item.getFilterCategory());
    }

    /**
     * Adds the read style.
     */
    public void addReadStyle()
    {
        addStyleName(style.read());
    }

    /**
     * Local styles.
     */
    interface LocalStyle extends CssResource
    {
        /** @return High-priority style. */
        String highPriority();

        /** @return Already read style. */
        String read();

        /** @return Style for making a link with no href still look like a link. */
        String fakeLink();
    }

    /**
     * Binder for building UI.
     */
    interface LocalUiBinder extends UiBinder<Widget, NotificationWidget>
    {
    }
}
