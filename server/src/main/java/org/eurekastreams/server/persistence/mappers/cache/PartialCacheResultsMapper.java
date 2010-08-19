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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.chained.PartialMapperResponse;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Generic cache mapper that returns the results and a partial mapper response with a new request.
 *
 * @param <KeySuffixType>
 *            the type of key suffix to find in cache
 * @param <CachedValueType>
 *            the type of value stored in cache
 */
public class PartialCacheResultsMapper<KeySuffixType, CachedValueType> extends CachedDomainMapper implements
        DomainMapper<Collection<KeySuffixType>,
        // line break
        PartialMapperResponse<Collection<KeySuffixType>, Collection<CachedValueType>>>
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
    public PartialCacheResultsMapper(final CacheKeySuffixTransformer<KeySuffixType> inKeySuffixTransformer,
            final String inCacheKeyPrefix)
    {
        keySuffixTransformer = inKeySuffixTransformer;
        cacheKeyPrefix = inCacheKeyPrefix;
    }

    /**
     * Constructor.
     *
     * @param inKeySuffixes
     *            suffixes to look for in cache
     * @return a partial mapper response containing the data found and a new request of the key suffixes not found
     */
    public PartialMapperResponse<Collection<KeySuffixType>, Collection<CachedValueType>> execute(
            final Collection<KeySuffixType> inKeySuffixes)
    {
        // build a list of all cache keys and a map to help find which are missing
        List<String> keys = new ArrayList<String>();
        Map<String, KeySuffixType> cacheKeyToSuffixMap = new HashMap<String, KeySuffixType>();
        for (KeySuffixType suffix : inKeySuffixes)
        {
            String cacheKey = cacheKeyPrefix + keySuffixTransformer.transform(suffix);
            cacheKeyToSuffixMap.put(cacheKey, suffix);
            keys.add(cacheKey);
        }

        // get the results from cache
        Map<String, CachedValueType> cachedResults = (Map<String, CachedValueType>) getCache().multiGet(keys);

        Set<CachedValueType> foundResults = new HashSet<CachedValueType>();
        Set<KeySuffixType> suffixesNotFound = new HashSet<KeySuffixType>();

        // interpret the results - building a list of results and new requests from those missing
        for (String cacheKey : cacheKeyToSuffixMap.keySet())
        {
            if (!cachedResults.containsKey(cacheKey) || cachedResults.get(cacheKey) == null)
            {
                // missing result - add the suffix to the new request
                suffixesNotFound.add(cacheKeyToSuffixMap.get(cacheKey));
            }
            else
            {
                // found the result - add it to the results
                foundResults.add(cachedResults.get(cacheKey));
            }
        }

        if (suffixesNotFound.size() == 0)
        {
            // complete response
            return new PartialMapperResponse<Collection<KeySuffixType>, Collection<CachedValueType>>(foundResults);
        }
        else
        {
            // partial response
            return new PartialMapperResponse<Collection<KeySuffixType>, Collection<CachedValueType>>(foundResults,
                    suffixesNotFound);
        }
    }
}
