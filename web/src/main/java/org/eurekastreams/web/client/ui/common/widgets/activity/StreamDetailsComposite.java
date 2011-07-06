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
package org.eurekastreams.web.client.ui.common.widgets.activity;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.action.request.profile.GetCurrentUserFollowingStatusRequest;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.HistoryViewsChangedEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotGroupModelViewInformationResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonFollowerStatusResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonalInformationResponseEvent;
import org.eurekastreams.web.client.events.data.GotStreamResponseEvent;
import org.eurekastreams.web.client.model.BaseModel;
import org.eurekastreams.web.client.model.CurrentUserPersonFollowingStatusModel;
import org.eurekastreams.web.client.model.Deletable;
import org.eurekastreams.web.client.model.GroupMembersModel;
import org.eurekastreams.web.client.model.Insertable;
import org.eurekastreams.web.client.model.PersonFollowersModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.animation.ExpandCollapseAnimation;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.common.charts.StreamAnalyticsChart;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.pager.FollowerPagerUiStrategy;
import org.eurekastreams.web.client.ui.common.pager.FollowingPagerUiStrategy;
import org.eurekastreams.web.client.ui.common.pager.PagerComposite;
import org.eurekastreams.web.client.ui.common.stream.FollowDialogContent;
import org.eurekastreams.web.client.ui.common.stream.renderers.AvatarRenderer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Post box.
 */
public class StreamDetailsComposite extends Composite
{
    /**
     * Binder for building UI.
     */
    private static LocalUiBinder binder = GWT.create(LocalUiBinder.class);

    /**
     * CSS resource.
     */
    interface StreamDetailsStyle extends CssResource
    {
        /**
         * Condensed Stream view.
         * 
         * @return Condensed Stream view.
         */
        String condensedStream();

        /**
         * Unfollow style.
         * 
         * @return Unfollow style.
         */
        String unFollowLink();;

        /**
         * Active option.
         * 
         * @return active option style.
         */
        String activeOption();

        String everyoneAvatar();

        String followingAvatar();

        String privateAvatar();
    }

    /**
     * CSS style.
     */
    @UiField
    StreamDetailsStyle style;

    /**
     * 
     * Binder for building UI.
     */
    interface LocalUiBinder extends UiBinder<Widget, StreamDetailsComposite>
    {
    }

    /**
     * Default constructor.
     */
    public StreamDetailsComposite()
    {
        initWidget(binder.createAndBindUi(this));
        buildPage();
    }

    /**
     * UI element for stream about panel.
     */
    @UiField
    HTMLPanel streamAbout;

    /**
     * UI element for stream details.
     */
    @UiField
    DivElement streamDetailsContainer;

    /**
     * UI element for chart.
     */
    @UiField
    HTMLPanel analyticsChartContainer;

    /**
     * UI element for about link.
     */
    @UiField
    Label aboutLink;

    /**
     * UI element for followers link.
     */
    @UiField
    Label followersLink;

    /**
     * UI element for following link.
     */
    @UiField
    Label followingLink;

    /**
     * UI element for toggling details.
     */
    @UiField
    Anchor toggleDetails;

    /**
     * UI element for configure link.
     */
    @UiField
    Anchor configureLink;

    /**
     * UI element for follower count.
     */
    @UiField
    SpanElement followerCount;

    /**
     * UI element for following count.
     */
    @UiField
    SpanElement followingCount;

    /**
     * UI element for stream name.
     */
    @UiField
    SpanElement streamName;

    /**
     * UI element for stream meta info.
     */
    @UiField
    SpanElement streamMeta;

    /**
     * UI element for stream avatar.
     */
    @UiField
    HTMLPanel streamAvatar;

    /**
     * UI element for condensed stream avatar.
     */
    @UiField
    HTMLPanel condensedAvatar;

    /**
     * UI element for stream description.
     */
    @UiField
    DivElement streamDescription;

    /**
     * UI element for stream interests.
     */
    @UiField
    DivElement streamInterests;

    /**
     * UI element for follow link.
     */
    @UiField
    Label followLink;

    /**
     * UI element for stream hash tags.
     */
    @UiField
    DivElement streamHashtags;

    /**
     * UI element for stream followers.
     */
    @UiField
    PagerComposite streamFollowers;

