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

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.StreamScopeTypeAndKeyRequest;

/**
 * Mapper to get a stream scope id by type and key.
 */
public class GetStreamScopeIdsByTypeAndKeyDbMapper extends BaseArgDomainMapper<StreamScopeTypeAndKeyRequest, Long>
{
    /**
     * Mapper to get Stream Scope Id from type and key.
     * 
     * @param inRequest
     *            the request
     * @return the id of the StreamScope, or null if does not exist
     */
    @Override
    public Long execute(final StreamScopeTypeAndKeyRequest inRequest)
    {
        List<Long> ids = getEntityManager().createQuery(
                "SELECT id FROM StreamScope WHERE scopeType = :scopeType AND uniqueKey = :uniqueKey").setParameter(
                "scopeType", inRequest.getScopeType()).setParameter("uniqueKey", inRequest.getKey()).getResultList();

        if (ids.size() == 1)
        {
            return ids.get(0);
        }
        return null;
    }
}
