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
package org.eurekastreams.server.service.actions.requests;

import java.io.Serializable;

import org.eurekastreams.server.domain.GalleryItemType;

/**
 * Search gallery request.
 */
public class SearchGalleryRequest implements Serializable
{
    /**
     * Serialized version.
     */
    private static final long serialVersionUID = -8685283677247225964L;

    /**
     * Search text.
     */
    private String searchText;

    /**
     * Item type.
     */
    private GalleryItemType type;

    /**
     * Max results.
     */
    private int maxResultsPerPage;

    /**
     * Starting index.
     */
    private int startingIndex;

    /**
     * Sorting.
     */
    private String sort;

    /**
     * @return the sort
     */
    public final String getSort()
    {
        return sort;
    }

    /**
     * @param inSort
     *            the sort to set
     */
    public final void setSort(final String inSort)
    {
        this.sort = inSort;
    }

    /**
     * Constructor.
     * 
     * @param inSearchText
     *            the search text.
     * @param inSort
     *            sorting.
     * @param inType
     *            target type.
     * @param inMaxResultsPerPage
     *            max results.
     * @param inStartingIndex
     *            starting index.
     */
    public SearchGalleryRequest(final String inSearchText, final String inSort, final GalleryItemType inType,
            final int inMaxResultsPerPage, final int inStartingIndex)
    {
        searchText = inSearchText;
        type = inType;
        sort = inSort;
        maxResultsPerPage = inMaxResultsPerPage;
        startingIndex = inStartingIndex;
    }

    /**
     * Used for serialization.
     */
    public SearchGalleryRequest()
    {

    }

    /**
     * @param inSearchText
     *            set the searchText.
     */
    public void setSearchText(final String inSearchText)
    {
        searchText = inSearchText;
    }

    /**
     * Set hte type.
     * 
     * @param inType
     *            the type.
     */
    public void setType(final GalleryItemType inType)
    {
        type = inType;
    }

    /**
     * Set the max results.
     * 
     * @param inMaxResultsPerPage
     *            the max results.
     */
    public void setMaxResultsPerPage(final int inMaxResultsPerPage)
    {
        maxResultsPerPage = inMaxResultsPerPage;
    }

    /**
     * Set the starting index.
     * 
     * @param inStartingIndex
     *            the index.
     */
    public void setStartingIndex(final int inStartingIndex)
    {
        startingIndex = inStartingIndex;
    }

    /**
     * @return the searchText
     */
    public String getSearchText()
    {
        return searchText;
    }

    /**
     * @return the type
     */
    public GalleryItemType getType()
    {
        return type;
    }

    /**
     * @return the maxResultsPerPage
     */
    public int getMaxResultsPerPage()
    {
        return maxResultsPerPage;
    }

    /**
     * @return the startingIndex
     */
    public int getStartingIndex()
    {
        return startingIndex;
    }
}
