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
package org.eurekastreams.web.client.ui.common.stream.filters.list;

import org.eurekastreams.server.action.request.stream.SetStreamFilterOrderRequest;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.StreamSearchDeletedEvent;
import org.eurekastreams.web.client.events.StreamViewCreatedEvent;
import org.eurekastreams.web.client.events.StreamViewDeletedEvent;
import org.eurekastreams.web.client.events.StreamViewUpdatedEvent;
import org.eurekastreams.web.client.events.SwitchedToActivityDetailViewEvent;
import org.eurekastreams.web.client.events.SwitchedToGroupStreamEvent;
import org.eurekastreams.web.client.events.SwitchedToSavedSearchEvent;
import org.eurekastreams.web.client.events.SwitchedToStreamViewEvent;
import org.eurekastreams.web.client.model.Reorderable;
import org.eurekastreams.web.client.model.StreamViewListModel;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.common.stream.filters.FilterListPanel;
import org.eurekastreams.web.client.ui.common.stream.filters.FilterPanel;
import org.eurekastreams.web.client.ui.common.stream.filters.FilterRenderStrategy;

/**
 * Renderer for stream views.
 *
 */
public class StreamViewRenderer implements FilterRenderStrategy
{
    /**
     * Gets the title of the list.
     *
     * @return the title.
     */
    public String getTitle()
    {
        return "Lists";
    }

    /**
     * Gets the drag reorder action.
     *
     * @return the action key.
     */
    public Reorderable<SetStreamFilterOrderRequest> getReorderableModel()
    {
        return StreamViewListModel.getInstance();
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
        return new StreamViewPanel((StreamView) filter);
    }

    /**
     * Sets up the events on the bus.
     *
     * @param listPanel
     *            the panel to tie events to.
     */
    public void setUpEvents(final FilterListPanel listPanel)
    {
        EventBus.getInstance().addObserver(SwitchedToStreamViewEvent.getEvent(),
                new Observer<SwitchedToStreamViewEvent>()
                {
                    public void update(final SwitchedToStreamViewEvent arg1)
                    {
                        listPanel.switchToFilter(arg1.getView());
                    }
                });

        EventBus.getInstance().addObserver(StreamViewCreatedEvent.getEvent(), new Observer<StreamViewCreatedEvent>()
        {
            public void update(final StreamViewCreatedEvent arg1)
            {
                listPanel.addFilter(arg1.getView());
                EventBus.getInstance().notifyObservers(new ShowNotificationEvent(
                        new Notification("Your list has been successfully saved")));
            }
        });

        EventBus.getInstance().addObserver(StreamViewDeletedEvent.getEvent(), new Observer<StreamViewDeletedEvent>()
        {
            public void update(final StreamViewDeletedEvent arg1)
            {
                listPanel.removeFilter(arg1.getView());
                EventBus.getInstance().notifyObservers(new ShowNotificationEvent(
                        new Notification("The list and associated searches have been deleted")));
            }
        });

        EventBus.getInstance().addObserver(StreamViewUpdatedEvent.getEvent(), new Observer<StreamViewUpdatedEvent>()
        {
            public void update(final StreamViewUpdatedEvent arg1)
            {
                listPanel.updateFilter(arg1.getView());
                EventBus.getInstance().notifyObservers(new ShowNotificationEvent(
                        new Notification("Your list has been successfully saved")));
            }
        });

        EventBus.getInstance().addObserver(SwitchedToSavedSearchEvent.getEvent(),
                new Observer<SwitchedToSavedSearchEvent>()
                {
                    public void update(final SwitchedToSavedSearchEvent arg1)
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

        EventBus.getInstance().addObserver(SwitchedToActivityDetailViewEvent.class,
                new Observer<SwitchedToActivityDetailViewEvent>()
                {
                    public void update(final SwitchedToActivityDetailViewEvent arg1)
                    {
                        listPanel.unactivateAll();
                    }
                });
        
        // activates the first item in the Lists section when a search is deleted - necessary to put this here
        // since there will always be at least one list but not always one search.
        EventBus.getInstance().addObserver(StreamSearchDeletedEvent.getEvent(),
                new Observer<StreamSearchDeletedEvent>()
                {
                    public void update(final StreamSearchDeletedEvent arg1)
                    {
                        listPanel.activateFirst();
                    }
                });

    }

    /**
     * Gets the create/edit dialog.
     *
     * @return the dialog.
     */
    public DialogContent getDialogContent()
    {
        return new StreamViewDialogContent();
    }

    /**
     * Look for viewId in the history token.
     *
     * @return the token.
     */
    public String getFilterHistoryToken()
    {
        return "listId";
    }

}
