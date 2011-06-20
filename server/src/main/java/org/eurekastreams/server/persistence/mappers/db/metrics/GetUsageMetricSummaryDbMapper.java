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
package org.eurekastreams.server.persistence.mappers.db.metrics;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.commons.date.DateDayExtractor;
import org.eurekastreams.server.domain.DailyUsageSummary;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.service.actions.requests.UsageMetricStreamSummaryRequest;

import com.ibm.icu.util.Calendar;

/**
 * Mapper to get UsageMetricSummary.
 */
public class GetUsageMetricSummaryDbMapper extends
        BaseArgDomainMapper<UsageMetricStreamSummaryRequest, List<DailyUsageSummary>>
{
    /**
     * Get Summary usage metrics for last X number of days.
     * 
     * @param inRequest
     *            the UsageMetricStreamSummaryRequest
     * @return List of DailyUsageSummary representing given time period.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<DailyUsageSummary> execute(final UsageMetricStreamSummaryRequest inRequest)
    {
        Query q;
        if (inRequest.getStreamRecipientStreamScopeId() == null)
        {
            // all streams
            q = getEntityManager().createQuery(
                    "FROM DailyUsageSummary WHERE streamViewStreamScopeId IS NULL "
                            + "AND usageDate >= :usageDate ORDER BY usageDate ASC");
        }
        else
        {
            // specific stream
            q = getEntityManager().createQuery(
                    "FROM DailyUsageSummary WHERE streamViewStreamScopeId = :streamViewStreamScopeId "
                            + "AND usageDate >= :usageDate ORDER BY usageDate ASC").setParameter(
                    "streamViewStreamScopeId", inRequest.getStreamRecipientStreamScopeId());
        }
        Calendar day = Calendar.getInstance();
        day.add(Calendar.DATE, -inRequest.getNumberOfDays());
        Date oldestReportDate = DateDayExtractor.getStartOfDay(new Date(day.getTimeInMillis()));
        q.setParameter("usageDate", oldestReportDate);
        return q.getResultList();
    }
}
