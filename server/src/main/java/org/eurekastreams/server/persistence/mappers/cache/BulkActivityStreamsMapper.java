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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.chained.PartialMapperResponse;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;
import org.eurekastreams.server.service.actions.strategies.activity.ListCollider;

/**
 * Bulk maps multiple activity streams and condenses them into a single list.
 */
public class BulkActivityStreamsMapper extends CachedDomainMapper implements
        DomainMapper<List<Long>, PartialMapperResponse<List<Long>, List<Long>>>
{
    /**
     * Or collider, used to cross the streams.
     */
    private ListCollider orCollider = null;

    /**
     * Max number of items.
     */
    private int maxItems = 0;

    /**
     * Set the Or collider.
     * 
     * @param inOrCollider
     *            the collider.
     */
    public void setOrCollider(final ListCollider inOrCollider)
    {
        orCollider = inOrCollider;
    }

    /**
     * Set the max items to return.
     * 
     * @param inMaxItems
     *            the max items.
     */
    public void setMaxItems(final int inMaxItems)
    {
        maxItems = inMaxItems;
    }

    /**
     * Execute the mapper.
     * 
     * @param inRequest
     *            the list of activity streams.
     * @return the combined stream.
     */
    public PartialMapperResponse<List<Long>, List<Long>> execute(final List<Long> inRequest)
    {
        Map<String, Long> keys = new HashMap<String, Long>();

        for (Long id : inRequest)
        {
            keys.put(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + id, id);
        }

        Map<String, ArrayList<Long>> results = getCache().multiGetList(keys.keySet());

        List<Long> items = new ArrayList<Long>();
        List<Long> remainingRequest = new ArrayList<Long>();

        for (Entry<String, Long> entry : keys.entrySet())
        {
            // Not found by multiget, add to partial mapper request.
            if (!results.containsKey(entry.getKey()))
            {
                remainingRequest.add(entry.getValue());
            }
            else
            {
                items = orCollider.collide(items, results.get(entry.getKey()), maxItems);
            }
        }

        if (remainingRequest.size() == 0)
        {
            return new PartialMapperResponse<List<Long>, List<Long>>(items);
        }
        else
        {
            return new PartialMapperResponse<List<Long>, List<Long>>(items, remainingRequest);
        }

    }

}
