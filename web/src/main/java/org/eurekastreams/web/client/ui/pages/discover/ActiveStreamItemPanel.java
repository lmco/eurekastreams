/*
 * Copyright (c) 2011-2012 Lockheed Martin Corporation
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
import org.eurekastreams.server.domain.dto.StreamDTO;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.avatar.AvatarLinkPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.pages.master.CoreCss;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * FlowPanel for the "Most Active Streams" panel items.
 */
public class ActiveStreamItemPanel extends Composite
{
    /** Binder for building UI. */
    private static LocalUiBinder binder = GWT.create(LocalUiBinder.class);

    /**
     * Local styles.
     */
    interface LocalStyle extends CssResource
    {
        /** @return Button style. */
        @ClassName("request")
        String request();

        /** @return Button style. */
        @ClassName("pending")
        String pending();

        /** @return Apply to the follow panel to allow custom styling. */
        String followPanel();
    }

    /** Local styles. */
    @UiField
    LocalStyle style;

    /** Global styles. */
    @UiField(provided = true)
    CoreCss coreCss;

    /** Avatar panel. */
    @UiField(provided = true)
    Widget avatarPanel;

    /** Panel holding the details. */
    @UiField
    HTMLPanel infoPanel;

    /** Name link. */
    @UiField
    Hyperlink streamNameLink;

    /** Message count display widget. */
    @UiField
    SpanElement messageCount;

    /**
     * Constructor.
     *
     * @param inStreamDTO
     *            the streamDTO to represent
     */
    public ActiveStreamItemPanel(final StreamDTO inStreamDTO)
    {
        coreCss = StaticResourceBundle.INSTANCE.coreCss();
        avatarPanel = new AvatarLinkPanel(inStreamDTO.getEntityType(), inStreamDTO.getUniqueId(),
                inStreamDTO.getAvatarId(), Size.Small);
        Widget main = binder.createAndBindUi(this);
        initWidget(main);

        // add follow controls if not the current person
        if (inStreamDTO.getEntityType() != EntityType.PERSON
                || inStreamDTO.getEntityId() != Session.getInstance().getCurrentPerson().getEntityId())
        {
            final Widget followPanel;
            // it's not the current user - see if it's a private group, and if we're not admin
            if (inStreamDTO.getEntityType() == EntityType.GROUP && inStreamDTO instanceof DomainGroupModelView
                    && ((DomainGroupModelView) inStreamDTO).isPublic() != null
                    && !((DomainGroupModelView) inStreamDTO).isPublic()
                    && !Session.getInstance().getCurrentPerson().getRoles().contains(Role.SYSTEM_ADMIN))
            {
                // this is a private group and we're not an admin, so we gotta request access
                followPanel = new FollowPanel(inStreamDTO, style.request(), StaticResourceBundle.INSTANCE.coreCss()
                        .unFollowLink(), StaticResourceBundle.INSTANCE.coreCss().followLink(), false, style.pending());
            }
            else
            {
                followPanel = new FollowPanel(inStreamDTO);
            }
            followPanel.addStyleName(style.followPanel());
            infoPanel.add(followPanel);
        }

        // set text and link for name; assume group if not person
        Page linkPage = (inStreamDTO.getEntityType() == EntityType.PERSON) ? Page.PEOPLE : Page.GROUPS;
        String nameUrl = Session.getInstance().generateUrl(//
                new CreateUrlRequest(linkPage, inStreamDTO.getUniqueId()));
        streamNameLink.setTargetHistoryToken(nameUrl);
        streamNameLink.setText(inStreamDTO.getDisplayName());
        streamNameLink.setTitle(inStreamDTO.getDisplayName());

        messageCount.setInnerText(inStreamDTO.getFollowersCount() == 1 ? "1 Daily Message" : Integer
                .toString(inStreamDTO.getFollowersCount()) + " Daily Messages");
    }

    /**
     * Binder for building UI.
     */
    interface LocalUiBinder extends UiBinder<Widget, ActiveStreamItemPanel>
    {
    }
}
