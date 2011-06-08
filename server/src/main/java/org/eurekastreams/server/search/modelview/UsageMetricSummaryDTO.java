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

    /**
     * Number of weekday records this summary is based on.
     */
    private long weekdayRecordCount;

    /**
     * average daily number of unique visitors.
     */
    private long averageDailyUniqueVisitorCount;

    /**
     * average daily number of page views.
     */
    private long averageDailyPageViewCount;

    /**
     * average daily number of people viewing streams.
     */
    private long averageDailyStreamViewerCount;

    /**
     * average daily number of streams viewed.
     */
    private long averageDailyStreamViewCount;

    /**
     * average daily number of people contributing to streams (comment and activities).
     */
    private long averageDailyStreamContributorCount;

    /**
     * average daily number of activities and comments posted.
     */
    private long averageDailyMessageCount;

    /**
     * Avg time (mins) to first comment (for activities that have comments).
     */
    private long averageDailyActivityResponseTime;

    /**
     * @return the weekdayRecordCount
     */
    public long getWeekdayRecordCount()
    {
        return weekdayRecordCount;
    }

    /**
     * @param inWeekdayRecordCount
     *            the weekdayRecordCount to set
     */
    public void setWeekdayRecordCount(final long inWeekdayRecordCount)
    {
        weekdayRecordCount = inWeekdayRecordCount;
    }

    /**
     * @return the averageDailyUniqueVisitorCount
     */
    public long getAverageDailyUniqueVisitorCount()
    {
        return averageDailyUniqueVisitorCount;
    }

    /**
     * @param inAverageDailyUniqueVisitorCount
     *            the averageDailyUniqueVisitorCount to set
     */
    public void setAverageDailyUniqueVisitorCount(final long inAverageDailyUniqueVisitorCount)
    {
        averageDailyUniqueVisitorCount = inAverageDailyUniqueVisitorCount;
    }

    /**
     * @return the averageDailyPageViewCount
     */
    public long getAverageDailyPageViewCount()
    {
        return averageDailyPageViewCount;
    }

    /**
     * @param inAverageDailyPageViewCount
     *            the averageDailyPageViewCount to set
     */
    public void setAverageDailyPageViewCount(final long inAverageDailyPageViewCount)
    {
        averageDailyPageViewCount = inAverageDailyPageViewCount;
    }

    /**
     * @return the averageDailyStreamViewerCount
     */
    public long getAverageDailyStreamViewerCount()
    {
        return averageDailyStreamViewerCount;
    }

    /**
     * @param inAverageDailyStreamViewerCount
     *            the averageDailyStreamViewerCount to set
     */
    public void setAverageDailyStreamViewerCount(final long inAverageDailyStreamViewerCount)
    {
        averageDailyStreamViewerCount = inAverageDailyStreamViewerCount;
    }

    /**
     * @return the averageDailyStreamViewCount
     */
    public long getAverageDailyStreamViewCount()
    {
        return averageDailyStreamViewCount;
    }

    /**
     * @param inAverageDailyStreamViewCount
     *            the averageDailyStreamViewCount to set
     */
    public void setAverageDailyStreamViewCount(final long inAverageDailyStreamViewCount)
    {
        averageDailyStreamViewCount = inAverageDailyStreamViewCount;
    }

    /**
     * @return the averageDailyStreamContributorCount
     */
    public long getAverageDailyStreamContributorCount()
    {
        return averageDailyStreamContributorCount;
    }

    /**
     * @param inAverageDailyStreamContributorCount
     *            the averageDailyStreamContributorCount to set
     */
    public void setAverageDailyStreamContributorCount(final long inAverageDailyStreamContributorCount)
    {
        averageDailyStreamContributorCount = inAverageDailyStreamContributorCount;
    }

    /**
     * @return the averageDailyMessageCount
     */
    public long getAverageDailyMessageCount()
    {
        return averageDailyMessageCount;
    }

    /**
     * @param inAverageDailyMessageCount
     *            the averageDailyMessageCount to set
     */
    public void setAverageDailyMessageCount(final long inAverageDailyMessageCount)
    {
        averageDailyMessageCount = inAverageDailyMessageCount;
    }

    /**
     * @return the averageDailyActivityResponseTime
     */
    public long getAverageDailyActivityResponseTime()
    {
        return averageDailyActivityResponseTime;
    }

    /**
     * @param inAverageDailyActivityResponseTime
     *            the averageDailyActivityResponseTime to set
     */
    public void setAverageDailyActivityResponseTime(final long inAverageDailyActivityResponseTime)
    {
        averageDailyActivityResponseTime = inAverageDailyActivityResponseTime;
    }

}
