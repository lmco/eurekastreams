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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

/**
 * Gets a list of objects for a given list of pointer ids.
 * 
 * @param <ValueType>
 *            the object type being pointed to.
 */
public abstract class GetItemsByPointerIds<ValueType> extends CachedDomainMapper
{
    /**
     * Gets the prefix.
     * 
     * @return the prefix.
     */
    public abstract String getCachePointerKeyPrefix();

    /**
     * Executes bulk method.
     * 
     * @param ids
     *            the ids to retrieve.
     * @return the items.
     */
    protected abstract List<ValueType> bulkExecute(final List<Long> ids);

    /**
     * Gets the property.
     * 
     * @return the property.
     */
    public abstract String getPointerProperty();

    /**
     * Gets the entity class.
     * 
     * @return the class.
     */
    @SuppressWarnings("unchecked")
    public abstract Class getEntityClass();

    /**
     * Fetch the Long ID for the input String ID.  This
     * requires only one cache hit.
     * 
     * @param inId
     *            the string ID
     * @return the long ID
     */
    public Long fetchId(final String inId)
    {
        List<String> stringIds = new ArrayList<String>();
        stringIds.add(inId);
        Map<String, Long> result = fetchIds(stringIds);
        return result.get(getCachePointerKeyPrefix() + inId);
    }

    /**
     * Fetch an object by its string id.  This requires two
     * cache hits.
     * 
     * @param inId
     *            the string id
     * @return the object with the string id, or null if not found
     */
    public ValueType fetchUniqueResult(final String inId)
    {
        List<String> ids = new ArrayList<String>();
        ids.add(inId);
        List<ValueType> results = execute(ids);
        return results.size() == 0 ? null : results.get(0);
    }

    /**
     * Fetch the Long ids from the String ids.
     * 
     * @param ids
     *            the string IDs
     * @return a Map of String ids to Long idsF
     */
    @SuppressWarnings("unchecked")
    public Map<String, Long> fetchIds(final List<String> ids)
    {
        if (ids == null || ids.size() == 0)
        {
            return new HashMap<String, Long>();
        }

        List<String> stringKeys = new ArrayList<String>();
        for (String key : ids)
        {
            stringKeys.add(getCachePointerKeyPrefix() + key);
        }

        // Finds item pointers in the cache.
        Map<String, Long> items = (Map<String, Long>) (Map<String, ? >) getCache().multiGet(stringKeys);

        // Determines if any of the item pointers were missing from the cache
        List<String> uncachedItemKeys = new ArrayList<String>();
        for (String itemKey : ids)
        {
            if (!items.containsKey(getCachePointerKeyPrefix() + itemKey))
            {
                uncachedItemKeys.add(itemKey);
            }
        }

        // One or more of the item pointers were missing in the cache so go to the database
        if (uncachedItemKeys.size() != 0)
        {
            Map<String, Long> itemMap = new HashMap<String, Long>();
            Criteria criteria = getHibernateSession().createCriteria(getEntityClass());
            ProjectionList fields = Projections.projectionList();
            fields.add(Projections.property("id").as("itemId"));
            fields.add(Projections.property(getPointerProperty()).as("pointerId"));
            criteria.setProjection(fields);

            // Creates the necessary "OR" clauses to get all uncached item pointers
            Criterion restriction = null;
            for (int i = 0; i < uncachedItemKeys.size(); i++)
            {
                String key = uncachedItemKeys.get(i);
                if (restriction == null)
                {
                    restriction = Restrictions.eq(getPointerProperty(), key);
                }
                else
                {
                    restriction = Restrictions.or(Restrictions.eq(getPointerProperty(), key), restriction);
                }
            }

            criteria.add(restriction);
            criteria.setResultTransformer(Transformers.aliasToBean(CacheItemPointer.class));
            List<CacheItemPointer> results = criteria.list();

            for (CacheItemPointer result : results)
            {
                itemMap.put(getCachePointerKeyPrefix() + result.getPointerId(), result.getItemId());
            }

            for (String key : itemMap.keySet())
            {
                getCache().set(key, itemMap.get(key));
            }

            items.putAll(itemMap);
        }

        return items;
    }

    /**
     * Looks in cache for the necessary DTOs and returns them if found. Otherwise, makes a database call, puts them in
     * cache, and returns them.
     * 
     * @param ids
     *            the list of ids that should be found.
     * @return list of DTO objects.
     */
    @SuppressWarnings("unchecked")
    public List<ValueType> execute(final List<String> ids)
    {
        Map<String, Long> idsMap = fetchIds(ids);

        // Checks to see if there's any real work to do
        if (idsMap == null || idsMap.size() == 0)
        {
            return new ArrayList<ValueType>();
        }

        return new ArrayList<ValueType>(bulkExecute(new ArrayList(idsMap.values())));
    }
}
