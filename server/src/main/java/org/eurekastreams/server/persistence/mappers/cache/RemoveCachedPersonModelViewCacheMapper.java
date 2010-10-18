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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Remove a PersonModelView from cache.
 */
public class RemoveCachedPersonModelViewCacheMapper extends CachedDomainMapper implements DomainMapper<Long, Boolean>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Remove a PersonModelView from cache by the person's id.
     * 
     * @param inPersonId
     *            the id of the person to remove from cache
     * @return true
     */
    @Override
    public Boolean execute(final Long inPersonId)
    {
        String cacheKey = CacheKeys.PERSON_BY_ID + inPersonId;
        log.info("Removing person #" + inPersonId + " from cache by deleting cache key " + cacheKey);
        getCache().delete(cacheKey);
        return true;
    }
}
