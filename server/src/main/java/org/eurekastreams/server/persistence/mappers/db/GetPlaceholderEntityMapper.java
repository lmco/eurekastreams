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

import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Mapper which returns a placeholder entity for use in persisting entities with foreign keys.
 *
 * @param <TReturnType>
 *            Type of entity to return.
 */
public class GetPlaceholderEntityMapper<TReturnType> extends BaseDomainMapper implements
        DomainMapper<Long, TReturnType>
{
    /** Type of entity to return. */
    private final Class<TReturnType> entityType;

    /**
     * Constructor.
     * 
     * @param inEntityType
     *            Type of the entity to return.
     */
    public GetPlaceholderEntityMapper(final Class<TReturnType> inEntityType)
    {
        entityType = inEntityType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TReturnType execute(final Long inRequest)
    {
        return (TReturnType) getHibernateSession().get(entityType, inRequest);
    }

}
