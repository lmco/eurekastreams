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
package org.eurekastreams.web.client.ui.common.stream.filters.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.server.action.request.stream.SetStreamFilterOrderRequest;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.StreamSearchBeginEvent;
import org.eurekastreams.web.client.events.StreamSearchCreatedEvent;
import org.eurekastreams.web.client.events.StreamSearchDeletedEvent;
import org.eurekastreams.web.client.events.StreamSearchUpdatedEvent;
import org.eurekastreams.web.client.events.StreamViewCreatedEvent;
import org.eurekastreams.web.client.events.StreamViewDeletedEvent;
import org.eurekastreams.web.client.events.StreamViewUpdatedEvent;
import org.eurekastreams.web.client.events.StreamViewsLoadedEvent;
import org.eurekastreams.web.client.events.SwitchedToActivityDetailViewEvent;
import org.eurekastreams.web.client.events.SwitchedToGroupStreamEvent;
import org.eurekastreams.web.client.events.SwitchedToSavedSearchEvent;
import org.eurekastreams.web.client.events.SwitchedToStreamViewEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.data.GotCurrentUserGroupStreamsResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.Reorderable;
import org.eurekastreams.web.client.model.StreamSearchListModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.common.stream.filters.FilterListPanel;
import org.eurekastreams.web.client.ui.common.stream.filters.FilterPanel;
import org.eurekastreams.web.client.ui.common.stream.filters.FilterRenderStrategy;

/**
 * Renderer for stream searches.
 * 
 */
public class StreamSearchRenderer implements FilterRenderStrategy
{
    /**
     * The views.
     */
    private List<StreamFilter> views = new ArrayList<StreamFilter>();

    /**
     * The collection of all the search objects.
     */
    private List<StreamSearch> searches = new ArrayList<StreamSearch>();

    /**
     * The search list panel.
     */
    private FilterListPanel listPanel;

    /**
     * Default constructor. Wait for the user to load up their views so we can buffer them here.
     */
    public StreamSearchRenderer()
    {
        // adds user's stream views to the views lists (will be shown in "new stream search" dialog dropdown)
        EventBus.getInstance().addObserver(StreamViewsLoadedEvent.getEvent(), new Observer<StreamViewsLoadedEvent>()
        {

            public void update(final StreamViewsLoadedEvent event)
            {
                views.addAll(event.getViews());
            }
        });

        // adds user's group streams to the views lists (will be shown in "new stream search" dialog dropdown)
        Session.getInstance().getEventBus().addObserver(GotCurrentUserGroupStreamsResponseEvent.class,
                new Observer<GotCurrentUserGroupStreamsResponseEvent>()
                {
                    public void update(final GotCurrentUserGroupStreamsResponseEvent event)
                    {
                        views.addAll(event.getResponse().getStreamFilters());
                    }
                });

        Session.getInstance().getEventBus().addObserver(StreamViewCreatedEvent.class,
                new Observer<StreamViewCreatedEvent>()
                {
                    public void update(final StreamViewCreatedEvent event)
                    {
                        views.add(event.getView());
                    }
                });

        Session.getInstance().getEventBus().addObserver(StreamViewDeletedEvent.class,
                new Observer<StreamViewDeletedEvent>()
                {
                    public void update(final StreamViewDeletedEvent event)
                    {
                        int index = -1;
                        for (int i = 0; i < views.size(); i++)
                        {
                            if (event.getView().getId() == views.get(i).getId())
                            {
                                index = i;
                                break;
                            }
                        }

                        if (index >= 0)
                        {
                            views.remove(index);
                        }

                        // removes any matching stream searches that referred to this deleted streamview
                        for (StreamSearch search : searches)
                        {
                            if (search != null && search.getStreamView() != null
                                    && search.getStreamView().getId() == event.getView().getId())
                            {
                                listPanel.removeFilter(search);
                            }
                        }
                    }
                });

        Session.getInstance().getEventBus().addObserver(StreamViewUpdatedEvent.class,
                new Observer<StreamViewUpdatedEvent>()
                {
                    public void update(final StreamViewUpdatedEvent event)
                    {
                        int index = -1;
                        for (int i = 0; i < views.size(); i++)
                        {
                            if (event.getView().getId() == views.get(i).getId())
                            {
                                index = i;
                                break;
                            }
                        }

                        if (index >= 0)
                        {
                            views.remove(index);
                            views.add(index, event.getView());
                        }
                    }
                });
    }

    /**
     * Gets the title of the list.
     * 
     * @return the title.
     */
    public String getTitle()
    {
        return "Saved Searches";
    }

    /**
     * Gets the create/edit dialog.
     * 
     * @return the dialog.
     */
    public DialogContent getDialogContent()
    {
        return new StreamSearchDialogContent(views);
    }

