/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.pages.stream;

import org.eurekastreams.web.client.events.ChangeShowStreamRecipientEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamPageLoadedEvent;
import org.eurekastreams.web.client.events.StreamViewsLoadedEvent;
import org.eurekastreams.web.client.events.SwitchedToGroupStreamEvent;
import org.eurekastreams.web.client.events.SwitchedToSavedSearchEvent;
import org.eurekastreams.web.client.events.SwitchedToStreamViewEvent;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.events.UserLoggedInEvent;
import org.eurekastreams.web.client.events.data.GotCurrentUserGroupStreamsResponseEvent;
import org.eurekastreams.web.client.events.data.GotCurrentUserStreamSearchesResponseEvent;
import org.eurekastreams.web.client.events.data.GotCurrentUserStreamViewsResponseEvent;
import org.eurekastreams.web.client.model.GroupStreamListModel;
import org.eurekastreams.web.client.model.StreamSearchListModel;
import org.eurekastreams.web.client.model.StreamViewListModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.stream.StreamPanel;
import org.eurekastreams.web.client.ui.common.stream.filters.FilterListPanel;
import org.eurekastreams.web.client.ui.common.stream.filters.group.GroupStreamRenderer;
import org.eurekastreams.web.client.ui.common.stream.filters.list.StreamViewRenderer;
import org.eurekastreams.web.client.ui.common.stream.filters.search.StreamSearchRenderer;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Page for Stream.
 * 
 */
public class StreamContent extends Composite
{
    /**
     * The div where the stream lives.
     */
    private FlowPanel streamPanel = new FlowPanel();
    /**
     * The main panel.
     */
    private FlowPanel panel = new FlowPanel();

    /**
     * The stream view.
     */
    private StreamPanel streamView = null;

    /**
     * The filters div.
     */
    private FlowPanel filters = new FlowPanel();

    /**
     * The session.
     */
    private Session session = Session.getInstance();

    /**
     * Error label.
     */
    Label errorLabel = new Label("You must be logged in to view this page.");

    /**
     * The search renderer strategy. This registers an event to listen for when the views load, so it has to be
     * instantiated up here.
     */
    private StreamSearchRenderer searchRenderer = new StreamSearchRenderer();

    /**
     * Select the first view.
     */
    private Boolean selectFirstView = false;

    /**
     * List container.
     */
    private FlowPanel listContainer = new FlowPanel();
    /**
     * Group container.
     */
    private FlowPanel groupContainer = new FlowPanel();
    /**
     * Search container.
     */
    private FlowPanel searchContainer = new FlowPanel();

    /**
     * The stream view list panel.
     */
    private FilterListPanel streanViewListWidget = null;

