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
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Cache updater - scaffolding for paged bulk updates of cache by IDs.
 * 
 * @param <CachedItemType>
 *            the type of item we're retrieving and updating in cache
 * @param <UpdateInfoType>
 *            the type of object the caller will pass into execute() and we'll
 *            pass into updateCachedEntity
 */
public abstract class UpdateCachedItemsByIds<CachedItemType, UpdateInfoType>
        extends CachedDomainMapper
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(UpdateCachedItemsByIds.class);

    /**
     * The batch size for getting cached items.
     */
    private Integer batchSize;

    /**
     * Constructor.
     * 
     * @param inBatchSize
     *            the batch size for getting items from cache.
     */
    public UpdateCachedItemsByIds(final Integer inBatchSize)
    {
        batchSize = inBatchSize;
    }

    /**
     * Update cached items by their ids.
     * 
     * @param inCachedDbIds
     *            list of ids of cached items to update in cache
     * @param inUpdateInfo
     *            the info passed into updateCachedEntity for all items that
     *            need to be updated
     */
    @SuppressWarnings("unchecked")
    public void execute(final List<Long> inCachedDbIds,
            final UpdateInfoType inUpdateInfo)
    {
        int batchStartingIndex = 0;
        int listSize = inCachedDbIds.size();

        log.info("Updating " + listSize + " cached objects for "
                + getCacheKeyPrefix());

        while (batchStartingIndex < listSize)
        {
            int toIndex = batchStartingIndex + batchSize;
            if (toIndex >= listSize)
            {
                toIndex = listSize;
            }

            log.info("Fetching batch of cached objects for "
                    + getCacheKeyPrefix()
                    + " from input collection from indices "
                    + batchStartingIndex + " to " + (toIndex - 1)
                    + " and batch size " + batchSize);

            // make the multiget request to the cache
            List<Long> batch = inCachedDbIds.subList(batchStartingIndex,
                    toIndex);
            List<String> keys = new ArrayList<String>(batchSize);
            for (Long objectId : batch)
            {
                keys.add(getCacheKeyPrefix() + objectId);
            }

            // get the batch of objects from cache
            Map<String, CachedItemType> cachedItems = (Map<String, CachedItemType>) getCache()
                    .multiGet(keys);

            // loop across and update the cached objects - loop across the
            // results we found in cache, not the IDs we were looking for
            // because we only need to update objects that are
            // already in cache
            for (String cacheKey : cachedItems.keySet())
            {
                CachedItemType cachedItem = cachedItems.get(cacheKey);
                if (updateCachedEntity(cachedItem, inUpdateInfo))
                {
                    getCache().set(cacheKey, cachedItem);
                }
            }
            batchStartingIndex = toIndex;
        }
    }

    /**
     * Get the cache key prefix.
     * 
     * @return the cache key prefix
     */
    protected abstract String getCacheKeyPrefix();

    /**
     * Update the input cached item with the update info that was passed into
     * the execute() method by the caller.
     * 
     * @param inCachedItem
     *            the item to update
     * @param inUpdateInfo
     *            object passed into execute from caller, used to update the
     *            cached item
     * @return whether or not the cached item changed
     */
    protected abstract Boolean updateCachedEntity(
            final CachedItemType inCachedItem, UpdateInfoType inUpdateInfo);
}
