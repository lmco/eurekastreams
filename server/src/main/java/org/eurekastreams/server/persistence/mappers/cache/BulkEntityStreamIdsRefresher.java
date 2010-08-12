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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.chained.RefreshStrategy;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Refreshes multiple entity stream IDs.
 */
public class BulkEntityStreamIdsRefresher extends CachedDomainMapper implements
        RefreshStrategy<Map<Long, EntityType>, List<Long>>
{
    /**
     * Refresh the entity stream IDs for the corresponding entities.
     * 
     * @param request
     *            the entity ID and corresponding type.
     * @param data
     *            the updated IDs.
     */
    public void refresh(final Map<Long, EntityType> request, final List<Long> data)
    {
        int i = 0;
        for (Entry<Long, EntityType> entry : request.entrySet())
        {
            switch (entry.getValue())
            {
            case PERSON:
                getCache().set(CacheKeys.PERSON_ENTITITY_STREAM_VIEW_ID + entry.getKey(), data.get(i));
                break;
            case GROUP:
                getCache().set(CacheKeys.GROUP_ENTITITY_STREAM_VIEW_ID + entry.getKey(), data.get(i));
                break;
            default:
                throw new RuntimeException("Unhandled type.");
            }

            i++;
        }
    }

}
