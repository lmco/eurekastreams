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
package org.eurekastreams.server.action.execution;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.commons.date.DateDayExtractor;
import org.eurekastreams.commons.date.WeekdaysInDateRangeStrategy;
import org.eurekastreams.server.domain.DailyUsageSummary;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.UsageMetricSummaryDTO;
import org.eurekastreams.server.service.actions.requests.UsageMetricStreamSummaryRequest;
import org.eurekastreams.server.testing.TestContextCreator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.ibm.icu.util.Calendar;

/**
 * Test fixture for GetUsageMetricSummaryExecution.
 */
public class GetUsageMetricSummaryExecutionTest
{
    /**
     * System under test.
     */
    private GetUsageMetricSummaryExecution sut;

    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mapper to get the summary data for a stream, or all streams.
     */
    private final DomainMapper<UsageMetricStreamSummaryRequest, List<DailyUsageSummary>> summaryDataMapper = context
            .mock(DomainMapper.class, "summaryDataMapper");

    /**
     * Strategy to get the number of weekdays between two dates.
     */
    private final WeekdaysInDateRangeStrategy weekdaysInDateRangeStrategy = context.mock(
            WeekdaysInDateRangeStrategy.class, "weekdaysInDateRangeStrategy");

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new GetUsageMetricSummaryExecution(summaryDataMapper, weekdaysInDateRangeStrategy);
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        final UsageMetricStreamSummaryRequest request = new UsageMetricStreamSummaryRequest(3, 4L);
        final List<DailyUsageSummary> results = new ArrayList<DailyUsageSummary>();
        Calendar cal;

        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        final Date yesterday = DateDayExtractor.getStartOfDay(cal.getTime());

        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);
        final Date twoDaysAgo = DateDayExtractor.getStartOfDay(cal.getTime());

        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -3);
        final Date threeDaysAgo = DateDayExtractor.getStartOfDay(cal.getTime());

        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -4);
        final Date fourDaysAgo = DateDayExtractor.getStartOfDay(cal.getTime());

        // first result is ignored b/c it's outside the date range
        results.add(new DailyUsageSummary(10L, 20L, 30L, 40L, 50L, 60L, 70L, fourDaysAgo, //
                1308238511000L, 80L, 91L, 101L, 111L, 121L));
        results.add(new DailyUsageSummary(20L, 30L, 40L, 50L, 60L, 70L, 80L, threeDaysAgo, //
                1308238511000L, 90L, 100L, 110L, 120L, 130L));
        results.add(new DailyUsageSummary(30L, 40L, 50L, 60L, 70L, 80L, 90L, twoDaysAgo, //
                1308238511000L, 100L, 110L, 120L, 130L, 140L));
        results.add(new DailyUsageSummary(40L, 50L, 60L, 70L, 80L, 90L, 110L, yesterday, //
                1308238511000L, 110L, 120L, 130L, 140L, 150L));

        context.checking(new Expectations()
        {
            {
                oneOf(summaryDataMapper).execute(with(request));
                will(returnValue(results));

                oneOf(weekdaysInDateRangeStrategy).getWeekdayCountBetweenDates(
                        with(DateDayExtractor.getStartOfDay(threeDaysAgo)),
                        with(DateDayExtractor.getStartOfDay(new Date())));
                will(returnValue(10));
            }
        });

        // invoke
        UsageMetricSummaryDTO result = (UsageMetricSummaryDTO) sut.execute(TestContextCreator
                .createPrincipalActionContext(request, null));

        Assert.assertEquals(10, result.getWeekdayRecordCount());
        Assert.assertEquals(9, result.getAverageDailyUniqueVisitorCount());
        Assert.assertEquals(12, result.getAverageDailyPageViewCount());
        Assert.assertEquals(15, result.getAverageDailyStreamViewerCount());
        Assert.assertEquals(18, result.getAverageDailyStreamViewCount());
        Assert.assertEquals(21, result.getAverageDailyStreamContributorCount());
        Assert.assertEquals(24, result.getAverageDailyMessageCount());
        Assert.assertEquals(new Long(120), result.getTotalActivityCount());
        Assert.assertEquals(new Long(130), result.getTotalCommentCount());
        Assert.assertEquals(new Long(140), result.getTotalStreamViewCount());
        Assert.assertEquals(new Long(150), result.getTotalContributorCount());
        Assert.assertEquals(2, result.getAverageDailyCommentCount());
    }
}
