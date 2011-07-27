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

import java.util.List;

import org.eurekastreams.server.action.request.profile.GetCurrentUserFollowingStatusRequest;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.action.request.stream.GetFeaturedStreamsPageRequest;
import org.eurekastreams.server.action.request.stream.StreamPopularHashTagsRequest;
import org.eurekastreams.server.domain.DailyUsageSummary;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.dto.FeaturedStreamDTO;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.server.search.modelview.UsageMetricSummaryDTO;
import org.eurekastreams.server.service.actions.requests.UsageMetricStreamSummaryRequest;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.GotStreamPopularHashTagsEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.PagerResponseEvent;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.data.DeletedRequestForGroupMembershipResponseEvent;
import org.eurekastreams.web.client.events.data.GotFeaturedStreamsPageResponseEvent;
import org.eurekastreams.web.client.events.data.GotGroupModelViewInformationResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonFollowerStatusResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonalInformationResponseEvent;
import org.eurekastreams.web.client.events.data.GotStreamResponseEvent;
import org.eurekastreams.web.client.events.data.GotUsageMetricSummaryEvent;
import org.eurekastreams.web.client.events.data.InsertedGroupMemberResponseEvent;
import org.eurekastreams.web.client.model.BaseModel;
import org.eurekastreams.web.client.model.CurrentUserPersonFollowingStatusModel;
import org.eurekastreams.web.client.model.Deletable;
import org.eurekastreams.web.client.model.FeaturedStreamModel;
import org.eurekastreams.web.client.model.GroupMembersModel;
import org.eurekastreams.web.client.model.Insertable;
import org.eurekastreams.web.client.model.PersonFollowersModel;
import org.eurekastreams.web.client.model.PopularHashTagsModel;
import org.eurekastreams.web.client.model.UsageMetricModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.animation.ExpandCollapseAnimation;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.common.charts.StreamAnalyticsChart;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.common.pager.FollowerPagerUiStrategy;
import org.eurekastreams.web.client.ui.common.pager.FollowingPagerUiStrategy;
import org.eurekastreams.web.client.ui.common.pager.GroupMembershipRequestPagerUiStrategy;
import org.eurekastreams.web.client.ui.common.pager.PagerComposite;
import org.eurekastreams.web.client.ui.common.stream.FeatureDialogContent;
import org.eurekastreams.web.client.ui.common.stream.FollowDialogContent;
import org.eurekastreams.web.client.ui.common.stream.renderers.AvatarRenderer;
import org.eurekastreams.web.client.ui.common.stream.transformers.StreamSearchLinkBuilder;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
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
         * Active option.
         * 
         * @return active option style.
         */
        String activeOption();

        /**
         * Everyone avatar.
         * 
         * @return everyone avatar.
         */
        String everyoneAvatar();

        /**
         * Following avatar.
         * 
         * @return following avatar.
         */
        String followingAvatar();

        /**
         * Private avatar.
         * 
         * @return Private avatar.
         */
        String privateAvatar();

        /**
         * Hide details button.
         * 
         * @return hide details button.
         */
        String hideDetails();

        /**
         * Featured item header link style.
         * 
         * @return Featured item header link style.
         */
        String headerFeatured();
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
     * UI element for featuring a stream.
     */
    @UiField
    Label featureLink;

    /**
     * UI element for admin link.
     */
    @UiField
    Label adminLink;

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
    DivElement streamName;

    /**
     * UI element for stream meta info.
     */
    @UiField
    DivElement streamMeta;

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
    DivElement contactInfo;
    
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
     * UI element admin tab content.
     */
    @UiField
    PagerComposite adminContent;

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
     * Chart.
     */
    @UiField
    StreamAnalyticsChart chart;

    /**
     * Average viewers.
     */
    @UiField
    SpanElement avgViewers;

    /**
     * Average views.
     */
    @UiField
    SpanElement avgViews;

    /**
     * Average contributors.
     */
    @UiField
    SpanElement avgContributors;

    /**
     * Average messages.
     */
    @UiField
    SpanElement avgMessages;

    /**
     * Average comments.
     */
    @UiField
    SpanElement avgComments;

    /**
     * Total views.
     */
    @UiField
    SpanElement totalViews;

    /**
     * Total contributors.
     */
    @UiField
    SpanElement totalContributors;

    /**
     * Total messages.
     */
    @UiField
    SpanElement totalMessages;

    /**
     * Current status.
     */
    private Follower.FollowerStatus status;

    /**
     * Expand animation duration.
     */
    private static final int EXPAND_ANIMATION_DURATION = 500;

    /**
     * Content padding for details.
     */
    private static final int CONTENT_PADDING = 0;

    /**
     * Number of days to gather metrics for.
     */
    private static final Integer NUM_DAYS_FOR_METRICS = 30;

    /**
     * Avatar Renderer.
     */
    private final AvatarRenderer avatarRenderer = new AvatarRenderer();

    /**
     * The helper to build hyperlinks to stream search.
     */
    private StreamSearchLinkBuilder streamSearchLinkBuilder;

    /**
     * Expand/Collapse animation.
     */
    private ExpandCollapseAnimation detailsContainerAnimation;

    /**
     * Last following handler.
     */
    private HandlerRegistration lastHandler;

    /**
     * Last feature handler.
     */
    private HandlerRegistration lastFeatureHandler;

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
     * Stream is featured.
     */
    private boolean inFeatured;

    /**
     * Featured streams.
     */
    private PagedSet<FeaturedStreamDTO> featuredStreams;

    /**
     * Custom Avatars.
     */
    public enum CustomAvatar
    {
        /**
         * Everyone avatar.
         */
        EVERYONE,
        /**
         * Following avatar.
         */
        FOLLOWING,
        /**
         * Custom stream avatar.
         */
        CUSTOM
    };

    /**
     * Build page.
     */
    private void buildPage()
    {
        // Default style. Prevent flashing.
        streamName.setInnerText("Following");
        addStyleName(style.condensedStream());
        followLink.setVisible(false);
        featureLink.setVisible(Session.getInstance().getCurrentPersonRoles().contains(Role.SYSTEM_ADMIN));
        detailsContainerAnimation = new ExpandCollapseAnimation(streamDetailsContainer, EXPAND_ANIMATION_DURATION);

        streamAvatar.add(avatarRenderer.render(0L, null, EntityType.PERSON, Size.Normal));

        streamFollowers.init(new FollowerPagerUiStrategy());
        streamFollowing.init(new FollowingPagerUiStrategy());
        adminContent.init(new GroupMembershipRequestPagerUiStrategy());

        streamFollowers.setVisible(false);
        streamFollowing.setVisible(false);
        configureLink.setVisible(false);
        adminLink.setVisible(false);

        showFollowing.setVisible(false);
        followingCount.getStyle().setDisplay(Display.NONE);
        followingLink.setVisible(false);
        adminContent.setVisible(false);

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

        adminLink.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                openAdmin();
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

        toggleDetails.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                if (detailsContainerAnimation.isExpanded())
                {
                    toggleDetails.removeStyleName(style.hideDetails());
                    detailsContainerAnimation.collapse();
                }
                else
                {
                    openAbout();
                }
            }
        });

        addEvents();

        if (Session.getInstance().getCurrentPersonRoles().contains(Role.SYSTEM_ADMIN))
        {
            FeaturedStreamModel.getInstance().fetch(new GetFeaturedStreamsPageRequest(0, Integer.MAX_VALUE), true);
        }
    }

    /**
     * Add events.
     */
    private void addEvents()
    {
        EventBus.getInstance().addObserver(GotStreamPopularHashTagsEvent.class,
                new Observer<GotStreamPopularHashTagsEvent>()
                {
                    public void update(final GotStreamPopularHashTagsEvent event)
                    {
                        String tagString = "";
                        for (String tag : event.getPopularHashTags())
                        {
                            tagString += ("<a href=\"" + streamSearchLinkBuilder.buildHashtagSearchLink(tag, null)
                                    + "\">" + tag + "</a> ");
                        }
                        streamHashtags.setInnerHTML(tagString);
                    }
                });

        EventBus.getInstance().addObserver(PagerResponseEvent.class, new Observer<PagerResponseEvent>()
        {
            public void update(final PagerResponseEvent event)
            {
                detailsContainerAnimation.expandWithPadding(CONTENT_PADDING);
            }
        });

        EventBus.getInstance().addObserver(GotUsageMetricSummaryEvent.class,
                new Observer<GotUsageMetricSummaryEvent>()
                {
                    public void update(final GotUsageMetricSummaryEvent event)
                    {
                        UsageMetricSummaryDTO data = event.getResponse();

                        List<DailyUsageSummary> stats = data.getDailyStatistics();
                        if (stats != null)
                        {
                            for (int i = 0; i < stats.size(); i++)
                            {
                                if (null == stats.get(i))
                                {
                                    chart.addPoint(i, 0.0);
                                }
                                else
                                {
                                    chart.addPoint(i, stats.get(i).getStreamViewCount());
                                }
                            }
                        }

                        avgComments.setInnerText("" + data.getAverageDailyCommentCount());
                        avgContributors.setInnerText("" + data.getAverageDailyStreamContributorCount());
                        avgMessages.setInnerText("" + data.getAverageDailyMessageCount());
                        avgViewers.setInnerText("" + data.getAverageDailyStreamViewerCount());
                        avgViews.setInnerText("" + data.getAverageDailyStreamViewCount());

                        Long totalMessagesNumber = (data.getTotalActivityCount() + data.getTotalCommentCount());
                        totalContributors.setInnerText("" + data.getTotalContributorCount());
                        totalMessages.setInnerText(totalMessagesNumber.toString());
                        totalViews.setInnerText("" + data.getTotalStreamViewCount());
                        chart.update();
                    }
                });

        addModelViewEvents();

        EventBus.getInstance().addObserver(GotStreamResponseEvent.class, new Observer<GotStreamResponseEvent>()
        {
            public void update(final GotStreamResponseEvent event)
            {
                streamReq = event.getJsonRequest();
            }
        });

        EventBus.getInstance().addObserver(DeletedRequestForGroupMembershipResponseEvent.class,
                new Observer<DeletedRequestForGroupMembershipResponseEvent>()
                {
                    public void update(final DeletedRequestForGroupMembershipResponseEvent event)
                    {
                        openAdmin();
                    }
                }, true);

        EventBus.getInstance().addObserver(InsertedGroupMemberResponseEvent.class,
                new Observer<InsertedGroupMemberResponseEvent>()
                {
                    public void update(final InsertedGroupMemberResponseEvent event)
                    {
                        openAdmin();
                    }
                }, true);
    }

    /**
     * Set the stream title and avatar.
     * 
     * @param inStreamTitle
     *            the title.
     * @param avatar
     *            the avatar.
     */
    public void setStreamTitle(final String inStreamTitle, final CustomAvatar avatar)
    {
        streamName.setInnerText(inStreamTitle);

        switch (avatar)
        {
        case EVERYONE:
            condensedAvatar.addStyleName(style.followingAvatar());
            break;
        case FOLLOWING:
            condensedAvatar.addStyleName(style.everyoneAvatar());
            break;
        case CUSTOM:
            break;
        default:
            break;
        }

    }

    /**
     * Initialize the view.
     */
    public void init()
    {
        chart.clearPoints();
        chart.update();

        // Collapse right away if open.
        streamDetailsContainer.getStyle().setHeight(0.0, Unit.PX);
        toggleDetails.removeStyleName(style.hideDetails());

        condensedAvatar.removeStyleName(style.everyoneAvatar());
        condensedAvatar.removeStyleName(style.followingAvatar());
        condensedAvatar.removeStyleName(style.privateAvatar());
    }

    /**
     * Set Condensed mode.
     * 
     * @param isCondensed
     *            condensed mode.
     */
    public void setCondensedMode(final boolean isCondensed)
    {
        if (isCondensed)
        {
            this.addStyleName(style.condensedStream());

        }
        else
        {
            this.removeStyleName(style.condensedStream());
        }
    }

    /**
     * Add the model view events.
     */
    private void addModelViewEvents()
    {
        final StreamDetailsComposite thisClass = this;

        EventBus.getInstance().addObserver(GotFeaturedStreamsPageResponseEvent.class,
                new Observer<GotFeaturedStreamsPageResponseEvent>()
                {
                    public void update(final GotFeaturedStreamsPageResponseEvent response)
                    {
                        featuredStreams = response.getResponse();
                    }
                });

        EventBus.getInstance().addObserver(GotPersonalInformationResponseEvent.class,
                new Observer<GotPersonalInformationResponseEvent>()
                {
                    public void update(final GotPersonalInformationResponseEvent event)
                    {
                        showFollowing.setVisible(true);
                        followingCount.getStyle().setDisplay(Display.INLINE);
                        followingLink.setVisible(true);
                        adminLink.setVisible(false);

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
                        FeaturedStreamDTO featuredStreamDTO = new FeaturedStreamDTO();
                        featuredStreamDTO.setDescription(person.getDescription());
                        featuredStreamDTO.setStreamId(person.getStreamId());
                        featuredStreamDTO.setStreamType(ScopeType.PERSON);
                        featuredStreamDTO.setDisplayName(person.getDisplayName());

                        updateFeatureLink(featuredStreamDTO);

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
                            interestString += "<a href='#search?query=" + interest + "'>" + interest + "</a> ";
                        }
                        streamInterests.setInnerHTML(interestString);

                        String contact = "";
                        if (person.getEmail() != null)
                        {
                        	contact = person.getEmail();
                        }
                        if (person.getWorkPhone() != null)
                        {
                        	 if (person.getEmail() != null)
                             {
                             	contact += "</br>";
                             }
                        	 contact += person.getWorkPhone();
                        }
                        contactInfo.setInnerHTML(contact);
                        
                        PopularHashTagsModel.getInstance().fetch(
                                new StreamPopularHashTagsRequest(ScopeType.PERSON, person.getAccountId()), true);

                        UsageMetricModel.getInstance().fetch(
                                new UsageMetricStreamSummaryRequest(NUM_DAYS_FOR_METRICS, person.getStreamId()), true);
                    }
                });

        EventBus.getInstance().addObserver(GotGroupModelViewInformationResponseEvent.class,
                new Observer<GotGroupModelViewInformationResponseEvent>()
                {
                    public void update(final GotGroupModelViewInformationResponseEvent event)
                    {
                        showFollowing.setVisible(false);
                        followingCount.getStyle().setDisplay(Display.NONE);
                        followingLink.setVisible(false);

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
                                if (!group.isPublic())
                                {
                                    adminLink.setVisible(true);
                                }
                                configureLink.setVisible(true);
                                configureLink.setHref("#groupsettings/" + group.getShortName());
                            }
                            else
                            {
                                configureLink.setVisible(false);
                            }

                            updateFollowLink(group.getShortName(), EntityType.GROUP);
                            FeaturedStreamDTO featuredStreamDTO = new FeaturedStreamDTO();
                            featuredStreamDTO.setDescription(group.getDescription());
                            featuredStreamDTO.setStreamId(group.getStreamId());
                            featuredStreamDTO.setStreamType(ScopeType.GROUP);
                            featuredStreamDTO.setDisplayName(group.getDisplayName());

                            updateFeatureLink(featuredStreamDTO);

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
                                interestString += "<a href='#search?query=" + interest + "'>" + interest + "</a> ";
                            }
                            streamInterests.setInnerHTML(interestString);

                            PopularHashTagsModel.getInstance().fetch(
                                    new StreamPopularHashTagsRequest(ScopeType.GROUP, group.getShortName()), true);
                            UsageMetricModel.getInstance().fetch(
                                    new UsageMetricStreamSummaryRequest(NUM_DAYS_FOR_METRICS, group.getStreamId()),
                                    true);
                        }
                    }
                });
    }

    /**
     * Update the feature link.
     * 
     * @param featuredStreamDTO
     *            the stream.
     */
    public void updateFeatureLink(final FeaturedStreamDTO featuredStreamDTO)
    {

        if (Session.getInstance().getCurrentPersonRoles().contains(Role.SYSTEM_ADMIN))
        {
            inFeatured = false;
            featureLink.removeStyleName(style.headerFeatured());

            for (FeaturedStreamDTO featured : featuredStreams.getPagedSet())
            {
                if (featured.getStreamId() == streamId
                        && featured.getEntityType().equals(featuredStreamDTO.getEntityType()))
                {
                    inFeatured = true;
                    featuredStreamDTO.setId(featured.getId());
                    featureLink.addStyleName(style.headerFeatured());
                    break;
                }
            }

            if (lastFeatureHandler != null)
            {
                lastFeatureHandler.removeHandler();
            }

            lastFeatureHandler = featureLink.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    if (inFeatured)
                    {
                        FeaturedStreamModel.getInstance().delete(featuredStreamDTO.getId());
                        EventBus.getInstance().notifyObservers(
                                new ShowNotificationEvent(new Notification(
                                        "Stream has been removed from the featured streams list.")));
                    }
                    else
                    {
                        Dialog.showCentered(new FeatureDialogContent(featuredStreamDTO));
                    }
                }
            });

        }
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
                        request = new SetFollowingStatusRequest(Session.getInstance().getCurrentPerson()
                                .getAccountId(), entityId, type, false, Follower.FollowerStatus.NOTFOLLOWING);
                        ((Deletable<SetFollowingStatusRequest>) followModel).delete(request);
                        onFollowerStatusChanged(Follower.FollowerStatus.NOTFOLLOWING);
                        break;
                    case NOTFOLLOWING:
                        request = new SetFollowingStatusRequest(Session.getInstance().getCurrentPerson()
                                .getAccountId(), entityId, type, false, Follower.FollowerStatus.FOLLOWING);
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

            Session.getInstance()
                    .getEventBus()
                    .addObserver(GotPersonFollowerStatusResponseEvent.class,
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
        toggleDetails.addStyleName(style.hideDetails());
        aboutLink.addStyleName(style.activeOption());
        followingLink.removeStyleName(style.activeOption());
        followersLink.removeStyleName(style.activeOption());
        adminLink.removeStyleName(style.activeOption());

        streamFollowing.setVisible(false);
        streamAbout.setVisible(true);
        streamFollowers.setVisible(false);
        adminContent.setVisible(false);
        detailsContainerAnimation.expandWithPadding(CONTENT_PADDING);
    }

    /**
     * Open the following panel.
     */
    private void openFollowing()
    {
        toggleDetails.addStyleName(style.hideDetails());
        aboutLink.removeStyleName(style.activeOption());
        followingLink.addStyleName(style.activeOption());
        followersLink.removeStyleName(style.activeOption());
        adminLink.removeStyleName(style.activeOption());

        streamFollowers.setVisible(false);
        streamAbout.setVisible(false);
        streamFollowing.setVisible(true);
        adminContent.setVisible(false);
        streamFollowing.load();
    }

    /**
     * Open the followers panel.
     */
    private void openFollower()
    {
        toggleDetails.addStyleName(style.hideDetails());
        aboutLink.removeStyleName(style.activeOption());
        followingLink.removeStyleName(style.activeOption());
        followersLink.addStyleName(style.activeOption());
        adminLink.removeStyleName(style.activeOption());

        streamFollowing.setVisible(false);
        streamAbout.setVisible(false);
        streamFollowers.setVisible(true);
        adminContent.setVisible(false);
        streamFollowers.load();
    }

    /**
     * Open the Admin panel.
     */
    private void openAdmin()
    {
        toggleDetails.addStyleName(style.hideDetails());
        aboutLink.removeStyleName(style.activeOption());
        followingLink.removeStyleName(style.activeOption());
        followersLink.removeStyleName(style.activeOption());
        adminLink.addStyleName(style.activeOption());

        streamFollowing.setVisible(false);
        streamAbout.setVisible(false);
        streamFollowers.setVisible(false);
        adminContent.setVisible(true);
        adminContent.load();
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
            followLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().unFollowLink());
            break;
        case NOTFOLLOWING:
            followLink.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().unFollowLink());
            break;
        default:
            break;
        }
    }
}
