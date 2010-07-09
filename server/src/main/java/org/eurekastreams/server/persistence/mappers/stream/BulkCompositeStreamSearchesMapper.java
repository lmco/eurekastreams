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

import javax.persistence.Query;

import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * Gets a list of composite stream search objects for a given list of composite stream search ids.
 */
public class BulkCompositeStreamSearchesMapper extends CachedDomainMapper
{
    /**
     * Looks in cache for the necessary items and returns them if found. Otherwise, makes a database call, puts them in
     * cache, and returns them.
     * 
     * @param compositeStreamSearchIds
     *            the list of ids that should be found.
     * @return list of composite stream search objects.
     */
    @SuppressWarnings("unchecked")
    public List<StreamFilter> execute(final List<Long> compositeStreamSearchIds)
    {
        // Checks to see if there's any real work to do
        if (compositeStreamSearchIds == null || compositeStreamSearchIds.size() == 0)
        {
            return new ArrayList<StreamFilter>();
        }

        List<String> stringKeys = new ArrayList<String>();
        for (long key : compositeStreamSearchIds)
        {
            stringKeys.add(CacheKeys.COMPOSITE_STREAM_SEARCH_BY_ID + key);
        }

        // Finds composite stream searches in the cache.
        Map<String, StreamSearch> searches = (Map<String, StreamSearch>) (Map<String, ? >) getCache().multiGet(
                stringKeys);

        // Determines if any of the composite stream searches were missing from the cache
        List<Long> uncached = new ArrayList<Long>();
        for (long compositeStreamSearchId : compositeStreamSearchIds)
        {
            if (!searches.containsKey(CacheKeys.COMPOSITE_STREAM_SEARCH_BY_ID + compositeStreamSearchId))
            {
                uncached.add(compositeStreamSearchId);
            }
        }

        // One or more of the activities were missing in the cache so go to the database
        if (uncached.size() != 0)
        {
            Map<String, StreamSearch> streamMap = new HashMap<String, StreamSearch>();

            StringBuilder query = new StringBuilder("FROM StreamSearch WHERE ");
            for (int i = 0; i < uncached.size(); i++)
            {
                long key = uncached.get(i);
                query.append("id=").append(key);
                if (i != uncached.size() - 1)
                {
                    query.append(" OR ");
                }
            }

            Query q = getEntityManager().createQuery(query.toString());

            List<StreamSearch> results = q.getResultList();

            for (StreamSearch search : results)
            {
                streamMap.put(CacheKeys.COMPOSITE_STREAM_SEARCH_BY_ID + search.getId(), search);
            }

            for (String key : streamMap.keySet())
            {
                getCache().set(key, streamMap.get(key));
            }

            searches.putAll(streamMap);
        }

        // Puts the composite stream searches in the same order as they were passed in.
        List<StreamFilter> results = new ArrayList<StreamFilter>();
        for (long id : compositeStreamSearchIds)
        {
            results.add(searches.get(CacheKeys.COMPOSITE_STREAM_SEARCH_BY_ID + id));
        }
        return results;
    }
}
