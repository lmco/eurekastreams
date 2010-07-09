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
package org.eurekastreams.web.client.events;

/**
 * Switch to a filter on a paged filter control event.
 *
 */
public class SwitchToFilterOnPagedFilterPanelEvent
{
    /**
     * The list id.
     */
    private String listId;
    /**
     * Name of the filter.
     */
    private String name = "";
    /**
     * Sort key.
     */
    private String sortKey = "";

    /**
     * Constructor that only has name.
     * @param inListId list id.
     * @param inName name
     * .
     */
    public SwitchToFilterOnPagedFilterPanelEvent(final String inListId, final String inName)
    {
        listId = inListId;
        name = inName;
    }

    /**
     * Constructor that has name and sort key.
     * @param inListId list id.
     * @param inName name.
     * @param inSortKey sortKey.
     */
    public SwitchToFilterOnPagedFilterPanelEvent(final String inListId, final String inName, final String inSortKey)
    {
        listId = inListId;
        name = inName;
        sortKey = inSortKey;
    }

    /**
     * Get sort key.
     * @return sort key.
     */
    public String getSortKey()
    {
        return sortKey;
    }

    /**
     * Get filter name.
     * @return filter name.
     */
    public String getFilterName()
    {
        return name;
    }

    /**
     * Get list id.
     * @return list id.
     */
    public String getListId()
    {
        return listId;
    }
}
