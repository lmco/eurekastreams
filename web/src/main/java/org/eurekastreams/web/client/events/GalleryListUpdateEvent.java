/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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

import org.eurekastreams.server.domain.GalleryItemType;
import org.eurekastreams.server.domain.PagedSet;

/**
 * Gallery update event.
 * 
 * @param <T>
 *            type of gallery item.
 */
public class GalleryListUpdateEvent<T>
{
    /**
     * The type of update.
     */
    private GalleryItemType itemType;

    /**
     * The gallery items.
     */
    private PagedSet<T> items;

    /**
     * Constructor.
     * 
     * @param inItems
     *            the items.
     * @param inItemType
     *            the type of gallery items.
     */
    public GalleryListUpdateEvent(final PagedSet<T> inItems, final GalleryItemType inItemType)
    {
        items = inItems;
        itemType = inItemType;
    }

    /**
     * @return the item associated with the event.
     */
    public PagedSet<T> getItems()
    {
        return items;
    }

    /**
     * @param inItemType
     *            the itemType to set
     */
    public void setUpdateType(final GalleryItemType inItemType)
    {
        this.itemType = inItemType;
    }

    /**
     * @return the itemType
     */
    public GalleryItemType getItemType()
    {
        return itemType;
    }
}
