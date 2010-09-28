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

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Get list of Serializable values for given entity/field name.
 * 
 */
public class GetSerializableField extends BaseArgDomainMapper<String, List<Serializable>>
{

    /**
     * Entity name.
     */
    private String entityName;

    /**
     * Field name.
     */
    private String fieldName;

    /**
     * Constructor.
     * 
     * @param inEntityName
     *            Entity name to find ids for.
     * @param inFieldName
     *            name of field to select.
     * 
     */
    public GetSerializableField(final String inEntityName, final String inFieldName)
    {
        entityName = inEntityName;
        fieldName = inFieldName;
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
    public List<Serializable> execute(final String inEntityName)
    {
        return getEntityManager().createQuery("SELECT " + fieldName + " FROM " + entityName).getResultList();
    }

}
