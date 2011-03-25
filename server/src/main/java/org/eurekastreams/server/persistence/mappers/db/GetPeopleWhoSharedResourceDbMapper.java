/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import org.eurekastreams.server.action.request.SharedResourceRequest;
import org.eurekastreams.server.domain.stream.ActorType;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Get the people who shared a resource.
 */
public class GetPeopleWhoSharedResourceDbMapper extends BaseArgDomainMapper<SharedResourceRequest, List<Long>>
{
    /**
     * Get the person ids that shared the input shared resource.
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
                "SELECT p.id FROM Person p, Activity a WHERE p.accountId = a.actorId AND a.actorType = :actorType "
                        + "AND a.sharedLink.resourceType = :resourceType AND a.sharedLink.uniqueKey = :uniqueKey")
                .setParameter("actorType", ActorType.PERSON).setParameter("uniqueKey",
                        inRequest.getUniqueKey().toLowerCase()).setParameter("resourceType",
                        inRequest.getResourceType()).getResultList();

        return peopleIds;
    }
}
