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

import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;

/**
 * DB Mapper to fetch the IDs of all activities posted to a stream with the input scope and unique key.
 */
public class GetActivityIdsPostedToStreamByUniqueKeyAndScopeType extends BaseDomainMapper
{
    /**
     * Fetch the IDs of all activities posted to a stream with the input scope and unique key.
     *
     * @param scopeType
     *            the scope type
     * @param uniqueKey
     *            the short name of the domain group to get activities for
     * @return a list of IDs of all activities posted to the stream with the input unique key and scope
     */
    @SuppressWarnings("unchecked")
    public List<Long> execute(final ScopeType scopeType, final String uniqueKey)
    {
        String query = "SELECT id FROM Activity WHERE recipientStreamScope.scopeType=:scopeType "
                + "AND recipientStreamScope.uniqueKey = :uniqueKey";
        return getEntityManager().createQuery(query).setParameter("scopeType", scopeType).setParameter("uniqueKey",
                uniqueKey).getResultList();
    }
}
