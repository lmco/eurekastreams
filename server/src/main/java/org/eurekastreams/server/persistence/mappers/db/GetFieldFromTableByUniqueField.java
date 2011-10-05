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

import javax.persistence.NoResultException;

import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Very generic, configurable mapper to select a single field from a table by a single unique field.
 *
 * @param <UniqueFieldType>
 *            the field we're scoping by
 * @param <ResultFieldType>
 *            the field we're selecting
 */
public class GetFieldFromTableByUniqueField<UniqueFieldType, ResultFieldType> extends BaseDomainMapper implements
        DomainMapper<UniqueFieldType, ResultFieldType>
{
    /**
     * The entity name.
     */
    private final String entityName;

    /**
     * Name of the unique field.
     */
    private final String uniqueFieldName;

    /**
     * Name of the result field.
     */
    private final String resultFieldName;

    /**
     * Constructor.
     *
     * @param inEntityName
     *            the entity name
     * @param inUniqueFieldName
     *            the field to return
     * @param inResultFieldName
     *            the field to search by
     */
    public GetFieldFromTableByUniqueField(final String inEntityName, final String inUniqueFieldName,
            final String inResultFieldName)
    {
        entityName = inEntityName;
        uniqueFieldName = inUniqueFieldName;
        resultFieldName = inResultFieldName;
    }

    /**
     * Get the field by the unique field.
     *
     * @param param
     *            the unique value to search on
     * @return the field found from the unique value
     */
    @SuppressWarnings("unchecked")
    public ResultFieldType execute(final UniqueFieldType param)
    {
        final String query = "SELECT " + resultFieldName + " FROM " + entityName + " WHERE " + uniqueFieldName
                + "=:uniqueValue";
        List<ResultFieldType> results = getEntityManager().createQuery(query).setParameter("uniqueValue", param)
                .getResultList();

        if (results.size() != 1)
        {
            throw new NoResultException("Expected 1 record from " + entityName + " with " + resultFieldName + ":"
                    + uniqueFieldName + ", but found " + results.size());
        }
        return results.get(0);
    }
}
