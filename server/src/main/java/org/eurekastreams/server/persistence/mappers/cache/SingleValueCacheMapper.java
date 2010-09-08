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

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Mapper to get back a single item from cache.
 *
 * @param <KeySuffixType>
 *            the type of object to convert to a cache key
 * @param <CachedValueType>
 *            the value type to get from cache
 */
public class SingleValueCacheMapper<KeySuffixType, CachedValueType> extends CachedDomainMapper implements
        DomainMapper<KeySuffixType, CachedValueType>
{
    /**
     * Cache key suffix transformer.
     */
    private CacheKeySuffixTransformer<KeySuffixType> keySuffixTransformer;

    /**
     * Cache key prefix.
     */
    private String cacheKeyPrefix;

    /**
     * Constructor.
     *
     * @param inKeySuffixTransformer
     *            the key suffix transformer
     * @param inCacheKeyPrefix
     *            the cache key prefix
     */
    public SingleValueCacheMapper(final CacheKeySuffixTransformer<KeySuffixType> inKeySuffixTransformer,
            final String inCacheKeyPrefix)
    {
        keySuffixTransformer = inKeySuffixTransformer;
        cacheKeyPrefix = inCacheKeyPrefix;
    }

    /**
     * Constructor.
     *
     * @param inKeySuffix
     *            suffixes to look for in cache
     * @return a partial mapper response containing the data found and a new request of the key suffixes not found
     */
    public CachedValueType execute(final KeySuffixType inKeySuffix)
    {
        String cacheKey = cacheKeyPrefix + keySuffixTransformer.transform(inKeySuffix);
        return (CachedValueType) getCache().get(cacheKey);
    }
}
