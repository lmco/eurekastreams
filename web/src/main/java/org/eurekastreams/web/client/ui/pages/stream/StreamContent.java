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

import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamPageLoadedEvent;
import org.eurekastreams.web.client.events.StreamViewsLoadedEvent;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.events.UserLoggedInEvent;
import org.eurekastreams.web.client.events.data.GotCurrentUserCustomStreamsResponseEvent;
import org.eurekastreams.web.client.events.data.GotCurrentUserGroupStreamsResponseEvent;
import org.eurekastreams.web.client.model.CustomStreamModel;
import org.eurekastreams.web.client.model.GroupStreamListModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.stream.StreamPanel;
import org.eurekastreams.web.client.ui.common.stream.filters.FilterListPanel;
import org.eurekastreams.web.client.ui.common.stream.filters.group.GroupStreamRenderer;
import org.eurekastreams.web.client.ui.common.stream.filters.list.CustomStreamRenderer;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
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
    private FlowPanel panel = null;

    /**
     * The stream view.
     */
    private StreamPanel streamView = null;

    /**
     * The filters div.
     */
    private FlowPanel filters = new FlowPanel();

    /**
     * Error label.
     */
    Label errorLabel = new Label("You must be logged in to view this page.");

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
     * The stream view list panel.
     */
    private FilterListPanel streanViewListWidget = null;

    /**
     * Default constructor.
     * 
     */
    public StreamContent()
    {
        panel = new FlowPanel();
        initWidget(panel);

        filters.addStyleName("filters");
        errorLabel.addStyleName("form-error-box");
        errorLabel.setVisible(false);

        streamView = new StreamPanel(true);
        streamPanel.add(streamView);

        RootPanel.get().addStyleName("stream");

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
                        if (event.getParameters().get("streamId") == null
                                && event.getParameters().get("activityId") == null
                                && event.getParameters().get("groupId") == null)
                        {
                            selectFirstView = true;

                            if (null != streanViewListWidget)
                            {
                                streanViewListWidget.activateFilter(streanViewListWidget.getViews().get(0));
                            }
                        }
                    }

                }, true);

        Session.getInstance().getEventBus().addObserver(GotCurrentUserCustomStreamsResponseEvent.class,
                new Observer<GotCurrentUserCustomStreamsResponseEvent>()
                {

                    public void update(final GotCurrentUserCustomStreamsResponseEvent event)
                    {
                        streanViewListWidget = new FilterListPanel(event.getResponse().getStreamFilters(), event
                                .getResponse().getHiddenLineIndex(), new CustomStreamRenderer(), false);

                        listContainer.add(streanViewListWidget);

                        if (selectFirstView)
                        {
                            streanViewListWidget.activateFilter(event.getResponse().getStreamFilters().get(0));
                        }
                        Session.getInstance().getEventBus().notifyObservers(
                                new StreamViewsLoadedEvent(event.getResponse().getStreamFilters()));

                        streamView.setStreamScope(new StreamScope(ScopeType.PERSON, Session.getInstance()
                                .getCurrentPerson().getAccountId()), true);
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

        filters.add(listContainer);
        filters.add(new HTML("<br />"));
        filters.add(groupContainer);

        panel.add(errorLabel);
        panel.add(filters);
        panel.add(streamPanel);

        DeferredCommand.addCommand(new Command()
        {
            public void execute()
            {
                Session.getInstance().getActionProcessor().setQueueRequests(true);
                CustomStreamModel.getInstance().fetch(null, true);
                GroupStreamListModel.getInstance().fetch(null, false);
                Session.getInstance().getActionProcessor().setQueueRequests(false);
                Session.getInstance().getActionProcessor().fireQueuedRequests();
            }
        });
    }
}
