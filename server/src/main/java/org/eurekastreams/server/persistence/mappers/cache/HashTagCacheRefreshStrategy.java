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
import org.eurekastreams.server.domain.stream.HashTag;
import org.eurekastreams.server.persistence.mappers.chained.RefreshStrategy;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Cache refresh strategy for HashTag. Takes a hashtag content and a HashTag object, storing the HashTag in cache.
 */
public class HashTagCacheRefreshStrategy extends CachedDomainMapper implements RefreshStrategy<String, HashTag>
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Refresh the cache with the input content and hash tag.
     *
     * @param inContent
     *            the content of the hashtag
     * @param inHashTag
     *            a HashTag retrieved from another data source
     */
    @Override
    public void refresh(final String inContent, final HashTag inHashTag)
    {
        String content = null;
        if (!inContent.startsWith("#"))
        {
            content = "#" + inContent.toLowerCase();
        }
        else
        {
            content = inContent.toLowerCase();
        }

        log.info("Caching hashtag: " + content);
        getCache().set(CacheKeys.HASH_TAG_BY_LOWERCASED_CONTENT + content, inHashTag);
    }

}
