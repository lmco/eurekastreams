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
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.dto.StreamDTO;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * DB Mapper to get the top N group stream sorted by most recent.
 */
public class GetStreamsByMostRecentDbMapper extends BaseArgDomainMapper<Serializable, List<StreamDTO>>
{
    /**
     * Number of streams to get.
     */
    private final Integer streamCount;

    /**
     * Constructor.
     *
     * @param inStreamCount
     *            the number of streams to fetch
     */
    public GetStreamsByMostRecentDbMapper(final Integer inStreamCount)
    {
        streamCount = inStreamCount;
    }

    /**
     * Get the top N streams sorted by most recent.
     *
     * @param inIgnored
     *            I don't personally care what you pass in here
     * @return a list of StreamDTOs of the most recent streams
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<StreamDTO> execute(final Serializable inIgnored)
    {
        Query q = getEntityManager().createQuery(
                "SELECT new org.eurekastreams.server.search.modelview.DomainGroupModelView(id, "
                        + "shortName, name, followersCount, dateAdded, streamScope.id, publicGroup) "
                        + "FROM DomainGroup WHERE isPending=false ORDER BY dateAdded DESC");
        if (streamCount > 0)
        {
            q.setMaxResults(streamCount).getResultList();
        }
        return q.getResultList();
    }
}