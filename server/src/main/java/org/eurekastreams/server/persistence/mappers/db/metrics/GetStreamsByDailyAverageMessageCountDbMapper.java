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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * DB Mapper to get a list of streams ordered by the daily average number of messages.
 */
public class GetStreamsByDailyAverageMessageCountDbMapper extends BaseArgDomainMapper<Serializable, List<Long>>
{
    /**
     * Number of streams to get.
     */
    private Integer streamCount;

    /**
     * Constructor.
     * 
     * @param inStreamCount
     *            the
     */
    public GetStreamsByDailyAverageMessageCountDbMapper(final Integer inStreamCount)
    {
        streamCount = inStreamCount;
    }

    /**
     * Get a list of the stream scope ids for the most active streams.
     * 
     * @param inIgnored
     *            ignored param - go nuts
     * @return list of stream scope ids
     */
    @Override
    public List<Long> execute(final Serializable inRequest)
    {
        return getEntityManager().createQuery(
                "SELECT streamViewStreamScopeId FROM DailyUsageSummary WHERE streamViewStreamScopeId IS NOT NULL "
                        + "GROUP BY streamViewStreamScopeId "
                        + "ORDER BY SUM(messageCount)*86400000.0/(:nowInMS - MIN(usageDateTimeStampInMs)) DESC")
                .setParameter("nowInMS", new Date().getTime()).setMaxResults(streamCount).getResultList();
    }

}
