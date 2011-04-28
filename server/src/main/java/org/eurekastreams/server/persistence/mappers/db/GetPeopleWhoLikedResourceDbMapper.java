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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.action.request.SharedResourceRequest;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Get the people who liked a resource.
 */
public class GetPeopleWhoLikedResourceDbMapper extends BaseArgDomainMapper<SharedResourceRequest, List<Long>>
{
    /**
     * Get the person ids that like the input shared resource.
     * 
     * @param inRequest
     *            the unique id of the resource
     * @return the list of ids of the people that like the resource with the input type and unique id.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Long> execute(final SharedResourceRequest inRequest)
    {
        Long sharedResourceId = null;
        if (inRequest.getSharedResourceId() != null && inRequest.getSharedResourceId() > 0)
        {
            // caller passed in the shared resource id - fast query
            sharedResourceId = inRequest.getSharedResourceId();
        }
        else
        {
            // caller passed in the shared resource unique key - get the id first - there's an index, so it should be
            // quick. tell hibernate to only get 1 record - hopefully this'll make the query quit quicker
            List<Long> sharedResourceIds = getEntityManager()
                    .createQuery("SELECT id FROM SharedResource WHERE uniqueKey = :uniqueKey")
                    .setParameter("uniqueKey", inRequest.getUniqueKey()).setMaxResults(1).getResultList();
            if (sharedResourceIds.size() == 0)
            {
                // shared resource doesn't exist - user can't be following it
                return new ArrayList<Long>();
            }
            sharedResourceId = sharedResourceIds.get(0);
        }

        List<Long> peopleIds = getEntityManager()
                .createQuery(
                        "SELECT DISTINCT pk.personId FROM LikedSharedResource "
                                + "WHERE pk.sharedResourceId = :sharedResourceId")
                .setParameter("sharedResourceId", sharedResourceId).getResultList();

        return peopleIds;
    }
}
