/*
 * Copyright (c) 2011-2012 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.web.client.ui.pages.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.server.domain.AvatarUrlGenerator;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.server.domain.Identifiable;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.web.client.events.CustomStreamCreatedEvent;
import org.eurekastreams.web.client.events.CustomStreamDeletedEvent;
import org.eurekastreams.web.client.events.CustomStreamUpdatedEvent;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.HistoryViewsChangedEvent;
import org.eurekastreams.web.client.events.MessageStreamAppendEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.StreamReinitializeRequestEvent;
import org.eurekastreams.web.client.events.StreamSearchBeginEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.events.data.AddedFeaturedStreamResponseEvent;
import org.eurekastreams.web.client.events.data.GotActivityResponseEvent;
import org.eurekastreams.web.client.events.data.GotCurrentUserCustomStreamsResponseEvent;
import org.eurekastreams.web.client.events.data.GotCurrentUserStreamBookmarks;
import org.eurekastreams.web.client.events.data.GotGroupModelViewInformationResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonFollowerStatusResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonalInformationResponseEvent;
import org.eurekastreams.web.client.events.data.GotStreamActivitySubscriptionResponseEvent;
import org.eurekastreams.web.client.events.data.GotStreamResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedGroupMemberResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedPersonFollowerResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedRequestForGroupMembershipResponseEvent;
import org.eurekastreams.web.client.events.data.PostableStreamScopeChangeEvent;
import org.eurekastreams.web.client.events.data.StreamActivitySubscriptionChangedEvent;
import org.eurekastreams.web.client.events.data.UpdatedGroupStickyActivityEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.jsni.EffectsFacade;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.ActivityModel;
import org.eurekastreams.web.client.model.CustomStreamModel;
import org.eurekastreams.web.client.model.Deletable;
import org.eurekastreams.web.client.model.GadgetModel;
import org.eurekastreams.web.client.model.GroupActivitySubscriptionModel;
import org.eurekastreams.web.client.model.GroupMembershipRequestModel;
import org.eurekastreams.web.client.model.GroupModel;
import org.eurekastreams.web.client.model.PersonActivitySubscriptionModel;
import org.eurekastreams.web.client.model.PersonalInformationModel;
import org.eurekastreams.web.client.model.StreamBookmarksModel;
import org.eurekastreams.web.client.model.StreamModel;
import org.eurekastreams.web.client.model.requests.AddGadgetToStartPageRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.SpinnerLabelButton;
import org.eurekastreams.web.client.ui.common.avatar.AvatarBadgeManager;
import org.eurekastreams.web.client.ui.common.avatar.AvatarLinkPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.common.stream.ActivityDetailPanel;
import org.eurekastreams.web.client.ui.common.stream.GroupEmailSubscribeOptionsDialogContent;
import org.eurekastreams.web.client.ui.common.stream.StreamJsonRequestFactory;
import org.eurekastreams.web.client.ui.common.stream.StreamSearchStatusWidget;
import org.eurekastreams.web.client.ui.common.stream.StreamToUrlTransformer;
import org.eurekastreams.web.client.ui.common.stream.UnseenActivityNotificationPanel;
import org.eurekastreams.web.client.ui.common.stream.filters.list.CustomStreamDialogContent;
import org.eurekastreams.web.client.ui.common.stream.renderers.ShowRecipient;
import org.eurekastreams.web.client.ui.common.stream.renderers.StickyActivityRenderer;
import org.eurekastreams.web.client.ui.common.stream.renderers.StreamMessageItemRenderer;
import org.eurekastreams.web.client.ui.common.widgets.activity.PostBoxComposite;
import org.eurekastreams.web.client.ui.common.widgets.activity.StreamDetailsComposite;
import org.eurekastreams.web.client.ui.common.widgets.activity.StreamDetailsComposite.CustomAvatar;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
//import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.http.client.URL;

/**
 * Activity Page.
 */
public class ActivityContent extends Composite
{
    /** Text displayed for locked users. */
    private static final String LOCKED_USER_TEXT = "This employee has no profile in Eureka Streams.  This could be due"
            + " to an incorrect or outdated link, a change in assignment within the company, or leaving the company.";

    /** Amount of time to wait after a key is pressed before performing a search. */
    private static final int SEARCH_UPDATE_DELAY = 500;

    /** Amount of time to wait after a key is pressed to update the URL with the search term. */
    private static final int SEARCH_URL_UPDATE_DELAY = 2000;

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

        /**
         * Active search style.
         *
         * @return active search style.
         */
        String activeSearch();

        /**
         * Current user link style.
         *
         * @return current user stream style.
         */
        String currentUserStreamLink();

        /**
         * Small avatar.
         *
         * @return small avatar style.
         */
        String smallAvatar();

        /**
         * Current user configure link.
         *
         * @return current user configure link.
         */
        String currentUserConfLink();

        /**
         * Message when no bookmarks exist.
         *
         * @return message when no bookmarks exist.
         */
        String noBookmarksMessage();

