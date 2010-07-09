/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.stream.filters.group;

import org.eurekastreams.server.action.request.stream.SetStreamFilterOrderRequest;
import org.eurekastreams.server.domain.stream.GroupStreamDTO;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.SwitchedToActivityDetailViewEvent;
import org.eurekastreams.web.client.events.SwitchedToGroupStreamEvent;
import org.eurekastreams.web.client.events.SwitchedToSavedSearchEvent;
import org.eurekastreams.web.client.events.SwitchedToStreamViewEvent;
import org.eurekastreams.web.client.model.GroupStreamListModel;
import org.eurekastreams.web.client.model.Reorderable;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;
import org.eurekastreams.web.client.ui.common.stream.filters.FilterListPanel;
import org.eurekastreams.web.client.ui.common.stream.filters.FilterPanel;
import org.eurekastreams.web.client.ui.common.stream.filters.FilterRenderStrategy;

/**
 * Renderer for group streams.
 *
 */
public class GroupStreamRenderer implements FilterRenderStrategy
{
    /**
     * Gets the title of the list.
     *
     * @return the title.
     */
    public String getTitle()
    {
        return "Groups";
    }

    /**
     * Gets the model.
     *
     * @return the model.
     */
    public Reorderable<SetStreamFilterOrderRequest> getReorderableModel()
    {
        return GroupStreamListModel.getInstance();
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
        return new GroupStreamPanel((GroupStreamDTO) filter, this);
    }

    /**
     * Sets up the events on the bus.
     *
     * @param listPanel
     *            the panel to tie events to.
     */
    public void setUpEvents(final FilterListPanel listPanel)
    {
        EventBus.getInstance().addObserver(SwitchedToGroupStreamEvent.getEvent(),
                new Observer<SwitchedToGroupStreamEvent>()
                {
                    public void update(final SwitchedToGroupStreamEvent arg1)
                    {
                        listPanel.switchToFilter(arg1.getView());
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

        EventBus.getInstance().addObserver(SwitchedToStreamViewEvent.getEvent(),
                new Observer<SwitchedToStreamViewEvent>()
                {
                    public void update(final SwitchedToStreamViewEvent arg1)
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
    }

    /**
     * Gets the create/edit dialog. Returning null here makes the create/edit dialog not available (no plus sign in the
     * UI)
     *
     * @return the dialog.
     */
    public DialogContent getDialogContent()
    {
        return null;
    }

    /**
     * Look for searchId in the token.
     *
     * @return the token.
     */
    public String getFilterHistoryToken()
    {
        return "groupId";
    }

}
