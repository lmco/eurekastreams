/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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

import org.eurekastreams.commons.model.DomainEntityIdentifiable;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;

/**
 * Mapper used for inserting DomainEntities.
 * 
 * @param <TDomainEntityType>
 *            Type of DomainEntity.
 */
public class InsertMapper<TDomainEntityType extends DomainEntityIdentifiable> extends
        BaseArgDomainMapper<PersistenceRequest<TDomainEntityType>, Long>
{
    /**
     * Inserts the DomainEntity.
     * 
     * @param inRequest
     *            The MapperRequest.
     * @return true if inserted.
     */
    @Override
    public Long execute(final PersistenceRequest<TDomainEntityType> inRequest)
    {
        getEntityManager().persist(inRequest.getDomainEnity());
        return inRequest.getDomainEnity().getId();
    }

}
