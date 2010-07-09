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
package org.eurekastreams.server.action.request.gallery;

import java.io.Serializable;

import org.eurekastreams.server.action.request.PageableRequest;

/**
 * Request object for the GetGalleryitems action.
 *
 */
public class GetGalleryItemsRequest implements Serializable, PageableRequest
{
    /**
     * Serialization id.
     */
    private static final long serialVersionUID = -8493178116294511494L;

    /**
     * Sort criteria for this request.
     */
    private String sortCriteria;

    /**
     * Category for this request.
     */
    private String category;

    /**
     * Paging information on where to start.
     */
    private int pageFrom;

    /**
     * Paging informatino on where to end.
     */
    private int pageTo;

    /**
     * This is a blank default constructor for passage through GWT RPC.
     */
    public GetGalleryItemsRequest()
    {
        //Default constructor.
    }

    /**
     * Constructor for request object.
     * @param inSortCriteria - Sort Criteria for this request.
     * @param inCategory - Category for this request.
     * @param inPageFrom - Paging information regarding where to start for this request.
     * @param inPageTo - Paging information regarding where to end for this request.
     */
    public GetGalleryItemsRequest(final String inSortCriteria, final String inCategory, final int inPageFrom,
            final int inPageTo)
    {
        sortCriteria = inSortCriteria;
        category = inCategory;
        pageFrom = inPageFrom;
        pageTo = inPageTo;
    }

    /**
     * @return the sortCriteria
     */
    public String getSortCriteria()
    {
        return sortCriteria;
    }

    /**
     * @param inSortCriteria
     *            the inSortCriteria to set
     */
    public void setSortCriteria(final String inSortCriteria)
    {
        this.sortCriteria = inSortCriteria;
    }

    /**
     * @return the category
     */
    public String getCategory()
    {
        return category;
    }

    /**
     * @param inCategory
     *            the category to set
     */
    public void setCategory(final String inCategory)
    {
        this.category = inCategory;
    }

    @Override
    public Integer getEndIndex()
    {
        return pageTo;
    }

    @Override
    public Integer getStartIndex()
    {
        return pageFrom;
    }

    @Override
    public void setEndIndex(final Integer inEndIndex)
    {
        this.pageTo = inEndIndex;
    }

    @Override
    public void setStartIndex(final Integer inStartIndex)
    {
        this.pageFrom = inStartIndex;

    }
}
