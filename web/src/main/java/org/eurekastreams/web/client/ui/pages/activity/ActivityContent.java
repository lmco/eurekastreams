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

import org.eurekastreams.server.domain.AvatarUrlGenerator;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.CustomStreamCreatedEvent;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.HistoryViewsChangedEvent;
import org.eurekastreams.web.client.events.MessageStreamAppendEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.events.data.GotActivityResponseEvent;
import org.eurekastreams.web.client.events.data.GotCurrentUserCustomStreamsResponseEvent;
import org.eurekastreams.web.client.events.data.GotCurrentUserStreamBookmarks;
import org.eurekastreams.web.client.events.data.GotGroupModelViewInformationResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonalInformationResponseEvent;
import org.eurekastreams.web.client.events.data.GotStreamResponseEvent;
import org.eurekastreams.web.client.events.data.PostableStreamScopeChangeEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.jsni.EffectsFacade;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.ActivityModel;
import org.eurekastreams.web.client.model.CustomStreamModel;
import org.eurekastreams.web.client.model.GadgetModel;
import org.eurekastreams.web.client.model.GroupModel;
import org.eurekastreams.web.client.model.PersonalInformationModel;
import org.eurekastreams.web.client.model.StreamBookmarksModel;
import org.eurekastreams.web.client.model.StreamModel;
import org.eurekastreams.web.client.model.requests.AddGadgetToStartPageRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.stream.ActivityDetailPanel;
import org.eurekastreams.web.client.ui.common.stream.StreamJsonRequestFactory;
import org.eurekastreams.web.client.ui.common.stream.StreamToUrlTransformer;
import org.eurekastreams.web.client.ui.common.stream.filters.list.CustomStreamDialogContent;
import org.eurekastreams.web.client.ui.common.stream.renderers.ShowRecipient;
import org.eurekastreams.web.client.ui.common.stream.renderers.StreamMessageItemRenderer;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Activity Page.
 */
public class ActivityContent extends Composite
{
    /** Binder for building UI. */
    private static LocalUiBinder binder = GWT.create(LocalUiBinder.class);

    /**
     * CSS resource.
     */
    interface ActivityStyle extends CssResource
    {
        /**
         * Active sort style.
         * 
         * @return Active sort style
         */
        String activeSort();

        /**
         * Active stream style.
         * 
         * @return Active stream style.
         */
        String activeStream();

        /**
         * Stream options child.
         * 
         * @return Stream options child.
         */
        String streamOptionChild();

        /**
         * Delete bookmark.
         * 
         * @return delete bookmark.
         */
        String deleteBookmark();

        /**
         * Edit custom stream.
         * 
         * @return edit custom stream.
         */
        String editCustomStream();

        /**
         * The stream name style.
         * 
         * @return the stream name style.
         */
        String streamName();
    }

    /**
     * CSS style.
     */
    @UiField
    ActivityStyle style;

    /**
     * UI element for streams.
     */
    @UiField
    FlowPanel streamPanel;

    /**
     * UI element for bookmarks.
     */
    @UiField
    FlowPanel bookmarkList;

    /**
     * UI element for stream container.
     */
    @UiField
    HTMLPanel streamContainerPanel;

    /**
     * UI element for filters.
     */
    @UiField
    FlowPanel filterList;

    /**
     * UI element for default streams.
     */
    @UiField
    FlowPanel defaultList;

    /**
     * UI element for recent sort.
     */
    @UiField
    Hyperlink recentSort;

    /**
     * UI element for popular sort.
     */
    @UiField
    Hyperlink popularSort;

    /**
     * UI element for active sort.
     */
    @UiField
    Hyperlink activeSort;

    /**
     * UI element for activity loading spinner.
     */
    @UiField
    DivElement activitySpinner;

    /**
     * Feed link.
     */
    @UiField
    Label feedLink;

    /**
     * UI element for more spinner.
     */
    @UiField
    DivElement moreSpinner;

    /**
     * UI element for more link.
     */
    @UiField
    Label moreLink;

