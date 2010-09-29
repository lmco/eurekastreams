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

import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.chained.RefreshStrategy;

/**
 * Reusable cache refresher - uses a {@link Transformer}s to generate the cache key/value.
 * 
 * @param <Request>
 *            Key type of request.
 * @param <Response>
 *            Type of response objects.
 */
public class MultiValueCacheRefreshStrategy<Request, Response> implements
        RefreshStrategy<Collection<Request>, Collection<Response>>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Cache key suffix.
     */
    private final String cacheKeyPrefix;

    /**
     * Cache key suffix transformer.
     */
    private final Transformer<Response, Serializable> cacheKeySuffixTransformer;

    /**
     * Cache key suffix transformer.
     */
    private final Transformer<Response, Serializable> cacheValueTransformer;

    /**
     * Cache.
     */
    private final Cache cache;

    /**
     * Constructor.
     * 
     * @param inCacheKeyPrefix
     *            cache key prefix
     * @param inCacheKeySuffixTransformer
     *            the cache key suffix transformer
     * @param inCacheValueTransformer
     *            Transformer for cache value.
     * @param inCache
     *            Cache to use.
     */
    public MultiValueCacheRefreshStrategy(final String inCacheKeyPrefix,
            final Transformer<Response, Serializable> inCacheKeySuffixTransformer,
            final Transformer<Response, Serializable> inCacheValueTransformer, final Cache inCache)
    {
        cacheKeyPrefix = inCacheKeyPrefix;
        cacheKeySuffixTransformer = inCacheKeySuffixTransformer;
        cacheValueTransformer = inCacheValueTransformer;
        cache = inCache;

    }

    /**
     * Loop through all the response objects and cache them according to key, suffix transformer and value transformer
     * results.
     * 
     * @param inRequest
     *            request objects.
     * @param inResponse
     *            response objects.
     */
    @Override
    public void refresh(final Collection<Request> inRequest, final Collection<Response> inResponse)
    {
        for (Response r : inResponse)
        {
            String key = cacheKeyPrefix + cacheKeySuffixTransformer.transform(r);

            if (log.isDebugEnabled())
            {
                log.debug("Caching value for key: " + key);
            }
            cache.set(key, cacheValueTransformer.transform(r));

        }

    }
}
