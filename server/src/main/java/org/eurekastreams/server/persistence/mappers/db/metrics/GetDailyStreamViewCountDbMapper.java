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

import javax.persistence.Query;

import org.eurekastreams.commons.date.DateDayExtractor;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.service.actions.requests.UsageMetricDailyStreamInfoRequest;

/**
 * DB Mapper to get the stream view count of a specific day.
 */
public class GetDailyStreamViewCountDbMapper extends BaseArgDomainMapper<UsageMetricDailyStreamInfoRequest, Long>
{
    /**
     * Get the number of stream view counts on a specific day.
     * 
     * @param inRequest
     *            the UsageMetricDailyStreamInfoRequest
     * @return the number of stream view counts on the input day
     */
    @Override
    public Long execute(final UsageMetricDailyStreamInfoRequest inRequest)
    {
        Query q;
        Date startOfDay, endOfDay;

        startOfDay = DateDayExtractor.getStartOfDay(inRequest.getMetricsDate());
        endOfDay = DateDayExtractor.getEndOfDay(inRequest.getMetricsDate());
        if (inRequest.getStreamRecipientStreamScopeId() == null)
        {
            // all streams
            q = getEntityManager().createQuery(
                    "SELECT COUNT(id) FROM UsageMetric "
                            + "WHERE isStreamView = true AND created >= :startDate AND created <= :endDate")
                    .setParameter("startDate", startOfDay).setParameter("endDate", endOfDay);
        }
        else
        {
            // specific stream
            q = getEntityManager().createQuery(
                    "SELECT COUNT(id) FROM UsageMetric "
                            + "WHERE isStreamView = true AND created >= :startDate AND created <= :endDate "
                            + "AND streamViewStreamScopeId = :streamViewStreamScopeId").setParameter("startDate",
                    startOfDay).setParameter("endDate", endOfDay).setParameter("streamViewStreamScopeId",
                    inRequest.getStreamRecipientStreamScopeId());
        }
        return (Long) q.getSingleResult();
    }
}
