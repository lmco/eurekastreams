/*
 * Copyright (c) 2011-2013 Lockheed Martin Corporation
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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.dto.StreamDTO;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * DB Mapper to get a list of StreamDTOs sorted by the average daily viewers, descending.
 */
public class GetStreamsByDailyAverageViewersDbMapper extends BaseArgDomainMapper<Serializable, List<StreamDTO>>
{
    /**
     * Number of streams to get.
     */
    private final Integer streamCount;

    /**
     * Constructor.
     * 
     * @param inStreamCount
     *            the number of streams to get
     */
    public GetStreamsByDailyAverageViewersDbMapper(final Integer inStreamCount)
    {
        streamCount = inStreamCount;
    }

    /**
     * Get a list of the stream scope ids for the most viewed streams.
     * 
     * @param inIgnored
     *            ignored param - go nuts
     * @return list of stream scope ids
     */
    @Override
    public List<StreamDTO> execute(final Serializable inIgnored)
    {
        List<StreamDTO> results = new ArrayList<StreamDTO>();
        Query q;

        // to get the number of daily stream viewers, add up all of the counts that we received so far for each stream,
        // then divide that by how many week days have passed since the first day's record
        q = getEntityManager().createQuery(
                "SELECT dus.streamViewStreamScopeId, "
                        + "sum(dus.streamViewerCount * 1.0)/max(week.numberOfWeekdaysSinceDate * 1.0) "
                        + "FROM DailyUsageSummary dus, TempWeekdaysSinceDate week "
                        + "WHERE dus.streamViewStreamScopeId IS NOT NULL "
                        + "AND week.numberOfWeekdaysSinceDate IS NOT NULL AND dus.streamViewerCount IS NOT NULL "
                        + "AND dus.usageDateTimeStampInMs = week.dateTimeStampInMilliseconds "
                        + "GROUP BY streamViewStreamScopeId HAVING MAX(week.numberOfWeekdaysSinceDate * 1.0) > 0 "
                        + "AND sum(dus.streamViewerCount * 1.0) > 0 "
                        + "ORDER BY SUM(dus.streamViewerCount * 1.0)/MAX(week.numberOfWeekdaysSinceDate * 1.0) DESC");

        if (streamCount > 0)
        {
            q.setMaxResults(streamCount);
        }
        List<Object[]> streamObjs = q.getResultList();

        // extract the stream scope ids to get the streams
        List<Long> streamScopeIds = new ArrayList<Long>();
        for (Object[] streamObj : streamObjs)
        {
            streamScopeIds.add((Long) streamObj[0]);
        }

        if (streamScopeIds.size() == 0)
        {
            return results;
        }

        // List of object arrays [streamScopeId, StreamDTO]
        List<StreamDTO> streamDtos = new ArrayList<StreamDTO>();

        // get the people streams
        q = getEntityManager().createQuery(
                "SELECT new org.eurekastreams.server.search.modelview.PersonModelView(id, accountId, "
                        + "preferredName, lastName, displayName, displayNameSuffix, followersCount, "
                        + "dateAdded, streamScope.id) FROM Person " + "WHERE streamScope.id IN(:streamScopeIds)")
                .setParameter("streamScopeIds", streamScopeIds);
        streamDtos.addAll(q.getResultList());

        // get the group streams
        q = getEntityManager().createQuery(
                "SELECT new org.eurekastreams.server.search.modelview.DomainGroupModelView(id, "
                        + "shortName, name, 0, dateAdded, streamScope.id, publicGroup) FROM DomainGroup "
                        + "WHERE streamScope.id IN(:streamScopeIds)").setParameter("streamScopeIds", streamScopeIds);
        streamDtos.addAll(q.getResultList());

        // put the list back together, sorting the list
        Long streamScopeId, viewerCount;
        for (Object[] streamObj : streamObjs)
        {
            streamScopeId = (Long) streamObj[0];
            viewerCount = -1L;
            if (streamObj[1] != null)
            {
                viewerCount = Math.round(Math.ceil((Double) streamObj[1]));
            }

            if (viewerCount > -1)
            {
                // find the StreamDTO with the stream scope - hijack the followersCount to display the data
                for (StreamDTO streamDTO : streamDtos)
                {
                    if (streamDTO.getStreamScopeId().equals(streamScopeId))
                    {
                        if (streamDTO.getEntityType() == EntityType.PERSON)
                        {
                            ((PersonModelView) streamDTO).setFollowersCount(viewerCount.intValue());
                        }
                        else if (streamDTO.getEntityType() == EntityType.GROUP)
                        {
                            ((DomainGroupModelView) streamDTO).setFollowersCount(viewerCount.intValue());
                        }

                        results.add(streamDTO);
                        break;
                    }
                }
            }
        }

        return results;
    }
}
