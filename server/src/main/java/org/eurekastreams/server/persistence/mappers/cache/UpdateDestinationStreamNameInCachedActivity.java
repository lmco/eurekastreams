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

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;

/**
 * Update the cached destination stream name in a cached entity.
 */
public class UpdateDestinationStreamNameInCachedActivity extends UpdateCachedItemsByIds<ActivityDTO, String>
{
    /**
     * Constructor.
     *
     * @param inBatchSize
     *            the batch size for cache fetches
     */
    public UpdateDestinationStreamNameInCachedActivity(final Integer inBatchSize)
    {
        super(inBatchSize);
    }

    /**
     * Get the cache key prefix.
     *
     * @return the cache key prefix for activity by id
     */
    @Override
    protected String getCacheKeyPrefix()
    {
        return CacheKeys.ACTIVITY_BY_ID;
    }

    /**
     * Update the destination stream entity's display name.
     *
     * @param inCachedItem
     *            the ActivityDTO to update
     * @param inUpdatedDestinationStreamName
     *            the new name of the domain group this activity was posted to
     * @return true
     */
    @Override
    protected Boolean updateCachedEntity(final ActivityDTO inCachedItem, final String inUpdatedDestinationStreamName)
    {
        StreamEntityDTO destinationStream = inCachedItem.getDestinationStream();
        if (destinationStream.getDisplayName().equals(inUpdatedDestinationStreamName))
        {
            return false;
        }
        destinationStream.setDisplayName(inUpdatedDestinationStreamName);
        return true;
    }

}
