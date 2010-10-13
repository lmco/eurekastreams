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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.CacheItemPointer;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

/**
 * Mapper to get a map of entity ids by a pointer.
 *
 * @param <PointerType>
 *            the type of pointer to lookup the id from
 */
public class GetIdsFromPointersDbMapper<PointerType> extends
        BaseArgDomainMapper<List<PointerType>, Map<PointerType, Long>>
{
    /**
     * Name of the pointer field.
     */
    private String pointerFieldName;

    /**
     * Entity class to select from.
     */
    private Class< ? > entityClass;

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
    public Map<PointerType, Long> execute(final List<PointerType> inPointerValues)
    {
        if (inPointerValues == null || inPointerValues.size() == 0)
        {
            return new HashMap<PointerType, Long>();
        }
        Map<PointerType, Long> itemMap = new HashMap<PointerType, Long>();
        Criteria criteria = getHibernateSession().createCriteria(entityClass);
        ProjectionList fields = Projections.projectionList();
        fields.add(Projections.property("id").as("itemId"));
        fields.add(Projections.property(pointerFieldName).as("pointerId"));
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
        criteria.setResultTransformer(Transformers.aliasToBean(CacheItemPointer.class));
        List<CacheItemPointer<PointerType>> results = criteria.list();

        for (CacheItemPointer<PointerType> result : results)
        {
            itemMap.put(result.getPointerId(), result.getItemId());
        }
        return itemMap;
    }
}
