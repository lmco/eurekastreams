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

import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Return stream scope for a configured ScopeType give a unique key, either returning null when not found.
 */
public class GetStreamScopeIdForScopeTypeByUniqueKeyDbMapper extends BaseArgDomainMapper<String, Long>
{
    /**
     * The scope type.
     */
    private ScopeType scopeType;

    /**
     * Constructor.
     * 
     * @param inScopeType
     *            the scope type to look for
     */
    public GetStreamScopeIdForScopeTypeByUniqueKeyDbMapper(final ScopeType inScopeType)
    {
        scopeType = inScopeType;
    }

    /**
     * Get the stream scope id for the specified type and input unique key.
     * 
     * @param inUniqueKey
     *            the unique key to find the stream scope id for
     * @return the stream scope id, or null if not exactly one was found
     */
    @Override
    public Long execute(final String inUniqueKey)
    {
        List<Long> results = getEntityManager().createQuery(
                "SELECT id FROM StreamScope WHERE scopeType = :scopeType AND uniqueKey = :uniqueKey").setParameter(
                "scopeType", scopeType).setMaxResults(1).setParameter("uniqueKey", inUniqueKey.toLowerCase())
                .getResultList();
        if (results != null && results.size() == 1)
        {
            // found results
            return results.get(0);
        }
        return null;
    }

}
