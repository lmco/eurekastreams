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

import java.util.Iterator;
import java.util.Set;

import org.eurekastreams.server.persistence.mappers.stream.BaseArgCachedDomainMapper;

/**
 * Mapper to remove keys from cache.
 * 
 */
public class DeleteCacheKeys extends BaseArgCachedDomainMapper<Set<String>, Boolean>
{

    /**
     * Remove keys from cache.
     * 
     * @param inRequest
     *            Set of keys to delete.
     * @return True if successful.
     */
    @Override
    public Boolean execute(final Set<String> inRequest)
    {
        Cache cache = getCache();

        // unfortunately cannot do bulk deletes have to loop thorough.
        Iterator<String> it = inRequest.iterator();
        while (it.hasNext())
        {
            cache.delete(it.next());
        }
        return Boolean.TRUE;
    }

}