    /**
     * Default constructor.
     * 
     */
    public StreamContent()
    {
        filters.addStyleName("filters");
        errorLabel.addStyleName("form-error-box");
        errorLabel.setVisible(false);

        streamView = new StreamPanel();
        streamPanel.add(streamView);

        Session.getInstance().getEventBus().addObserver(SwitchedToSavedSearchEvent.class,
                new Observer<SwitchedToSavedSearchEvent>()
                {
                    public void update(final SwitchedToSavedSearchEvent event)
                    {
                        Session.getInstance().getEventBus().notifyObservers(new ChangeShowStreamRecipientEvent(true));
                    }
                });

        Session.getInstance().getEventBus().addObserver(SwitchedToStreamViewEvent.class,
                new Observer<SwitchedToStreamViewEvent>()
                {
                    public void update(final SwitchedToStreamViewEvent event)
                    {
//                        streamView.setPostScope(new StreamScope(ScopeType.PERSON, session.getCurrentPerson()
//                                .getAccountId()));
//                        streamView.setView(event.getView());
//                        streamView.setPostable(true);

                        Session.getInstance().getEventBus().notifyObservers(new ChangeShowStreamRecipientEvent(true));
                    }
                });

        Session.getInstance().getEventBus().addObserver(SwitchedToGroupStreamEvent.class,
                new Observer<SwitchedToGroupStreamEvent>()
                {
                    public void update(final SwitchedToGroupStreamEvent event)
                    {
//                        GroupStreamDTO group = event.getView();
//                        streamView.setPostable(group.isPostable());
//                        streamView.setView(event.getView().getStreamView());

                        Session.getInstance().getEventBus().notifyObservers(new ChangeShowStreamRecipientEvent(false));
                    }
                });

        RootPanel.get().addStyleName("stream");

        // Label activityHeader = new Label("Activity");
        // activityHeader.addStyleName("directory-header");
        // panel.add(activityHeader);
        panel.addStyleName("stream-page-container");
        streamPanel.addStyleName("stream-container");

        Session.getInstance().getEventBus().addObserver(UserLoggedInEvent.class, new Observer<UserLoggedInEvent>()
        {
            public void update(final UserLoggedInEvent event)
            {
                Session.getInstance().getEventBus().notifyObservers(StreamPageLoadedEvent.getEvent());
            }
        });

        Session.getInstance().getEventBus().addObserver(UpdatedHistoryParametersEvent.class,
                new Observer<UpdatedHistoryParametersEvent>()
                {

                    public void update(final UpdatedHistoryParametersEvent event)
                    {
                        if (event.getParameters().get("viewId") == null
                                && event.getParameters().get("activityId") == null
                                && event.getParameters().get("groupId") == null
                                && event.getParameters().get("streamSearch") == null)
                        {
                            selectFirstView = true;

                            if (null != streanViewListWidget)
                            {
                                streanViewListWidget.activateFilter(streanViewListWidget.getViews().get(0));
                            }
                        }
                    }

                }, true);

        Session.getInstance().getEventBus().addObserver(GotCurrentUserStreamViewsResponseEvent.class,
                new Observer<GotCurrentUserStreamViewsResponseEvent>()
                {

                    public void update(final GotCurrentUserStreamViewsResponseEvent event)
                    {
                        streanViewListWidget = new FilterListPanel(event.getResponse().getStreamFilters(), event
                                .getResponse().getHiddenLineIndex(), new StreamViewRenderer(), false);

                        listContainer.add(streanViewListWidget);

                        if (selectFirstView)
                        {
                            streanViewListWidget.activateFilter(event.getResponse().getStreamFilters().get(0));
                        }
                        Session.getInstance().getEventBus().notifyObservers(
                                new StreamViewsLoadedEvent(event.getResponse().getStreamFilters()));
                    }

                });

        Session.getInstance().getEventBus().addObserver(GotCurrentUserGroupStreamsResponseEvent.class,
                new Observer<GotCurrentUserGroupStreamsResponseEvent>()
                {
                    public void update(final GotCurrentUserGroupStreamsResponseEvent event)
                    {
                        FilterListPanel viewListWidget = new FilterListPanel(event.getResponse().getStreamFilters(),
                                event.getResponse().getHiddenLineIndex(), new GroupStreamRenderer(), true);

                        groupContainer.add(viewListWidget);
                    }
                });

        Session.getInstance().getEventBus().addObserver(GotCurrentUserStreamSearchesResponseEvent.class,
                new Observer<GotCurrentUserStreamSearchesResponseEvent>()
                {

                    public void update(final GotCurrentUserStreamSearchesResponseEvent event)
                    {
                        FilterListPanel viewListWidget = new FilterListPanel(event.getResponse().getStreamFilters(),
                                event.getResponse().getHiddenLineIndex(), searchRenderer, false);

                        searchContainer.add(viewListWidget);
                    }

                });

        filters.add(listContainer);
        filters.add(new HTML("<br />"));
        filters.add(groupContainer);
        filters.add(new HTML("<br />"));
        filters.add(searchContainer);

        panel.add(errorLabel);
        panel.add(filters);
        panel.add(streamPanel);

        initWidget(panel);

        DeferredCommand.addCommand(new Command()
        {
            public void execute()
            {
                Session.getInstance().getActionProcessor().setQueueRequests(true);
                StreamViewListModel.getInstance().fetch(null, true);
                GroupStreamListModel.getInstance().fetch(null, false);
                StreamSearchListModel.getInstance().fetch(null, true);
                Session.getInstance().getActionProcessor().setQueueRequests(false);
                Session.getInstance().getActionProcessor().fireQueuedRequests();
            }
        });

    }
}
