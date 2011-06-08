/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.eurekastreams.commons.model.DomainEntityIdentifiable;

/**
 * Entity representing a day's usage metrics.
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "streamViewStreamScopeId", "usageDate" }) })
public class DailyUsageSummary implements Serializable, DomainEntityIdentifiable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -6570376871385314251L;

    /**
     * Primary key ID field for ORM.
     * 
     * Where you set the @Id on entities tells the ORM if you're using field or property-based entity mapping. if you
     * set it on a private variable, then the ORM will not use getters/setters at all. If you set it on getId(), then
     * you need to have getters/setters on everything.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The number of unique visitors on this day.
     */
    private long uniqueVisitorCount;

    /**
     * Number of page views on this day.
     */
    private long pageViewCount;

    /**
     * Number of people viewing streams on this day.
     */
    private long streamViewerCount;

    /**
     * Number of views of streams on this day.
     */
    private long streamViewCount;

    /**
     * Number of people contributing to streams (comment and activities) on this day.
     */
    private long streamContributorCount;

    /**
     * Number of activities and comments posted on this day.
     */
    private long messageCount;

    /**
     * Total number of views of streams for all time.
     */
    private Long totalStreamViewCount;

    /**
     * Total activity count for all time.
     */
    private Long totalActivityCount;

    /**
     * Total comment count for all time.
     */
    private Long totalCommentCount;

    /**
     * Total contributor count - for all time - applies to streams only.
     */
    private Long totalContributorCount;

    /**
     * Average response time for activities that have comments.
     */
    private long avgActivityResponseTime;

    /**
     * The stream scope id for metrics that are stream views - null for non-streamview page views.
     */
    private Long streamViewStreamScopeId;

    /**
     * The date.
     */
    @Basic(optional = false)
    @Temporal(TemporalType.DATE)
    private Date usageDate;

    /**
     * Constructor for ORM.
     */
    public DailyUsageSummary()
    {
    }

    /**
     * Constructor.
     * 
     * @param inUniqueVisitorCount
     *            the number of unique visitors
     * @param inPageViewCount
     *            number of page views
     * @param inStreamViewerCount
     *            number of people viewing streams (contributor)
     * @param inStreamViewCount
     *            number of streams viewed
     * @param inStreamContributorCount
     *            number of people contributing to streams (comment and activities)
     * @param inMessageCount
     *            number of activities and comments posted
     * @param inAvgActivityResponseTime
     *            Average response time for activities that have comments.
     * @param inUsageDate
     *            the date
     * @param inStreamViewStreamScopeId
     *            the stream view that these stats apply to, or null if for all streams
     * @param inTotalActivityCount
     *            the total activity count
     * @param inTotalCommentCount
     *            the total comment count
     * @param inTotalStreamViewCount
     *            the total stream views since records started
     * @param inTotalContributorCount
     *            the total contributors for the stream
     */
    public DailyUsageSummary(final long inUniqueVisitorCount, final long inPageViewCount,
            final long inStreamViewerCount, final long inStreamViewCount, final long inStreamContributorCount,
            final long inMessageCount, final long inAvgActivityResponseTime, final Date inUsageDate,
            final Long inStreamViewStreamScopeId, final Long inTotalActivityCount, final Long inTotalCommentCount,
            final Long inTotalStreamViewCount, final Long inTotalContributorCount)
    {
        uniqueVisitorCount = inUniqueVisitorCount;
        pageViewCount = inPageViewCount;
        streamViewerCount = inStreamViewerCount;
        streamViewCount = inStreamViewCount;
        streamContributorCount = inStreamContributorCount;
        messageCount = inMessageCount;
        avgActivityResponseTime = inAvgActivityResponseTime;
        usageDate = inUsageDate;
        streamViewStreamScopeId = inStreamViewStreamScopeId;
        totalActivityCount = inTotalActivityCount;
        totalCommentCount = inTotalCommentCount;
        totalStreamViewCount = inTotalStreamViewCount;
        totalContributorCount = inTotalContributorCount;
    }

    /**
     * @return the id
     */
    public long getId()
    {
        return id;
    }

    /**
     * @param inId
     *            the id to set
     */
    public void setId(final long inId)
    {
        id = inId;
    }

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
     * @return the usageDate
     */
    public Date getUsageDate()
    {
        return usageDate;
    }

    /**
     * @param inUsageDate
     *            the usageDate to set
     */
    public void setUsageDate(final Date inUsageDate)
    {
        usageDate = inUsageDate;
    }

    /**
     * @return the avgActivityResponseTime
     */
    public long getAvgActivityResponseTime()
    {
        return avgActivityResponseTime;
    }

    /**
     * @param inAvgActivityResponseTime
     *            the avgActivityResponseTime to set
     */
    public void setAvgActivityResponseTime(final long inAvgActivityResponseTime)
    {
        avgActivityResponseTime = inAvgActivityResponseTime;
    }

    /**
     * @return the streamViewStreamScopeId
     */
    public Long getStreamViewStreamScopeId()
    {
        return streamViewStreamScopeId;
    }

    /**
     * @param inStreamViewStreamScopeId
     *            the streamViewStreamScopeId to set
     */
    public void setStreamViewStreamScopeId(final Long inStreamViewStreamScopeId)
    {
        streamViewStreamScopeId = inStreamViewStreamScopeId;
    }

    /**
     * @return the totalActivityCount
     */
    public Long getTotalActivityCount()
    {
        return totalActivityCount;
    }

    /**
     * @param inTotalActivityCount
     *            the totalActivityCount to set
     */
    public void setTotalActivityCount(final Long inTotalActivityCount)
    {
        totalActivityCount = inTotalActivityCount;
    }

    /**
     * @return the totalCommentCount
     */
    public Long getTotalCommentCount()
    {
        return totalCommentCount;
    }

    /**
     * @param inTotalCommentCount
     *            the totalCommentCount to set
     */
    public void setTotalCommentCount(final Long inTotalCommentCount)
    {
        totalCommentCount = inTotalCommentCount;
    }

    /**
     * @return the totalStreamViewCount
     */
    public Long getTotalStreamViewCount()
    {
        return totalStreamViewCount;
    }

    /**
     * @param inTotalStreamViewCount
     *            the totalStreamViewCount to set
     */
    public void setTotalStreamViewCount(final Long inTotalStreamViewCount)
    {
        totalStreamViewCount = inTotalStreamViewCount;
    }

    /**
     * @return the totalContributorCount
     */
    public Long getTotalContributorCount()
    {
        return totalContributorCount;
    }

    /**
     * @param inTotalContributorCount
     *            the totalContributorCount to set
     */
    public void setTotalContributorCount(final Long inTotalContributorCount)
    {
        totalContributorCount = inTotalContributorCount;
    }
}
