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
package org.eurekastreams.server.action.execution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.date.DateDayExtractor;
import org.eurekastreams.commons.date.DayOfWeekStrategy;
import org.eurekastreams.commons.date.GetDateFromDaysAgoStrategy;
import org.eurekastreams.commons.test.IsEqualInternally;
import org.eurekastreams.server.domain.DailyUsageSummary;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.eurekastreams.server.service.actions.requests.UsageMetricDailyStreamInfoRequest;
import org.eurekastreams.server.service.actions.requests.UsageMetricStreamSummaryRequest;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test fixture for GenerateDailyUsageSummaryExecution.
 */
public class GenerateDailyUsageSummaryExecutionTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Strategy to get a Date from a day ago.
     */
    private final GetDateFromDaysAgoStrategy daysAgoDateStrategy = context.mock(GetDateFromDaysAgoStrategy.class);

    /**
     * Mapper to get a single day's DailyUsageSummary.
     */
    private final DomainMapper<UsageMetricDailyStreamInfoRequest, DailyUsageSummary> // 
    getDailyUsageSummaryByDateMapper = context.mock(DomainMapper.class, "getDailyUsageSummaryByDateMapper");

    /**
     * Mapper to get the most recent DailyUsageSummary before the input date.
     */
    private DomainMapper<UsageMetricDailyStreamInfoRequest, DailyUsageSummary> // 
    getPreviousDailyUsageSummaryByDateMapper = context.mock(DomainMapper.class,
            "getPreviousDailyUsageSummaryByDateMapper");

    /**
     * Mapper to get a day's message count.
     */
    private final DomainMapper<UsageMetricDailyStreamInfoRequest, Long> getDailyMessageCountMapper = context.mock(
            DomainMapper.class, "getDailyMessageCountMapper");

    /**
     * Mapper to get a day's page view count.
     */
    private final DomainMapper<Date, Long> getDailyPageViewCountMapper = context.mock(DomainMapper.class,
            "getDailyPageViewCountMapper");

    /**
     * Mapper to get a day's stream contributor count.
     */
    private final DomainMapper<UsageMetricDailyStreamInfoRequest, Long> getDailyStreamContributorCountMapper = context
            .mock(DomainMapper.class, "getDailyStreamContributorCountMapper");

    /**
     * Mapper to get a day's stream view count.
     */
    private final DomainMapper<UsageMetricDailyStreamInfoRequest, Long> getDailyStreamViewCountMapper = context.mock(
            DomainMapper.class, "getDailyStreamViewCountMapper");

    /**
     * Mapper to get a day's stream viewer count.
     */
    private final DomainMapper<UsageMetricDailyStreamInfoRequest, Long> getDailyStreamViewerCountMapper = context.mock(
            DomainMapper.class, "getDailyStreamViewerCountMapper");

    /**
     * Mapper to get a day's unique visitor count.
     */
    private final DomainMapper<Date, Long> getDailyUniqueVisitorCountMapper = context.mock(DomainMapper.class,
            "getDailyUniqueVisitorCountMapper");

    /**
     * Mapper to insert the DailyUsageSummary entity.
     */
    private final InsertMapperFake insertMapper = new InsertMapperFake();

    /**
     * Mapper to delete old UsageMetric data.
     */
    private final DomainMapper<Serializable, Serializable> usageMetricDataCleanupMapper = context.mock(
            DomainMapper.class, "usageMetricDataCleanupMapper");

    /**
     * Mapper to get day's average activity response time (for those that had responses).
     */
    private DomainMapper<Date, Long> getDailyMessageResponseTimeMapper = context.mock(DomainMapper.class,
            "getDailyMessageResponseTimeMapper");

    /**
     * Mapper to get stream scope ids to generate metrics for.
     */
    private DomainMapper<Date, List<Long>> streamScopeIdsMapper = context.mock(DomainMapper.class,
            "streamScopeIdsMapper");
    /**
     * Mapper to get the total number of activities posted to a stream.
     */
    private DomainMapper<Long, Long> getTotalActivityCountMapper = context.mock(DomainMapper.class,
            "getTotalActivityCountMapper");

    /**
     * Mapper to get the total number of comments posted to a stream.
     */
    private DomainMapper<Long, Long> getTotalCommentCountMapper = context.mock(DomainMapper.class,
            "getTotalCommentCountMapper");

    /**
     * Mapper to get the total number of contributors to a stream by stream scope id.
     */
    private DomainMapper<Long, Long> getTotalStreamContributorMapper = context.mock(DomainMapper.class,
            "getTotalStreamContributorMapper");

    /**
     * Day of week strategy.
     */
    private DayOfWeekStrategy dayOfWeekStrategy = context.mock(DayOfWeekStrategy.class);

    /**
     * Mapper to clear the entity manager.
     */
    private DomainMapper<Serializable, Boolean> clearEntityManagerMapper = context.mock(DomainMapper.class,
            "clearEntityManagerMapper");

    /**
     * Mapper to get the summary data for a stream, or all streams - used for caching.
     */
    private DomainMapper<UsageMetricStreamSummaryRequest, List<DailyUsageSummary>> summaryDataMapper = context.mock(
            DomainMapper.class, "summaryDataMapper");

    /**
     * The number of days to cache summary data for.
     */
    private final int numberOfDaysToCacheSummaryDataFor = 38;

    /**
     * Input to mapper.
     */
    private final TaskHandlerActionContext actionContext = context.mock(TaskHandlerActionContext.class);

    /**
     * Test execute when we already have data from yesterday.
     */
    @Test
    public void testExecuteWithDataAlreadyExisting()
    {
        GenerateDailyUsageSummaryExecution sut = new GenerateDailyUsageSummaryExecution(1, daysAgoDateStrategy,
                getDailyUsageSummaryByDateMapper, getPreviousDailyUsageSummaryByDateMapper, getDailyMessageCountMapper,
                getDailyPageViewCountMapper, getDailyStreamContributorCountMapper, getDailyStreamViewCountMapper,
                getDailyStreamViewerCountMapper, getDailyUniqueVisitorCountMapper, getDailyMessageResponseTimeMapper,
                insertMapper, usageMetricDataCleanupMapper, dayOfWeekStrategy, streamScopeIdsMapper,
                getTotalCommentCountMapper, getTotalCommentCountMapper, getTotalStreamContributorMapper,
                clearEntityManagerMapper, summaryDataMapper, numberOfDaysToCacheSummaryDataFor);

        final DailyUsageSummary existingSummary = context.mock(DailyUsageSummary.class);
        final Date date = new Date(2011, 1, 22);

        context.checking(new Expectations()
        {
            {
                oneOf(daysAgoDateStrategy).execute(with(1));
                will(returnValue(date));

                oneOf(getDailyUsageSummaryByDateMapper).execute(
                        with(IsEqualInternally.equalInternally(new UsageMetricDailyStreamInfoRequest(DateDayExtractor
                                .getStartOfDay(date), null))));
                will(returnValue(existingSummary));

                oneOf(dayOfWeekStrategy).isWeekday(with(DateDayExtractor.getStartOfDay(date)));
                will(returnValue(true));

                oneOf(usageMetricDataCleanupMapper).execute(null);

                allowing(clearEntityManagerMapper).execute(null);
            }
        });

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }

    /**
     * Test execute.
     */
    @Test
    public void executeWithNoDataAlreadyExisting()
    {
        GenerateDailyUsageSummaryExecution sut = new GenerateDailyUsageSummaryExecution(1, daysAgoDateStrategy,
                getDailyUsageSummaryByDateMapper, getPreviousDailyUsageSummaryByDateMapper, getDailyMessageCountMapper,
                getDailyPageViewCountMapper, getDailyStreamContributorCountMapper, getDailyStreamViewCountMapper,
                getDailyStreamViewerCountMapper, getDailyUniqueVisitorCountMapper, getDailyMessageResponseTimeMapper,
                insertMapper, usageMetricDataCleanupMapper, dayOfWeekStrategy, streamScopeIdsMapper,
                getTotalCommentCountMapper, getTotalCommentCountMapper, getTotalStreamContributorMapper,
                clearEntityManagerMapper, summaryDataMapper, numberOfDaysToCacheSummaryDataFor);

        final Date dateRaw = new Date(2011, 1, 22);
        final Date date = DateDayExtractor.getStartOfDay(dateRaw);
        final long uniqueVisitorCount = 1L;
        final long pageViewCount = 2L;
        final long streamViewerCount = 3L;
        final long streamViewCount = 4L;
        final long streamContributorCount = 5L;
        final long messageCount = 6L;
        final long avgActivityResponseTime = 3L;

        final List<Long> streamScopeIds = new ArrayList<Long>();

        context.checking(new Expectations()
        {
            {
                oneOf(daysAgoDateStrategy).execute(with(1));
                will(returnValue(dateRaw));

                // no data found
                oneOf(getDailyUsageSummaryByDateMapper).execute(
                        with(IsEqualInternally.equalInternally(new UsageMetricDailyStreamInfoRequest(date, null))));
                will(returnValue(null));

                oneOf(getDailyUniqueVisitorCountMapper).execute(with(date));
                will(returnValue(uniqueVisitorCount));

                oneOf(getDailyPageViewCountMapper).execute(with(date));
                will(returnValue(pageViewCount));

                oneOf(getDailyStreamViewerCountMapper).execute(
                        with(IsEqualInternally.equalInternally(new UsageMetricDailyStreamInfoRequest(date, null))));
                will(returnValue(streamViewerCount));

                oneOf(getDailyStreamViewCountMapper).execute(
                        with(IsEqualInternally.equalInternally(new UsageMetricDailyStreamInfoRequest(date, null))));
                will(returnValue(streamViewCount));

                oneOf(getDailyStreamContributorCountMapper).execute(
                        with(IsEqualInternally.equalInternally(new UsageMetricDailyStreamInfoRequest(date, null))));
                will(returnValue(streamContributorCount));

                oneOf(getDailyMessageResponseTimeMapper).execute(with(date));
                will(returnValue(avgActivityResponseTime));

                oneOf(getDailyMessageCountMapper).execute(
                        with(IsEqualInternally.equalInternally(new UsageMetricDailyStreamInfoRequest(date, null))));
                will(returnValue(messageCount));

                oneOf(dayOfWeekStrategy).isWeekday(with(date));
                will(returnValue(true));

                oneOf(usageMetricDataCleanupMapper).execute(null);

                oneOf(streamScopeIdsMapper).execute(with(date));
                will(returnValue(streamScopeIds));

                oneOf(summaryDataMapper).execute(
                        with(IsEqualInternally.equalInternally(new UsageMetricStreamSummaryRequest(
                                numberOfDaysToCacheSummaryDataFor, null))));

                allowing(clearEntityManagerMapper).execute(null);
            }
        });

        this.insertMapper.setRequest(null);
        sut.execute(actionContext);

        DailyUsageSummary ds = (DailyUsageSummary) insertMapper.getRequest().getDomainEnity();
        Assert.assertEquals(uniqueVisitorCount, ds.getUniqueVisitorCount());
        Assert.assertEquals(pageViewCount, ds.getPageViewCount());
        Assert.assertEquals(streamViewerCount, ds.getStreamViewerCount());
        Assert.assertEquals(streamViewCount, ds.getStreamViewCount());
        Assert.assertEquals(streamContributorCount, ds.getStreamContributorCount());
        Assert.assertEquals(avgActivityResponseTime, ds.getAvgActivityResponseTime());
        Assert.assertEquals(messageCount, ds.getMessageCount());
        Assert.assertEquals(date, ds.getUsageDate());
        Assert.assertEquals(new Long(date.getTime()), ds.getUsageDateTimeStampInMs());

        context.assertIsSatisfied();
    }

    /**
     * Fake class to hang onto the generated DailyUsageSummary so we can test the values.
     */
    private class InsertMapperFake implements DomainMapper<PersistenceRequest<DailyUsageSummary>, Boolean>
    {
        /**
         * The request.
         */
        private PersistenceRequest request;

        /**
         * execute.
         * 
         * @param inRequest
         *            the request to persist.
         * @return true
         */
        @Override
        public Boolean execute(final PersistenceRequest<DailyUsageSummary> inRequest)
        {
            request = inRequest;
            return Boolean.TRUE;
        }

        /**
         * @return the request
         */
        public PersistenceRequest getRequest()
        {
            return request;
        }

        /**
         * @param inRequest
         *            the request to set
         */
        public void setRequest(final PersistenceRequest inRequest)
        {
            request = inRequest;
        }

    }
}
