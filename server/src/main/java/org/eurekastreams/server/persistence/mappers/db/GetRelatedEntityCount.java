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

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.GetRelatedEntityCountRequest;

/**
 * Return count of related entities.
 */
public class GetRelatedEntityCount extends BaseArgDomainMapper<GetRelatedEntityCountRequest, Long>
{

    /**
     * Return count of related entities for given entity.
     * 
     * @param inRequest
     *            {@link GetRelatedEntityCountRequest}.
     * @return Count of related entities.
     */
    @Override
    public Long execute(final GetRelatedEntityCountRequest inRequest)
    {
        String whereClause = (inRequest.getWhereClauseAddition() == null) ? "" : inRequest.getWhereClauseAddition();
        return (Long) getEntityManager().createQuery(
                "SELECT Count(id) FROM " + inRequest.getRelatedEntityName() + " WHERE "
                        + inRequest.getTargetEntityFieldName() + ".id = :id " + whereClause).setParameter("id",
                inRequest.getTargetEntityId()).getSingleResult();
    }
}
