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
package org.eurekastreams.web.client.ui.pages.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.HistoryViewsChangedEvent;
import org.eurekastreams.web.client.events.MessageStreamAppendEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.events.data.GotCurrentUserCustomStreamsResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonalInformationResponseEvent;
import org.eurekastreams.web.client.events.data.GotStreamResponseEvent;
import org.eurekastreams.web.client.events.data.PostableStreamScopeChangeEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.jsni.EffectsFacade;
import org.eurekastreams.web.client.model.CustomStreamModel;
import org.eurekastreams.web.client.model.PersonalInformationModel;
import org.eurekastreams.web.client.model.StreamModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.animation.ExpandCollapseAnimation;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.common.charts.StreamAnalyticsChart;
import org.eurekastreams.web.client.ui.common.stream.StreamJsonRequestFactory;
import org.eurekastreams.web.client.ui.common.stream.renderers.AvatarRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.ShowRecipient;
import org.eurekastreams.web.client.ui.common.stream.renderers.StreamMessageItemRenderer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Activity Page.
 */
public class ActivityContent extends Composite
{
    /** Binder for building UI. */
    private static LocalUiBinder binder = GWT.create(LocalUiBinder.class);

    interface ActivityStyle extends CssResource
    {
        String activeSort();
    }

    @UiField
    ActivityStyle style;

    /** UI element for streams. */
    @UiField
    FlowPanel streamPanel;

    /** UI element for bookmarks. */
    @UiField
    UListElement bookmarkList;

    /** UI element for filters. */
    @UiField
    UListElement filterList;

    /** UI element for filters. */
    @UiField
    UListElement defaultList;

    @UiField
    SpanElement streamName;

    @UiField
    SpanElement streamMeta;

    @UiField
    HTMLPanel streamAvatar;

    @UiField
    DivElement streamDescription;

    @UiField
    DivElement streamInterests;

    @UiField
    DivElement streamHashtags;

    @UiField
    Anchor recentSort;

    @UiField
    HTMLPanel streamConnections;

    @UiField
    Anchor popularSort;

    @UiField
    Anchor activeSort;

    @UiField
    Anchor toggleDetails;

    @UiField
    HTMLPanel streamAbout;

    @UiField
    SpanElement followerCount;

    @UiField
    SpanElement followingCount;

    @UiField
    DivElement streamDetailsContainer;

    @UiField
    HTMLPanel analyticsChartContainer;

    @UiField
    Anchor aboutLink;

    @UiField
    Anchor followersLink;

    private ExpandCollapseAnimation detailsContainerAnimation;

    /**
     * Message Renderer.
     */
    StreamMessageItemRenderer renderer = new StreamMessageItemRenderer(ShowRecipient.ALL);

    private long longNewestActivityId = 0L;

    /**
     * Avatar Renderer.
     */
    private AvatarRenderer avatarRenderer = new AvatarRenderer();

    private JSONObject currentRequestObj = null;

    /**
     * Default constructor.
     */
    public ActivityContent()
    {
        initWidget(binder.createAndBindUi(this));
        buildPage();
    }

    /**
     * Build the page.
     */
    private void buildPage()
    {
        detailsContainerAnimation = new ExpandCollapseAnimation(streamDetailsContainer, 330, 500);
        final StreamAnalyticsChart chart = new StreamAnalyticsChart();

        addEventHandlers();

        defaultList.appendChild(createLI("Following", "/following"));
        defaultList.appendChild(createLI("Everyone", "/everyone"));

        bookmarkList.appendChild(createLI(Session.getInstance().getCurrentPerson().getPreferredName(), "/person/"
                + Session.getInstance().getCurrentPerson().getAccountId()));

        streamAvatar.add(avatarRenderer.render(0L, null, EntityType.PERSON, Size.Normal));

        CustomStreamModel.getInstance().fetch(null, true);

        analyticsChartContainer.add(chart);
        chart.update();
        streamConnections.setVisible(false);
    }

