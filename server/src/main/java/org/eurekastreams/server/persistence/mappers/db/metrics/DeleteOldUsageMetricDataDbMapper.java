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

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

import com.ibm.icu.util.Calendar;

/**
 * Mapper to delete old UsageMetric data.
 */
public class DeleteOldUsageMetricDataDbMapper extends BaseArgDomainMapper<Serializable, Serializable>
{
    /**
     * The number of days of archive data to store.
     */
    private int daysOfDataToRetain;

    /**
     * Constructor.
     * 
     * @param inDaysOfDataToRetain
     *            the number of days of archive data to store
     */
    public DeleteOldUsageMetricDataDbMapper(final int inDaysOfDataToRetain)
    {
        daysOfDataToRetain = inDaysOfDataToRetain;
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
        Calendar day = Calendar.getInstance();
        day.add(Calendar.DATE, -daysOfDataToRetain);
        Date lastRetainDate = new Date(day.getTimeInMillis());

        getEntityManager().createQuery("DELETE FROM UsageMetric WHERE created < :lastRetainDate").setParameter(
                "lastRetainDate", lastRetainDate).executeUpdate();

        return Boolean.TRUE;
    }
}