    /**
     * UI element for stream following.
     */
    @UiField
    PagerComposite streamFollowing;

    /**
     * Show following link.
     */
    @UiField
    Label showFollowing;

    /**
     * Show followers link.
     */
    @UiField
    Label showFollowers;

    /**
     * Current status.
     */
    private Follower.FollowerStatus status;

    /**
     * Expand animation duration.
     */
    private static final int EXPAND_ANIMATION_DURATION = 500;

    /**
     * Default stream details container size.
     */
    private static final int DEFAULT_STREAM_DETAILS_CONTAINER_SIZE = 550;

    /**
     * Avatar Renderer.
     */
    private AvatarRenderer avatarRenderer = new AvatarRenderer();

    /**
     * Expand/Collapse animation.
     */
    private ExpandCollapseAnimation detailsContainerAnimation;

    /**
     * Last following handler.
     */
    private HandlerRegistration lastHandler;

    /**
     * Model used to set following status.
     */
    private BaseModel followModel;

    /**
     * Stream request.
     */
    private String streamReq;

    /**
     * Stream ID.
     */
    private Long streamId;

    /**
     * Build page.
     */
    private void buildPage()
    {
        // Default style. Prevent flashing.
        streamName.setInnerText("Following");
        this.addStyleName(style.condensedStream());

        final StreamDetailsComposite thisClass = this;

        detailsContainerAnimation = new ExpandCollapseAnimation(streamDetailsContainer, EXPAND_ANIMATION_DURATION);
        final StreamAnalyticsChart chart = new StreamAnalyticsChart();

        streamAvatar.add(avatarRenderer.render(0L, null, EntityType.PERSON, Size.Normal));
        analyticsChartContainer.add(chart);
        chart.update();

        streamFollowers.init(new FollowerPagerUiStrategy());
        streamFollowing.init(new FollowingPagerUiStrategy());

        streamFollowers.setVisible(false);
        streamFollowing.setVisible(false);
        configureLink.setVisible(false);

        followersLink.addClickHandler(new ClickHandler()
        {

            public void onClick(final ClickEvent event)
            {
                openFollower();
            }
        });

        followingLink.addClickHandler(new ClickHandler()
        {

            public void onClick(final ClickEvent event)
            {
                openFollowing();
            }
        });

        showFollowers.addClickHandler(new ClickHandler()
        {

            public void onClick(final ClickEvent event)
            {
                openFollower();
            }
        });

        showFollowing.addClickHandler(new ClickHandler()
        {

            public void onClick(final ClickEvent event)
            {
                openFollowing();
            }
        });

        aboutLink.addClickHandler(new ClickHandler()
        {

            public void onClick(final ClickEvent event)
            {
                openAbout();
            }
        });

        EventBus.getInstance().addObserver(GotPersonalInformationResponseEvent.class,
                new Observer<GotPersonalInformationResponseEvent>()
                {
                    public void update(final GotPersonalInformationResponseEvent event)
                    {
                        PersonModelView person = event.getResponse();
                        streamId = person.getStreamId();
                        Session.getInstance().setPageTitle(person.getDisplayName());

                        if (person.getAccountId().equals(Session.getInstance().getCurrentPerson().getAccountId()))
                        {
                            configureLink.setVisible(true);
                            configureLink.setHref("#personalsettings/" + person.getAccountId());
                        }
                        else
                        {
                            configureLink.setVisible(false);
                        }

                        updateFollowLink(person.getAccountId(), EntityType.PERSON);

                        streamName.setInnerText(person.getDisplayName());
                        streamMeta.setInnerText(person.getTitle());
                        streamAvatar.clear();
                        streamAvatar.add(avatarRenderer.render(person.getEntityId(), person.getAvatarId(),
                                EntityType.PERSON, Size.Normal));

                        followerCount.setInnerText(Integer.toString(person.getFollowersCount()));
                        followingCount.setInnerText(Integer.toString(person.getFollowingCount()));
                        streamDescription.setInnerText(person.getJobDescription());
                        String interestString = "";
                        for (String interest : person.getInterests())
                        {
                            interestString += "<a href='#" + interest + "'>" + interest + "</a>";
                        }
                        streamInterests.setInnerHTML(interestString);

                        streamHashtags.setInnerHTML("<a href='#something'>#something</a>");
                    }
                });

        EventBus.getInstance().addObserver(GotGroupModelViewInformationResponseEvent.class,
                new Observer<GotGroupModelViewInformationResponseEvent>()
                {
                    public void update(final GotGroupModelViewInformationResponseEvent event)
                    {
                        DomainGroupModelView group = event.getResponse();
                        Session.getInstance().setPageTitle(group.getName());

                        if (group.isRestricted())
                        {
                            condensedAvatar.addStyleName(style.privateAvatar());
                            thisClass.addStyleName(style.condensedStream());
                        }
                        else
                        {

                            streamId = group.getStreamId();

                            boolean isCoordinator = false;

                            for (PersonModelView coordinator : group.getCoordinators())
                            {
                                if (coordinator.getAccountId().equals(
                                        Session.getInstance().getCurrentPerson().getAccountId()))
                                {
                                    isCoordinator = true;
                                    break;
                                }
                            }

                            if (isCoordinator
                                    || Session.getInstance().getCurrentPersonRoles().contains(Role.SYSTEM_ADMIN))
                            {
                                configureLink.setVisible(true);
                                configureLink.setHref("#groupsettings/" + group.getShortName());
                            }
                            else
                            {
                                configureLink.setVisible(false);
                            }

                            updateFollowLink(group.getShortName(), EntityType.GROUP);

                            streamName.setInnerText(group.getName());
                            streamMeta.setInnerText("");
                            // streamMeta.setInnerText(group.get);
                            streamAvatar.clear();
                            streamAvatar.add(avatarRenderer.render(group.getEntityId(), group.getAvatarId(),
                                    EntityType.GROUP, Size.Normal));

                            followerCount.setInnerText(Integer.toString(group.getFollowersCount()));
                            streamDescription.setInnerText(group.getDescription());
                            String interestString = "";
                            for (String interest : group.getCapabilities())
                            {
                                interestString += "<a href='#" + interest + "'>" + interest + "</a>";
                            }
                            streamInterests.setInnerHTML(interestString);
                            streamHashtags.setInnerHTML("<a href='#something'>#something</a>");
                        }
                    }
                });

        EventBus.getInstance().addObserver(GotStreamResponseEvent.class, new Observer<GotStreamResponseEvent>()
        {
            public void update(final GotStreamResponseEvent event)
            {
                streamReq = event.getJsonRequest();
            }
        });

        EventBus.getInstance().addObserver(HistoryViewsChangedEvent.class, new Observer<HistoryViewsChangedEvent>()
        {
            public void update(final HistoryViewsChangedEvent event)
            {
                detailsContainerAnimation.collapse();

                List<String> views = new ArrayList<String>(event.getViews());

                condensedAvatar.removeStyleName(style.everyoneAvatar());
                condensedAvatar.removeStyleName(style.followingAvatar());
                condensedAvatar.removeStyleName(style.privateAvatar());

                if (views == null || views.size() == 0 || views.get(0).equals("following"))
                {
                    streamName.setInnerText("Following");
                    Session.getInstance().setPageTitle("Following");
                    condensedAvatar.addStyleName(style.followingAvatar());
                    thisClass.addStyleName(style.condensedStream());
                }
                else if (views.get(0).equals("person") && views.size() >= 2)
                {
                    thisClass.removeStyleName(style.condensedStream());
                }
                else if (views.get(0).equals("group") && views.size() >= 2)
                {
                    thisClass.removeStyleName(style.condensedStream());
                }
                else if (views.get(0).equals("custom") && views.size() >= 3)
                {
                    streamName.setInnerText("Custom");
                    Session.getInstance().setPageTitle("Custom Stream");
                    thisClass.addStyleName(style.condensedStream());
                }
                else if (views.get(0).equals("everyone"))
                {
                    streamName.setInnerText("Everyone");
                    Session.getInstance().setPageTitle("Everyone");
                    condensedAvatar.addStyleName(style.everyoneAvatar());
                    thisClass.addStyleName(style.condensedStream());
                }
                else if (views.size() == 1)
                {
                    thisClass.addStyleName(style.condensedStream());
                }
            }
        }, true);

        toggleDetails.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                if (detailsContainerAnimation.isExpanded())
                {
                    detailsContainerAnimation.collapse();
                }
                else
                {
                    openAbout();
                }
            }
        });
    }

    /**
     * Update the following element.
     * 
     * @param entityId
     *            the id of the entity.
     * @param type
     *            the type.
     */
    public void updateFollowLink(final String entityId, final EntityType type)
    {
        if (!entityId.equals(Session.getInstance().getCurrentPerson().getAccountId()))
        {
            followLink.setVisible(true);
            followModel = GroupMembersModel.getInstance();

            if (type.equals(EntityType.PERSON))
            {
                followModel = PersonFollowersModel.getInstance();
            }

            if (lastHandler != null)
            {
                lastHandler.removeHandler();
            }

            lastHandler = followLink.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    SetFollowingStatusRequest request;

                    switch (status)
                    {
                    case FOLLOWING:
                        request = new SetFollowingStatusRequest(
                                Session.getInstance().getCurrentPerson().getAccountId(), entityId, type, false,
                                Follower.FollowerStatus.NOTFOLLOWING);
                        ((Deletable<SetFollowingStatusRequest>) followModel).delete(request);
                        onFollowerStatusChanged(Follower.FollowerStatus.NOTFOLLOWING);
                        break;
                    case NOTFOLLOWING:
                        request = new SetFollowingStatusRequest(
                                Session.getInstance().getCurrentPerson().getAccountId(), entityId, type, false,
                                Follower.FollowerStatus.FOLLOWING);
                        ((Insertable<SetFollowingStatusRequest>) followModel).insert(request);
                        Dialog.showCentered(new FollowDialogContent(streamName.getInnerText(), streamReq, streamId));
                        onFollowerStatusChanged(Follower.FollowerStatus.FOLLOWING);
                        break;
                    default:
                        // do nothing.
                        break;
                    }
                }
            });

            Session.getInstance().getEventBus().addObserver(GotPersonFollowerStatusResponseEvent.class,
                    new Observer<GotPersonFollowerStatusResponseEvent>()
                    {
                        public void update(final GotPersonFollowerStatusResponseEvent event)
                        {
                            onFollowerStatusChanged(event.getResponse());
                        }
                    });

            CurrentUserPersonFollowingStatusModel.getInstance().fetch(
                    new GetCurrentUserFollowingStatusRequest(entityId, type), true);
        }
        else
        {
            followLink.setVisible(false);
        }
    }

    /**
     * Open the about panel.
     */
    private void openAbout()
    {
        final int aboutPadding = 20;
        aboutLink.addStyleName(style.activeOption());
        followingLink.removeStyleName(style.activeOption());
        followersLink.removeStyleName(style.activeOption());

        streamFollowing.setVisible(false);
        streamAbout.setVisible(true);
        streamFollowers.setVisible(false);
        detailsContainerAnimation.expandWithPadding(aboutPadding);
    }

    /**
     * Open the following panel.
     */
    private void openFollowing()
    {
        final int followingSize = 475;
        aboutLink.removeStyleName(style.activeOption());
        followingLink.addStyleName(style.activeOption());
        followersLink.removeStyleName(style.activeOption());

        streamFollowers.setVisible(false);
        streamAbout.setVisible(false);
        streamFollowing.setVisible(true);
        streamFollowing.load();
        detailsContainerAnimation.expand(followingSize);
    }

    /**
     * Open the followers panel.
     */
    private void openFollower()
    {
        final int followerSize = 475;
        aboutLink.removeStyleName(style.activeOption());
        followingLink.removeStyleName(style.activeOption());
        followersLink.addStyleName(style.activeOption());

        streamFollowing.setVisible(false);
        streamAbout.setVisible(false);
        streamFollowers.setVisible(true);
        streamFollowers.load();
        detailsContainerAnimation.expand(followerSize);
    }

    /**
     * When the following status changes.
     * 
     * @param inStatus
     *            status.
     */
    private void onFollowerStatusChanged(final Follower.FollowerStatus inStatus)
    {
        status = inStatus;

        switch (inStatus)
        {
        case FOLLOWING:
            followLink.addStyleName(style.unFollowLink());
            break;
        case NOTFOLLOWING:
            followLink.removeStyleName(style.unFollowLink());
            break;
        default:
            break;
        }
    }
}
