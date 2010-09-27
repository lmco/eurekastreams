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

import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.chained.RefreshStrategy;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Reusable cache refresher - uses a CacheKeySuffixTransformer to generate the cache key.
 *
 * @param <Request>
 *            the request that the data was found with
 */
public class SingleListValueCacheRefreshStrategy<Request> extends CachedDomainMapper implements
        RefreshStrategy<Request, List<Long>>
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Cache key suffix.
     */
    private final String cacheKeyPrefix;

    /**
     * Cache key suffix transformer.
     */
    private final CacheKeySuffixTransformer<Request> cacheKeySuffixTransformer;

    /**
     * Constructor.
     *
     * @param inCacheKeyPrefix
     *            cache key prefix
     * @param inCacheKeySuffixTransformer
     *            the cache key suffix transformer
     */
    public SingleListValueCacheRefreshStrategy(final String inCacheKeyPrefix,
            final CacheKeySuffixTransformer<Request> inCacheKeySuffixTransformer)
    {
        cacheKeyPrefix = inCacheKeyPrefix;
        cacheKeySuffixTransformer = inCacheKeySuffixTransformer;
    }

    /**
     * Refresh the list.
     *
     * @param inRequest
     *            the request that generated the response
     * @param inResponse
     *            the list of longs of the response
     */
    @Override
    public void refresh(final Request inRequest, final List<Long> inResponse)
    {
        String cacheKeySuffix = cacheKeySuffixTransformer.transform(inRequest);

        if (log.isTraceEnabled())
        {
            log.trace("Setting List<Long> of size " + (inResponse == null ? "(null)" : inResponse.size())
                    + " to cache with cache key: " + cacheKeyPrefix + cacheKeySuffix);
        }
        getCache().setList(cacheKeyPrefix + cacheKeySuffix, inResponse);
    }
}
