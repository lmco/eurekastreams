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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.ActivitySecurityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.chained.PartialMapperResponse;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Maps activity security information from the cache.
 */
public class BulkActivitySecurityMapper extends CachedDomainMapper implements
        DomainMapper<List<Long>, PartialMapperResponse<List<Long>, Collection<ActivitySecurityDTO>>>
{
    /**
     * Log.
     */
    private static Log log = LogFactory.make();
    
    /**
     * @param activityIds
     *            the activity IDs.
     * @return the security information associated with the activities..
     */
    public PartialMapperResponse<List<Long>, Collection<ActivitySecurityDTO>> execute(final List<Long> activityIds)
    {
        List<String> stringKeys = new ArrayList<String>();
        for (long key : activityIds)
        {
            stringKeys.add(CacheKeys.ACTIVITY_SECURITY_BY_ID + key);
        }

        // Finds activities in the cache.
        Map<String, ActivitySecurityDTO> activities = (Map<String, ActivitySecurityDTO>) (Map<String, ? >) getCache()
                .multiGet(stringKeys); // Determines if any of the activities were missing from the cache

        List<Long> uncachedActivityKeys = new ArrayList<Long>();

        for (int i = 0; i < activityIds.size(); i++)
        {
            if (!activities.containsKey(CacheKeys.ACTIVITY_SECURITY_BY_ID + activityIds.get(i)))
            {
                uncachedActivityKeys.add(activityIds.get(i));
            }
        }
        if (uncachedActivityKeys.size() > 0)
        {
            return new PartialMapperResponse<List<Long>, Collection<ActivitySecurityDTO>>(activities.values(),
                    uncachedActivityKeys);
        }
        else
        {
            return new PartialMapperResponse<List<Long>, Collection<ActivitySecurityDTO>>(activities.values());
        }
    }
}
