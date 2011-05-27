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
package org.eurekastreams.server.persistence.mappers.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.eurekastreams.server.domain.DailyUsageSummary;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.db.metrics.GetUsageMetricSummaryDbMapper;
import org.eurekastreams.server.search.modelview.UsageMetricSummaryDTO;
import org.eurekastreams.server.service.actions.requests.UsageMetricStreamSummaryRequest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetUsageMetricSummaryDbMapper.
 * 
 */
public class GetUsageMetricSummaryDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private final GetUsageMetricSummaryDbMapper sut = new GetUsageMetricSummaryDbMapper();

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test.
     */
    @Test
    public void test()
    {
        getEntityManager().createQuery("DELETE FROM DailyUsageSummary").executeUpdate();
        getEntityManager().flush();

        UsageMetricSummaryDTO result = sut.execute(new UsageMetricStreamSummaryRequest(3, null));
        assertNotNull(result);
        assertEquals(0, result.getRecordCount());
    }

    /**
     * Test.
     */
    @Test
    public void testValues()
    {
        /**
         * April 7th, 2011 in ticks.
         */
        final long april11th2001 = 987012529000L;
        final long jan1th2001 = 1302804695000L;
        final long msInDay = 86400000L;

        final long resultValue = 15;
        final long count = 10;
        // clear table.
        getEntityManager().createQuery("DELETE FROM DailyUsageSummary").executeUpdate();
        getEntityManager().flush();

        // put in two records, one with 10 for values, one with 20
        for (int i = 1; i <= 2; i++)
        {
            DailyUsageSummary foo = new DailyUsageSummary();
            foo.setMessageCount(count * i);
            foo.setPageViewCount(count * i);
            foo.setStreamContributorCount(count * i);
            foo.setStreamViewCount(count * i);
            foo.setStreamViewerCount(count * i);
            foo.setUniqueVisitorCount(count * i);
            foo.setUsageDate(new Date(april11th2001 + i * msInDay));
            foo.setAvgActivityResponseTime(count * i);
            foo.setWeekday(true);

            getEntityManager().persist(foo);
            getEntityManager().flush();
            assertTrue(foo.getId() > 0);
        }

        // throw in some weekend days that will be ignored
        getEntityManager().persist(
                new DailyUsageSummary(1, 2, 3, 4, 5, 6, 7, new Date(jan1th2001 + 1 * msInDay), false));
        getEntityManager().persist(
                new DailyUsageSummary(1, 2, 3, 4, 5, 6, 7, new Date(jan1th2001 + 2 * msInDay), false));
        getEntityManager().persist(
                new DailyUsageSummary(1, 2, 3, 4, 5, 6, 7, new Date(jan1th2001 + 3 * msInDay), false));
        getEntityManager().persist(
                new DailyUsageSummary(1, 2, 3, 4, 5, 6, 7, new Date(jan1th2001 + 4 * msInDay), false));
        getEntityManager().persist(
                new DailyUsageSummary(1, 2, 3, 4, 5, 6, 7, new Date(jan1th2001 + 5 * msInDay), false));
        getEntityManager().persist(
                new DailyUsageSummary(1, 2, 3, 4, 5, 6, 7, new Date(jan1th2001 + 6 * msInDay), false));
        getEntityManager().persist(
                new DailyUsageSummary(1, 2, 3, 4, 5, 6, 7, new Date(jan1th2001 + 7 * msInDay), false));
        getEntityManager().persist(
                new DailyUsageSummary(1, 2, 3, 4, 5, 6, 7, new Date(jan1th2001 + 8 * msInDay), false));

        getEntityManager().flush();
        getEntityManager().clear();

        // execute sut.
        UsageMetricSummaryDTO result = sut.execute(new UsageMetricStreamSummaryRequest(3, null));

        // verfiy row count and averages.
        assertNotNull(result);
        assertEquals(2, result.getRecordCount());
        assertEquals(resultValue, result.getMessageCount());
        assertEquals(resultValue, result.getPageViewCount());
        assertEquals(resultValue, result.getStreamContributorCount());
        assertEquals(resultValue, result.getStreamViewCount());
        assertEquals(resultValue, result.getStreamViewerCount());
        assertEquals(resultValue, result.getUniqueVisitorCount());
        assertEquals(resultValue, result.getAvgActivityResponseTime());
    }

}
