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

import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Mapper to get unread alert counts from cache.
 */
public class GetCachedAlertCountByUserId extends CachedDomainMapper
{
    /**
     * Mapper to sync unread alert counts in cache with database count.
     */
    private SyncUnreadApplicationAlertCountCacheByUserId syncMapper;

    /**
     * Constructor.
     * 
     * @param inSyncMapper
     *            sync mapper to set.
     */
    public GetCachedAlertCountByUserId(final SyncUnreadApplicationAlertCountCacheByUserId inSyncMapper)
    {
        syncMapper = inSyncMapper;
    }

    /**
     * Gets the value from cache. If nothing was found in cache, use the sync mapper to set it and return the value.
     * 
     * @param userId
     *            the user id to find unread counts for.
     * @return the count of unread alerts.
     */
    public int execute(final long userId)
    {
        Integer result = (Integer) getCache().get(CacheKeys.UNREAD_APPLICATION_ALERT_COUNT_BY_USER + userId);
        if (result == null)
        {
            result = syncMapper.execute(userId);
        }
        return result;
    }
}
