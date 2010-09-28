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
package org.eurekastreams.server.service.actions;


import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.persistence.GalleryItemMapper;

/**
 * Document creator, used by AddThemeAction.
 */
public class ThemeMapperFake implements GalleryItemMapper
{
    /**
     * the theme that is passed into the insert method.
     */
    private Theme theme;
    
    /**
     * Fakes inserting a theme and simply stores the theme in a public attribute.
     * 
     * @param inTheme
     *            the theme
     */
    public void insert(final Theme inTheme)
    {
        setTheme(inTheme);
    }

    /**
     * Set the theme.
     * 
     * @param inTheme
     *            the theme to set
     */
    public void setTheme(final Theme inTheme)
    {
        this.theme = inTheme;
    }

    /**
     * Get the theme.
     * 
     * @return the theme
     */
    public Theme getTheme()
    {
        return theme;
    }

    /**
     * Look up the theme identified by this URL.
     * 
     * @param themeLocation
     *            URL of the XML file that defines the Theme
     * @return the Theme specified by the URL
     */
    public Theme findByUrl(final String themeLocation)
    {
        // Theme theme = new Theme(null, "my theme", null, null, null, null, null, null, null);
        return null;
    }

    /**
     * Delete a theme.
     * 
     * @param inTheme
     *            The theme to delete.
     */
    public void delete(final Object inTheme)
    {
    }

    /**
     * Find the domain entity by id.
     * 
     * @param galleryItemId
     *            ID of the entity to look up
     * @return the entity with the input
     */
    public Object findById(final Long galleryItemId)
    {
        return null;
    }

    /**
     * Find the default theme.
     * 
     * @return a theme
     */
    public Object findDefault()
    {
        return null;
    }

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
    public PagedSet findForCategorySortedByPopularity(final String inCategory, final int inStart, final int inEnd)
    {
        return null;
    }

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
    public PagedSet findForCategorySortedByRecent(final String inCategory, final int inStart, final int inEnd)
    {
        return null;
    }

    /**
     * Finds all gadget definitions sorted by popularity.
     * 
     * @param inStart
     *            paging inStart.
     * @param inEnd
     *            paging inEnd.
     * @return a list of gadget def
     */
    public PagedSet findSortedByPopularity(final int inStart, final int inEnd)
    {
        return null;
    }

    /**
     * Finds all gadget definitions sorted by most recent.
     * 
     * @param inStart
     *            paging inStart.
     * @param inEnd
     *            paging inEnd.
     * @return a list of gadget def
     */
    public PagedSet findSortedByRecent(final int inStart, final int inEnd)
    {
        return null;
    }

    /**
     * Update all entities that have changed since they were loaded within the
     * same context.
     */
    public void flush()
    {
    }

    /**
     * Insert the domain entity.
     * 
     * @param domainEntity
     *            The domainEntity to operate on.
     */
    public void insert(final Object domainEntity)
    {
    }

    /**
     * Refresh the domain entity.
     * @param inGalleryItem the domain entity.
     */
    public void refresh(final Object inGalleryItem)
    {
    }
}
