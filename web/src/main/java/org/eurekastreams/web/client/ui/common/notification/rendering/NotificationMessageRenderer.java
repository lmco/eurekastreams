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

import java.util.Map;

import org.eurekastreams.server.domain.ApplicationAlertNotification;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.web.client.events.DialogLinkClickedEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.utility.InContextActivityLinkBuilder;
import org.eurekastreams.web.client.utility.LinkBuilderHelper;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base class for building notification messages.
 */
public abstract class NotificationMessageRenderer
{
    /** For help building links. (Static just to save overhead since it's stateless.) */
    private static LinkBuilderHelper linkBuilderHelper = new LinkBuilderHelper();

    /** For building links to activities. */
    private InContextActivityLinkBuilder activityLinkBuilder = new InContextActivityLinkBuilder();

    /**
     * Handler used on all links to make them raise an event the dialog can use to close itself. (Static just to save
     * overhead since it's stateless.)
     */
    private static ClickHandler linkClickHandler = new ClickHandler()
    {
        public void onClick(final ClickEvent inArg0)
        {
            Session.getInstance().getEventBus().notifyObservers(new DialogLinkClickedEvent());
        }
    };

    /**
     * Notification-specific method for building the message.
     * 
     * @param parent
     *            Parent panel into which to build the message.
     * @param notification
     *            The notification for which to render.
     */
    protected abstract void render(final Panel parent, final ApplicationAlertNotification notification);

    /**
     * Renders the message.
     * 
     * @param notification
     *            The notification for which to render.
     * @return Widget to display.
     */
    public Widget render(final ApplicationAlertNotification notification)
    {
        Panel message = new FlowPanel();

        render(message, notification);

        return message;
    }

    /**
     * @return A CSS style name to apply to the entire notification entry widget.
     */
    public abstract String getStyleName();

    /**
     * Gets the profile page for a given entity type.
     * 
     * @param type
     *            Entity type.
     * @return Page.
     */
    protected Page getEntityProfilePage(final EntityType type)
    {
        return linkBuilderHelper.getEntityProfilePage(type);
    }

    /**
     * Returns the text to display for an activity.
     * 
     * @param notification
     *            Notification.
     * @return The text to display for an activity.
     */
    protected String getActivityTypeName(final ApplicationAlertNotification notification)
    {
        BaseObjectType type = notification.getActivityType();
        if (type != null)
        {
            switch (type)
            {
            case BOOKMARK:
                return "link";
            case NOTE:
                return "message";
            default:
                return type.name().toLowerCase();
            }
        }

        return "post";
    }

    /**
     * Appends a link to the profile page for the notification's destination entity.
     * 
     * @param parent
     *            Parent panel.
     * @param notification
     *            Notification.
     */
    protected void appendDestinationLink(final Panel parent, final ApplicationAlertNotification notification)
    {
        Page page = getEntityProfilePage(notification.getDestinationType());
        appendLink(parent, notification.getDestinationName(), new CreateUrlRequest(page, notification
                .getDestinationUniqueId()));
    }

    /**
     * Appends a link to the profile page for the notification's destination entity.
     * 
     * @param parent
     *            Parent panel.
     * @param notification
     *            Notification.
     * @param extraParameters
     *            Extra parameters for the URL.
     */
    protected void appendDestinationLink(final Panel parent, final ApplicationAlertNotification notification,
            final Map<String, String> extraParameters)
    {
        Page page = getEntityProfilePage(notification.getDestinationType());
        appendLink(parent, notification.getDestinationName(), new CreateUrlRequest(page, notification
                .getDestinationUniqueId(), extraParameters));
    }

    /**
     * Appends a link to the profile page for the notification's auxiliary entity.
     * 
     * @param parent
     *            Parent panel.
     * @param notification
     *            Notification.
     */
    protected void appendAuxiliaryLink(final Panel parent, final ApplicationAlertNotification notification)
    {
        Page page = getEntityProfilePage(notification.getAuxiliaryType());
        appendLink(parent, notification.getAuxiliaryName(), new CreateUrlRequest(page, notification
                .getAuxiliaryUniqueId()));
    }

    /**
     * Appends a link to the notification's actor to the given element.
     * 
     * @param parent
     *            Parent panel..
     * @param notification
     *            Notification.
     */
    protected void appendActorLink(final Panel parent, final ApplicationAlertNotification notification)
    {
        appendLink(parent, notification.getActorName(), new CreateUrlRequest(Page.PEOPLE, notification
                .getActorAccountId()));
    }

    /**
     * Appends a link to the notification's activity to the given element.
     * 
     * @param parent
     *            Parent panel..
     * @param notification
     *            Notification.
     */
    protected void appendActivityLink(final Panel parent, final ApplicationAlertNotification notification)
    {
        appendActivityLink(parent, notification, null);
    }

    /**
     * Appends a link to the notification's activity to the given element.
     * 
     * @param parent
     *            Parent panel..
     * @param notification
     *            Notification.
     * @param extraParameters
     *            Extra parameters for the URL.
     */
    protected void appendActivityLink(final Panel parent, final ApplicationAlertNotification notification,
            final Map<String, String> extraParameters)
    {
        appendLink(parent, getActivityTypeName(notification), activityLinkBuilder.buildActivityPermalinkUrlRequest(
                notification.getActivityId(), notification.getDestinationType(), notification.getDestinationUniqueId(),
                extraParameters));
        // Note: Builds the link to display the activity "in context", i.e. on the profile page where the activity is
        // posted.
    }

    /**
     * Appends the given text (which will be automatically HTML encoded) to the given element.
     * 
     * @param parent
     *            Parent panel..
     * @param text
     *            Text.
     */
    protected void appendText(final Panel parent, final String text)
    {
        parent.add(new InlineLabel(text));
    }

    /**
     * Appends a link (whose text will be automatically HTML encoded) to the given element.
     * 
     * @param parent
     *            Parent panel..
     * @param text
     *            Display text.
     * @param urlSpec
     *            Defines the URL to be constructed.
     */
    protected void appendLink(final Panel parent, final String text, final CreateUrlRequest urlSpec)
    {
        String url = Session.getInstance().generateUrl(urlSpec);
        Hyperlink link = new InlineHyperlink(text, url);
        link.addClickHandler(linkClickHandler);
        parent.add(link);
    }
}
