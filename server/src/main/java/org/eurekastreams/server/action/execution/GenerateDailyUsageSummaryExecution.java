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
import java.util.Date;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.date.DayOfWeekStrategy;
import org.eurekastreams.commons.date.GetDateFromDaysAgoStrategy;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.DailyUsageSummary;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.eurekastreams.server.service.actions.requests.UsageMetricDailyStreamInfoRequest;

/**
 * Execution strategy to generate the daily usage summary for the previous day.
 */
public class GenerateDailyUsageSummaryExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * Logger.
     */
    private Log logger = LogFactory.make();

    /**
     * strategy to get a date from N days ago.
     */
    private GetDateFromDaysAgoStrategy daysAgoDateStrategy;

    // mappers that apply to the whole system

    /**
     * Mapper to get a day's page view count - for the whole system.
     */
    private DomainMapper<Date, Long> getDailyPageViewCountMapper;

    /**
     * Mapper to get a day's unique visitor count - for whole system.
     */
    private DomainMapper<Date, Long> getDailyUniqueVisitorCountMapper;

    // mappers that may be scoped to a particular thread

    /**
     * Mapper to get a single day's DailyUsageSummary - for a stream or the whole system.
     */
    private DomainMapper<UsageMetricDailyStreamInfoRequest, DailyUsageSummary> getDailyUsageSummaryByDateMapper;

    /**
     * Mapper to get a day's message count - for a stream or the whole system.
     */
    private DomainMapper<UsageMetricDailyStreamInfoRequest, Long> getDailyMessageCountMapper;

    /**
     * Mapper to get a day's stream contributor count - for a stream or the whole system.
     */
    private DomainMapper<UsageMetricDailyStreamInfoRequest, Long> getDailyStreamContributorCountMapper;

    /**
     * Mapper to get a day's stream view count - for a stream or the whole system.
     */
    private DomainMapper<UsageMetricDailyStreamInfoRequest, Long> getDailyStreamViewCountMapper;

    /**
     * Mapper to get a day's stream viewer count - for a stream or the whole system.
     */
    private DomainMapper<UsageMetricDailyStreamInfoRequest, Long> getDailyStreamViewerCountMapper;

    /**
     * Mapper to get day's average activity response time (for those that had responses) - for a stream or the whole
     * system.
     */
    private DomainMapper<UsageMetricDailyStreamInfoRequest, Long> getDailyMessageResponseTimeMapper;

    // helpers

    /**
     * Mapper to delete old UsageMetric data.
     */
    private DomainMapper<Serializable, Serializable> usageMetricDataCleanupMapper;

    /**
     * Mapper to insert the DailyUsageSummary entity.
     */
    private DomainMapper<PersistenceRequest<DailyUsageSummary>, Boolean> insertMapper;

    /**
     * Strategy to determine if a day is a weekday.
     */
    private DayOfWeekStrategy dayOfWeekStrategy;

    /**
     * Constructor.
     * 
     * @param inDaysAgoDateStrategy
     *            strategy to get a date from yesterday
     * @param inGetDailyUsageSummaryByDateMapper
     *            Mapper to get a single day's DailyUsageSummary
     * @param inGetDailyMessageCountMapper
     *            Mapper to get a day's message count
     * @param inGetDailyPageViewCountMapper
     *            Mapper to get a day's page view count.
     * @param inGetDailyStreamContributorCountMapper
     *            Mapper to get a day's stream contributor count.
     * @param inGetDailyStreamViewCountMapper
     *            Mapper to get a day's stream view count.
     * @param inGetDailyStreamViewerCountMapper
     *            Mapper to get a day's stream viewer count.
     * @param inGetDailyUniqueVisitorCountMapper
     *            Mapper to get a day's unique visitor count.
     * @param inGetDailyMessageResponseTimeMapper
     *            Mapper to get day's average activity response time (for those that had responses).
     * @param inInsertMapper
     *            mapper to insert DailyUsageSummary
     * @param inUsageMetricDataCleanupMapper
     *            mapper to delete old UsageMetric data
     * @param inDayOfWeekStrategy
     *            dayOfWeekStrategy strategy to determine if a day is a weekday
     */
    public GenerateDailyUsageSummaryExecution(
            final GetDateFromDaysAgoStrategy inDaysAgoDateStrategy,
            final DomainMapper<UsageMetricDailyStreamInfoRequest, DailyUsageSummary> inGetDailyUsageSummaryByDateMapper,
            final DomainMapper<UsageMetricDailyStreamInfoRequest, Long> inGetDailyMessageCountMapper,
            final DomainMapper<Date, Long> inGetDailyPageViewCountMapper,
            final DomainMapper<UsageMetricDailyStreamInfoRequest, Long> inGetDailyStreamContributorCountMapper,
            final DomainMapper<UsageMetricDailyStreamInfoRequest, Long> inGetDailyStreamViewCountMapper,
            final DomainMapper<UsageMetricDailyStreamInfoRequest, Long> inGetDailyStreamViewerCountMapper,
            final DomainMapper<Date, Long> inGetDailyUniqueVisitorCountMapper,
            final DomainMapper<UsageMetricDailyStreamInfoRequest, Long> inGetDailyMessageResponseTimeMapper,
            final DomainMapper<PersistenceRequest<DailyUsageSummary>, Boolean> inInsertMapper,
            final DomainMapper<Serializable, Serializable> inUsageMetricDataCleanupMapper,
            final DayOfWeekStrategy inDayOfWeekStrategy)
    {
        daysAgoDateStrategy = inDaysAgoDateStrategy;
        getDailyUsageSummaryByDateMapper = inGetDailyUsageSummaryByDateMapper;
        getDailyMessageCountMapper = inGetDailyMessageCountMapper;
        getDailyPageViewCountMapper = inGetDailyPageViewCountMapper;
        getDailyStreamContributorCountMapper = inGetDailyStreamContributorCountMapper;
        getDailyStreamViewCountMapper = inGetDailyStreamViewCountMapper;
        getDailyStreamViewerCountMapper = inGetDailyStreamViewerCountMapper;
        getDailyUniqueVisitorCountMapper = inGetDailyUniqueVisitorCountMapper;
        getDailyMessageResponseTimeMapper = inGetDailyMessageResponseTimeMapper;
        insertMapper = inInsertMapper;
        usageMetricDataCleanupMapper = inUsageMetricDataCleanupMapper;
        dayOfWeekStrategy = inDayOfWeekStrategy;
    }

    /**
     * Generate the daily usage summary for the previous day.
     * 
     * @param inActionContext
     *            the action context
     * @return true if data was inserted, false if already existed
     * @throws ExecutionException
     *             when something really, really bad happens
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
            throws ExecutionException
    {
        Date yesterday = daysAgoDateStrategy.execute(1);

        // see if we already have data for yesterday
        DailyUsageSummary data = getDailyUsageSummaryByDateMapper.execute(new UsageMetricDailyStreamInfoRequest(
                yesterday, null));
        if (data != null)
        {
            logger.info("No need to create daily usage data for " + yesterday + " - already exists.");
            return Boolean.FALSE;
        }

        logger.info("Generating number of unique visitors for " + yesterday);
        long uniqueVisitorCount = getDailyUniqueVisitorCountMapper.execute(yesterday);

        logger.info("Generating number of page views for " + yesterday);
        long pageViewCount = getDailyPageViewCountMapper.execute(yesterday);

        logger.info("Generating number of stream views for " + yesterday);
        long streamViewCount = getDailyStreamViewCountMapper.execute(new UsageMetricDailyStreamInfoRequest(yesterday,
                null));

        logger.info("Generating number of stream viewers for " + yesterday);
        long streamViewerCount = getDailyStreamViewerCountMapper.execute(new UsageMetricDailyStreamInfoRequest(
                yesterday, null));

        logger.info("Generating number of stream contributors for " + yesterday);
        long streamContributorCount = getDailyStreamContributorCountMapper
                .execute(new UsageMetricDailyStreamInfoRequest(yesterday, null));

        logger.info("Generating number of messages (activities and comments) for " + yesterday);
        long messageCount = getDailyMessageCountMapper.execute(new UsageMetricDailyStreamInfoRequest(yesterday, null));

        logger.info("Generating average activity comment time (for those with comments on the same day) for "
                + yesterday);
        long avgActvityResponeTime = getDailyMessageResponseTimeMapper.execute(new UsageMetricDailyStreamInfoRequest(
                yesterday, null));

        boolean isWeekday = dayOfWeekStrategy.isWeekday(yesterday);

        data = new DailyUsageSummary(uniqueVisitorCount, pageViewCount, streamViewerCount, streamViewCount,
                streamContributorCount, messageCount, avgActvityResponeTime, yesterday, isWeekday);

        // store this
        logger.info("Inserting daily usage metric data for " + yesterday);
        insertMapper.execute(new PersistenceRequest<DailyUsageSummary>(data));

        // delete old data
        logger.info("Deleting old daily usage metric data");
        usageMetricDataCleanupMapper.execute(0);

        logger.info("Inserted Daily Summary metrics for " + yesterday);
        return Boolean.TRUE;
    }
}
