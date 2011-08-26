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

import java.util.Date;

import org.eurekastreams.commons.formatting.DateFormatter;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.dto.StreamDTO;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.BlockedSuggestionModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.master.CoreCss;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * FlowPanel for the four simple lists of StreamDTOs on the Discover page.
 */
public class DiscoverListItemPanel extends Composite
{
    /**
     * The type subtext type to show under the name.
     */
    public enum ListItemType
    {
        /**
         * Mutual follower(s).
         */
        MUTUAL_FOLLOWERS,

        /**
         * Daily viewer(s).
         */
        DAILY_VIEWERS,

        /**
         * Follower(s).
         */
        FOLLOWERS,

        /**
         * Formatted time ago.
         */
        TIME_AGO
    };

    /** Binder for building UI. */
    private static LocalUiBinder binder = GWT.create(LocalUiBinder.class);

    /** JSNI. */
    private static WidgetJSNIFacadeImpl jsniFacade = new WidgetJSNIFacadeImpl();

    /**
     * Local styles.
     */
    interface LocalStyle extends CssResource
    {
        /** @return Button style. */
        @ClassName("request-button")
        String requestButton();

        /** @return Button style. */
        @ClassName("pending-button")
        String pendingButton();

        /** @return Apply to the follow panel to allow custom styling. */
        @ClassName("follow-controls-panel")
        String followControlsPanel();

        /** @return Button style. */
        @ClassName("follow-button")
        String followButton();

        /** @return Button style. */
        @ClassName("unfollow-button")
        String unfollowButton();

        /** @return Button style. */
        @ClassName("block-button")
        String blockButton();

        /** @return When multiple controls are in the follower controls panel. */
        String multi();
    }

    /** Local styles. */
    @UiField
    LocalStyle style;

    /** Global styles. */
    @UiField(provided = true)
    CoreCss coreCss;

    /** Name link. */
    @UiField
    Hyperlink streamNameLink;

    /** Displays info about stream. */
    @UiField
    Element streamInfoText;

    /**
     * Constructor.
     * 
     * @param inStreamDTO
     *            the streamDTO to represent
     * @param inListItemType
     *            list item type
     */
    public DiscoverListItemPanel(final StreamDTO inStreamDTO, final ListItemType inListItemType)
    {
        this(inStreamDTO, inListItemType, false);
    }

    /**
     * Constructor.
     * 
     * @param inStreamDTO
     *            the streamDTO to represent
     * @param inListItemType
     *            list item type
     * @param showBlockSuggestion
     *            show block suggestion controls.
     */
    public DiscoverListItemPanel(final StreamDTO inStreamDTO, final ListItemType inListItemType,
            final boolean showBlockSuggestion)
    {
        coreCss = StaticResourceBundle.INSTANCE.coreCss();
        HTMLPanel main = (HTMLPanel) binder.createAndBindUi(this);
        initWidget(main);

        // set text and link for name; assume group if not person
        Page linkPage = (inStreamDTO.getEntityType() == EntityType.PERSON) ? Page.PEOPLE : Page.GROUPS;
        String nameUrl = Session.getInstance().generateUrl(//
                new CreateUrlRequest(linkPage, inStreamDTO.getUniqueId()));
        streamNameLink.setTargetHistoryToken(nameUrl);
        streamNameLink.setText(inStreamDTO.getDisplayName());
        streamNameLink.setTitle(inStreamDTO.getDisplayName());

        // set info text
        switch (inListItemType)
        {
        case MUTUAL_FOLLOWERS:
            if (inStreamDTO.getFollowersCount() == 1)
            {
                streamInfoText.setInnerText("1 Mutual Follower");
            }
            else
            {
                streamInfoText.setInnerText(Integer.toString(inStreamDTO.getFollowersCount()) + " Mutual Followers");
            }
            break;
        case DAILY_VIEWERS:
            if (inStreamDTO.getFollowersCount() == 1)
            {
                streamInfoText.setInnerText("1 Daily Viewer");
            }
            else
            {
                streamInfoText.setInnerText(Integer.toString(inStreamDTO.getFollowersCount()) + " Daily Viewers");
            }
            break;
        case FOLLOWERS:
            if (inStreamDTO.getFollowersCount() == 1)
            {
                streamInfoText.setInnerText("1 Follower");
            }
            else
            {
                streamInfoText.setInnerText(Integer.toString(inStreamDTO.getFollowersCount()) + " Followers");
            }
            break;
        case TIME_AGO:
            DateFormatter dateFormatter = new DateFormatter(new Date());
            streamInfoText.setInnerText(dateFormatter.timeAgo(inStreamDTO.getDateAdded(), true));
            break;
        default:
            break;
        }

        // add following controls if not the current person
        if (inStreamDTO.getEntityType() != EntityType.PERSON
                || inStreamDTO.getEntityId() != Session.getInstance().getCurrentPerson().getEntityId())
        {
            final FollowPanel followPanel;
            ClickHandler clickHandler = null;
            if (showBlockSuggestion)
            {
                // NOTE: this is a hack - this doesn't have anything to do with blocking suggestions, it just happens
                // that the only list that removes streams after joining them happens to be the one that allows streams
                // to be blocked
                clickHandler = new ClickHandler()
                {
                    public void onClick(final ClickEvent event)
                    {
                        removeFromParent();
                    }
                };
            }

            // it's not the current user - see if it's a private group, and if we're not admin
            if (inStreamDTO.getEntityType() == EntityType.GROUP && inStreamDTO instanceof DomainGroupModelView
                    && ((DomainGroupModelView) inStreamDTO).isPublic() != null
                    && !((DomainGroupModelView) inStreamDTO).isPublic()
                    && !Session.getInstance().getCurrentPerson().getRoles().contains(Role.SYSTEM_ADMIN))
            {
                // this is a private group and we're not an admin, so we gotta request access
                // note: no click handler since you can't join this group - just show it as pending
                followPanel = new FollowPanel(inStreamDTO, style.requestButton(), style.unfollowButton(),
                        coreCss.buttonLabel(), true, style.pendingButton());
            }
            else
            {
                // either not a private group, or we're admin and it doesn't matter - just show join/unjoin
                followPanel = new FollowPanel(inStreamDTO, style.followButton(), style.unfollowButton(),
                        coreCss.buttonLabel(), true, clickHandler, null);
            }

            if (!showBlockSuggestion)
            {
                followPanel.addStyleName(style.followControlsPanel());
                main.add(followPanel);
            }
            else
            {
                Panel panel = new FlowPanel();
                panel.addStyleName(style.followControlsPanel());
                panel.addStyleName(style.multi());
                panel.add(followPanel);

                final Label block = new Label();
                block.addStyleName(style.blockButton());
                block.setTitle("Block this suggestion");
                block.addClickHandler(new ClickHandler()
                {
                    public void onClick(final ClickEvent arg0)
                    {
                        if (jsniFacade.confirm("Are you sure you want to block this suggestion?"))
                        {
                            BlockedSuggestionModel.getInstance().insert(inStreamDTO.getStreamScopeId());
                            removeFromParent();
                        }
                    }
                });

                panel.add(block);
                main.add(panel);
            }
        }
    }

    /**
     * Binder for building UI.
     */
    interface LocalUiBinder extends UiBinder<Widget, DiscoverListItemPanel>
    {
    }
}
