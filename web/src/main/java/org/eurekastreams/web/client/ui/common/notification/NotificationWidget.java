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
package org.eurekastreams.web.client.ui.common.notification;

import org.eurekastreams.commons.formatting.DateFormatter;
import org.eurekastreams.server.domain.InAppNotificationDTO;
import org.eurekastreams.web.client.ui.pages.master.CoreCss;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
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

    /** Global CSS. */
    @UiField(provided = true)
    CoreCss coreCss;

    /** Global resources. */
    @UiField(provided = true)
    StaticResourceBundle globalResources;

    /** Local styles. */
    @UiField
    LocalStyle style;

    /** Avatar. */
    @UiField
    Image avatarUi;

    /** UI element holding the message text. */
    @UiField
    DivElement messageTextUi;

    /** UI element holding the timestamp. */
    @UiField
    DivElement dateUi;

    /**
     * Constructor.
     *
     * @param item
     *            Notification to display.
     */
    public NotificationWidget(final InAppNotificationDTO item)
    {
        coreCss = StaticResourceBundle.INSTANCE.coreCss();
        globalResources = StaticResourceBundle.INSTANCE;

        Widget main = binder.createAndBindUi(this);
        initWidget(main);

        // TODO: allow bolding

        messageTextUi.setInnerText(item.getMessage());
        dateUi.setInnerText(dateFormatter.timeAgo(item.getNotificationDate()));

        if (item.isHighPriority())
        {
            main.addStyleName(style.highPriority());
        }
        if (item.isRead())
        {
            main.addStyleName(style.read());
        }

        // TODO Auto-generated constructor stub
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
    }

    /**
     * Binder for building UI.
     */
    interface LocalUiBinder extends UiBinder<Widget, NotificationWidget>
    {
    }
}
