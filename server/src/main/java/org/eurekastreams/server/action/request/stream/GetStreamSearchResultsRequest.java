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
package org.eurekastreams.server.action.request.stream;

import java.io.Serializable;

/**
 * Request for searching a stream.
 */
public class GetStreamSearchResultsRequest implements Serializable
{
    /**
     * Serialization id.
     */
    private static final long serialVersionUID = 2121794770698732272L;

    /**
     * The ID of the stream.
     */
    private long streamViewId;

    /**
     * If set greater than 0, all stream items must have a stream item id less than this value.
     */
    private long lastSeenStreamItemId;

    /**
     * The search text to search the stream for.
     */
    private String searchText;

    /**
     * The page size to use.
     */
    private int pageSize;

    /**
     * The min activity id.
     */
    private long minActivityId = 0;

    /**
     * Max number of activites.
     * TODO:
     */
    private static final int MAXACTIVITIES = 100;

    /**
     * Empty constructor.
     */
    public GetStreamSearchResultsRequest()
    {
    }

    /**
     * Constructor.
     *
     * @param inStreamViewId
     *            the id of the stream view to search, or 0 for all
     * @param inSearchText
     *            the search text
     * @param inPageSize
     *            the page size
     * @param inLastSeenStreamItemId
     *            if set greater than 0, all stream items must have a stream item id less than this value.
     *
     */
    public GetStreamSearchResultsRequest(final long inStreamViewId, final String inSearchText, final int inPageSize,
            final long inLastSeenStreamItemId)
    {
        streamViewId = inStreamViewId;
        searchText = inSearchText;
        pageSize = inPageSize;
        lastSeenStreamItemId = inLastSeenStreamItemId;
    }

    /**
     * Constructor.
     *
     * @param inStreamViewId
     *            the id of the stream view to search, or 0 for all
     * @param inSearchText
     *            the search text
     * @param inMinActivityId
     *            the min activity id.
     *
     */
    public GetStreamSearchResultsRequest(final long inStreamViewId, final String inSearchText,
            final long inMinActivityId)
    {
        streamViewId = inStreamViewId;
        searchText = inSearchText;
        pageSize = MAXACTIVITIES;
        lastSeenStreamItemId = 0L;
        minActivityId = inMinActivityId;
    }

    /**
     * @return the streamId
     */
    public long getStreamViewId()
    {
        return streamViewId;
    }

    /**
     * @param inStreamViewId
     *            the streamId to set
     */
    public void setStreamViewId(final long inStreamViewId)
    {
        this.streamViewId = inStreamViewId;
    }

    /**
     * Get the search text.
     *
     * @return the searchText
     */
    public String getSearchText()
    {
        return searchText;
    }

    /**
     * Set the search text.
     *
     * @param inSearchText
     *            the searchText to set
     */
    public void setSearchText(final String inSearchText)
    {
        this.searchText = inSearchText;
    }

    /**
     * Get the page size.
     *
     * @return the pageSize
     */
    public int getPageSize()
    {
        return pageSize;
    }

    /**
     * Set the page size.
     *
     * @param inPageSize
     *            the pageSize to set
     */
    public void setPageSize(final int inPageSize)
    {
        this.pageSize = inPageSize;
    }

    /**
     * If set greater than 0, all stream items must have a stream item id less than this value.
     *
     * @return the lastSeenStreamItemId
     */
    public long getLastSeenStreamItemId()
    {
        return lastSeenStreamItemId;
    }

    /**
     * If set greater than 0, all stream items must have a stream item id less than this value.
     *
     * @param inLastSeenStreamItemId
     *            the lastSeenStreamItemId to set
     */
    public void setLastSeenStreamItemId(final long inLastSeenStreamItemId)
    {
        this.lastSeenStreamItemId = inLastSeenStreamItemId;
    }

    /**
     * Get the min activity id.
     * @return the min activity id.
     */
    public long getMinActivityId()
    {
        return minActivityId;
    }

    /**
     * Set the min activity id.
     * @param inMinActivityId the min activity id.
     */
    public void setMinActivityId(final long inMinActivityId)
    {
        minActivityId = inMinActivityId;
    }
}
