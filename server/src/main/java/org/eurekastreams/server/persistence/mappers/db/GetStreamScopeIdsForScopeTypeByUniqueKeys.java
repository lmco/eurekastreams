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

import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.springframework.util.Assert;

/**
 * Return stream scope ids for a configured ScopeType give a list of unique keys.
 * 
 */
public class GetStreamScopeIdsForScopeTypeByUniqueKeys extends BaseArgDomainMapper<List<String>, List<Long>>
{

    /**
     * The scope type.
     */
    private ScopeType scopeType;

    /**
     * @param inScopeType
     *            ScopeType search will be restricted to.
     */
    public GetStreamScopeIdsForScopeTypeByUniqueKeys(final ScopeType inScopeType)
    {
        scopeType = inScopeType;
        Assert.notNull(scopeType);
    }

    /**
     * Return stream scope ids for a configured ScopeType give a list of unique keys.
     * 
     * @param inRequest
     *            List of unique keys.
     * @return list of stream scope ids for keys if present.
     */
    @Override
    public List<Long> execute(final List<String> inRequest)
    {
        return getEntityManager().createQuery(
                "SELECT id FROM StreamScope WHERE scopeType = :scopeType AND uniqueKey IN (:uniqueKeys)").setParameter(
                "scopeType", scopeType).setParameter("uniqueKeys", inRequest).getResultList();
    }
}
