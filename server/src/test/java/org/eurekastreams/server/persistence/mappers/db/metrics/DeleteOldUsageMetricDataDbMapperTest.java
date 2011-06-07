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

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.eurekastreams.server.domain.DailyUsageSummary;
import org.eurekastreams.server.domain.UsageMetric;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ibm.icu.util.Calendar;

/**
 * Test fixture for DeleteOldUsageMetricDataDbMapper.
 */
public class DeleteOldUsageMetricDataDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private DeleteOldUsageMetricDataDbMapper sut;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        final int ten = 10;
        sut = new DeleteOldUsageMetricDataDbMapper(3, ten);
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        final int neg1 = -1;
        final int neg2 = -2;
        final int neg4 = -4;
        final int neg9 = -9;
        final int neg11 = -11;
        final int neg12 = -12;

        Calendar day = Calendar.getInstance();
        day.add(Calendar.DATE, neg2);
        Date twoDaysAgo = new Date(day.getTimeInMillis());

        day = Calendar.getInstance();
        day.add(Calendar.DATE, neg4);
        day.add(Calendar.MINUTE, neg1);
        Date fourDaysAgo = new Date(day.getTimeInMillis());

        day = Calendar.getInstance();
        day.add(Calendar.DATE, neg9);
        day.add(Calendar.MINUTE, neg1);
        Date nineDaysAgo = new Date(day.getTimeInMillis());

        day = Calendar.getInstance();
        day.add(Calendar.DATE, neg11);
        day.add(Calendar.MINUTE, neg1);
        Date elevenDaysAgo = new Date(day.getTimeInMillis());

        day = Calendar.getInstance();
        day.add(Calendar.DATE, neg12);
        day.add(Calendar.MINUTE, neg1);
        Date twelveDaysAgo = new Date(day.getTimeInMillis());

        // delete all existing usage metrics and summary
        getEntityManager().createQuery("DELETE FROM UsageMetric").executeUpdate();
        getEntityManager().createQuery("DELETE FROM DailyUsageSummary").executeUpdate();

        // three usage metrics from two days ago
        getEntityManager().persist(new UsageMetric(1, true, true, 1L, twoDaysAgo));
        getEntityManager().persist(new UsageMetric(1, true, true, 1L, twoDaysAgo));
        getEntityManager().persist(new UsageMetric(1, true, true, 1L, twoDaysAgo));

        // 2 usage metrics from just over 4 days ago - these should be deleted
        getEntityManager().persist(new UsageMetric(2, true, true, 1L, fourDaysAgo));
        getEntityManager().persist(new UsageMetric(2, true, true, 1L, fourDaysAgo));

        // summary metrics from 4 & 9 days ago - should be saved
        getEntityManager().persist(new DailyUsageSummary(1, 2, 3, 4, 5, 6, 7, fourDaysAgo, 5L, 5L, 5L, 5L, 5L));
        getEntityManager().persist(new DailyUsageSummary(1, 2, 3, 4, 5, 6, 7, nineDaysAgo, 5L, 5L, 5L, 5L, 5L));

        // summary metrics from 11-12 days ago - should be deleted
        getEntityManager().persist(new DailyUsageSummary(2, 2, 3, 4, 5, 6, 7, elevenDaysAgo, 5L, 5L, 5L, 5L, 5L));
        getEntityManager().persist(new DailyUsageSummary(2, 2, 3, 4, 5, 6, 7, twelveDaysAgo, 5L, 5L, 5L, 5L, 5L));

        getEntityManager().flush();
        getEntityManager().clear();

        sut.execute(0);

        List<UsageMetric> existingMetricData = getEntityManager().createQuery("FROM UsageMetric").getResultList();
        Assert.assertEquals(3, existingMetricData.size());

        assertEquals(1L, existingMetricData.get(0).getActorPersonId());
        assertEquals(1L, existingMetricData.get(1).getActorPersonId());

        List<DailyUsageSummary> existingSummaryData = getEntityManager().createQuery("FROM DailyUsageSummary")
                .getResultList();
        Assert.assertEquals(2, existingSummaryData.size());

        assertEquals(1L, existingSummaryData.get(0).getUniqueVisitorCount());
        assertEquals(1L, existingSummaryData.get(1).getUniqueVisitorCount());
    }
}
