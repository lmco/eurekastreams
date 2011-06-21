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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.dto.StreamDTO;
import org.eurekastreams.server.domain.dto.SublistWithResultCount;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * DB Mapper to get a list of streams ordered by the daily average number of messages (most active).
 */
public class GetStreamsByDailyAverageMessageCountDbMapper extends
        BaseArgDomainMapper<Serializable, SublistWithResultCount<StreamDTO>>
{
    /**
     * Number of streams to get.
     */
    private Integer streamCount;

    /**
     * Constructor.
     * 
     * @param inStreamCount
     *            the number of streams to pull
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
    public SublistWithResultCount<StreamDTO> execute(final Serializable inIgnored)
    {
        List<StreamDTO> results = new ArrayList<StreamDTO>();

        Query q = getEntityManager().createQuery(
                "SELECT streamViewStreamScopeId, SUM(messageCount)*86400000.0/(:nowInMS - MIN(usageDateTimeStampInMs)) "
                        + "FROM DailyUsageSummary WHERE streamViewStreamScopeId IS NOT NULL "
                        + "GROUP BY streamViewStreamScopeId "
                        + "ORDER BY SUM(messageCount)*86400000.0/(:nowInMS - MIN(usageDateTimeStampInMs)) DESC")
                .setParameter("nowInMS", new Date().getTime());

        List<Object[]> scopeIdAndMessageCountArray = q.getResultList();
        int resultCount = scopeIdAndMessageCountArray.size();
        if (streamCount > 0 && resultCount > streamCount)
        {
            scopeIdAndMessageCountArray = scopeIdAndMessageCountArray.subList(0, streamCount);
        }

        // extract the stream scope ids to get the streams
        List<Long> streamScopeIds = new ArrayList<Long>();
        for (Object[] streamObj : scopeIdAndMessageCountArray)
        {
            streamScopeIds.add((Long) streamObj[0]);
        }

        if (streamScopeIds.size() == 0)
        {
            return new SublistWithResultCount<StreamDTO>(results, new Long(resultCount));
        }

        // List of object arrays [streamScopeId, StreamDTO]
        List<StreamDTO> streamDtos = new ArrayList<StreamDTO>();

        // get the people streams
        q = getEntityManager().createQuery(
                "SELECT new org.eurekastreams.server.search.modelview.PersonModelView(id, accountId, "
                        + "preferredName, lastName, 0, dateAdded, streamScope.id) FROM Person "
                        + "WHERE streamScope.id IN(:streamScopeIds)").setParameter("streamScopeIds", streamScopeIds);
        streamDtos.addAll(q.getResultList());

        // get the group streams
        q = getEntityManager().createQuery(
                "SELECT new org.eurekastreams.server.search.modelview.DomainGroupModelView(id, "
                        + "shortName, name, 0, dateAdded, streamScope.id) FROM DomainGroup "
                        + "WHERE streamScope.id IN(:streamScopeIds)").setParameter("streamScopeIds", streamScopeIds);
        streamDtos.addAll(q.getResultList());

        // put the list back together, sorting the list
        Long streamScopeId, messageCount;
        for (Object[] streamObj : scopeIdAndMessageCountArray)
        {
            streamScopeId = (Long) streamObj[0];
            messageCount = -1L;
            if (streamObj[1] != null)
            {
                messageCount = Math.round(((Double) streamObj[1]));
            }

            // find the StreamDTO with the stream scope
            for (StreamDTO streamDTO : streamDtos)
            {
                if (streamDTO.getStreamScopeId().equals(streamScopeId))
                {
                    if (streamDTO.getEntityType() == EntityType.PERSON)
                    {
                        ((PersonModelView) streamDTO).setFollowersCount(messageCount.intValue());
                    }
                    else if (streamDTO.getEntityType() == EntityType.GROUP)
                    {
                        ((DomainGroupModelView) streamDTO).setFollowersCount(messageCount.intValue());
                    }

                    results.add(streamDTO);
                    break;
                }
            }
        }
        return new SublistWithResultCount<StreamDTO>(results, new Long(resultCount));
    }

}
