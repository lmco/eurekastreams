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

import org.eurekastreams.commons.formatting.DateFormatter;
import org.eurekastreams.server.domain.ApplicationAlertNotification;
import org.eurekastreams.server.domain.NotificationType;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Creates the widgets for a notification.
 */
@SuppressWarnings("serial")
public class NotificationsRenderer
{
    /** Date formatter. */
    private DateFormatter dateFormatter = new DateFormatter();

    /** Renderers for the message text. (Static to not waste time re-creating these.) */
    private static Map<NotificationType, NotificationMessageRenderer> messageRenderers = // \n
            new HashMap<NotificationType, NotificationMessageRenderer>()
            {
                {
                    put(NotificationType.POST_TO_PERSONAL_STREAM, new PostToPersonalStreamMessageRenderer());
                    put(NotificationType.COMMENT_TO_PERSONAL_STREAM, new CommentToPersonalStreamMessageRenderer());
                    put(NotificationType.COMMENT_TO_PERSONAL_POST, new CommentToPersonalPostMessageRenderer());
                    put(NotificationType.COMMENT_TO_COMMENTED_POST, new CommentToCommentedPostMessageRenderer());
                    put(NotificationType.FOLLOW_PERSON, new FollowPersonMessageRenderer());
                    put(NotificationType.POST_TO_GROUP_STREAM, new PostToGroupStreamMessageRenderer());
                    put(NotificationType.COMMENT_TO_GROUP_STREAM, new CommentToGroupStreamMessageRenderer());
                    put(NotificationType.FOLLOW_GROUP, new FollowGroupMessageRenderer());
                    put(NotificationType.FLAG_PERSONAL_ACTIVITY, new FlagActivityMessageRenderer());
                    put(NotificationType.FLAG_GROUP_ACTIVITY, new FlagActivityMessageRenderer());
                    put(NotificationType.REQUEST_NEW_GROUP, new RequestNewGroupMessageRenderer());
                    put(NotificationType.REQUEST_GROUP_ACCESS, new RequestGroupAccessMessageRenderer());
                }
            };

    /**
     * Creates the widgets for a notification.
     *
     * @param notif
     *            Notification.
     * @return Widget to display.
     */
    public Widget render(final ApplicationAlertNotification notif)
    {
        // build the main panel
        Panel main = new FlowPanel();
        main.addStyleName("notif-entry");
        if (!notif.isRead())
        {
            main.addStyleName("notif-entry-unread");
        }

        // -- icon side --
        Widget icon = new Label();
        icon.addStyleName("icon");
        main.add(icon);

        // -- text side --

        Panel textPanel = new FlowPanel();
        textPanel.addStyleName("notif-text-side");
        main.add(textPanel);

        // build the message text
        NotificationMessageRenderer msgRenderer = messageRenderers.get(notif.getNotificiationType());
        if (msgRenderer != null)
        {
            textPanel.add(msgRenderer.render(notif));
            main.addStyleName(msgRenderer.getStyleName());
        }

        // build the time
        Label when = new Label(dateFormatter.timeAgo(notif.getNotificationDate()));
        when.addStyleName("notif-timestamp");
        textPanel.add(when);

        return main;
    }
}
