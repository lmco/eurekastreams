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
package org.eurekastreams.server.persistence.mappers.db.metrics;

import java.util.Date;

import org.eurekastreams.server.domain.DailyUsageSummary;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.service.actions.requests.UsageMetricDailyStreamInfoRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetDailyUsageSummaryByDateDbMapper.
 */
public class GetDailyUsageSummaryByDateDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetDailyUsageSummaryByDateDbMapper sut;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new GetDailyUsageSummaryByDateDbMapper();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute() with expected data for all streams.
     */
    @Test
    public void testExecuteWithDataForAllStreams()
    {
        final long id = 1L;
        final long messagecount = 5L;
        final long pageviewcount = 55L;
        final long streamcontributorcount = 555L;
        final long streamviewcount = 5555L;
        final long streamviewercount = 55555L;
        final long uniquevisitorcount = 555555L;
        final long responseTime = 5555555L;

        final long april7th2011 = 1302196002000L;
        Date date = new Date(april7th2011);

        DailyUsageSummary result = sut.execute(new UsageMetricDailyStreamInfoRequest(date, null));
        Assert.assertEquals(id, result.getId());
        Assert.assertEquals(messagecount, result.getMessageCount());
        Assert.assertEquals(pageviewcount, result.getPageViewCount());
        Assert.assertEquals(streamcontributorcount, result.getStreamContributorCount());
        Assert.assertEquals(streamviewcount, result.getStreamViewCount());
        Assert.assertEquals(streamviewercount, result.getStreamViewerCount());
        Assert.assertEquals(uniquevisitorcount, result.getUniqueVisitorCount());
        Assert.assertEquals(responseTime, result.getAvgActivityResponseTime());
    }

    /**
     * Test execute() with expected data for specific stream.
     */
    @Test
    public void testExecuteWithDataForSpecificStreams()
    {
        final long streamScopeId = 1L;

        final long id = 4L;
        final long messagecount = 9L;
        final long pageviewcount = 99L;
        final long streamcontributorcount = 999L;
        final long streamviewcount = 9999L;
        final long streamviewercount = 100000L;
        final long uniquevisitorcount = 999999L;
        final long responseTime = 9999999L;

        final long april7th2011 = 1302196002000L;
        Date date = new Date(april7th2011);

        DailyUsageSummary result = sut.execute(new UsageMetricDailyStreamInfoRequest(date, streamScopeId));
        Assert.assertEquals(id, result.getId());
        Assert.assertEquals(messagecount, result.getMessageCount());
        Assert.assertEquals(pageviewcount, result.getPageViewCount());
        Assert.assertEquals(streamcontributorcount, result.getStreamContributorCount());
        Assert.assertEquals(streamviewcount, result.getStreamViewCount());
        Assert.assertEquals(streamviewercount, result.getStreamViewerCount());
        Assert.assertEquals(uniquevisitorcount, result.getUniqueVisitorCount());
        Assert.assertEquals(responseTime, result.getAvgActivityResponseTime());
    }

    /**
     * Test execute() with no expected data.
     */
    @Test
    public void testExecuteWithNoData()
    {
        final long april12th2011 = 1302628002000L;
        Date date = new Date(april12th2011);

        Assert.assertNull(sut.execute(new UsageMetricDailyStreamInfoRequest(date, null)));
    }
}
