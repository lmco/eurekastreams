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

import java.util.HashMap;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.events.data.GotCurrentUserCustomStreamsResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonalInformationResponseEvent;
import org.eurekastreams.web.client.events.data.GotStreamResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.CustomStreamModel;
import org.eurekastreams.web.client.model.PersonalInformationModel;
import org.eurekastreams.web.client.model.StreamModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.animation.ExpandCollapseAnimation;
import org.eurekastreams.web.client.ui.common.autocomplete.ExtendedTextArea;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.common.charts.StreamAnalyticsChart;
import org.eurekastreams.web.client.ui.common.stream.StreamJsonRequestFactory;
import org.eurekastreams.web.client.ui.common.stream.renderers.AvatarRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.ShowRecipient;
import org.eurekastreams.web.client.ui.common.stream.renderers.StreamMessageItemRenderer;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
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
    HTMLPanel streamPanel;

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
    HTMLPanel posterAvatar;

    @UiField
    DivElement postPanel;

    @UiField
    ExtendedTextArea postBox;

    @UiField
    Anchor recentSort;

    @UiField
    Anchor popularSort;

    @UiField
    Anchor activeSort;

    @UiField
    Anchor toggleDetails;

    @UiField
    SpanElement followerCount;

    @UiField
    SpanElement followingCount;

    @UiField
    DivElement streamDetailsContainer;
    
    @UiField
    HTMLPanel analyticsChartContainer;

    private ExpandCollapseAnimation detailsContainerAnimation;

    private ExpandCollapseAnimation postBoxAnimation;

    /**
     * Message Renderer.
     */
    StreamMessageItemRenderer renderer = new StreamMessageItemRenderer(ShowRecipient.FOREIGN_ONLY);

    /**
     * Avatar Widget.
     */
    private AvatarRenderer avatarRenderer = new AvatarRenderer();

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
        detailsContainerAnimation = new ExpandCollapseAnimation(streamDetailsContainer, 250, 500);
        postBoxAnimation = new ExpandCollapseAnimation(postBox.getElement(), 250, 100);
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
                PagedSet<ActivityDTO> activitySet = event.getStream();
                streamPanel.clear();

                for (ActivityDTO activity : activitySet.getPagedSet())
                {
                    streamPanel.add(renderer.render(activity));
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

                            filterList.appendChild(createLI(filter.getName(), "?stream=custom&id="
                                    + filter.getId()
                                    + "&request="
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
                        loadStream(event.getParameters());
                        HashMap<String, String> params = new HashMap<String, String>(event.getParameters());

                        params.put("sort", "date");
                        recentSort.setHref("#" + Session.getInstance().generateUrl(new CreateUrlRequest(params)));

                        params.put("sort", "interesting");
                        popularSort.setHref("#" + Session.getInstance().generateUrl(new CreateUrlRequest(params)));

                        params.put("sort", "commentdate");
                        activeSort.setHref("#" + Session.getInstance().generateUrl(new CreateUrlRequest(params)));
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
                    }
                });

        postBox.addKeyUpHandler(new KeyUpHandler()
        {
            public void onKeyUp(KeyUpEvent event)
            {
                checkPostBox();
            }
        });

        postBox.addChangeHandler(new ChangeHandler()
        {

            public void onChange(ChangeEvent event)
            {
                checkPostBox();
            }
        });

        defaultList.appendChild(createLI("Following", ""));
        defaultList.appendChild(createLI("Everyone", "?stream=everyone"));

        bookmarkList.appendChild(createLI(Session.getInstance().getCurrentPerson().getPreferredName(),
                "?stream=person&uniqueid=" + Session.getInstance().getCurrentPerson().getAccountId()));

        posterAvatar.add(avatarRenderer.render(Session.getInstance().getCurrentPerson().getEntityId(), Session
                .getInstance().getCurrentPerson().getAvatarId(), EntityType.PERSON, Size.Small));

        streamAvatar.add(avatarRenderer.render(0L, null, EntityType.PERSON, Size.Normal));

        CustomStreamModel.getInstance().fetch(null, true);
        
        StreamAnalyticsChart chart = new StreamAnalyticsChart();
        analyticsChartContainer.add(chart);
        chart.update();
    }

    protected void checkPostBox()
    {
        if (postBox.getElement().getClientHeight() != postBox.getElement().getScrollHeight())
        {
            postBoxAnimation.expand(postBox.getElement().getScrollHeight());
        }
    }

    private void loadStream(final HashMap<String, String> params)
    {
        JSONObject jsonObj = StreamJsonRequestFactory.getEmptyRequest();

        if (params == null || params.size() == 0)
        {
            jsonObj = StreamJsonRequestFactory.setSourceAsFollowing(jsonObj);
            streamName.setInnerHTML("Following");
            postPanel.removeClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());
        }
        else if (params.get("stream").equals("person"))
        {
            String accountId = params.get("uniqueid");
            jsonObj = StreamJsonRequestFactory.addRecipient(EntityType.PERSON, accountId, jsonObj);
            PersonalInformationModel.getInstance().fetch(accountId, false);
            postPanel.removeClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());
        }
        else if (params.get("stream").equals("custom"))
        {
            jsonObj = StreamJsonRequestFactory.getJSONRequest(params.get("request"));
            streamName.setInnerHTML("Custom Stream");
            postPanel.addClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());
        }
        else if (params.get("stream").equals("everyone"))
        {
            streamName.setInnerHTML("Everyone");
            postPanel.removeClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());
        }

        String sortBy = "date";

        if (params != null && params.containsKey("sort"))
        {
            sortBy = params.get("sort");
        }

        jsonObj = StreamJsonRequestFactory.setSort(sortBy, jsonObj);

        recentSort.removeStyleName(style.activeSort());
        popularSort.removeStyleName(style.activeSort());
        activeSort.removeStyleName(style.activeSort());

        if ("date".equals(sortBy))
        {
            recentSort.addStyleName(style.activeSort());
        }
        else if ("interesting".equals(sortBy))
        {
            popularSort.addStyleName(style.activeSort());
        }
        else if ("commentdate".equals(sortBy))
        {
            activeSort.addStyleName(style.activeSort());
        }

        StreamModel.getInstance().fetch(jsonObj.toString(), false);
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
