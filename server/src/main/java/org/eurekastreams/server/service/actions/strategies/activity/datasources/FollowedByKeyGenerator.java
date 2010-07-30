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
package org.eurekastreams.server.service.actions.strategies.activity.datasources;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;

/**
 * Generate the memcached key for followed by.
 *
 */
public class FollowedByKeyGenerator implements MemcachedKeyGenerator
{
    /**
     * Person mapper.
     */
    private GetPeopleByAccountIds personMapper;

    /**
     * Default constructor.
     * @param inPersonMapper person mapper.
     */
    public FollowedByKeyGenerator(final GetPeopleByAccountIds inPersonMapper)
    {
        personMapper = inPersonMapper;
    }

    /**
     * Get the keys for followed by.
     * @param request the JSON request object.
     * @return the key for followed by.
     */
    public List<String> getKeys(final JSONObject request)
    {
        Long id = personMapper.fetchUniqueResult(request.getString("followedBy")).getId();

        List<String> returned = new ArrayList<String>();
        returned.add(CacheKeys.ACTIVITIES_BY_FOLLOWING + id);
        return returned;
    }

}
