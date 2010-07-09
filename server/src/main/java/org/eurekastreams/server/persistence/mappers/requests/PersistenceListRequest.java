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
package org.eurekastreams.server.persistence.mappers.requests;

import java.util.List;

import org.eurekastreams.commons.model.DomainEntity;

/**
 * Request for persistence that contains a list of entities.
 *
 * @param <TEntityType> the entity type.
 */
public class PersistenceListRequest<TEntityType extends DomainEntity> implements MapperRequest<TEntityType>
{
    /**
     * Entity to persist.
     */
    private List<TEntityType> entity;
    
    /**
     * Constructor.
     * @param inEntities Entity to persist.
     */
    public PersistenceListRequest(final List<TEntityType> inEntities)
    {
        entity = inEntities;
    }
    
    /**
     * Getter for DomainEntity.
     * @return The DomainEntity.
     */
    public List<TEntityType> getDomainEnities()
    {
        return entity;
    }
}
