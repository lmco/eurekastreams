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
import java.util.HashSet;

import javax.persistence.Query;

import org.eurekastreams.commons.date.DateDayExtractor;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.service.actions.requests.UsageMetricDailyStreamInfoRequest;

/**
 * DB Mapper to get the stream contributor count of a specific day, or all time if the day is null.
 */
public class GetDailyStreamContributorCountDbMapper extends
        BaseArgDomainMapper<UsageMetricDailyStreamInfoRequest, Long>
{
    /**
     * Get the number of stream contributors on a specific day, or all time if the day is null.
     * 
     * @param inRequest
     *            the UsageMetricDailyStreamInfoRequest
     * @return the number of stream contributors on the input day
     */
    @Override
    public Long execute(final UsageMetricDailyStreamInfoRequest inRequest)
    {
        Query activityQuery, commentQuery;
        Date startOfDay, endOfDay;
        HashSet<String> peopleIds = new HashSet<String>();

        startOfDay = DateDayExtractor.getStartOfDay(inRequest.getMetricsDate());
        endOfDay = DateDayExtractor.getEndOfDay(inRequest.getMetricsDate());

        if (inRequest.getStreamRecipientStreamScopeId() == null)
        {
            // all streams
            activityQuery = getEntityManager().createQuery(
                    "SELECT DISTINCT(actorId) FROM Activity WHERE actorType=:actorType"
                            + " AND postedTime >= :startDate AND postedTime <= :endDate").setParameter("startDate",
                    startOfDay).setParameter("endDate", endOfDay).setParameter("actorType", EntityType.PERSON);

            commentQuery = getEntityManager().createQuery(
                    "SELECT DISTINCT(author.accountId) FROM Comment WHERE timeSent >= :startDate"
                            + " AND timeSent <= :endDate").setParameter("startDate", startOfDay).setParameter(
                    "endDate", endOfDay);
        }
        else
        {
            // specific stream
            activityQuery = getEntityManager().createQuery(
                    "SELECT DISTINCT(actorId) FROM Activity WHERE actorType=:actorType"
                            + " AND postedTime >= :startDate AND postedTime <= :endDate "
                            + "AND recipientStreamScope.id = :recipientStreamScopeId").setParameter("startDate",
                    startOfDay).setParameter("endDate", endOfDay).setParameter("actorType", EntityType.PERSON)
                    .setParameter("recipientStreamScopeId", inRequest.getStreamRecipientStreamScopeId());

            commentQuery = getEntityManager().createQuery(
                    "SELECT DISTINCT(author.accountId) FROM Comment WHERE timeSent >= :startDate"
                            + " AND timeSent <= :endDate "
                            + "AND target.recipientStreamScope.id = :recipientStreamScopeId").setParameter("startDate",
                    startOfDay).setParameter("endDate", endOfDay).setParameter("recipientStreamScopeId",
                    inRequest.getStreamRecipientStreamScopeId());
        }

        // need to use a set here to find the uniques between the activity and comment authors
        peopleIds.addAll(activityQuery.getResultList());
        peopleIds.addAll(commentQuery.getResultList());

        return new Long(peopleIds.size());
    }
}
