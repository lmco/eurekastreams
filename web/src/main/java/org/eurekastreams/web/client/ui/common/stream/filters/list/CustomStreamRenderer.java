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
package org.eurekastreams.web.client.ui.common.stream.filters.list;

import org.eurekastreams.server.action.request.stream.SetStreamOrderRequest;
import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.web.client.events.CustomStreamCreatedEvent;
import org.eurekastreams.web.client.events.CustomStreamDeletedEvent;
import org.eurekastreams.web.client.events.CustomStreamUpdatedEvent;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.model.Reorderable;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.common.stream.filters.FilterListPanel;
import org.eurekastreams.web.client.ui.common.stream.filters.FilterPanel;
import org.eurekastreams.web.client.ui.common.stream.filters.FilterRenderStrategy;

/**
 * Renderer for stream views.
 * 
 */
public class CustomStreamRenderer implements FilterRenderStrategy
{
    /**
     * No-op reorderable model.
     */
    private NoOpReorderableModel reorderable = new NoOpReorderableModel();

    /**
     * Gets the title of the list.
     * 
     * @return the title.
     */
    public String getTitle()
    {
        return "Streams";
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
        return new CustomStreamPanel((Stream) filter);
    }

    /**
     * Sets up the events on the bus.
     * 
     * @param listPanel
     *            the panel to tie events to.
     */
    public void setUpEvents(final FilterListPanel listPanel)
    {
        EventBus.getInstance().addObserver(CustomStreamCreatedEvent.getEvent(),
                new Observer<CustomStreamCreatedEvent>()
                {
                    public void update(final CustomStreamCreatedEvent arg1)
                    {
                        listPanel.addFilter(arg1.getStream());
                        EventBus.getInstance().notifyObservers(
                                new ShowNotificationEvent(new Notification("Your stream has been successfully saved")));
                    }
                });

        EventBus.getInstance().addObserver(CustomStreamDeletedEvent.getEvent(),
                new Observer<CustomStreamDeletedEvent>()
                {
                    public void update(final CustomStreamDeletedEvent arg1)
                    {
                        listPanel.removeFilter(arg1.getStream());
                        EventBus.getInstance().notifyObservers(
                                new ShowNotificationEvent(new Notification("The stream has been deleted")));
                    }
                });

        EventBus.getInstance().addObserver(CustomStreamUpdatedEvent.getEvent(),
                new Observer<CustomStreamUpdatedEvent>()
                {
                    public void update(final CustomStreamUpdatedEvent arg1)
                    {
                        listPanel.updateFilter(arg1.getStream());
                        EventBus.getInstance().notifyObservers(
                                new ShowNotificationEvent(new Notification("Your stream has been successfully saved")));
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
        return new CustomStreamDialogContent();
    }

    /**
     * Look for viewId in the history token.
     * 
     * @return the token.
     */
    public String getFilterHistoryToken()
    {
        return "streamId";
    }

    /**
     * No reordering of custom streams.
     * 
     * @return a no-op reorderable
     */
    public Reorderable<SetStreamOrderRequest> getReorderableModel()
    {
        return reorderable;
    }

}
