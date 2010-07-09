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
package org.eurekastreams.server.persistence.mappers.requests;

import java.io.Serializable;

/**
 * Request for searching a stream.
 */
public class StreamSearchRequest implements Serializable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 8318654601344866980L;

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
     * The account id of the requesting user.
     */
    private String requestingUserAccountId;

    /**
     * Empty constructor.
     */
    public StreamSearchRequest()
    {
    }

    /**
     * Constructor.
     * 
     * @param inRequestingUserAccountId
     *            the account id of the requesting user
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
    public StreamSearchRequest(final String inRequestingUserAccountId, final long inStreamViewId,
            final String inSearchText, final int inPageSize, final long inLastSeenStreamItemId)
    {
        requestingUserAccountId = inRequestingUserAccountId;
        streamViewId = inStreamViewId;
        searchText = inSearchText;
        pageSize = inPageSize;
        lastSeenStreamItemId = inLastSeenStreamItemId;
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
     * Get the account id of the requesting user.
     * 
     * @return the requestingUserAccountId
     */
    public String getRequestingUserAccountId()
    {
        return requestingUserAccountId;
    }

    /**
     * Set the account id of the requesting user.
     * 
     * @param inRequestingUserAccountId
     *            the requestingUserAccountId to set
     */
    public void setRequestingUserAccountId(final String inRequestingUserAccountId)
    {
        this.requestingUserAccountId = inRequestingUserAccountId;
    }
}
