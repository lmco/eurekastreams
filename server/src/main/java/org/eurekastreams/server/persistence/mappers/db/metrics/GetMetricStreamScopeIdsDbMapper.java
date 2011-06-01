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
package org.eurekastreams.server.persistence.mappers.db.metrics;

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * DB mapper to get the stream scope ids to generate nightly metrics for. This includes person and group streams.
 */
public class GetMetricStreamScopeIdsDbMapper extends BaseArgDomainMapper<Serializable, List<Long>>
{
    /**
     * Get the IDs of the stream scopes of views to generate metrics for.
     * 
     * @param inRequest
     *            ignored - have fun, be creative
     * @return all stream scope ids of people and group streams
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Long> execute(final Serializable inRequest)
    {
        return getEntityManager().createQuery(
                "SELECT id FROM StreamScope where scopeType = :personScopeType " + "OR scopeType = :groupScopeType")
                .setParameter("personScopeType", StreamScope.ScopeType.PERSON).setParameter("groupScopeType",
                        StreamScope.ScopeType.GROUP).getResultList();
    }
}
