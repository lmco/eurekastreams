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
package org.eurekastreams.server.persistence;

import org.eurekastreams.server.domain.PagedSet;

/**
 * This interface specifies the mapper operations for any class that can map gallery items.
 * @param <T>
 *            the type of gallery item mapped
 */
public interface GalleryItemMapper<T>
{
    /**
     * Look up the theme identified by this URL.
     * 
     * @param inGalleryItemUrl
     *            URL of the XML file that defines the gallery item
     * @return the Theme specified by the URL
     */
    T findByUrl(final String inGalleryItemUrl);

    /**
     * Finds gadget definitions of a specified category sorted by popularity.
     * 
     * @param inCategory
     *            The category to which the gadget defs must belong.
     * @param inStart
     *            paging inStart.
     * @param inEnd
     *            paging inEnd.
     * @return a list of gadget def
     */
    PagedSet<T> findForCategorySortedByPopularity(
            final String inCategory, final int inStart, final int inEnd);

    /**
     * Finds all gadget definitions sorted by popularity.
     * 
     * @param inStart
     *            paging inStart.
     * @param inEnd
     *            paging inEnd.
     * @return a list of gadget def
     */
    PagedSet<T> findSortedByPopularity(
            final int inStart, final int inEnd);

    /**
     * Finds gadget definitions of a specified category sorted by most recent.
     * 
     * @param inCategory
     *            The category to which the gadget defs must belong.
     * @param inStart
     *            paging inStart.
     * @param inEnd
     *            paging inEnd.
     * @return a list of gadget def
     */
    PagedSet<T> findForCategorySortedByRecent(
            final String inCategory, final int inStart, final int inEnd);

    /**
     * Finds all gadget definitions sorted by most recent.
     * 
     * @param inStart
     *            paging inStart.
     * @param inEnd
     *            paging inEnd.
     * @return a list of gadget def
     */
    PagedSet<T> findSortedByRecent(
            final int inStart, final int inEnd);
    
    /**
     * Find the gallery item by id.
     * 
     * @param galleryItemId
     *            ID of the entity to look up
     * 
     * @return the entity with the input
     */
    T findById(final Long galleryItemId);
    
    /**
     * Delete a gallery item.
     * 
     * @param inGalleryItem
     *            The gallery item to delete.
     */
    void delete(final T inGalleryItem);
    
    /**
     * Insert the domain entity.
     * 
     * @param domainEntity
     *            The domainEntity to operate on.
     */
    void insert(final T domainEntity);
    
    /**
     * Flushes the mapper.
     */
    void flush();
    
    /**
     * Refresh the gallery item.
     * @param inGalleryItem the gallery item.
     */
    void refresh(final T inGalleryItem);
}
