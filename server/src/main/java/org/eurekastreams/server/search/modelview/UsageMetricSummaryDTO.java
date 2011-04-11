/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.search.modelview;

import java.io.Serializable;

/**
 * Contains Summary usage metrics.
 * 
 */
public class UsageMetricSummaryDTO implements Serializable
{

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 5547126202116017950L;

    /**
     * Number of daily records this summary is based on.
     */
    private long recordCount;

    /**
     * The number of unique visitors.
     */
    private long uniqueVisitorCount;

    /**
     * Number of page views.
     */
    private long pageViewCount;

    /**
     * Number of people viewing streams.
     */
    private long streamViewerCount;

    /**
     * Number of streams viewed.
     */
    private long streamViewCount;

    /**
     * Number of people contributing to streams (comment and activities).
     */
    private long streamContributorCount;

    /**
     * Number of activities and comments posted.
     */
    private long messageCount;

    /**
     * @return the uniqueVisitorCount
     */
    public long getUniqueVisitorCount()
    {
        return uniqueVisitorCount;
    }

    /**
     * @param inUniqueVisitorCount
     *            the uniqueVisitorCount to set
     */
    public void setUniqueVisitorCount(final long inUniqueVisitorCount)
    {
        uniqueVisitorCount = inUniqueVisitorCount;
    }

    /**
     * @return the pageViewCount
     */
    public long getPageViewCount()
    {
        return pageViewCount;
    }

    /**
     * @param inPageViewCount
     *            the pageViewCount to set
     */
    public void setPageViewCount(final long inPageViewCount)
    {
        pageViewCount = inPageViewCount;
    }

    /**
     * @return the streamViewerCount
     */
    public long getStreamViewerCount()
    {
        return streamViewerCount;
    }

    /**
     * @param inStreamViewerCount
     *            the streamViewerCount to set
     */
    public void setStreamViewerCount(final long inStreamViewerCount)
    {
        streamViewerCount = inStreamViewerCount;
    }

    /**
     * @return the streamViewCount
     */
    public long getStreamViewCount()
    {
        return streamViewCount;
    }

    /**
     * @param inStreamViewCount
     *            the streamViewCount to set
     */
    public void setStreamViewCount(final long inStreamViewCount)
    {
        streamViewCount = inStreamViewCount;
    }

    /**
     * @return the streamContributorCount
     */
    public long getStreamContributorCount()
    {
        return streamContributorCount;
    }

    /**
     * @param inStreamContributorCount
     *            the streamContributorCount to set
     */
    public void setStreamContributorCount(final long inStreamContributorCount)
    {
        streamContributorCount = inStreamContributorCount;
    }

    /**
     * @return the messageCount
     */
    public long getMessageCount()
    {
        return messageCount;
    }

    /**
     * @param inMessageCount
     *            the messageCount to set
     */
    public void setMessageCount(final long inMessageCount)
    {
        messageCount = inMessageCount;
    }

    /**
     * @return the recordCount
     */
    public long getRecordCount()
    {
        return recordCount;
    }

    /**
     * @param inRecordCount
     *            the recordCount to set
     */
    public void setRecordCount(final long inRecordCount)
    {
        recordCount = inRecordCount;
    }

}
