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
package org.eurekastreams.web.client.ui.pages.discover;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.dto.StreamDTO;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.avatar.AvatarLinkPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * FlowPanel for the "Most Active Streams" panel items.
 */
public class ActiveStreamsItemPanel extends FlowPanel
{
    /**
     * Constructor.
     * 
     * @param inStreamDTO
     *            the streamDTO to represent
     */
    public ActiveStreamsItemPanel(final StreamDTO inStreamDTO)
    {
        addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItem());
        addStyleName(StaticResourceBundle.INSTANCE.coreCss().listItem());
        addStyleName(StaticResourceBundle.INSTANCE.coreCss().person());

        add(new AvatarLinkPanel(inStreamDTO.getEntityType(), inStreamDTO.getUniqueId(), inStreamDTO.getId(),
                inStreamDTO.getAvatarId(), Size.Small));

        FlowPanel infoPanel = new FlowPanel();
        infoPanel.setStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemInfo());

        Widget name;

        Page linkPage;
        if (inStreamDTO.getEntityType() == EntityType.PERSON)
        {
            linkPage = Page.PEOPLE;
        }
        else
        {
            // assume group
            linkPage = Page.GROUPS;
        }
        String nameUrl = Session.getInstance().generateUrl(//
                new CreateUrlRequest(linkPage, inStreamDTO.getUniqueId()));

        name = new Hyperlink(inStreamDTO.getDisplayName(), nameUrl);
        name.setStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemName());
        infoPanel.add(name);

        FlowPanel followersPanel = new FlowPanel();
        followersPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemFollowers());

        if (inStreamDTO.getFollowersCount() == 1)
        {
            followersPanel.add(new InlineLabel("1 Daily Message"));
        }
        else
        {
            followersPanel.add(new InlineLabel(Integer.toString(inStreamDTO.getFollowersCount()) + " Daily Messages"));
        }
        insertActionSeparator(followersPanel);

        if (inStreamDTO.getFollowerStatus() == FollowerStatus.FOLLOWING)
        {
            followersPanel.add(new HTML("(FOLLOWING)"));
        }
        infoPanel.add(followersPanel);

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
        sep.addStyleName(StaticResourceBundle.INSTANCE.coreCss().actionLinkSeparator());
        panel.add(sep);
    }
}
