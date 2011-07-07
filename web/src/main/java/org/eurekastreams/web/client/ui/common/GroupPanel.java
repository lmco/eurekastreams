/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.Insertable;
import org.eurekastreams.web.client.model.PersonFollowersModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.avatar.AvatarLinkPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
        addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItem());
        addStyleName(StaticResourceBundle.INSTANCE.coreCss().listItem());
        addStyleName(StaticResourceBundle.INSTANCE.coreCss().group());

        add(new AvatarLinkPanel(EntityType.GROUP, group.getUniqueId(), group.getId(), group.getAvatarId(), Size.Small));

        FlowPanel infoPanel = new FlowPanel();
        infoPanel.setStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemInfo());

        if (!group.isPublic())
        {
            Label icon = new Label();
            icon.addStyleName(StaticResourceBundle.INSTANCE.coreCss().privateIcon());
            icon.setTitle("Private Group");
            infoPanel.add(icon);
        }

        Widget name;
        if (makeLinkable)
        {
            String nameUrl = Session.getInstance().generateUrl(new CreateUrlRequest(Page.GROUPS, group.getShortName()));
            name = new Hyperlink(group.getName(), nameUrl);
        }
        else
        {
            name = new Label(group.getName());
        }
        name.setStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemName());

        infoPanel.add(name);

        if (showDescription)
        {
            String descriptionText = group.getDescription();
            if (descriptionText != null && !descriptionText.isEmpty())
            {
                Label about = new Label(descriptionText);
                about.addStyleName(StaticResourceBundle.INSTANCE.coreCss().missionStatement());
                infoPanel.add(about);
            }
        }

        if (showFollowers)
        {
            FlowPanel followersPanel = new FlowPanel();
            followersPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemFollowers());

            InlineLabel followers = new InlineLabel("Followers: ");
            followersPanel.add(followers);

            InlineLabel followersCount = new InlineLabel(Integer.toString(group.getFollowersCount()));
            followersCount.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemFollowersData());
            followersPanel.add(followersCount);

            insertActionSeparator(followersPanel);

            followersPanel.add(new InlineLabel("Added: "));

            DateFormatter dateFormatter = new DateFormatter(new Date());
            InlineLabel dateAdded = new InlineLabel(dateFormatter.timeAgo(group.getDateAdded(), true));
            dateAdded.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemFollowersData());
            followersPanel.add(dateAdded);

            infoPanel.add(followersPanel);
        }
        
        infoPanel.add(getFollowWidget(group));

        this.add(infoPanel);
    }

    
    /**
     * Get the follow widget.
     * 
     * @param group
     *            the group.
     * @return the widget.
     */
    private Widget getFollowWidget(final DomainGroupModelView group)
    {
        FollowerStatus status = group.getFollowerStatus();
        FlowPanel followPanel = new FlowPanel();

        if (status != null)
        {
            final Label unfollowLink = new Label("");
            unfollowLink.setVisible(false);
            final Label followLink = new Label("");
            followLink.setVisible(false);

            unfollowLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().followLink());
            unfollowLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().unFollowLink());
            unfollowLink.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    SetFollowingStatusRequest request = new SetFollowingStatusRequest(Session.getInstance()
                            .getCurrentPerson().getAccountId(), group.getShortName(), EntityType.GROUP, false,
                            Follower.FollowerStatus.NOTFOLLOWING);
                    ((Insertable<SetFollowingStatusRequest>) PersonFollowersModel.getInstance()).insert(request);
                    unfollowLink.setVisible(false);
                    followLink.setVisible(true);
                }
            });

            followPanel.add(unfollowLink);

            followLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().followLink());
            followLink.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    SetFollowingStatusRequest request = new SetFollowingStatusRequest(Session.getInstance()
                            .getCurrentPerson().getAccountId(), group.getShortName(), EntityType.GROUP, false,
                            Follower.FollowerStatus.FOLLOWING);
                    ((Insertable<SetFollowingStatusRequest>) PersonFollowersModel.getInstance()).insert(request);
                    unfollowLink.setVisible(true);
                    followLink.setVisible(false);
                }
            });

            followPanel.add(followLink);

            switch (status)
            {
            case FOLLOWING:
                unfollowLink.setVisible(true);
                break;
            case NOTFOLLOWING:
                followLink.setVisible(true);
                break;
            default:
                break;
            }
        }
        
        return followPanel;
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
        sep.addStyleName(StaticResourceBundle.INSTANCE.coreCss().actionLinkSeparator());
        panel.add(sep);
    }
}
