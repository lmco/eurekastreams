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
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.dto.FeaturedStreamDTO;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.avatar.AvatarLinkPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * FlowPanel for the "Featured Streams" panel items.
 */
public class FeaturedStreamItemPanel extends FlowPanel
{
    /**
     * Constructor.
     *
     * @param inFeaturedStreamDTO
     *            the streamDTO to represent
     */
    public FeaturedStreamItemPanel(final FeaturedStreamDTO inFeaturedStreamDTO)
    {
        addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItem());
        addStyleName(StaticResourceBundle.INSTANCE.coreCss().listItem());
        addStyleName(StaticResourceBundle.INSTANCE.coreCss().person());

        add(new AvatarLinkPanel(inFeaturedStreamDTO.getEntityType(), inFeaturedStreamDTO.getUniqueId(),
                inFeaturedStreamDTO.getId(), inFeaturedStreamDTO.getAvatarId(), Size.Small));

        FlowPanel infoPanel = new FlowPanel();
        infoPanel.setStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemInfo());

        Widget name;

        Page linkPage;
        if (inFeaturedStreamDTO.getEntityType() == EntityType.PERSON)
        {
            linkPage = Page.PEOPLE;
        }
        else
        {
            // assume group
            linkPage = Page.GROUPS;
        }
        String nameUrl = Session.getInstance().generateUrl(//
                new CreateUrlRequest(linkPage, inFeaturedStreamDTO.getUniqueId()));

        name = new Hyperlink(inFeaturedStreamDTO.getDisplayName(), nameUrl);
        name.setStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemName());
        infoPanel.add(name);
        insertActionSeparator(infoPanel);
        infoPanel.add(new Label(inFeaturedStreamDTO.getDescription()));

        FlowPanel followersPanel = new FlowPanel();
        followersPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemFollowers());

        insertActionSeparator(followersPanel);

        if (inFeaturedStreamDTO.getEntityType() != EntityType.PERSON
                || inFeaturedStreamDTO.getEntityId() != Session.getInstance().getCurrentPerson().getEntityId())
        {
            // not the current person
            followersPanel.add(new FollowPanel(inFeaturedStreamDTO));
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
