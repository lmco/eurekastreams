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
import org.eurekastreams.web.client.ui.pages.master.CoreCss;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget to display a featured stream.
 */
public class FeaturedStreamItemPanel extends Composite
{
    /** Binder for building UI. */
    private static LocalUiBinder binder = GWT.create(LocalUiBinder.class);

    /** Global styles. */
    @UiField(provided = true)
    CoreCss coreCss;

    /** Left column. */
    @UiField
    DivElement leftPanel;

    /** Avatar panel. */
    @UiField(provided = true)
    AvatarLinkPanel avatarPanel;

    /** Link for stream name. */
    @UiField
    Hyperlink streamNameLink;

    /** Text area for stream description. */
    @UiField
    DivElement streamDescriptionText;

    /**
     * Constructor.
     *
     * @param inFeaturedStreamDTO
     *            the streamDTO to represent
     */
    public FeaturedStreamItemPanel(final FeaturedStreamDTO inFeaturedStreamDTO)
    {
        coreCss = StaticResourceBundle.INSTANCE.coreCss();
        avatarPanel = new AvatarLinkPanel(inFeaturedStreamDTO.getEntityType(), inFeaturedStreamDTO.getUniqueId(),
                inFeaturedStreamDTO.getId(), inFeaturedStreamDTO.getAvatarId(), Size.Normal);
        Widget main = binder.createAndBindUi(this);
        initWidget(main);

        // add follow controls if not the current person
        if (inFeaturedStreamDTO.getEntityType() != EntityType.PERSON
                || inFeaturedStreamDTO.getEntityId() != Session.getInstance().getCurrentPerson().getEntityId())
        {
            leftPanel.appendChild(new FollowPanel(inFeaturedStreamDTO).getElement());
        }

        // assume group if not person
        Page linkPage = (inFeaturedStreamDTO.getEntityType() == EntityType.PERSON) ? Page.PEOPLE : Page.GROUPS;
        String nameUrl = Session.getInstance().generateUrl(//
                new CreateUrlRequest(linkPage, inFeaturedStreamDTO.getUniqueId()));
        streamNameLink.setTargetHistoryToken(nameUrl);
        streamNameLink.setText(inFeaturedStreamDTO.getDisplayName());

        streamDescriptionText.setInnerText(inFeaturedStreamDTO.getDescription());

        /*
         * Panel main = new FlowPanel(); Panel left = new FlowPanel();
         *
         * left.add(new AvatarLinkPanel(inFeaturedStreamDTO.getEntityType(), inFeaturedStreamDTO.getUniqueId(),
         * inFeaturedStreamDTO.getId(), inFeaturedStreamDTO.getAvatarId(), Size.Normal));
         *
         * // add follow controls if not the current person if (inFeaturedStreamDTO.getEntityType() != EntityType.PERSON
         * || inFeaturedStreamDTO.getEntityId() != Session.getInstance().getCurrentPerson().getEntityId()) {
         * left.add(new FollowPanel(inFeaturedStreamDTO)); }
         *
         * main.add(left);
         *
         * FlowPanel infoPanel = new FlowPanel();
         * infoPanel.setStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemInfo());
         *
         * // assume group if not person Page linkPage = (inFeaturedStreamDTO.getEntityType() == EntityType.PERSON) ?
         * Page.PEOPLE : Page.GROUPS; String nameUrl = Session.getInstance().generateUrl(// new
         * CreateUrlRequest(linkPage, inFeaturedStreamDTO.getUniqueId())); Widget name = new
         * Hyperlink(inFeaturedStreamDTO.getDisplayName(), nameUrl);
         * name.setStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemName()); infoPanel.add(name);
         * infoPanel.add(new Label(inFeaturedStreamDTO.getDescription()));
         *
         * main.add(infoPanel); initWidget(main);
         *
         * addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItem());
         * addStyleName(StaticResourceBundle.INSTANCE.coreCss().listItem());
         * addStyleName(StaticResourceBundle.INSTANCE.coreCss().person());
         */
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

    /**
     * Binder for building UI.
     */
    interface LocalUiBinder extends UiBinder<Widget, FeaturedStreamItemPanel>
    {
    }
}
