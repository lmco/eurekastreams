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

import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;
import org.eurekastreams.server.persistence.mappers.db.GetIdsFromPointersDbMapper;

/**
 * Gets a list of objects for a given list of pointer ids.
 *
 * @param <ValueType>
 *            the object type being pointed to.
 */
public abstract class GetItemsByPointerIds<ValueType> extends BaseDomainMapper
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
     * Fetch the Long ID for the input String ID. This requires only one cache hit.
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
     * Fetch an object by its string id. This requires two cache hits.
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
    public Map<String, Long> fetchIds(final List<String> ids)
    {
        // this is temporary
        GetIdsFromPointersDbMapper mapper = new GetIdsFromPointersDbMapper(getPointerProperty(), getEntityClass());
        mapper.setEntityManager(getEntityManager());
        Map<String, Long> propertiesToIdsMap = mapper.execute(ids);
        Map<String, Long> results = new HashMap<String, Long>();
        for (String keyWithoutPrefix : propertiesToIdsMap.keySet())
        {
            results.put(getCachePointerKeyPrefix() + keyWithoutPrefix, propertiesToIdsMap.get(keyWithoutPrefix));
        }
        return results;
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
