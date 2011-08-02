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
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget to display a featured stream.
 */
public class FeaturedStreamItemPanel extends Composite
{
    /** Binder for building UI. */
    private static LocalUiBinder binder = GWT.create(LocalUiBinder.class);

    /**
     * Local styles.
     */
    interface LocalStyle extends CssResource
    {
        /** @return Apply to the follow panel to allow custom styling. */
        String followPanel();
    }

    /** Local styles. */
    @UiField
    LocalStyle style;

    /** Global styles. */
    @UiField(provided = true)
    CoreCss coreCss;

    /** Left column. */
    @UiField
    HTMLPanel leftPanel;

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
     * @param inStreamDTO
     *            the streamDTO to represent
     */
    public FeaturedStreamItemPanel(final FeaturedStreamDTO inStreamDTO)
    {
        coreCss = StaticResourceBundle.INSTANCE.coreCss();
        avatarPanel = new AvatarLinkPanel(inStreamDTO.getEntityType(), inStreamDTO.getUniqueId(), inStreamDTO.getId(),
                inStreamDTO.getAvatarId(), Size.Normal);
        Widget main = binder.createAndBindUi(this);
        initWidget(main);

        // add follow controls if not the current person
        if (inStreamDTO.getEntityType() != EntityType.PERSON
                || inStreamDTO.getEntityId() != Session.getInstance().getCurrentPerson().getEntityId())
        {
            Widget followPanel = new FollowPanel(inStreamDTO);
            followPanel.addStyleName(style.followPanel());
            leftPanel.add(followPanel);
        }

        // assume group if not person
        Page linkPage = (inStreamDTO.getEntityType() == EntityType.PERSON) ? Page.PEOPLE : Page.GROUPS;
        String nameUrl = Session.getInstance().generateUrl(//
                new CreateUrlRequest(linkPage, inStreamDTO.getUniqueId()));
        streamNameLink.setTargetHistoryToken(nameUrl);
        streamNameLink.setText(inStreamDTO.getDisplayName());

        streamDescriptionText.setInnerText(inStreamDTO.getDescription());
    }

    /**
     * Binder for building UI.
     */
    interface LocalUiBinder extends UiBinder<Widget, FeaturedStreamItemPanel>
    {
    }
}
