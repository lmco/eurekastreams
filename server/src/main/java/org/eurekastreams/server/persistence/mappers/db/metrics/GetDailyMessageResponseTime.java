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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.commons.date.DateDayExtractor;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.service.actions.requests.UsageMetricDailyStreamInfoRequest;

/**
 * DB Mapper to get the avgerage activity response time of a specific day.
 */
public class GetDailyMessageResponseTime extends BaseArgDomainMapper<UsageMetricDailyStreamInfoRequest, Long>
{

    /**
     * Thanks checkstyle.
     */
    private final int millisInAMinute = 60000;

    /**
     * Get the number of stream viewers on a specific day.
     * 
     * @param inRequest
     *            the UsageMetricDailyStreamInfoRequest
     * @return the number of stream viewers on the input day
     */
    @SuppressWarnings("unchecked")
    @Override
    public Long execute(final UsageMetricDailyStreamInfoRequest inRequest)
    {
        Date startOfDay, endOfDay;
        Query q;

        startOfDay = DateDayExtractor.getStartOfDay(inRequest.getMetricsDate());
        endOfDay = DateDayExtractor.getEndOfDay(inRequest.getMetricsDate());

        if (inRequest.getStreamRecipientStreamScopeId() == null)
        {
            // all streams
            q = getEntityManager().createQuery(
                    "SELECT MIN(c.timeSent), MIN(c.target.postedTime) FROM Comment c INNER JOIN c.target "
                            + "WHERE c.timeSent >= :startDate AND c.timeSent <= :endDate AND "
                            + "c.target.postedTime >= :startDate AND c.target.postedTime <= :endDate "
                            + "GROUP BY c.target.id").setParameter("startDate", startOfDay).setParameter("endDate",
                    endOfDay);
        }
        else
        {
            // specific stream
            q = getEntityManager().createQuery(
                    "SELECT MIN(c.timeSent), MIN(c.target.postedTime) FROM Comment c INNER JOIN c.target "
                            + "WHERE c.timeSent >= :startDate AND c.timeSent <= :endDate AND "
                            + "c.target.postedTime >= :startDate AND c.target.postedTime <= :endDate "
                            + "AND c.target.recipientStreamScope.id = :recipientStreamScopeId "
                            + "GROUP BY c.target.id").setParameter("startDate", startOfDay).setParameter("endDate",
                    endOfDay).setParameter("recipientStreamScopeId", inRequest.getStreamRecipientStreamScopeId());
        }

        List<Object[]> results = q.getResultList();

        // short circuit if no results.
        int numResults = results.size();
        if (numResults == 0)
        {
            return 0L;
        }

        Calendar commentDate = Calendar.getInstance();
        Calendar activityDate = Calendar.getInstance();

        long commentTimeMillis, activityTimeMillis;
        long timeDiffInMillis = 0;

        for (Object[] row : results)
        {
            commentDate.setTime((Date) row[0]);
            activityDate.setTime((Date) row[1]);

            commentTimeMillis = commentDate.getTimeInMillis();
            activityTimeMillis = activityDate.getTimeInMillis();

            timeDiffInMillis += (commentTimeMillis - activityTimeMillis);
        }

        // return avg activity response time in min.
        return (timeDiffInMillis / numResults) / millisInAMinute;
    }
}
