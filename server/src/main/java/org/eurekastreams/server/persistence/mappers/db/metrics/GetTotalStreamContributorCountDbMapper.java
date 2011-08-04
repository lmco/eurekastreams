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

import java.util.HashSet;

import javax.persistence.Query;

import org.eurekastreams.commons.date.DateDayExtractor;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.service.actions.requests.UsageMetricDailyStreamInfoRequest;

/**
 * Mapper that gets the total number of contributors (activity and comment) for a stream by stream scope id.
 */
public class GetTotalStreamContributorCountDbMapper extends
        BaseArgDomainMapper<UsageMetricDailyStreamInfoRequest, Long>
{
    /**
     * Get the total number of contributors to a stream by stream scope id.
     * 
     * @param inRequest
     *            the recipient stream scope and date to get the statistics for
     * @return the number of distinct authors of activity and comments for the stream with the input stream scope id
     */
    @Override
    public Long execute(final UsageMetricDailyStreamInfoRequest inRequest)
    {
        Query activityQuery, commentQuery;
        HashSet<String> peopleIds = new HashSet<String>();

        activityQuery = getEntityManager()
                .createQuery(
                        "SELECT DISTINCT(actorId) FROM Activity WHERE actorType=:actorType "
                                + "AND recipientStreamScope.id = :recipientStreamScopeId AND postedTime < :reportDate")
                .setParameter("actorType", EntityType.PERSON)
                .setParameter("recipientStreamScopeId", inRequest.getStreamRecipientStreamScopeId())
                .setParameter("reportDate", DateDayExtractor.getEndOfDay(inRequest.getMetricsDate()));

        commentQuery = getEntityManager()
                .createQuery(
                        "SELECT DISTINCT(author.accountId) FROM Comment "
                                + "WHERE target.recipientStreamScope.id = :recipientStreamScopeId "
                                + "AND timeSent < :reportDate")
                .setParameter("recipientStreamScopeId", inRequest.getStreamRecipientStreamScopeId())
                .setParameter("reportDate", DateDayExtractor.getEndOfDay(inRequest.getMetricsDate()));

        // need to use a set here to find the uniques between the activity and comment authors
        peopleIds.addAll(activityQuery.getResultList());
        peopleIds.addAll(commentQuery.getResultList());

        return new Long(peopleIds.size());
    }

}
