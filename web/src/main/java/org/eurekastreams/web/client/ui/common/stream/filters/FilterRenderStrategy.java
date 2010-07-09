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
package org.eurekastreams.web.client.ui.common.stream.filters;

import org.eurekastreams.server.action.request.stream.SetStreamFilterOrderRequest;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.web.client.model.Reorderable;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;

/**
 * Interface for filter render strategy.
 *
 */
public interface FilterRenderStrategy
{
    /**
     * Gets the token to look for in the history token to switch to the filter. (i.e. viewId, searchid. etc).
     *
     * @return the token.
     */
    String getFilterHistoryToken();

    /**
     * Gets the title of the list.
     *
     * @return the title.
     */
    String getTitle();

    /**
     * Sets up the events on the bus.
     *
     * @param listPanel
     *            the panel to tie events to.
     */
    void setUpEvents(FilterListPanel listPanel);

    /**
     * Gets the drag reorder action.
     *
     * @return the action key.
     */
    Reorderable<SetStreamFilterOrderRequest> getReorderableModel();

    /**
     * Gets the filter panel.
     *
     * @param filter
     *            the filter.
     * @return the filter panel.
     */
    FilterPanel getFilterPanel(StreamFilter filter);

    /**
     * Gets the create/edit dialog.
     *
     * @return the dialog.
     */
    DialogContent getDialogContent();
}
