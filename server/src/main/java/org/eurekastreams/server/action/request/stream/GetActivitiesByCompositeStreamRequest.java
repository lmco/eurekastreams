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
 * Gets the activities for a composite stream.
 */
public class GetActivitiesByCompositeStreamRequest implements Serializable
{
    /**
     * Serialization ID.
     */
    private static final long serialVersionUID = -1567732216242221989L;

    /**
     * The CompositeStreamId.
     */
    private Long compositeStreamId;

    /**
     * the number of results to return.
     */
    private int maxResults;

    /**
     * if greater than zero, all requests will have a streamItemId less than this value.
     */
    private long maxActivityId;

    /**
     * The minimum activity Id.
     */
    private long minActivityId;

    /**
     * Constructor, returning the most recent page of data.
     * 
     * @param inCompositeStreamId
     *            the stream view id.
     * @param inMaxResults
     *            the number of results to return.
     */
    public GetActivitiesByCompositeStreamRequest(final Long inCompositeStreamId, final int inMaxResults)
    {
        this(inCompositeStreamId, inMaxResults, 0);
    }

    /**
     * Constructor.
     * 
     * @param inCompositeStreamId
     *            the stream view id.
     * @param inMaxResults
     *            the number of results to return.
     * @param inMaxActivityId
     *            all items will have a streamItemId less than this value.
     */
    public GetActivitiesByCompositeStreamRequest(final Long inCompositeStreamId, final int inMaxResults,
            final long inMaxActivityId)
    {
        compositeStreamId = inCompositeStreamId;
        maxResults = inMaxResults;
        minActivityId = 0;
        maxActivityId = inMaxActivityId;
    }

    /**
     * Constructor.
     * 
     * @param inCompositeStreamId
     *            the stream view id.
     * @param inMinActivityId
     *            all items will have a streamItemId greater than this value.
     */
    public GetActivitiesByCompositeStreamRequest(final Long inCompositeStreamId, final long inMinActivityId)
    {
        compositeStreamId = inCompositeStreamId;
        minActivityId = inMinActivityId;
        maxResults = Integer.MAX_VALUE;
        maxActivityId = Long.MAX_VALUE;
    }

    /**
     * Used for Serialization.
     */
    @SuppressWarnings("unused")
    private GetActivitiesByCompositeStreamRequest()
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
        maxResults = inMaxResults;
    }

    /**
     * If set greater than zero, all results will have a streamItemId less than this value.
     * 
     * @return the maxActivityId
     */
    public long getMaxActivityId()
    {
        return maxActivityId;
    }

    /**
     * If set greater than zero, all results will have a streamItemId less than this value.
     * 
     * @param inMaxActivityId
     *            the maxActivityId to set
     */
    public void setMaxActivityId(final long inMaxActivityId)
    {
        maxActivityId = inMaxActivityId;
    }

    /**
     * @return the compositeStreamId
     */
    public Long getCompositeStreamId()
    {
        return compositeStreamId;
    }

    /**
     * @param inCompositeStreamId
     *            the compositeStreamId to set
     */
    public void setCompositeStreamId(final Long inCompositeStreamId)
    {
        compositeStreamId = inCompositeStreamId;
    }

    /**
     * Get the min activity id.
     * 
     * @return the min activity id.
     */
    public long getMinActivityId()
    {
        return minActivityId;
    }

    /**
     * Set the min activity id.
     * 
     * @param inMinActivityId
     *            the min activity id.
     */
    public void setMinActivityId(final long inMinActivityId)
    {
        minActivityId = inMinActivityId;
    }

    /**
     * Return string representation.
     * 
     * @return string representation.
     */
    @Override
    public String toString()
    {
        return this.getClass().getName() + " compositeStreamId: " + compositeStreamId + " maxResults: " + maxResults
                + " minActivityId: " + minActivityId + " maxActivityId: " + maxActivityId;
    }

}
