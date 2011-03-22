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

import java.util.List;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.SharedResourceRequest;

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
        List<Long> peopleIds = getEntityManager().createQuery(
                "SELECT p.id FROM Person p, SharedResource sr WHERE p MEMBER OF sr.likedBy "
                        + "AND sr.resourceType = :resourceType AND uniqueKey = :uniqueKey").setParameter("uniqueKey",
                inRequest.getUniqueKey()).setParameter("resourceType", inRequest.getResourceType()).getResultList();

        return peopleIds;
    }
}
