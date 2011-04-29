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

import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.springframework.util.Assert;

/**
 * Return stream scope for a configured ScopeType give a unique key, either returning null or exception when not found,
 * determined by the constructor arg.
 */
public class GetStreamScopeForScopeTypeByUniqueKey extends BaseArgDomainMapper<String, StreamScope>
{
    /**
     * The scope type.
     */
    private ScopeType scopeType;

    /**
     * Whether to throw an exception if not found, else null.
     */
    private boolean throwExceptionOnNotFound;

    /**
     * Constructor.
     * 
     * @param inScopeType
     *            ScopeType search will be restricted to.
     * @param inThrowExceptionOnNotFound
     *            whether to throw a RuntimeException when no results found - else returns null
     */
    public GetStreamScopeForScopeTypeByUniqueKey(final ScopeType inScopeType, //
            final boolean inThrowExceptionOnNotFound)
    {
        scopeType = inScopeType;
        throwExceptionOnNotFound = inThrowExceptionOnNotFound;
        Assert.notNull(scopeType);
    }

    /**
     * Return stream scope id for a configured ScopeType give a unique key.
     * 
     * @param inUniqueKey
     *            unique key
     * @return stream scope for key if present
     */
    @Override
    public StreamScope execute(final String inUniqueKey)
    {
        // short circuit here as IN clause with emtpy list causes syntax error.
        if (inUniqueKey != null && !inUniqueKey.isEmpty())
        {
            List<StreamScope> results = getEntityManager()
                    .createQuery("FROM StreamScope WHERE scopeType = :scopeType AND uniqueKey = :uniqueKey")
                    .setParameter("scopeType", scopeType).setMaxResults(1).setParameter("uniqueKey", inUniqueKey)
                    .getResultList();
            if (results != null && results.size() == 1)
            {
                // found results
                return results.get(0);
            }
        }

        if (throwExceptionOnNotFound)
        {
            throw new RuntimeException("Could not find the stream scope with type " + scopeType + " and unique key "
                    + inUniqueKey);
        }
        // not found - just return null
        return null;
    }
}
