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

import org.eurekastreams.server.domain.stream.StreamFilter;

import com.google.gwt.user.client.ui.Label;

/**
 * Interface for a filter panel.
 *
 */
public interface FilterPanel
{
    /**
     * Returns the move handle.
     * @return the move handle.
     */
    Label getMoveHandle();
    /**
     * Activates the view item.
     */
    void activate();

    /**
     * Unactivates the item.
     */
    void unActivate();

    /**
     * Updates the history.
     */
    void updateHistory();

    /**
     * Sets the filter.
     * @param inView the filter.
     */
    void setFilter(final StreamFilter inView);

    /**
     * Returns the filter.
     * @return the filter.
     */
    StreamFilter getFilter();

    /**
     * Gets the filters id.
     * @return the id.
     */
    Long getItemId();
}
