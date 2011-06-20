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

import java.io.Serializable;
import java.util.Date;

import org.eurekastreams.commons.date.DateDayExtractor;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

import com.ibm.icu.util.Calendar;

/**
 * Mapper to delete old UsageMetric data.
 */
public class DeleteOldUsageMetricDataDbMapper extends BaseArgDomainMapper<Serializable, Serializable>
{
    /**
     * The number of days of archive metric data to store.
     */
    private int daysOfMetricDataToRetain;

    /**
     * The number of days to archive summary data for.
     */
    private int daysOfSummaryDataToRetain;

    /**
     * Constructor.
     * 
     * @param inDaysOfMetricDataToRetain
     *            number of days of archive metric data to store
     * @param inDaysOfSummaryDataToRetain
     *            number of days to archive summary data for
     */
    public DeleteOldUsageMetricDataDbMapper(final int inDaysOfMetricDataToRetain, final int inDaysOfSummaryDataToRetain)
    {
        daysOfMetricDataToRetain = inDaysOfMetricDataToRetain;
        daysOfSummaryDataToRetain = inDaysOfSummaryDataToRetain;
    }

    /**
     * Delete UsageMetric data older than daysOfDataToRetain days.
     * 
     * @param inRequest
     *            ignored
     * @return Boolean.TRUE
     */
    @Override
    public Serializable execute(final Serializable inRequest)
    {
        Calendar day;
        Date lastRetainDate;

        // clean out metric data
        day = Calendar.getInstance();
        day.add(Calendar.DATE, -daysOfMetricDataToRetain);
        lastRetainDate = DateDayExtractor.getStartOfDay(new Date(day.getTimeInMillis()));
        getEntityManager().createQuery("DELETE FROM UsageMetric WHERE created < :lastRetainDate").setParameter(
                "lastRetainDate", lastRetainDate).executeUpdate();

        // clean out summary data
        day = Calendar.getInstance();
        day.add(Calendar.DATE, -daysOfSummaryDataToRetain);
        lastRetainDate = DateDayExtractor.getStartOfDay(new Date(day.getTimeInMillis()));
        getEntityManager().createQuery("DELETE FROM DailyUsageSummary WHERE usageDate < :lastRetainDate").setParameter(
                "lastRetainDate", lastRetainDate).executeUpdate();

        return Boolean.TRUE;
    }
}
