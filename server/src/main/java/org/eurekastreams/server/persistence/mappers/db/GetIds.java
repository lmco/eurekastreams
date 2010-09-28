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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.List;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Get list of ids for all entities of given entity name.
 * 
 */
public class GetIds extends BaseArgDomainMapper<String, List<Long>>
{

    /**
     * Entity name.
     */
    private String entityName;

    /**
     * Constructor.
     * 
     * @param inEntityName
     *            Entity name to find ids for.
     * 
     */
    public GetIds(final String inEntityName)
    {
        entityName = inEntityName;
    }

    /**
     * Return ids for all entities of given entity name.
     * 
     * @param inEntityName
     *            Entity name to find ids for.
     * @return List of ids for all entities of given entity name.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Long> execute(final String inEntityName)
    {
        return getEntityManager().createQuery("SELECT id FROM " + entityName).getResultList();
    }

}
