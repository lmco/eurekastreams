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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * Mapper to get a map of entity ids by a pointer.
 *
 * @param <PointerType>
 *            the type of pointer to lookup the id from
 */
public class GetIdsFromPointersDbMapper<PointerType> extends BaseArgDomainMapper<List<PointerType>, List<Long>>
{
    /**
     * Name of the pointer field.
     */
    private final String pointerFieldName;

    /**
     * Entity class to select from.
     */
    private final Class< ? > entityClass;

    /**
     * Constructor.
     *
     * @param inPointerFieldName
     *            the name of the pointer field
     * @param inEntityClass
     *            the class to select from
     */
    public GetIdsFromPointersDbMapper(final String inPointerFieldName, final Class< ? > inEntityClass)
    {
        pointerFieldName = inPointerFieldName;
        entityClass = inEntityClass;
    }

    /**
     * Get the entity ids from their pointer values.
     *
     * @param inPointerValues
     *            the values of the pointers to look up ids for
     * @return a Map of pointer values to Long entity ids
     */
    @Override
    public List<Long> execute(final List<PointerType> inPointerValues)
    {
        if (inPointerValues == null || inPointerValues.isEmpty())
        {
            return new ArrayList<Long>();
        }

        Criteria criteria = getHibernateSession().createCriteria(entityClass);
        ProjectionList fields = Projections.projectionList();
        fields.add(Projections.property(pointerFieldName).as("key"));
        fields.add(Projections.property("id").as("itemId"));
        criteria.setProjection(fields);

        // Creates the necessary "OR" clauses to get all uncached item pointers
        Criterion restriction = null;
        for (PointerType pointer : inPointerValues)
        {
            if (restriction == null)
            {
                restriction = Restrictions.eq(pointerFieldName, pointer);
            }
            else
            {
                restriction = Restrictions.or(Restrictions.eq(pointerFieldName, pointer), restriction);
            }
        }
        criteria.add(restriction);

        // Build the list of results
        // Insure a 1-to-1 correspondence to the input pointers (same order and nulls for missing values)
        // Note: results is a list of Object arrays. Each array contains the fields for the given row in the same order
        // as they were added to the ProjectionList above.
        List<Object[]> dbResults = criteria.list();
        Map<PointerType, Long> dbIndex = new HashMap<PointerType, Long>();
        for (Object[] row : dbResults)
        {
            dbIndex.put((PointerType) row[0], (Long) row[1]);
        }
        List<Long> results = new ArrayList<Long>(inPointerValues.size());
        for (PointerType key : inPointerValues)
        {
            // Note: this automatically handles nulls for unknown elements
            results.add(dbIndex.get(key));
        }
        return results;
    }
}
