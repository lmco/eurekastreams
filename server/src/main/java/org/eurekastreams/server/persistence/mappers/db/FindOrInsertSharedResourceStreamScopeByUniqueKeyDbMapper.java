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

import javax.persistence.Query;

import org.eurekastreams.server.action.request.SharedResourceRequest;
import org.eurekastreams.server.domain.stream.SharedResource;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.springframework.util.Assert;

/**
 * Find or insert streamScope based on unique key, including potentially creating associated SharedResource object if it
 * didn't already exist.
 * 
 */
public class FindOrInsertSharedResourceStreamScopeByUniqueKeyDbMapper extends BaseArgDomainMapper<String, StreamScope>
{
    /**
     * Mapper to find or insert Shared Resource entity.
     */
    DomainMapper<SharedResourceRequest, SharedResource> findOrInsertSharedResourceByUniqueKeyMapper;

    /**
     * Constructor.
     * 
     * @param inFindOrInsertSharedResourceByUniqueKeyMapper
     *            Mapper to find or insert Shared Resource entity.
     */
    public FindOrInsertSharedResourceStreamScopeByUniqueKeyDbMapper(
            final DomainMapper<SharedResourceRequest, SharedResource> inFindOrInsertSharedResourceByUniqueKeyMapper)
    {
        findOrInsertSharedResourceByUniqueKeyMapper = inFindOrInsertSharedResourceByUniqueKeyMapper;
    }

    /**
     * Find or insert streamScope based on unique key, including potentially creating associated SharedResource object
     * if it didn't already exist.
     * 
     * @param inRequest
     *            unique key for streamScope.
     * @return StreamScope for given unique key, created if needed.
     */
    @Override
    public StreamScope execute(final String inRequest)
    {
        Assert.notNull(inRequest, "StreamScope uniqueKey cannot be null");

        Query q = getEntityManager().createQuery(
                "FROM StreamScope WHERE uniqueKey = :uniqueKey AND scopeType = :scopeType").setParameter("uniqueKey",
                inRequest.toLowerCase()).setParameter("scopeType", ScopeType.RESOURCE);
        List<StreamScope> resources = q.getResultList();

        // found streamScope, return it.
        if (resources != null && !resources.isEmpty())
        {
            return resources.get(0);
        }

        // no stream scope found, create StreamScope/StreamResource
        SharedResource sr = findOrInsertSharedResourceByUniqueKeyMapper.execute(new SharedResourceRequest(inRequest));
        StreamScope ss = new StreamScope(ScopeType.RESOURCE, inRequest.toLowerCase());
        ss.setDestinationEntityId(sr.getId());
        getEntityManager().persist(ss);

        return ss;
    }

}
