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
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.service.actions.requests.UsageMetricDailyStreamInfoRequest;

/**
 * DB Mapper to get the message (activity and comment) count of a specific day.
 */
public class GetDailyMessageCountDbMapper extends BaseArgDomainMapper<UsageMetricDailyStreamInfoRequest, Long>
{
    /**
     * Get the number of messages (activity and comment) posted on a specific day.
     * 
     * @param inRequest
     *            the UsageMetricDailyStreamInfoRequest
     * @return the number of activities and comments posted on the input day
     */
    @Override
    public Long execute(final UsageMetricDailyStreamInfoRequest inRequest)
    {
        Long activityCount, commentCount;
        Date startOfDay, endOfDay;
        Query q;

        startOfDay = DateDayExtractor.getStartOfDay(inRequest.getMetricsDate());
        endOfDay = DateDayExtractor.getEndOfDay(inRequest.getMetricsDate());

        // get the activity count
        q = getEntityManager().createQuery(
                "SELECT COUNT(*) FROM Activity WHERE (appType = null OR appType != :plugin) AND "
                        + "postedTime >= :startDate AND postedTime <= :endDate").setParameter("startDate", startOfDay)
                .setParameter("endDate", endOfDay).setParameter("plugin", EntityType.PLUGIN);
        activityCount = (Long) q.getSingleResult();

        // get the comment count
        q = getEntityManager().createQuery(
                "SELECT COUNT(*) FROM Comment WHERE timeSent >= :startDate AND timeSent <= :endDate").setParameter(
                "startDate", startOfDay).setParameter("endDate", endOfDay);
        commentCount = (Long) q.getSingleResult();

        return activityCount + commentCount;
    }

}