        /** @return The stream container panel. */
        @ClassName("stream-container-panel")
        String streamContainerPanel();
    }

    /**
     * CSS style.
     */
    @UiField
    ActivityStyle style;

    /**
     * Stream details.
     */
    @UiField
    StreamDetailsComposite streamDetailsComposite;

    /**
     * No results panel.
     */
    @UiField
    DivElement noResults;

    /**
     * Search container.
     */
    @UiField
    DivElement searchContainer;

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
     * UI element for default streams.
     */
    @UiField
    FlowPanel errorPanel;

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
     * Subscribe via email.
     */
    @UiField
    Label subscribeViaEmail;

    /**
     * Stream search status widget.
     */
    @UiField
    StreamSearchStatusWidget streamSearchStatusWidget;

    /** Link to get contact for emailing to stream. */
    //@UiField
    //Anchor getEmailContactLink;

    /**
     * Panel for unseen activity notifications - hidden when viewing a single activity.
     */
    @UiField
    UnseenActivityNotificationPanel unseenActivityNotificationPanel;

    /**
     * Message Renderer.
     */
    StreamMessageItemRenderer renderer = new StreamMessageItemRenderer(ShowRecipient.YES);

    /** Sticky activity renderer (for groups only). */
    private static StickyActivityRenderer stickyActivityRenderer = new StickyActivityRenderer();

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
     * Stream options panel.
     */
    @UiField
    DivElement streamOptionsPanel;

    /**
     * Current stream scope.
     */
    private StreamScope currentStream;

    /**
     * Current scope id.
     */
    private long currentScopeId;

    /**
     * Current stream display name.
     */
    private String currentDisplayName;

    /**
     * Entity for the current stream. Used by the sticky activity logic to determine if any stick/unstick events pertain
     * to the current view. Would use currentStream, but there is other code that clobbers the entity type if the stream
     * isn't postable and thus renders currentStream useless for the purpose.
     */
    private Identifiable currentStreamEntity;

    /**
     * New activity polling.
     */
    private static final int NEW_ACTIVITY_POLLING_DELAY = 1200000;

    /**
     * Custom streams map.
     */
    private final HashMap<Long, StreamNamePanel> customStreamWidgetMap = new HashMap<Long, StreamNamePanel>();

    /**
     * Stream bookmarks map.
     */
    private final HashMap<String, StreamNamePanel> bookmarksWidgetMap = new HashMap<String, StreamNamePanel>();

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
     * Is subscribed.
     */
    private boolean isSubscribed = false;

    /**
     * Post Box.
     */
    @UiField
    PostBoxComposite postBox;

    /**
     * User panel.
     */
    @UiField
    FlowPanel userPanel;

    /**
     * Current sort keyword.
     */
    private String sortKeyword = "";

    /**
     * Single activity mode.
     */
    private boolean singleActivityMode;

    /**
     * Bookmarks initially loaded.
     */
    private boolean bookmarksLoaded = false;

    /**
     * Bookmarks initially loaded.
     */
    private boolean customStreamsLoaded = false;

    /**
     * If the page has ran init.
     */
    private boolean hasInited = false;

    /** Stream to URL transformer. */
    private static final StreamToUrlTransformer STREAM_URL_TRANSFORMER = new StreamToUrlTransformer();

    /**
     * If the entity still needs to be received before making the activity query.
     *
     * Explanation: Fetching the activities for a group requires knowing which activity is sticky so it can be excluded
     * on the query. So the query must be built in loadStream and the DomainGroupModelView must be recieved.
     * Unfortunately, the event bus notifies inline instead of queuing, thus the call to the GroupModel in loadStream
     * may fire its event immediately (if the group is in cache), causing the group to be received before loadStream
     * finishes. So we have to handle two different possible orders. The approach taken is to set this flag and the one
     * below; whenever they are both reset, the activity query is sent.
     */
    private boolean deferLoadAwaitingEntityReceived;

    /**
     * If the activity query isn't done being constructed yet (before making the activity query). See
     * deferLoadAwaitingEntityReceived.
     */
    private boolean deferLoadAwaitingQueryBuilt;

    /** Place to put sticky activity. */
    @UiField
    SimplePanel stickyActivityHolder;

    /** Area containing the sticky activity. */
    @UiField
    DivElement stickyActivityArea;

    /** Views used to load the current stream. */
    List<String> loadedViews = Collections.singletonList("[do not match]");

    /** So results for the wrong stream can be detected and ignored. */
    private String currentStreamRequest;

    /** Search term used to load the current stream. */
    String loadedSearchTerm = "";

    /** Timer to delay progressive search until the user pauses. */
    private final Timer searchTimer = new Timer()
    {
        @Override
        public void run()
        {
            String searchText = searchBox.getText();
            loadStream(Session.getInstance().getUrlViews(), searchText);
            searchUrlTimer.schedule(SEARCH_URL_UPDATE_DELAY);
        }
    };

    /** Timer to update the URL with the search term. */
    private final Timer searchUrlTimer = new Timer()
    {
        @Override
        public void run()
        {
            updateUrlWithSearchTerm();
        }
    };

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
        moreLink.setVisible(false);
        streamSearchStatusWidget.setVisible(false);
        errorPanel.setVisible(false);

        followingFilterPanel = createPanel("Following", "following", "style/images/customStream.png", null, "", "",
                false);
        everyoneFilterPanel = createPanel("Everyone", "everyone", "style/images/customStream.png", null, "", "", false);

        defaultList.add(followingFilterPanel);
        defaultList.add(everyoneFilterPanel);

        final PersonModelView currentPerson = Session.getInstance().getCurrentPerson();

        AvatarLinkPanel userAvatar = new AvatarLinkPanel(currentPerson.getEntityType(), currentPerson.getAccountId(),
                currentPerson.getAvatarId(), Size.Small, currentPerson.getDisplayName());
        userPanel.add(userAvatar);

        FlowPanel userLinkPanel = new FlowPanel();

        String nameUrl = Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.PEOPLE, currentPerson.getAccountId()));
        Hyperlink name = new Hyperlink(currentPerson.getDisplayName(), nameUrl);
        name.setTitle(currentPerson.getDisplayName());
        name.addStyleName(style.currentUserStreamLink());
        name.addStyleName(StaticResourceBundle.INSTANCE.coreCss().ellipsisChild());
        userLinkPanel.add(name);

        String confUrl = Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.PERSONAL_SETTINGS, currentPerson.getAccountId()));
        Hyperlink conf = new Hyperlink("Configure My Stream", confUrl);
        conf.addStyleName(style.currentUserConfLink());
        userLinkPanel.add(conf);

        userPanel.add(userLinkPanel);

        CustomStreamModel.getInstance().fetch(null, true);
        StreamBookmarksModel.getInstance().fetch(null, true);

        moreSpinner.addClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());
        noResults.addClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());
        unseenActivityNotificationPanel.setActive(true);
    }

    /**
     * Got activity.
     *
     * @param event
     *            the event.
     */
    private void gotActivity(final GotActivityResponseEvent event)
    {
        streamPanel.clear();
        unseenActivityNotificationPanel.setActive(false);
        activitySpinner.addClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());

        if (event.getResponse() != null)
        {
            EntityType actorType = event.getResponse().getDestinationStream().getEntityType();
            String actorName = event.getResponse().getDestinationStream().getUniqueId();

            if (actorType.equals(EntityType.GROUP))
            {
                GroupModel.getInstance().fetch(actorName, false);

            }
            else if (actorType.equals(EntityType.PERSON))
            {
                PersonalInformationModel.getInstance().fetch(actorName, false);
            }
        }

        streamPanel.add(new ActivityDetailPanel(event.getResponse(), ShowRecipient.YES));
        streamPanel.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().hidden());
    }

    /**
     * Add events.
     */
    private void addObservers()
    {
        final EventBus eventBus = EventBus.getInstance();
        eventBus.addObserver(GotActivityResponseEvent.class, new Observer<GotActivityResponseEvent>()
        {

            public void update(final GotActivityResponseEvent event)
            {
                gotActivity(event);
            }
        });
        eventBus.addObserver(GotStreamResponseEvent.class, new Observer<GotStreamResponseEvent>()
        {
            public void update(final GotStreamResponseEvent event)
            {
                // throw out results if for the wrong stream (or we don't want the results)
                if (currentStreamRequest == null || !currentStreamRequest.equals(event.getRequest()))
                {
                    return;
                }

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
                    unseenActivityNotificationPanel.setActive(true);
                    activitySpinner.addClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());
                    streamPanel.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().hidden());

                    List<ActivityDTO> activities = activitySet.getPagedSet();
                    for (ActivityDTO activity : activities)
                    {
                        streamPanel.add(renderer.render(activity));
                    }
                    if (activities.size() == 0)
                    {
                        noResults.removeClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());
                    }
                    moreLink.setVisible(activitySet.getTotal() > activities.size());
                }
                if (activitySet.getPagedSet().size() > 0)
                {
                    noResults.addClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());
                }
            }
        });

        // users are not initially subscribed for emails when following a person/group, so set the status properly (else
        // if you were following and unsubscribed, then re-subscribed, the status would be old and wrong)
        eventBus.addObserver(InsertedPersonFollowerResponseEvent.class,
                new Observer<InsertedPersonFollowerResponseEvent>()
                {
                    public void update(final InsertedPersonFollowerResponseEvent ev)
                    {
                        setSubscribeStatus(false);
                    }
                });

        eventBus.addObserver(InsertedGroupMemberResponseEvent.class, new Observer<InsertedGroupMemberResponseEvent>()
        {
            public void update(final InsertedGroupMemberResponseEvent ev)
            {
                setSubscribeStatus(false);
            }
        });

        eventBus.addObserver(GotPersonFollowerStatusResponseEvent.class,
                new Observer<GotPersonFollowerStatusResponseEvent>()
                {
                    public void update(final GotPersonFollowerStatusResponseEvent event)
                    {
                        subscribeViaEmail.setVisible(event.getResponse().equals(FollowerStatus.FOLLOWING));
                    }
                });

        eventBus.addObserver(HistoryViewsChangedEvent.class, new Observer<HistoryViewsChangedEvent>()
        {
            public void update(final HistoryViewsChangedEvent event)
            {
                searchTimer.cancel();
                searchUrlTimer.cancel();
                handleViewsChanged(event.getViews());
                final String searchText = Session.getInstance().getParameterValue("search");
                if (!searchBox.getText().equals(searchText))
                {
                    searchBox.setText(searchText);
                }
            }
        });

        eventBus.addObserver(MessageStreamAppendEvent.class, new Observer<MessageStreamAppendEvent>()
        {
            public void update(final MessageStreamAppendEvent event)
            {
                longNewestActivityId = event.getMessage().getId();

                if (sortKeyword.equals("date"))
                {
                    appendActivity(event.getMessage());
                    noResults.addClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());
                }
                else
                {
                    recentSort.getElement().dispatchEvent(
                            Document.get().createClickEvent(1, 0, 0, 0, 0, false, false, false, false));
                }

            }
        });

        eventBus.addObserver(CustomStreamCreatedEvent.class, new Observer<CustomStreamCreatedEvent>()
        {
            public void update(final CustomStreamCreatedEvent event)
            {
                CustomStreamModel.getInstance().fetch(null, true);
            }
        });

        eventBus.addObserver(CustomStreamDeletedEvent.class, new Observer<CustomStreamDeletedEvent>()
        {
            public void update(final CustomStreamDeletedEvent event)
            {
                CustomStreamModel.getInstance().fetch(null, true);
            }
        });

        eventBus.addObserver(CustomStreamUpdatedEvent.class, new Observer<CustomStreamUpdatedEvent>()
        {
            public void update(final CustomStreamUpdatedEvent event)
            {
                CustomStreamModel.getInstance().fetch(null, true);
            }
        });

        eventBus.addObserver(StreamReinitializeRequestEvent.class, new Observer<StreamReinitializeRequestEvent>()
        {
            public void update(final StreamReinitializeRequestEvent event)
            {
                loadStream(Session.getInstance().getUrlViews(), Session.getInstance().getParameterValue("search"));
            }
        });

        eventBus.addObserver(UpdatedHistoryParametersEvent.class, new Observer<UpdatedHistoryParametersEvent>()
        {
            public void update(final UpdatedHistoryParametersEvent event)
            {
                searchTimer.cancel();
                searchUrlTimer.cancel();
                if (!event.getViewChanged())
                {
                    handleViewsChanged(Session.getInstance().getUrlViews());
                }
            }
        });

        eventBus.addObserver(AddedFeaturedStreamResponseEvent.class, new Observer<AddedFeaturedStreamResponseEvent>()
        {
            public void update(final AddedFeaturedStreamResponseEvent event)
            {
                eventBus.notifyObservers(new ShowNotificationEvent(new Notification("Stream has been featured.")));
            }
        });

        eventBus.addObserver(UpdatedGroupStickyActivityEvent.class, new Observer<UpdatedGroupStickyActivityEvent>()
        {
            public void update(final UpdatedGroupStickyActivityEvent ev)
            {
                // make sure event applies to the current view (since current view may not even be a group)
                if (currentStreamEntity != null && currentStreamEntity.getEntityType() == EntityType.GROUP
                        && ev.getGroupId() == currentStreamEntity.getEntityId() && !singleActivityMode)
                {
                    if (ev.getActivity() == null)
                    {
                        stickyActivityHolder.clear();
                        UIObject.setVisible(stickyActivityArea, false);
                    }
                    else
                    {
                        stickyActivityHolder.clear();
                        stickyActivityHolder.add(stickyActivityRenderer.render(ev.getActivity()));
                        UIObject.setVisible(stickyActivityArea, true);
                    }

                    // reload the stream to get prior sticky activities back in it (and a freshly stuck activity out)
                    eventBus.notifyObservers(StreamReinitializeRequestEvent.getEvent());
                }
            }
        });

        addEntityObservers();
    }

    /**
     * Add entity observers.
     */
    private void addEntityObservers()
    {
        EventBus.getInstance().addObserver(GotPersonalInformationResponseEvent.class,
                new Observer<GotPersonalInformationResponseEvent>()
                {
                    public void update(final GotPersonalInformationResponseEvent event)
                    {
                        PersonModelView person = event.getResponse();
                        currentDisplayName = person.getDisplayName();
                        currentScopeId = person.getStreamId();

                        if (person.isAccountLocked())
                        {
                            streamOptionsPanel.getStyle().setDisplay(Display.NONE);
                            currentStream.setScopeType(null);
                            errorPanel.clear();
                            errorPanel.setVisible(true);
                            activitySpinner.addClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());
                            errorPanel.add(new Label("Employee profile not found"));
                            errorPanel.add(new Label(LOCKED_USER_TEXT));
                            streamPanel.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().hidden());

                            streamDetailsComposite.setVisible(false);
                            currentStream.setScopeType(null);

                            // block display of activities
                            currentStreamRequest = null;
                            streamPanel.clear();
                            unseenActivityNotificationPanel.setActive(false);
                            activitySpinner.addClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());
                            streamPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().hidden());
                            noResults.addClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());
                            moreLink.setVisible(false);
                        }
                        else
                        {
                            currentStream.setDisplayName(person.getDisplayName());
                            streamDetailsComposite.setVisible(true);
                        }
                        if (!person.isStreamPostable()
                                && !person.getAccountId().equals(
                                        Session.getInstance().getCurrentPerson().getAccountId()))
                        {
                            currentStream.setScopeType(null);
                        }
                        /*if (currentStream.getScopeType() != null)
                        {
                            getEmailContactLink.setHref("/resources/emailcontact/stream/person/" + person.getId());
                            getEmailContactLink.setVisible(true);
                        }*/
                        if (!singleActivityMode)
                        {
                            EventBus.getInstance().notifyObservers(new PostableStreamScopeChangeEvent(currentStream));
                        }
                    }
                });

        EventBus.getInstance().addObserver(GotGroupModelViewInformationResponseEvent.class,
                new Observer<GotGroupModelViewInformationResponseEvent>()
                {
                    public void update(final GotGroupModelViewInformationResponseEvent event)
                    {
                        onGroupModelViewReceived(event.getResponse());
                    }
                });
    }

    /**
     * Processing when group information is received.
     *
     * @param group
     *            The group.
     */
    private void onGroupModelViewReceived(final DomainGroupModelView group)
    {
    	// If group is null, then that means that the group 
    	// was not found. As a result, a "Group not found" 
    	// page should be displayed. 
    	// This page is very similar to the "Activity not found" page
    	if (group == null)
    	{	
    		showGroupNotFoundPage();
    	}
    	else
	    {
	    	currentDisplayName = group.getDisplayName();
	        currentScopeId = group.getStreamId();
	
	        currentStream.setDisplayName(group.getName());
	        streamDetailsComposite.setVisible(true);
	
	        if (group.isRestricted())
	        {
	            streamOptionsPanel.getStyle().setDisplay(Display.NONE);
	            currentStream.setScopeType(null);
	            postBox.setVisible(false);
	
	            errorPanel.clear();
	            errorPanel.setVisible(true);
	            activitySpinner.addClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());
	            errorPanel.add(new Label("Access to this group is restricted"));
	            errorPanel.add(new Label("To view this group's stream please request access from its coordinator"));
	
	            final SpinnerLabelButton button = new SpinnerLabelButton(new ClickHandler()
	            {
	                public void onClick(final ClickEvent inArg0)
	                {
	                    GroupMembershipRequestModel.getInstance().insert(group.getShortName());
	                }
	            });
	
	            EventBus.getInstance().addObserver(InsertedRequestForGroupMembershipResponseEvent.class,
	                    new Observer<InsertedRequestForGroupMembershipResponseEvent>()
	                    {
	                        public void update(final InsertedRequestForGroupMembershipResponseEvent inArg1)
	                        {
	                            button.disable();
	                            EventBus.getInstance()
	                                    .notifyObservers(
	                                            new ShowNotificationEvent(new Notification(
	                                                    "Your request for access has been sent")));
	                        }
	                    });
	
	            button.addStyleName(StaticResourceBundle.INSTANCE.coreCss().requestAccessButton());
	            errorPanel.add(button);
	
	            streamPanel.clear();
	            unseenActivityNotificationPanel.setActive(true);
	        }
	        else
	        {
	            currentStreamEntity = group;
	
	            if (group.getStickyActivity() != null && !singleActivityMode
	                    && (loadedSearchTerm == null || loadedSearchTerm == ""))
	            {
	                stickyActivityHolder.add(stickyActivityRenderer.render(group.getStickyActivity()));
	                UIObject.setVisible(stickyActivityArea, true);
	            }
	        }
	        boolean isCoordinator = false;
	
	        for (PersonModelView coordinator : group.getCoordinators())
	        {
	            AvatarBadgeManager.getInstance().setBadge(style.streamContainerPanel(), coordinator.getUniqueId());
	            if (coordinator.getAccountId().equals(Session.getInstance().getCurrentPerson().getAccountId()))
	            {
	                isCoordinator = true;
	            }
	        }
	        if (!group.isStreamPostable() && !isCoordinator)
	        {
	            currentStream.setScopeType(null);
	        }
	        /*else
	        {
	            getEmailContactLink.setHref("/resources/emailcontact/stream/group/" + group.getId());
	            getEmailContactLink.setVisible(true);
	        }*/
	
	        if (!singleActivityMode)
	        {
	            if (Session.getInstance().getCurrentPersonRoles().contains(Role.SYSTEM_ADMIN) || isCoordinator)
	            {
	                streamContainerPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().hasOwnerRights());
	            }
	
	            EventBus.getInstance().notifyObservers(new PostableStreamScopeChangeEvent(currentStream));
	        }
	
	        if (deferLoadAwaitingEntityReceived)
	        {
	            deferLoadAwaitingEntityReceived = false;
	            currentRequestObj = StreamJsonRequestFactory.setExcludeId(group.getStickyActivityId(), 
	            		currentRequestObj);
	            if (!deferLoadAwaitingQueryBuilt)
	            {
	                fetchStream(currentRequestObj);
	            }
	        }
    	}
    }
    
    /**
     * Shows a "not found" message.
     */
    private void showGroupNotFoundPage()
    {
    	activitySpinner.addClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());
    	streamPanel.clear();
		errorPanel.clear();
		postBox.setVisible(false);
		
    	Panel errorReport = new FlowPanel();
        errorReport.addStyleName(StaticResourceBundle.INSTANCE.coreCss().warningReport());

        FlowPanel centeringPanel = new FlowPanel();
        centeringPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().warningReportContainer());
        centeringPanel.add(errorReport);
        streamPanel.add(centeringPanel);

        FlowPanel msgPanel = new FlowPanel();

        Label msgHeader = new Label("Group not found");
        msgHeader.addStyleName(StaticResourceBundle.INSTANCE.coreCss().warningMessage());

        Label msgText = new Label("The group you were looking for has been deleted or could not be found.");
        FlowPanel text = new FlowPanel();
        text.add(msgText);
        text.addStyleName(StaticResourceBundle.INSTANCE.coreCss().errorMessageText());

        msgPanel.add(msgHeader);
        msgPanel.add(msgText);

        streamPanel.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().hidden());
        
		streamPanel.setVisible(true);
		streamContainerPanel.setVisible(true);
        errorReport.add(msgPanel);
		errorPanel.setVisible(true);
		errorReport.setVisible(true);
		centeringPanel.setVisible(true);
		msgPanel.setVisible(true);
    }
    
    /**
     * Handle views changed.
     *
     * @param inViews
     *            the views.
     */
    protected void handleViewsChanged(final List<String> inViews)
    {
        String search = Session.getInstance().getParameterValue("search");

        // prevent reloading the same content
        if (!loadedViews.equals(inViews) || !loadedSearchTerm.equals(search))
        {
            loadStream(inViews, search);
        }
        List<String> views = new ArrayList<String>(inViews);

        if (views.size() < 2 || !"sort".equals(views.get(views.size() - 2)))
        {
            views.add("sort");
            views.add("recent");
        }

        Map<String, String> params = (search == null || search.isEmpty()) ? Collections.EMPTY_MAP : Collections
                .singletonMap("search", search);

        views.set(views.size() - 1, "recent");
        recentSort.setTargetHistoryToken(Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.ACTIVITY, views, params)));

        views.set(views.size() - 1, "popular");
        popularSort.setTargetHistoryToken(Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.ACTIVITY, views, params)));

        views.set(views.size() - 1, "active");
        activeSort.setTargetHistoryToken(Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.ACTIVITY, views, params)));
    }

    /**
     * Setup bookmarks and custom streams.
     */
    private void setupStreamsAndBookmarks()
    {
        final EventBus eventBus = EventBus.getInstance();

        eventBus.addObserver(GotCurrentUserStreamBookmarks.class, new Observer<GotCurrentUserStreamBookmarks>()
        {
            private final AvatarUrlGenerator groupUrlGen = new AvatarUrlGenerator(EntityType.GROUP);
            private final AvatarUrlGenerator personUrlGen = new AvatarUrlGenerator(EntityType.PERSON);

            public void update(final GotCurrentUserStreamBookmarks event)
            {
                bookmarkList.clear();
                bookmarksWidgetMap.clear();

                List<StreamFilter> sortedStreamFilters = event.getResponse();
                Collections.sort(sortedStreamFilters, new StreamFilterNameComparator());

                for (final StreamFilter filter : sortedStreamFilters)
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

                                imgUrl = urlGen.getSmallAvatarUrl(filter.getOwnerAvatarId());

                            }
                        }

                    }

                    if (uniqueId != null && entityType != null)
                    {
                        String bookmarkUrl = entityType + "/" + uniqueId;
                        StreamNamePanel bookmarkFilter = createPanel(filter.getName(), bookmarkUrl, imgUrl,
                                new ClickHandler()
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
                                }, style.deleteBookmark(), "", true);

                        bookmarkList.add(bookmarkFilter);
                        bookmarksWidgetMap.put(bookmarkUrl, bookmarkFilter);
                    }

                }
                if (sortedStreamFilters.size() == 0)
                {
                    Label defaultLabel = new Label("Bookmarks allow you to quickly jump to any stream in Eureka.");
                    defaultLabel.addStyleName(style.noBookmarksMessage());
                    bookmarkList.add(defaultLabel);
                }

                bookmarksLoaded = true;
                checkInit();
            }
        });

        eventBus.addObserver(StreamActivitySubscriptionChangedEvent.class,
                new Observer<StreamActivitySubscriptionChangedEvent>()
                {
                    public void update(final StreamActivitySubscriptionChangedEvent ev)
                    {
                        boolean newStatus = ev.getResponse().getReceiveNewActivityNotifications();
                        setSubscribeStatus(newStatus);

                        String msg = newStatus ? "You will now receive emails for new activities to this stream"
                                : "You will no longer receive emails for new activities to this stream";
                        eventBus.notifyObservers(new ShowNotificationEvent(new Notification(msg)));
                    }
                });
        eventBus.addObserver(GotStreamActivitySubscriptionResponseEvent.class,
                new Observer<GotStreamActivitySubscriptionResponseEvent>()
                {
                    public void update(final GotStreamActivitySubscriptionResponseEvent result)
                    {
                        setSubscribeStatus(result.isSubscribed());
                    }
                });

        eventBus.addObserver(GotCurrentUserCustomStreamsResponseEvent.class,
                new Observer<GotCurrentUserCustomStreamsResponseEvent>()
                {
                    public void update(final GotCurrentUserCustomStreamsResponseEvent event)
                    {
                        filterList.clear();
                        customStreamWidgetMap.clear();

                        StreamNamePanel savedBy = createPanel("My Saved Items", "custom/0/"
                                + "{\"query\":{\"savedBy\":\""
                                + Session.getInstance().getCurrentPerson().getAccountId() + "\"}}",
                                "style/images/customStream.png", null, "", "", false);

                        filterList.add(savedBy);
                        customStreamWidgetMap.put(0L, savedBy);

                        StreamNamePanel likedBy = createPanel("My Liked Items", "custom/1/"
                                + "{\"query\":{\"likedBy\":[{\"type\":\"PERSON\", \"name\":\""
                                + Session.getInstance().getCurrentPerson().getAccountId() + "\"}]}}/My Liked Items",
                                "style/images/customStream.png", null, "", "", false);

                        filterList.add(likedBy);
                        customStreamWidgetMap.put(1L, likedBy);

                        for (final StreamFilter filter : event.getResponse().getStreamFilters())
                        {
                            StreamNamePanel filterPanel = createPanel(
                                    filter.getName(),
                                    "custom/"
                                            + filter.getId()
                                            + "/"
                                            + URL.encodeComponent(filter.getRequest().
                                                        replace("%%CURRENT_USER_ACCOUNT_ID%%",
                                                            Session.getInstance().getCurrentPerson().getAccountId())),
                                    "style/images/customStream.png", new ClickHandler()
                                    {

                                        public void onClick(final ClickEvent event)
                                        {
                                            Dialog.showCentered(new CustomStreamDialogContent((Stream) filter));
                                            event.stopPropagation();
                                        }
                                    }, style.editCustomStream(), "edit", false);

                            filterList.add(filterPanel);
                            customStreamWidgetMap.put(filter.getId(), filterPanel);
                        }

                        customStreamsLoaded = true;
                        checkInit();
                    }
                });

        eventBus.addObserver(StreamSearchBeginEvent.class, new Observer<StreamSearchBeginEvent>()
        {
            public void update(final StreamSearchBeginEvent event)
            {
                if (null == event.getSearchText())
                {
                    eventBus.notifyObservers(new UpdateHistoryEvent(new CreateUrlRequest("search", "", false)));
                    searchBox.setText("");
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

                fetchStream(moreItemsRequest);
            }
        });

        searchBox.addKeyUpHandler(new KeyUpHandler()
        {
            private int lastSearchLength = 0;

            public void onKeyUp(final KeyUpEvent event)
            {
                final String searchText = searchBox.getText();
                final int searchTextLength = searchText.length();
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
                {
                    lastSearchLength = searchTextLength;

                    searchTimer.cancel();
                    searchUrlTimer.cancel();

                    // don't load stream here - the URL change will cause it to be reloaded

                    EventBus.getInstance().notifyObservers(
                            new UpdateHistoryEvent(new CreateUrlRequest("search", searchText, false)));
                }
                else if ((searchTextLength > 2 && searchTextLength != lastSearchLength)
                        || searchTextLength < lastSearchLength)
                {
                    lastSearchLength = searchTextLength;

                    searchTimer.schedule(SEARCH_UPDATE_DELAY);
                }
            }
        });

        searchBox.addBlurHandler(new BlurHandler()
        {
            public void onBlur(final BlurEvent inEvent)
            {
                updateUrlWithSearchTerm();
            }
        });

        addBookmark.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                StreamBookmarksModel.getInstance().insert(currentScopeId);
                addBookmark.setVisible(false);
                EventBus.getInstance().notifyObservers(
                        new ShowNotificationEvent(new Notification("You have bookmarked this stream.")));
            }
        });

        subscribeViaEmail.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                if (!isSubscribed)
                {
                    if (currentStream.getScopeType().equals(ScopeType.GROUP))
                    {
                        Dialog.showCentered(new GroupEmailSubscribeOptionsDialogContent(currentStream.getUniqueKey()));
                    }
                    else if (currentStream.getScopeType().equals(ScopeType.PERSON))
                    {
                        PersonActivitySubscriptionModel.getInstance().insert(currentStream.getUniqueKey());
                        setSubscribeStatus(true);
                    }
                }
                else
                {
                    Deletable<String> deletable = null;

                    if (currentStream.getScopeType().equals(ScopeType.GROUP))
                    {
                        deletable = GroupActivitySubscriptionModel.getInstance();
                    }
                    else if (currentStream.getScopeType().equals(ScopeType.PERSON))
                    {
                        deletable = PersonActivitySubscriptionModel.getInstance();
                    }

                    if (deletable != null)
                    {
                        deletable.delete(currentStream.getUniqueKey());
                        setSubscribeStatus(false);
                    }
                }
            }
        });

        addToStartPage.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                // For the app's location, use the current URL minus a few parameters we know we don't want. (They are
                // used by other lists, but get left in the URL when switching tabs.)
                // We don't build the URL from the stream id, since that doesn't take search terms into account.
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("listId", null);
                params.put("listFilter", null);
                params.put("listSort", null);
                params.put("startIndex", null);
                params.put("endIndex", null);
                String url = Session.getInstance().generateUrl(new CreateUrlRequest(params));

                // make a version of the query that doesn't have the exclusion list
                JSONObject request = currentRequestObj;
                if (request.containsKey("exclude"))
                {
                    request = new JSONObject();
                    for (String key : currentRequestObj.keySet())
                    {
                        if (!"exclude".equals(key))
                        {
                            request.put(key, currentRequestObj.get(key));
                        }
                    }
                }
                // TODO: get correct title from somewhere.
                String prefs = "{\"streamQuery\":"
                        + makeJsonString(STREAM_URL_TRANSFORMER.getUrl(null, request.toString()))
                        + ",\"gadgetTitle\":" + makeJsonString(currentDisplayName) + ",\"streamLocation\":"
                        + makeJsonString(url) + "}";

                GadgetModel.getInstance().insert(
                        new AddGadgetToStartPageRequest("{d7a58391-5375-4c76-b5fc-a431c42a7555}", null, prefs));
                EventBus.getInstance()
                        .notifyObservers(
                                new ShowNotificationEvent(new Notification(
                                        "This stream will now show up on your start page.")));
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
    }

    /**
     * Requests a stream via the model and tracks it for proper reciept matching.
     *
     * @param request
     *            The request in JSON form.
     */
    private void fetchStream(final JSONObject request)
    {
        currentStreamRequest = request.toString();
        StreamModel.getInstance().fetch(currentStreamRequest, false);
    }

    /**
     * Updates the URL to include the search term.
     */
    private void updateUrlWithSearchTerm()
    {
        String searchText = searchBox.getText();
        String searchParam = Session.getInstance().getParameterValue("search");
        if (!searchText.equals(searchParam))
        {
            EventBus.getInstance().notifyObservers(
                    new UpdateHistoryEvent(new CreateUrlRequest("search", searchText, false)));
        }
    }

    /**
     * Update subscription status consistently.
     *
     * @param inIsSubscribed
     *            New status.
     */
    private void setSubscribeStatus(final boolean inIsSubscribed)
    {
        isSubscribed = inIsSubscribed;
        subscribeViaEmail.setText(isSubscribed ? "Unsubscribe to Emails" : "Subscribe via Email");
    }

    /**
     * Creates the JSON representation of a string value. (Escapes characters and adds string delimiters or returns null
     * keyword as applicable.) See http://www.json.org/ for syntax. Assumes the string contains no control characters.
     *
     * @param input
     *            Input string, possibly null.
     * @return JSON string representation.
     */
    private static native String makeJsonString(final String input)
    /*-{
     return input == null ? 'null' : '"' + input.replace(/\\/g,'\\\\').replace(/"/g,'\\"') + '"';
     }-*/;

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
     * @param searchTerm
     *            the search term.
     */
    private void loadStream(final List<String> views, final String searchTerm)
    {
        // save for change detection
        loadedViews = new ArrayList<String>(views);
        loadedSearchTerm = searchTerm;

        Window.scrollTo(0, 0);
        noResults.addClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());
        Session.getInstance().getActionProcessor().setQueueRequests(true);

        addBookmark.setVisible(false);
        subscribeViaEmail.setVisible(false);
        feedLink.setVisible(false);

        streamOptionsPanel.getStyle().setDisplay(Display.BLOCK);
        streamDetailsComposite.init();

        errorPanel.clear();
        errorPanel.setVisible(false);

        AvatarBadgeManager.getInstance().clearBadges();

        singleActivityMode = false;
        activitySpinner.removeClassName(StaticResourceBundle.INSTANCE.coreCss().displayNone());
        moreLink.setVisible(false);
        streamPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().hidden());
        currentRequestObj = StreamJsonRequestFactory.getEmptyRequest();
        currentStream = new StreamScope(ScopeType.PERSON, Session.getInstance().getCurrentPerson().getAccountId());
        ShowRecipient showRecipient = ShowRecipient.YES;

        streamContainerPanel.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().hasOwnerRights());

        stickyActivityHolder.clear();
        UIObject.setVisible(stickyActivityArea, false);

        renderer.setShowStickActivity(false);

        currentStreamEntity = null;
        deferLoadAwaitingQueryBuilt = false;
        deferLoadAwaitingEntityReceived = false;

        //getEmailContactLink.setVisible(false);

        boolean streamIsAnEntity = false;

        if (views == null || views.size() == 0 || views.get(0).equals("following")
                || ((views.get(0).equals("sort") && (views.size() == 2))))
        {
            currentRequestObj = StreamJsonRequestFactory.setSourceAsFollowing(currentRequestObj);
            setAsActiveStream(followingFilterPanel);
            EventBus.getInstance().notifyObservers(new PostableStreamScopeChangeEvent(currentStream));
            feedLink.setVisible(true);
            streamDetailsComposite.setStreamTitle("Following", CustomAvatar.FOLLOWING);
            streamDetailsComposite.setCondensedMode(true);
            currentDisplayName = "Following";
        }
        else if (views.get(0).equals("person") && views.size() >= 2)
        {
            streamIsAnEntity = true;
            showRecipient = ShowRecipient.RESOURCE_ONLY;
            String accountId = views.get(1);
            currentRequestObj = StreamJsonRequestFactory.addRecipient(EntityType.PERSON, accountId, currentRequestObj);
            PersonalInformationModel.getInstance().fetch(accountId, false);
            currentStream.setScopeType(ScopeType.PERSON);
            currentStream.setUniqueKey(accountId);
            String mapKey = "person/" + accountId;
            setAsActiveStream(bookmarksWidgetMap.get(mapKey));
            if (!bookmarksWidgetMap.containsKey(mapKey))
            {
                addBookmark.setVisible(true);
            }
            else
            {
                addBookmark.setVisible(false);
                currentDisplayName = bookmarksWidgetMap.get(mapKey).getStreamName();
            }
            subscribeViaEmail.setVisible(true);
            feedLink.setVisible(true);
            streamDetailsComposite.setCondensedMode(false);

            PersonActivitySubscriptionModel.getInstance().fetch(currentStream.getUniqueKey(), true);
        }
        else if (views.get(0).equals("group") && views.size() >= 2)
        {
            streamIsAnEntity = true;
            showRecipient = ShowRecipient.RESOURCE_ONLY;
            String shortName = views.get(1);
            currentRequestObj = StreamJsonRequestFactory.addRecipient(EntityType.GROUP, shortName, currentRequestObj);
            GroupModel.getInstance().fetch(shortName, false);
            currentStream.setScopeType(ScopeType.GROUP);
            currentStream.setUniqueKey(shortName);
            String mapKey = "group/" + shortName;
            setAsActiveStream(bookmarksWidgetMap.get(mapKey));
            if (!bookmarksWidgetMap.containsKey(mapKey))
            {
                addBookmark.setVisible(true);
            }
            else
            {
                addBookmark.setVisible(false);
                currentDisplayName = bookmarksWidgetMap.get(mapKey).getStreamName();
            }
            subscribeViaEmail.setVisible(true);
            feedLink.setVisible(true);
            streamDetailsComposite.setCondensedMode(false);

            // Note: the links this will generate will only be visible if user is an admin/coordinator (via CSS)
            renderer.setShowStickActivity(true);

            deferLoadAwaitingEntityReceived = true;
            deferLoadAwaitingQueryBuilt = true;

            GroupActivitySubscriptionModel.getInstance().fetch(currentStream.getUniqueKey(), true);
        }
        else if (views.get(0).equals("custom") && views.size() >= 3)
        {
            currentRequestObj = StreamJsonRequestFactory.getJSONRequest(URL.decodeComponent(views.get(2)));
            setAsActiveStream(customStreamWidgetMap.get(Long.parseLong(views.get(1))));
            currentStream.setScopeType(null);
            EventBus.getInstance().notifyObservers(new PostableStreamScopeChangeEvent(currentStream));
            feedLink.setVisible(true);
            String streamName = customStreamWidgetMap.get(Long.parseLong(views.get(1))).getStreamName();
            streamDetailsComposite.setStreamTitle(streamName, CustomAvatar.FOLLOWING);
            streamDetailsComposite.setCondensedMode(true);
            currentDisplayName = streamName;
        }
        else if (views.get(0).equals("everyone"))
        {
            currentRequestObj = StreamJsonRequestFactory.getEmptyRequest();
            setAsActiveStream(everyoneFilterPanel);
            EventBus.getInstance().notifyObservers(new PostableStreamScopeChangeEvent(currentStream));
            feedLink.setVisible(true);
            streamDetailsComposite.setStreamTitle("Everyone", CustomAvatar.EVERYONE);
            streamDetailsComposite.setCondensedMode(true);
            currentDisplayName = "Everyone";
        }
        else if (views.size() == 1)
        {
            showRecipient = ShowRecipient.RESOURCE_ONLY;
            singleActivityMode = true;
        }

        if (searchTerm != null && searchTerm.length() > 0)
        {
            streamSearchStatusWidget.setSearchTerm(searchTerm);
            currentRequestObj = StreamJsonRequestFactory.setSearchTerm(searchTerm, currentRequestObj);
            searchContainer.addClassName(style.activeSearch());
            searchBox.setText(searchTerm);
        }
        else
        {
            streamSearchStatusWidget.onSearchCanceled();
            searchContainer.removeClassName(style.activeSearch());
        }

        if (!streamIsAnEntity)
        {
            streamDetailsComposite.setVisible(true);
        }
        renderer.setShowRecipientInStream(showRecipient);

        if (!singleActivityMode)
        {
            streamOptionsPanel.getStyle().setDisplay(Display.BLOCK);

            String sortBy = "recent";

            if (views != null && views.size() >= 2 && "sort".equals(views.get(views.size() - 2)))
            {
                sortBy = views.get(views.size() - 1);
            }

            recentSort.removeStyleName(style.activeSort());
            popularSort.removeStyleName(style.activeSort());
            activeSort.removeStyleName(style.activeSort());

            sortKeyword = "date";

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

            // see notes where field declared
            deferLoadAwaitingQueryBuilt = false;
            if (!deferLoadAwaitingEntityReceived)
            {
                fetchStream(currentRequestObj);
            }
        }
        else
        {
            streamDetailsComposite.setCondensedMode(false);
            streamOptionsPanel.getStyle().setDisplay(Display.NONE);
            postBox.setVisible(false);

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
     * Check if we should init.
     */
    private void checkInit()
    {
        if (bookmarksLoaded && customStreamsLoaded && !hasInited)
        {
            hasInited = true;
            handleViewsChanged(Session.getInstance().getUrlViews());
        }
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
     * @param isAvatar
     *            if the image is an avatar.
     * @return the LI.
     */
    private StreamNamePanel createPanel(final String name, final String view, final String imgUrl,
            final ClickHandler modifyClickHandler, final String modifyClass, final String modifyText,
            final boolean isAvatar)
    {
        StreamNamePanel panel = new StreamNamePanel(name);
        panel.addStyleName(style.streamOptionChild());
        panel.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                History.newItem(Session.getInstance().generateUrl(new CreateUrlRequest(Page.ACTIVITY, view)));
            }
        });

        FlowPanel innerPanel = new FlowPanel();

        Image streamImage = new Image(imgUrl);
        if (isAvatar)
        {
            streamImage.addStyleName(style.smallAvatar());
        }

        innerPanel.add(streamImage);

        Label streamName = new Label(name);
        streamName.setTitle(name);
        streamName.addStyleName(style.streamName());
        streamName.addStyleName(StaticResourceBundle.INSTANCE.coreCss().ellipsis());
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
