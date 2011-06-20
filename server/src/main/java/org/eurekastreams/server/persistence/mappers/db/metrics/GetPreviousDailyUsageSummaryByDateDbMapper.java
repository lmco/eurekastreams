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
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.commons.date.DateDayExtractor;
import org.eurekastreams.server.domain.DailyUsageSummary;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.service.actions.requests.UsageMetricDailyStreamInfoRequest;

/**
 * Mapper to get the most recent DailyUsageSummary before a specified date.
 */
public class GetPreviousDailyUsageSummaryByDateDbMapper extends
        BaseArgDomainMapper<UsageMetricDailyStreamInfoRequest, DailyUsageSummary>
{
    /**
     * Get the DailyUsageSummary prior to the input date - the date is normalized to be 12:00AM that day.
     * 
     * @param inRequest
     *            the request
     * @return the daily usage summary on the most recent date prior to the input one, or null
     */
    @Override
    public DailyUsageSummary execute(final UsageMetricDailyStreamInfoRequest inRequest)
    {
        List<DailyUsageSummary> results;
        Query q;

        if (inRequest.getStreamRecipientStreamScopeId() == null)
        {
            return null;
        }
        Date date = DateDayExtractor.getStartOfDay(inRequest.getMetricsDate());

        // specific stream
        q = getEntityManager().createQuery(
                "FROM DailyUsageSummary WHERE usageDate < :usageDate "
                        + "AND streamViewStreamScopeId = :streamViewStreamScopeId ORDER BY usageDate DESC");
        q.setParameter("usageDate", date);
        q.setParameter("streamViewStreamScopeId", inRequest.getStreamRecipientStreamScopeId());
        q.setMaxResults(1);

        results = q.getResultList();
        if (results.size() > 0)
        {
            return results.get(0);
        }
        return null;
    }

}
