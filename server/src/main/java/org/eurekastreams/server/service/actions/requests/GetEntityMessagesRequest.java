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

/**
 * Gets the messages for an entity.
 */
public class GetEntityMessagesRequest implements Serializable
{
    /**
     * Serialization ID.
     */
    private static final long serialVersionUID = -1567732216242221989L;

    /**
     * The StreamViewId.
     */
    private Long streamViewId;

    /**
     * the number of results to return.
     */
    private int maxResults;

    /**
     * if greater than zero, all requests will have a streamItemId less than this value.
     */
    private long havingStreamIdLessThan;

    /**
     * All requests will have a streamItemId greater than this value.
     */
    private long havingStreamIdGreaterThan;

    /**
     * Constructor, returning the most recent page of data.
     * 
     * @param inStreamViewId
     *            the stream view id.
     * @param inMaxResults
     *            the number of results to return.
     */
    public GetEntityMessagesRequest(final Long inStreamViewId, final int inMaxResults)
    {
        this(inStreamViewId, inMaxResults, 0, 0);
    }

    /**
     * Constructor.
     * 
     * @param inStreamViewId
     *            the stream view id.
     * @param inMaxResults
     *            the number of results to return.
     * @param inHavingStreamIdLessThan
     *            all items will have a streamItemId less than this value.
     * @param inHavingStreamIdGreaterThan
     *            all items will have a streamItemId greater than this value.
     */
    public GetEntityMessagesRequest(final Long inStreamViewId, final int inMaxResults,
            final long inHavingStreamIdGreaterThan, final long inHavingStreamIdLessThan)
    {
        streamViewId = inStreamViewId;
        maxResults = inMaxResults;
        havingStreamIdLessThan = inHavingStreamIdLessThan;
        setHavingStreamIdGreaterThan(inHavingStreamIdGreaterThan);
    }

    /**
     * Used for Serialization.
     */
    private GetEntityMessagesRequest()
    {
    }

    /**
     * Get the max number of results to return.
     * 
     * @return the maxRequests
     */
    public int getMaxResults()
    {
        return maxResults;
    }

    /**
     * Set the max number of results to return.
     * 
     * @param inMaxResults
     *            the maxRequests to set
     */
    public void setMaxResults(final int inMaxResults)
    {
        this.maxResults = inMaxResults;
    }

    /**
     * If set greater than zero, all results will have a streamItemId less than this value.
     * 
     * @return the havingStreamIdLessThan
     */
    public long getHavingStreamIdLessThan()
    {
        return havingStreamIdLessThan;
    }

    /**
     * If set greater than zero, all results will have a streamItemId less than this value.
     * 
     * @param inHavingStreamIdLessThan
     *            the havingStreamIdLessThan to set
     */
    public void setHavingStreamIdLessThan(final long inHavingStreamIdLessThan)
    {
        this.havingStreamIdLessThan = inHavingStreamIdLessThan;
    }

    /**
     * @return the streamViewId
     */
    public Long getStreamViewId()
    {
        return streamViewId;
    }

    /**
     * @param inStreamViewId
     *            the streamViewId to set
     */
    public void setStreamViewId(final Long inStreamViewId)
    {
        streamViewId = inStreamViewId;
    }

    /**
     * @param inHavingStreamIdGreaterThan
     *            the havingStreamIdGreaterThan to set.
     */
    public void setHavingStreamIdGreaterThan(final long inHavingStreamIdGreaterThan)
    {
        this.havingStreamIdGreaterThan = inHavingStreamIdGreaterThan;
    }

    /**
     * @return the havingStreamIdGreaterThan.
     */
    public long getHavingStreamIdGreaterThan()
    {
        return havingStreamIdGreaterThan;
    }

}
