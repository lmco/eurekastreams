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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.chained.PartialMapperResponse;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Maps multiple short names to their corresponding entity streams.  Partial cache mapper.
 */
public class BulkEntityStreamIdsMapper extends CachedDomainMapper implements
        DomainMapper<Map<Long, EntityType>, PartialMapperResponse<Map<Long, EntityType>, List<Long>>>
{
    /**
     * Execute the mapper. Takes a map of shortnames and corresponding entity types.
     * 
     * @param request
     *            map of short names and corresponding entity types.
     * @return the partial respond of entity streams, and the remaining request.
     */
    public PartialMapperResponse<Map<Long, EntityType>, List<Long>> execute(final Map<Long, EntityType> request)
    {
        final Map<String, Long> keys = new HashMap<String, Long>();

        for (Entry<Long, EntityType> entry : request.entrySet())
        {
            switch (entry.getValue())
            {
            case PERSON:
                keys.put(CacheKeys.PERSON_ENTITITY_STREAM_VIEW_ID + entry.getKey(), entry.getKey());
                break;
            case GROUP:
                keys.put(CacheKeys.GROUP_ENTITITY_STREAM_VIEW_ID + entry.getKey(), entry.getKey());
                break;
            default:
                throw new RuntimeException("Unhandled type.");
            }
        }

        final Map<String, Object> results = getCache().multiGet(keys.keySet());

        final List<Long> ids = new ArrayList<Long>();
        final Map<Long, EntityType> remainingRequest = new HashMap<Long, EntityType>();

        for (Entry<String, Long> entry : keys.entrySet())
        {
            // Not found by multiget, add to partial mapper request.
            if (!results.containsKey(entry.getKey()))
            {
                remainingRequest.put(entry.getValue(), request.get(entry.getValue()));
            }
            else
            {
                ids.add((Long) results.get(entry.getKey()));
            }
        }

        if (0 == remainingRequest.size())
        {
            return new PartialMapperResponse<Map<Long, EntityType>, List<Long>>(ids);
        }
        else
        {
            return new PartialMapperResponse<Map<Long, EntityType>, List<Long>>(ids, remainingRequest);
        }
    }
}