    private void addEventHandlers()
    {
        toggleDetails.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                detailsContainerAnimation.toggle();
            }
        });

        EventBus.getInstance().addObserver(GotStreamResponseEvent.class, new Observer<GotStreamResponseEvent>()
        {
            public void update(final GotStreamResponseEvent event)
            {
                final PagedSet<ActivityDTO> activitySet = event.getStream();

                if (activitySet.getPagedSet().size() > 0)
                {
                    longNewestActivityId = activitySet.getPagedSet().get(0).getEntityId();
                }

                if (StreamJsonRequestFactory.getJSONRequest(event.getJsonRequest()).containsKey("minId"))
                {
                    for (int i = activitySet.getPagedSet().size(); i > 0; i--)
                    {
                        appendActivity(activitySet.getPagedSet().get(i - 1));
                    }
                }
                else
                {
                    streamPanel.clear();

                    for (ActivityDTO activity : activitySet.getPagedSet())
                    {
                        streamPanel.add(renderer.render(activity));
                    }
                }
            }
        });

        EventBus.getInstance().addObserver(GotCurrentUserCustomStreamsResponseEvent.class,
                new Observer<GotCurrentUserCustomStreamsResponseEvent>()
                {
                    public void update(final GotCurrentUserCustomStreamsResponseEvent event)
                    {
                        for (StreamFilter filter : event.getResponse().getStreamFilters())
                        {

                            filterList.appendChild(createLI(filter.getName(), "/custom/"
                                    + filter.getId()
                                    + "/"
                                    + URL.encodeQueryString(filter.getRequest().replace("%%CURRENT_USER_ACCOUNT_ID%%",
                                            Session.getInstance().getCurrentPerson().getAccountId()))));
                        }
                    }
                });

        EventBus.getInstance().addObserver(UpdatedHistoryParametersEvent.class,
                new Observer<UpdatedHistoryParametersEvent>()
                {
                    public void update(UpdatedHistoryParametersEvent event)
                    {
                        if (event.getParameters().containsKey("details"))
                        {
                            streamConnections.setVisible("connections".equals(event.getParameters().get("details")));
                            streamAbout.setVisible("about".equals(event.getParameters().get("details")));
                        }
                    }
                });

        EventBus.getInstance().addObserver(HistoryViewsChangedEvent.class, new Observer<HistoryViewsChangedEvent>()
        {
            public void update(HistoryViewsChangedEvent event)
            {
                loadStream(event.getViews());
                List<String> views = new ArrayList<String>(event.getViews());

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("details", "about");
                aboutLink.setHref("#"
                        + Session.getInstance().generateUrl(new CreateUrlRequest(Page.ACTIVITY_NEW, views, params)));

                params.put("details", "connections");
                followersLink.setHref("#"
                        + Session.getInstance().generateUrl(new CreateUrlRequest(Page.ACTIVITY_NEW, views, params)));

                
                if (views.size() < 2 || !"sort".equals(views.get(views.size() - 2)))
                {
                    views.add("sort");
                    views.add("recent");
                }
              
                views.set(views.size() - 1, "recent");
                recentSort.setHref("#"
                        + Session.getInstance().generateUrl(new CreateUrlRequest(Page.ACTIVITY_NEW, views)));

                views.set(views.size() - 1, "popular");
                popularSort.setHref("#"
                        + Session.getInstance().generateUrl(new CreateUrlRequest(Page.ACTIVITY_NEW, views)));

                views.set(views.size() - 1, "active");
                activeSort.setHref("#"
                        + Session.getInstance().generateUrl(new CreateUrlRequest(Page.ACTIVITY_NEW, views)));
                
            }
        }, true);

        EventBus.getInstance().addObserver(GotPersonalInformationResponseEvent.class,
                new Observer<GotPersonalInformationResponseEvent>()
                {
                    public void update(final GotPersonalInformationResponseEvent event)
                    {
                        PersonModelView person = event.getResponse();
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

                        streamConnections.clear();
                        streamConnections.add(GetPersonFollowingTab.getFollowingTab(person));
                    }
                });

        EventBus.getInstance().addObserver(MessageStreamAppendEvent.class, new Observer<MessageStreamAppendEvent>()
        {
            public void update(MessageStreamAppendEvent event)
            {
                longNewestActivityId = event.getMessage().getId();
                appendActivity(event.getMessage());

            }
        });

        Scheduler.get().scheduleFixedDelay(new RepeatingCommand()
        {
            public boolean execute()
            {
                if (null != currentRequestObj
                        && "date".equals(currentRequestObj.get("query").isObject().get("sortBy").isString()
                                .stringValue()))
                {
                    if (Document.get().getScrollTop() < streamDetailsContainer.getAbsoluteTop())
                    {
                        JSONObject newItemsRequest = StreamJsonRequestFactory.setMinId(longNewestActivityId,
                                StreamJsonRequestFactory.getJSONRequest(currentRequestObj.toString()));

                        StreamModel.getInstance().fetch(newItemsRequest.toString(), false);
                    }
                }

                return Session.getInstance().getUrlPage().equals(Page.ACTIVITY_NEW);
            }
        }, 30000);

    }

    private void appendActivity(ActivityDTO message)
    {
        Panel newActivity = renderer.render(message);
        newActivity.setVisible(false);
        streamPanel.insert(newActivity, 0);
        EffectsFacade.nativeFadeIn(newActivity.getElement(), true);
    }

    private void loadStream(final List<String> views)
    {
        Session.getInstance().getActionProcessor().setQueueRequests(true);
        currentRequestObj = StreamJsonRequestFactory.getEmptyRequest();
        StreamScope currentStream = new StreamScope(ScopeType.PERSON, Session.getInstance().getCurrentPerson()
                .getAccountId());

        if (views == null || views.size() == 0 || views.get(0).equals("following"))
        {
            currentRequestObj = StreamJsonRequestFactory.setSourceAsFollowing(currentRequestObj);
            streamName.setInnerHTML("Following");
        }
        else if (views.get(0).equals("person") && views.size() >= 2)
        {
            String accountId = views.get(1);
            currentRequestObj = StreamJsonRequestFactory.addRecipient(EntityType.PERSON, accountId, currentRequestObj);
            PersonalInformationModel.getInstance().fetch(accountId, false);
            currentStream.setScopeType(ScopeType.PERSON);
            currentStream.setUniqueKey(accountId);
        }
        else if (views.get(0).equals("custom") && views.size() >= 3)
        {
            currentRequestObj = StreamJsonRequestFactory.getJSONRequest(views.get(2));
            streamName.setInnerHTML("Custom Stream");
            currentStream.setScopeType(null);
        }
        else if (views.get(0).equals("everyone"))
        {
            streamName.setInnerHTML("Everyone");
        }

        String sortBy = "recent";

        if (views != null && views.size() >= 2 && "sort".equals(views.get(views.size() - 2)))
        {
            sortBy = views.get(views.size() - 1);
        }

        recentSort.removeStyleName(style.activeSort());
        popularSort.removeStyleName(style.activeSort());
        activeSort.removeStyleName(style.activeSort());

        String sortKeyword = "date";

        if ("recent".equals(sortBy))
        {
            recentSort.addStyleName(style.activeSort());
            sortKeyword = "date";
        }
        else if ("popular".equals(sortBy))
        {
            popularSort.addStyleName(style.activeSort());
            sortKeyword = "interesting";
        }
        else if ("active".equals(sortBy))
        {
            activeSort.addStyleName(style.activeSort());
            sortKeyword = "commentdate";
        }

        currentRequestObj = StreamJsonRequestFactory.setSort(sortKeyword, currentRequestObj);

        StreamModel.getInstance().fetch(currentRequestObj.toString(), false);

        Session.getInstance().getActionProcessor().fireQueuedRequests();
        Session.getInstance().getActionProcessor().setQueueRequests(false);
        EventBus.getInstance().notifyObservers(new PostableStreamScopeChangeEvent(currentStream));
    }

    private LIElement createLI(final String name, final String view)
    {
        AnchorElement aElem = Document.get().createAnchorElement();
        aElem.setInnerHTML(name);
        aElem.setHref("#activity-new" + view);

        LIElement filterElem = Document.get().createLIElement();
        filterElem.appendChild(aElem);

        return filterElem;
    }

    /**
     * Binder for building UI.
     */
    interface LocalUiBinder extends UiBinder<Widget, ActivityContent>
    {
    }
}
