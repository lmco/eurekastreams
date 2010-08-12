/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Query;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Finds multiple entity streams belonging to groups and people.
 */
public class BulkEntityStreamIdsDbMapper extends BaseArgDomainMapper<Map<Long, EntityType>, List<Long>> implements
        DomainMapper<Map<Long, EntityType>, List<Long>>
{
    /**
     * Iterate through a map of requests to find entity streams and return a list of results.
     * 
     * @param request
     *            a map of requests request containing the id of the entity, and the EntityType.
     * @return the entity stream ID corresponding to each entity.
     */
    public List<Long> execute(final Map<Long, EntityType> request)
    {
        List<Long> ids = new ArrayList<Long>();

        for (Entry<Long, EntityType> entry : request.entrySet())
        {
            switch (entry.getValue())
            {
            case PERSON:
                Query personQuery = getEntityManager().createQuery(
                        "select entityStreamView.id from Person p where p.id =:personId").setParameter("personId",
                        entry.getKey());
                ids.add((Long) personQuery.getSingleResult());
                break;
            case GROUP:
                Query groupQuery = getEntityManager().createQuery(
                        "select entityStreamView.id from DomainGroup dg where dg.id =:domainGroupId").setParameter(
                        "domainGroupId", entry.getKey());
                ids.add((Long) groupQuery.getSingleResult());
                break;
            default:
                throw new RuntimeException("Unhandled type.");
            }
        }

        return ids;
    }

}
