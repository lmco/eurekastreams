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

import org.eurekastreams.server.domain.stream.ActivitySecurityDTO;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Maps activity security information from the DB.
 */
public class BulkActivitySecurityDbMapper extends BaseArgDomainMapper<List<Long>, List<ActivitySecurityDTO>> implements
        DomainMapper<List<Long>, List<ActivitySecurityDTO>>
{
    /**
     * @param inRequest
     *            the request of activity IDs..
     * @return security information for the activites in the request.
     */
    @SuppressWarnings("unchecked")
    public List<ActivitySecurityDTO> execute(final List<Long> inRequest)
    {
        String q = "select new org.eurekastreams.server.domain.stream.ActivitySecurityDTO "
                + "(id, recipientStreamScope.destinationEntityId, isDestinationStreamPublic) "
                + "from Activity where id in (:activityIds)";

        return getEntityManager().createQuery(q).setParameter("activityIds", inRequest).getResultList();
    }

}
