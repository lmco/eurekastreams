/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers;

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.stream.StreamScope;

/**
 * Get the stream scopes for a stream view by id.
 */
public class GetStreamScopesByStreamViewId extends BaseArgDomainMapper<Long, List<StreamScope>>
{
    /**
     * Get the stream scopes by stream view id.
     * 
     * @param inStreamViewId
     *            the stream view to load scopes for
     * @return the stream scopes by stream view id
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<StreamScope> execute(final Long inStreamViewId)
    {
        Query q = getEntityManager().createQuery("SELECT includedScopes FROM StreamView sv WHERE sv.id = :streamViewId")
                .setParameter("streamViewId", inStreamViewId);
        return (List<StreamScope>) q.getResultList();
    }

}
