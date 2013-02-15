/*
 * Copyright (c) 2013 Lockheed Martin Corporation
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

import org.eurekastreams.server.domain.dto.StreamDTO;
import org.eurekastreams.server.persistence.comparators.StreamDTOFollowerCountDescendingComparator;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * DB mapper to get the top N streams sorted by follower count.
 */
public class GetStreamsByFollowersCountDbMapper extends BaseArgDomainMapper<Serializable, List<StreamDTO>>
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
    public GetStreamsByFollowersCountDbMapper(final Integer inStreamCount)
    {
        streamCount = inStreamCount;
    }

    /**
     * Get the top N streams sorted by follower count.
     * 
     * @param inIgnored
     *            I don't personally care what you pass in here
     * @return a list of StreamDTOs of the most followed streams
     */
    @Override
    public List<StreamDTO> execute(final Serializable inIgnored)
    {
        Query q;
        List<StreamDTO> results = new ArrayList<StreamDTO>();

        q = getEntityManager().createQuery(
                "SELECT new org.eurekastreams.server.search.modelview.PersonModelView(id, accountId, "
                        + "preferredName, lastName, displayName, displayNameSuffix, followersCount, dateAdded, "
                        + "streamScope.id) " + "FROM Person WHERE followersCount > 0 ORDER BY followersCount DESC");
        if (streamCount > 0)
        {
            q.setMaxResults(streamCount).getResultList();
        }
        results.addAll(q.getResultList());

        q = getEntityManager().createQuery(
                "SELECT new org.eurekastreams.server.search.modelview.DomainGroupModelView(id, "
                        + "shortName, name, followersCount, dateAdded, streamScope.id, publicGroup) "
                        + "FROM DomainGroup WHERE isPending = false AND followersCount > 0 "
                        + "ORDER BY followersCount DESC");
        if (streamCount > 0)
        {
            q.setMaxResults(streamCount).getResultList();
        }
        results.addAll(q.getResultList());

        // sort the list
        Collections.sort(results, new StreamDTOFollowerCountDescendingComparator());
        if (results.size() > streamCount)
        {
            results = results.subList(0, streamCount);
        }
        return results;
    }
}