    /**
     * Gets the drag reorder action.
     * 
     * @return the action key.
     */
    public Reorderable<SetStreamFilterOrderRequest> getReorderableModel()
    {
        return StreamSearchListModel.getInstance();
    }

    /**
     * Gets the drag reorder action.
     * 
     * @return the action key.
     */
    public String getDragKey()
    {
        return "setStreamSearchOrder";
    }

    /**
     * Gets the filter panel.
     * 
     * @param filter
     *            the filter.
     * @return the filter panel.
     */
    public FilterPanel getFilterPanel(final StreamFilter filter)
    {
        StreamSearch search = (StreamSearch) filter;
        searches.add(search);
        return new StreamSearchPanel(search, this);
    }

    /**
     * Gets the edit dialog.
     * 
     * @param search
     *            the search.
     * @return the edit dialog.
     */
    public StreamSearchDialogContent getEditDialog(final StreamSearch search)
    {
        return new StreamSearchDialogContent(search, views);
    }

    /**
     * Sets up the events on the bus.
     * 
     * @param inListPanel
     *            the panel to tie events to.
     */
    public void setUpEvents(final FilterListPanel inListPanel)
    {
        listPanel = inListPanel;

        EventBus.getInstance().addObserver(SwitchedToSavedSearchEvent.getEvent(),
                new Observer<SwitchedToSavedSearchEvent>()
                {
                    public void update(final SwitchedToSavedSearchEvent arg1)
                    {
                        listPanel.switchToFilter(arg1.getSearch());
                    }
                });

        EventBus.getInstance().addObserver(StreamSearchCreatedEvent.getEvent(),
                new Observer<StreamSearchCreatedEvent>()
                {
                    public void update(final StreamSearchCreatedEvent event)
                    {

                        listPanel.addFilter(event.getSearch());
                        EventBus.getInstance().notifyObservers(
                                new ShowNotificationEvent(new Notification("Your search has been successfully saved")));

                        // updates the url to be the newly created search
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("searchId", String.valueOf(event.getSearch().getId()));
                        params.put("streamSearch", String.valueOf(event.getSearch().getKeywordsAsString()));
                        params.put("viewId", String.valueOf(event.getSearch().getStreamView().getId()));

                        Session.getInstance().getEventBus().notifyObservers(
                                new UpdateHistoryEvent(new CreateUrlRequest(params, false)));

                        Session.getInstance().getEventBus().notifyObservers(
                                new StreamSearchBeginEvent(event.getSearch().getKeywordsAsString(), event.getSearch()));
                    }
                });

        EventBus.getInstance().addObserver(StreamSearchUpdatedEvent.getEvent(),
                new Observer<StreamSearchUpdatedEvent>()
                {
                    public void update(final StreamSearchUpdatedEvent event)
                    {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("streamSearch", event.getSearch().getKeywordsAsString());
                        params.put("viewId", String.valueOf(event.getSearch().getStreamView().getId()));

                        Session.getInstance().getEventBus().notifyObservers(
                                new UpdateHistoryEvent(new CreateUrlRequest(params, false)));

                        Session.getInstance().getEventBus().notifyObservers(
                                new StreamSearchBeginEvent(event.getSearch().getKeywordsAsString(), event.getSearch()));

                        listPanel.updateFilter(event.getSearch());
                        EventBus.getInstance().notifyObservers(
                                new ShowNotificationEvent(new Notification("Your search has been successfully saved")));
                    }
                });

        EventBus.getInstance().addObserver(SwitchedToStreamViewEvent.getEvent(),
                new Observer<SwitchedToStreamViewEvent>()
                {
                    public void update(final SwitchedToStreamViewEvent arg1)
                    {
                        listPanel.unactivateAll();
                    }
                });

        EventBus.getInstance().addObserver(SwitchedToGroupStreamEvent.getEvent(),
                new Observer<SwitchedToGroupStreamEvent>()
                {
                    public void update(final SwitchedToGroupStreamEvent arg1)
                    {
                        listPanel.unactivateAll();
                    }
                });

        EventBus.getInstance().addObserver(StreamSearchDeletedEvent.getEvent(),
                new Observer<StreamSearchDeletedEvent>()
                {
                    public void update(final StreamSearchDeletedEvent arg1)
                    {
                        listPanel.removeFilter(arg1.getSearch());
                        EventBus.getInstance().notifyObservers(
                                new ShowNotificationEvent(new Notification("The search has been deleted")));
                    }
                });

        EventBus.getInstance().addObserver(SwitchedToActivityDetailViewEvent.class,
                new Observer<SwitchedToActivityDetailViewEvent>()
                {
                    public void update(final SwitchedToActivityDetailViewEvent arg1)
                    {
                        listPanel.unactivateAll();
                    }
                });
    }

    /**
     * Look for searchId in the token.
     * 
     * @return the token.
     */
    public String getFilterHistoryToken()
    {
        return "searchId";
    }

}