    /**
     * UI element for adding a bookmark.
     */
    @UiField
    Label addBookmark;

    /**
     * UI element for adding a a stream to the start page.
     */
    @UiField
    Label addToStartPage;

    /**
     * Create Filter.
     */
    @UiField
    Label createFilter;

    /**
     * Message Renderer.
     */
    StreamMessageItemRenderer renderer = new StreamMessageItemRenderer(ShowRecipient.ALL);

    /**
     * Newest activity ID.
     */
    private long longNewestActivityId = 0L;

    /**
     * Oldest Activity ID.
     */
    private long longOldestActivityId = 0;

    /**
     * Current Request.
     */
    private JSONObject currentRequestObj = null;

    /**
     * Search Box.
     */
    @UiField
    TextBox searchBox;

    /**
     * Current stream scope.
     */
    private StreamScope currentStream;

    /**
     * Current scope id.
     */
    private long currentScopeId;

    /**
     * New activity polling.
     */
    private static final int NEW_ACTIVITY_POLLING_DELAY = 1200000;

    /**
     * Custom streams map.
     */
    private HashMap<Long, Panel> customStreamWidgetMap = new HashMap<Long, Panel>();

    /**
     * Stream bookmarks map.
     */
    private HashMap<String, Panel> bookmarksWidgetMap = new HashMap<String, Panel>();

    /**
     * Currently active stream.
     */
    private Panel currentlyActiveStream = null;

    /**
     * Following filter panel.
     */
    private Panel followingFilterPanel = null;

    /**
     * Everyone filter panel.
     */
    private Panel everyoneFilterPanel = null;

    /**
     * Stream to URL transformer.
     * */
    private static final StreamToUrlTransformer STREAM_URL_TRANSFORMER = new StreamToUrlTransformer();

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
        addEventHandlers();
        addObservers();
        setupStreamsAndBookmarks();

        followingFilterPanel = createPanel("Following", "following", "style/images/customStream.png", null, "", "");
        everyoneFilterPanel = createPanel("Everyone", "everyone", "style/images/customStream.png", null, "", "");

        defaultList.add(followingFilterPanel);
        defaultList.add(everyoneFilterPanel);

        CustomStreamModel.getInstance().fetch(null, true);
        StreamBookmarksModel.getInstance().fetch(null, true);

