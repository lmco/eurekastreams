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
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Remove a group from cache for later update.
 */
public class RemoveDomainGroupFromCacheMapper extends CachedDomainMapper
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Remove a group from cache.
     *
     * @param inGroup
     *            the group to remove from cache
     */
    public void execute(final DomainGroup inGroup)
    {
        if (log.isInfoEnabled())
        {
            log.info("DomainGroup updated - clearing cache for domain group: " + inGroup.getId());
        }
        getCache().delete(CacheKeys.GROUP_BY_ID + inGroup.getId());
        getCache().delete(CacheKeys.FOLLOWERS_BY_GROUP + inGroup.getId());

        if (log.isInfoEnabled())
        {
            log.info("Need to clear out the stream scope for group with id:" + inGroup.getId() + " - finding it by "
                    + inGroup.getShortName());
        }
        StreamScope groupScope = inGroup.getStreamScope();
        getCache().delete(CacheKeys.STREAM_BY_ID + groupScope.getId());
        if (log.isInfoEnabled())
        {
            log.info("StreamScope for group with id: " + inGroup.getId() + ", stream scope id: " + groupScope.getId()
                    + " deleted from cache.");
        }
    }
}
