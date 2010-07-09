/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common;

import java.util.Date;

import org.eurekastreams.commons.formatting.DateFormatter;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.avatar.AvatarLinkPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Display information about a group.
 */
public class GroupPanel extends FlowPanel
{
    /**
     * Constructor.
     * 
     * @param group
     *            to display
     * @param showFollowers
     *            to display
     * @param makeLinkable
     *            whether to display it or not
     * @param showDescription
     *            Whether to display the description line.
     */
    public GroupPanel(final DomainGroupModelView group, final boolean makeLinkable, final boolean showFollowers,
            final boolean showDescription)
    {
        addStyleName("connection-item");
        addStyleName("list-item");
        addStyleName("group");

        add(new AvatarLinkPanel(EntityType.GROUP, group.getUniqueId(), group.getId(), group.getAvatarId(), Size.Small));

        FlowPanel infoPanel = new FlowPanel();
        infoPanel.setStyleName("connection-item-info");

        if (!group.isPublic())
        {
            Label icon = new Label();
            icon.addStyleName("private-icon");
            icon.setTitle("Private Group");
            infoPanel.add(icon);
        }

        Widget name;
        Widget org;
        if (makeLinkable)
        {
            String nameUrl = Session.getInstance().generateUrl(new CreateUrlRequest(Page.GROUPS, group.getShortName()));
            String orgUrl = Session.getInstance().generateUrl(
                    new CreateUrlRequest(Page.ORGANIZATIONS, group.getParentOrganizationShortName()));
            name = new Hyperlink(group.getName(), nameUrl);
            org = new Hyperlink(group.getParentOrganizationName(), orgUrl);
        }
        else
        {
            name = new Label(group.getName());
            org = new Label(group.getParentOrganizationName());
        }
        name.setStyleName("connection-item-name");
        org.setStyleName("connection-item-organization");

        infoPanel.add(name);
        infoPanel.add(org);

        if (showDescription)
        {
            String descriptionText = group.getDescription();
            if (descriptionText != null && !descriptionText.isEmpty())
            {
                Label about = new Label(descriptionText);
                about.addStyleName("mission-statement");
                infoPanel.add(about);
            }
        }

        if (showFollowers)
        {
            FlowPanel followersPanel = new FlowPanel();
            followersPanel.addStyleName("connection-item-followers");

            InlineLabel followers = new InlineLabel("Followers: ");
            followersPanel.add(followers);

            InlineLabel followersCount = new InlineLabel(Integer.toString(group.getFollowersCount()));
            followersCount.addStyleName("connection-item-followers-data");
            followersPanel.add(followersCount);

            insertActionSeparator(followersPanel);

            followersPanel.add(new InlineLabel("Added: "));

            DateFormatter dateFormatter = new DateFormatter(new Date());
            InlineLabel dateAdded = new InlineLabel(dateFormatter.timeAgo(group.getDateAdded(), true));
            dateAdded.addStyleName("connection-item-followers-data");
            followersPanel.add(dateAdded);

            infoPanel.add(followersPanel);
        }

        this.add(infoPanel);
    }

    /**
     * Adds a separator (dot).
     * 
     * @param panel
     *            Panel to put the separator in.
     */
    private void insertActionSeparator(final Panel panel)
    {
        Label sep = new InlineLabel("\u2219");
        sep.addStyleName("action-link-separator");
        panel.add(sep);
    }
}
