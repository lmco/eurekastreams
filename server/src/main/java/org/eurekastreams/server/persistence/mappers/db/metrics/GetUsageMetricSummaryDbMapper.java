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

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.DailyUsageSummary;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.search.modelview.UsageMetricSummaryDTO;
import org.eurekastreams.server.service.actions.requests.UsageMetricStreamSummaryRequest;

/**
 * Mapper to get UsageMetricSummary.
 */
public class GetUsageMetricSummaryDbMapper extends
        BaseArgDomainMapper<UsageMetricStreamSummaryRequest, UsageMetricSummaryDTO>
{
    /**
     * Get Summary usage metrics for last X number of days.
     * 
     * @param inRequest
     *            the UsageMetricStreamSummaryRequest
     * @return {@link UsageMetricSummaryDTO} representing given time period.
     */
    @SuppressWarnings("unchecked")
    @Override
    public UsageMetricSummaryDTO execute(final UsageMetricStreamSummaryRequest inRequest)
    {
        Query q;
        if (inRequest.getStreamRecipientStreamScopeId() == null)
        {
            // all streams
            q = getEntityManager().createQuery(
                    "FROM DailyUsageSummary WHERE streamViewStreamScopeId IS NULL "
                            + "AND isWeekday = true ORDER BY id DESC");
        }
        else
        {
            // specific stream
            q = getEntityManager().createQuery(
                    "FROM DailyUsageSummary WHERE streamViewStreamScopeId = :streamViewStreamScopeId "
                            + "AND isWeekday = true ORDER BY id DESC").setParameter("streamViewStreamScopeId",
                    inRequest.getStreamRecipientStreamScopeId());
        }
        q.setMaxResults(inRequest.getNumberOfDays());

        List<DailyUsageSummary> results = q.getResultList();

        int numResults = results.size();

        UsageMetricSummaryDTO result = new UsageMetricSummaryDTO();
        result.setRecordCount(results.size());

        // short-circuit if no results.
        if (numResults == 0)
        {
            return result;
        }

        long msgCount = 0;
        long pageViewCount = 0;
        long streamContributorCount = 0;
        long streamViewCount = 0;
        long streamViewerCount = 0;
        long uniqueVisitorCount = 0;
        long avgActivityResponseTime = 0;

        for (DailyUsageSummary dus : results)
        {
            msgCount += dus.getMessageCount();
            pageViewCount += dus.getPageViewCount();
            streamContributorCount += dus.getStreamContributorCount();
            streamViewCount += dus.getStreamViewCount();
            streamViewerCount += dus.getStreamViewerCount();
            uniqueVisitorCount += dus.getUniqueVisitorCount();
            avgActivityResponseTime += dus.getAvgActivityResponseTime();
        }

        result.setMessageCount(msgCount / numResults);
        result.setPageViewCount(pageViewCount / numResults);
        result.setStreamContributorCount(streamContributorCount / numResults);
        result.setStreamViewCount(streamViewCount / numResults);
        result.setStreamViewerCount(streamViewerCount / numResults);
        result.setUniqueVisitorCount(uniqueVisitorCount / numResults);
        result.setAvgActivityResponseTime(avgActivityResponseTime / numResults);

        return result;
    }
}