        moreSpinner.addClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());

        loadStream(Session.getInstance().getUrlViews());
    }

    /**
     * Add events.
     */
    private void addObservers()
    {
        EventBus.getInstance().addObserver(GotActivityResponseEvent.class, new Observer<GotActivityResponseEvent>()
        {

            public void update(final GotActivityResponseEvent event)
            {
                streamPanel.clear();
                activitySpinner.addClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());
                streamPanel.add(new ActivityDetailPanel(event.getResponse(), ShowRecipient.ALL));
                streamPanel.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().hidden());
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
                    longOldestActivityId = activitySet.getPagedSet().get(activitySet.getPagedSet().size() - 1)
                            .getEntityId();
                }

                if (StreamJsonRequestFactory.getJSONRequest(event.getJsonRequest()).containsKey("minId"))
                {
                    for (int i = activitySet.getPagedSet().size(); i > 0; i--)
                    {
                        appendActivity(activitySet.getPagedSet().get(i - 1));
                    }
                }
                else if (StreamJsonRequestFactory.getJSONRequest(event.getJsonRequest()).containsKey("maxId"))
                {
                    moreSpinner.addClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());

                    for (ActivityDTO activity : activitySet.getPagedSet())
                    {
                        streamPanel.add(renderer.render(activity));
                    }

                    moreLink.setVisible(activitySet.getTotal() > activitySet.getPagedSet().size());
                }
                else
                {
                    streamPanel.clear();
                    activitySpinner.addClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());
                    streamPanel.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().hidden());

                    List<ActivityDTO> activities = activitySet.getPagedSet();

                    for (ActivityDTO activity : activities)
                    {
                        streamPanel.add(renderer.render(activity));
                    }

                    moreLink.setVisible(activitySet.getTotal() > activities.size());
                }

            }
        });

        EventBus.getInstance().addObserver(GotPersonalInformationResponseEvent.class,
                new Observer<GotPersonalInformationResponseEvent>()
                {
                    public void update(final GotPersonalInformationResponseEvent event)
                    {
                        PersonModelView person = event.getResponse();
                        currentScopeId = person.getStreamId();
                    }
                });

        EventBus.getInstance().addObserver(GotGroupModelViewInformationResponseEvent.class,
                new Observer<GotGroupModelViewInformationResponseEvent>()
                {
                    public void update(final GotGroupModelViewInformationResponseEvent event)
                    {
                        DomainGroupModelView group = event.getResponse();
                        currentScopeId = group.getStreamId();
                    }
                });

        EventBus.getInstance().addObserver(HistoryViewsChangedEvent.class, new Observer<HistoryViewsChangedEvent>()
        {
            public void update(final HistoryViewsChangedEvent event)
            {
                handleViewsChanged(event.getViews());
            }
        });

        EventBus.getInstance().addObserver(MessageStreamAppendEvent.class, new Observer<MessageStreamAppendEvent>()
        {
            public void update(final MessageStreamAppendEvent event)
            {
                longNewestActivityId = event.getMessage().getId();
                appendActivity(event.getMessage());

            }
        });

        EventBus.getInstance().addObserver(CustomStreamCreatedEvent.class, new Observer<CustomStreamCreatedEvent>()
        {
            public void update(final CustomStreamCreatedEvent event)
            {
                CustomStreamModel.getInstance().fetch(null, true);
            }
        });

        EventBus.getInstance().addObserver(UpdatedHistoryParametersEvent.class,
                new Observer<UpdatedHistoryParametersEvent>()
                {
                    public void update(final UpdatedHistoryParametersEvent event)
                    {
                        // loadStream(Session.getInstance().getUrlViews(), searchBox.getText());
                    }
                });
    }

    /**
     * Handle views changed.
     * 
     * @param inViews
     *            the views.
     */
    protected void handleViewsChanged(final List<String> inViews)
    {
        loadStream(inViews);
        List<String> views = new ArrayList<String>(inViews);

        if (views.size() < 2 || !"sort".equals(views.get(views.size() - 2)))
        {
            views.add("sort");
            views.add("recent");
        }

        views.set(views.size() - 1, "recent");
        recentSort.setTargetHistoryToken(Session.getInstance().generateUrl(new CreateUrlRequest(Page.ACTIVITY, views)));

        views.set(views.size() - 1, "popular");
        popularSort
                .setTargetHistoryToken(Session.getInstance().generateUrl(new CreateUrlRequest(Page.ACTIVITY, views)));

        views.set(views.size() - 1, "active");
        activeSort.setTargetHistoryToken(Session.getInstance().generateUrl(new CreateUrlRequest(Page.ACTIVITY, views)));

    }

    /**
     * Setup bookmarks and custom streams.
     */
    private void setupStreamsAndBookmarks()
    {
        EventBus.getInstance().addObserver(GotCurrentUserStreamBookmarks.class,

        new Observer<GotCurrentUserStreamBookmarks>()
        {
            private AvatarUrlGenerator groupUrlGen = new AvatarUrlGenerator(EntityType.GROUP);
            private AvatarUrlGenerator personUrlGen = new AvatarUrlGenerator(EntityType.PERSON);

            public void update(final GotCurrentUserStreamBookmarks event)
            {
                bookmarkList.clear();
                bookmarksWidgetMap.clear();

                String personUrl = "person/" + Session.getInstance().getCurrentPerson().getAccountId();
                Panel userBookmark = createPanel(Session.getInstance().getCurrentPerson().getDisplayName(), personUrl,
                        personUrlGen.getSmallAvatarUrl(Session.getInstance().getCurrentPerson().getEntityId(), Session
                                .getInstance().getCurrentPerson().getAvatarId()), null, "", "");

                bookmarkList.add(userBookmark);
                bookmarksWidgetMap.put(personUrl, userBookmark);

                for (final StreamFilter filter : event.getResponse())
                {
                    JSONObject req = StreamJsonRequestFactory.getJSONRequest(filter.getRequest());
                    String uniqueId = null;
                    String entityType = null;

                    String imgUrl = "";

                    if (req.containsKey("query"))
                    {
                        JSONObject query = req.get("query").isObject();
                        if (query.containsKey(StreamJsonRequestFactory.RECIPIENT_KEY))
                        {
                            JSONArray recipient = query.get(StreamJsonRequestFactory.RECIPIENT_KEY).isArray();
                            if (recipient.size() > 0)
                            {
                                JSONObject recipientObj = recipient.get(0).isObject();
                                uniqueId = recipientObj.get("name").isString().stringValue();
                                entityType = recipientObj.get("type").isString().stringValue().toLowerCase();

                                AvatarUrlGenerator urlGen = groupUrlGen;

                                if ("person".equals(entityType))
                                {
                                    urlGen = personUrlGen;
                                }

                                imgUrl = urlGen.getSmallAvatarUrl(filter.getOwnerEntityId(), filter.getOwnerAvatarId());

                            }
                        }

                    }

                    if (uniqueId != null && entityType != null)
                    {
                        String bookmarkUrl = entityType + "/" + uniqueId;
                        Panel bookmarkFilter = createPanel(filter.getName(), bookmarkUrl, imgUrl, new ClickHandler()
                        {
                            public void onClick(final ClickEvent event)
                            {
                                if (new WidgetJSNIFacadeImpl()
                                        .confirm("Are you sure you want to delete this bookmark?"))
                                {
                                    StreamBookmarksModel.getInstance().delete(filter.getId());
                                }

                                event.stopPropagation();
                            }
                        }, style.deleteBookmark(), "");

                        bookmarkList.add(bookmarkFilter);
                        bookmarksWidgetMap.put(bookmarkUrl, bookmarkFilter);
                    }
                }
            }
        });

        EventBus.getInstance().addObserver(GotCurrentUserCustomStreamsResponseEvent.class,
                new Observer<GotCurrentUserCustomStreamsResponseEvent>()
                {
                    public void update(final GotCurrentUserCustomStreamsResponseEvent event)
                    {
                        filterList.clear();
                        customStreamWidgetMap.clear();

                        Panel savedBy = createPanel("My Saved Items", "custom/0/" + "{\"query\":{\"savedBy\":\""
                                + Session.getInstance().getCurrentPerson().getAccountId() + "\"}}",
                                "style/images/customStream.png", null, "", "");

                        filterList.add(savedBy);
                        customStreamWidgetMap.put(0L, savedBy);

                        Panel likedBy = createPanel("My Liked Items", "custom/1/"
                                + "{\"query\":{\"likedBy\":[{\"type\":\"PERSON\", \"name\":\""
                                + Session.getInstance().getCurrentPerson().getAccountId() + "\"}]}}",
                                "style/images/customStream.png", null, "", "");

                        filterList.add(likedBy);
                        customStreamWidgetMap.put(1L, likedBy);

                        for (final StreamFilter filter : event.getResponse().getStreamFilters())
                        {
                            Panel filterPanel = createPanel(filter.getName(), "custom/"
                                    + filter.getId()
                                    + "/"
                                    + filter.getRequest().replace("%%CURRENT_USER_ACCOUNT_ID%%",
                                            Session.getInstance().getCurrentPerson().getAccountId()),
                                    "style/images/customStream.png", new ClickHandler()
                                    {

                                        public void onClick(final ClickEvent event)
                                        {
                                            Dialog.showCentered(new CustomStreamDialogContent((Stream) filter));
                                            event.stopPropagation();
                                        }
                                    }, style.editCustomStream(), "edit");

                            filterList.add(filterPanel);
                            customStreamWidgetMap.put(filter.getId(), filterPanel);
                        }
                    }
                });
    }

    /**
     * Add events.
     */
    private void addEventHandlers()
    {

        moreLink.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                moreSpinner.removeClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());

                JSONObject moreItemsRequest = StreamJsonRequestFactory.setMaxId(longOldestActivityId,
                        StreamJsonRequestFactory.getJSONRequest(currentRequestObj.toString()));

                StreamModel.getInstance().fetch(moreItemsRequest.toString(), false);
            }
        });

        searchBox.addKeyUpHandler(new KeyUpHandler()
        {
            private int lastSearchLength = 0;

            public void onKeyUp(final KeyUpEvent event)
            {
                if (searchBox.getText().length() > 3 && searchBox.getText().length() != lastSearchLength)
                {
                    lastSearchLength = searchBox.getText().length();
                    EventBus.getInstance().notifyObservers(
                            new UpdateHistoryEvent(new CreateUrlRequest("search", searchBox.getText(), false)));
                }
            }
        });

        addBookmark.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                StreamBookmarksModel.getInstance().insert(currentScopeId);
            }
        });

        addToStartPage.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                GadgetModel.getInstance().insert(
                        new AddGadgetToStartPageRequest("{d7a58391-5375-4c76-b5fc-a431c42a7555}", null,
                                STREAM_URL_TRANSFORMER.getUrl(null, currentRequestObj.toString())));
            }
        });

        createFilter.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                Dialog.showCentered(new CustomStreamDialogContent());
            }
        });

        feedLink.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                Window.Location.assign("/resources/atom/stream/query/recipient/" + currentStream.getScopeType() + ":"
                        + currentStream.getUniqueKey());
            }
        });

        // Scheduler.get().scheduleFixedDelay(new RepeatingCommand()
        // {
        // public boolean execute()
        // {
        // if (null != currentRequestObj
        // && "date".equals(currentRequestObj.get("query").isObject().get("sortBy").isString()
        // .stringValue()))
        // {
        // if (Document.get().getScrollTop() < streamDetailsContainer.getAbsoluteTop())
        // {
        // JSONObject newItemsRequest = StreamJsonRequestFactory.setMinId(longNewestActivityId,
        // StreamJsonRequestFactory.getJSONRequest(currentRequestObj.toString()));
        //
        // StreamModel.getInstance().fetch(newItemsRequest.toString(), false);
        // }
        // }
        //
        // return Session.getInstance().getUrlPage().equals(Page.ACTIVITY);
        // }
        // }, NEW_ACTIVITY_POLLING_DELAY);

    }

    /**
     * Append a new message.
     * 
     * @param message
     *            the messa.ge
     */
    private void appendActivity(final ActivityDTO message)
    {
        Panel newActivity = renderer.render(message);
        newActivity.setVisible(false);
        streamPanel.insert(newActivity, 0);
        EffectsFacade.nativeFadeIn(newActivity.getElement(), true);
    }

    /**
     * Load a stream.
     * 
     * @param views
     *            the stream history link.
     */
    private void loadStream(final List<String> views)
    {
        loadStream(views, "");
    }

    /**
     * Load a stream.
     * 
     * @param views
     *            the stream history link.
     * @param searchTerm
     *            the search term.
     */
    private void loadStream(final List<String> views, final String searchTerm)
    {
        Session.getInstance().getActionProcessor().setQueueRequests(true);

        boolean singleActivityMode = false;
        activitySpinner.removeClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());
        moreLink.setVisible(false);
        streamPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().hidden());
        currentRequestObj = StreamJsonRequestFactory.getEmptyRequest();
        currentStream = new StreamScope(ScopeType.PERSON, Session.getInstance().getCurrentPerson().getAccountId());

        if (views == null || views.size() == 0 || views.get(0).equals("following"))
        {
            currentRequestObj = StreamJsonRequestFactory.setSourceAsFollowing(currentRequestObj);
            setAsActiveStream(followingFilterPanel);
        }
        else if (views.get(0).equals("person") && views.size() >= 2)
        {
            String accountId = views.get(1);
            currentRequestObj = StreamJsonRequestFactory.addRecipient(EntityType.PERSON, accountId, currentRequestObj);
            PersonalInformationModel.getInstance().fetch(accountId, false);
            currentStream.setScopeType(ScopeType.PERSON);
            currentStream.setUniqueKey(accountId);
            setAsActiveStream(bookmarksWidgetMap.get("person/" + accountId));
        }
        else if (views.get(0).equals("group") && views.size() >= 2)
        {
            String shortName = views.get(1);
            currentRequestObj = StreamJsonRequestFactory.addRecipient(EntityType.GROUP, shortName, currentRequestObj);
            GroupModel.getInstance().fetch(shortName, false);
            currentStream.setScopeType(ScopeType.GROUP);
            currentStream.setUniqueKey(shortName);
            setAsActiveStream(bookmarksWidgetMap.get("group/" + shortName));
        }
        else if (views.get(0).equals("custom") && views.size() >= 3)
        {
            currentRequestObj = StreamJsonRequestFactory.getJSONRequest(views.get(2));
            setAsActiveStream(customStreamWidgetMap.get(Long.parseLong(views.get(1))));
            currentStream.setScopeType(null);
        }
        else if (views.get(0).equals("everyone"))
        {
            currentRequestObj = StreamJsonRequestFactory.getEmptyRequest();
            setAsActiveStream(everyoneFilterPanel);
        }
        else if (views.size() == 1)
        {
            singleActivityMode = true;
        }

        if (searchTerm.length() > 0)
        {
            currentRequestObj = StreamJsonRequestFactory.setSearchTerm(searchTerm, currentRequestObj);
        }

        if (!singleActivityMode)
        {
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
            EventBus.getInstance().notifyObservers(new PostableStreamScopeChangeEvent(currentStream));
        }
        else
        {
            try
            {
                ActivityModel.getInstance().fetch(Long.parseLong(views.get(0)), true);
            }
            catch (Exception e)
            {
                // Do nothing.
                int x = 0;
            }
        }

        Session.getInstance().getActionProcessor().fireQueuedRequests();
        Session.getInstance().getActionProcessor().setQueueRequests(false);
    }

    /**
     * Set a stream as active.
     * 
     * @param panel
     *            the panel.
     */
    private void setAsActiveStream(final Panel panel)
    {
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {
            public void execute()
            {
                if (currentlyActiveStream != null)
                {
                    currentlyActiveStream.removeStyleName(style.activeStream());
                }

                if (panel != null)
                {
                    currentlyActiveStream = panel;
                    panel.addStyleName(style.activeStream());
                }
            }
        });

    }

    /**
     * Create LI Element for stream lists.
     * 
     * @param name
     *            the name of the item.
     * @param view
     *            the history token to load.
     * @param modifyClickHandler
     *            click handler for modify.
     * @param modifyClass
     *            the class for the modify button.
     * @param modifyText
     *            the text for the modify button.
     * @param imgUrl
     *            the img url.
     * @return the LI.
     */
    private Panel createPanel(final String name, final String view, final String imgUrl,
            final ClickHandler modifyClickHandler, final String modifyClass, final String modifyText)
    {
        FocusPanel panel = new FocusPanel();
        panel.addStyleName(style.streamOptionChild());
        panel.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                History.newItem(Session.getInstance().generateUrl(new CreateUrlRequest(Page.ACTIVITY, view)));
            }
        });

        FlowPanel innerPanel = new FlowPanel();

        innerPanel.add(new Image(imgUrl));

        Label streamName = new Label(name);
        streamName.addStyleName(style.streamName());
        innerPanel.add(streamName);

        if (modifyClickHandler != null)
        {
            Label modifyLink = new Label(modifyText);
            modifyLink.addStyleName(modifyClass);
            modifyLink.addClickHandler(modifyClickHandler);
            innerPanel.add(modifyLink);
        }

        panel.add(innerPanel);

        return panel;
    }

    /**
     * Binder for building UI.
     */
    interface LocalUiBinder extends UiBinder<Widget, ActivityContent>
    {
    }
}
