/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * Gets a list of composite stream objects for a given list of composite stream ids.
 */
public class BulkCompositeStreamsMapper extends CachedDomainMapper
{
    /**
     * Logger.
     */
    private static Log log = LogFactory.make();

    /**
     * Looks in cache for the necessary items and returns them if found. Otherwise, makes a database call, puts them in
     * cache, and returns them.
     * 
     * @param compositeStreamIds
     *            the list of ids that should be found.
     * @return list of composite stream objects.
     */
    @SuppressWarnings("unchecked")
    public List<StreamFilter> execute(final List<Long> compositeStreamIds)
    {
        // Checks to see if there's any real work to do
        if (compositeStreamIds == null || compositeStreamIds.size() == 0)
        {
            return new ArrayList<StreamFilter>();
        }

        List<String> stringKeys = new ArrayList<String>();
        for (long key : compositeStreamIds)
        {
            stringKeys.add(CacheKeys.COMPOSITE_STREAM_BY_ID + key);
        }

        // Finds composite streams in the cache.
        Map<String, StreamView> streams = (Map<String, StreamView>) (Map<String, ? >) getCache().multiGet(stringKeys);

        // Determines if any of the composite streams were missing from the cache
        List<Long> uncached = new ArrayList<Long>();
        for (long compositeStreamId : compositeStreamIds)
        {
            if (!streams.containsKey(CacheKeys.COMPOSITE_STREAM_BY_ID + compositeStreamId))
            {
                uncached.add(compositeStreamId);
            }
        }

        // One or more of the activities were missing in the cache so go to the database
        if (uncached.size() != 0)
        {
            Map<String, StreamView> streamMap = new HashMap<String, StreamView>();

            StringBuilder query = new StringBuilder("FROM StreamView WHERE ");
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

            List<StreamView> results = q.getResultList();

            for (StreamView compositeStream : results)
            {
                streamMap.put(CacheKeys.COMPOSITE_STREAM_BY_ID + compositeStream.getId(), compositeStream);
            }

            for (String key : streamMap.keySet())
            {
                getCache().set(key, streamMap.get(key));
            }

            streams.putAll(streamMap);
        }

        // Puts the composite streams in the same order as they were passed in.
        List<StreamFilter> results = new ArrayList<StreamFilter>();
        for (long id : compositeStreamIds)
        {
            String key = CacheKeys.COMPOSITE_STREAM_BY_ID + id;
            if (streams.containsKey(key))
            {
                results.add(streams.get(key));
            }
            else
            {
                log.warn("Requested Composite Stream with id " + id
                        + " was not found in cache or DB, removing from result list");
            }
        }
        return results;
    }
}
