/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Get all the activities.
 *
 */
public class GetEveryoneActivityDbMapper extends BaseDomainMapper implements DomainMapper<Long, List<Long>>
{
    /**
     * Maximum number of items for the activity id lists.
     */
    private static final int MAX_RESULTS = 10000;

    /**
     * Get the activity ids for all people.
     *
     * @param inRequest
     *            ignored
     * @return the most recent MAX_RESULTS activity ids
     */
    @Override
    public List<Long> execute(final Long inRequest)
    {
        String idsQueryString = "select id FROM Activity ORDER BY id DESC";
        Query idsQuery = getEntityManager().createQuery(idsQueryString);
        idsQuery.setMaxResults(MAX_RESULTS);
        return idsQuery.getResultList();
    }

}
