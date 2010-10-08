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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
        DomainMapper<List<KeySuffixType>,
        // line break
        PartialMapperResponse<List<KeySuffixType>, List<CachedValueType>>>
{
    /**
     * Cache key suffix transformer.
     */
    private Transformer<KeySuffixType, String> keySuffixTransformer;

    /**
     * Cache key prefix.
     */
    private String cacheKeyPrefix;

    /**
     * True if the return type is a list of lists.
     */
    private Boolean listOfLists;

    /**
     * Constructor.
     * 
     * @param inKeySuffixTransformer
     *            the key suffix transformer
     * @param inCacheKeyPrefix
     *            the cache key prefix
     * @param isListOfLists
     *            if the mapper is returning a list of lists.
     */
    public PartialCacheResultsMapper(final Transformer<KeySuffixType, String> inKeySuffixTransformer,
            final String inCacheKeyPrefix, final Boolean isListOfLists)
    {
        keySuffixTransformer = inKeySuffixTransformer;
        cacheKeyPrefix = inCacheKeyPrefix;
        listOfLists = isListOfLists;
    }

    /**
     * Constructor.
     * 
     * @param inKeySuffixes
     *            suffixes to look for in cache
     * @return a partial mapper response containing the data found and a new request of the key suffixes not found
     */
    public PartialMapperResponse<List<KeySuffixType>, List<CachedValueType>> execute(
            final List<KeySuffixType> inKeySuffixes)
    {
        // build a list of all cache keys and a map to help find which are missing
        List<String> keys = new ArrayList<String>();
        Map<KeySuffixType, String> suffixToCacheKeyMap = new HashMap<KeySuffixType, String>();
        for (KeySuffixType suffix : inKeySuffixes)
        {
            String cacheKey = cacheKeyPrefix + keySuffixTransformer.transform(suffix);
            suffixToCacheKeyMap.put(suffix, cacheKey);
            keys.add(cacheKey);
        }

        // get the results from cache

        Map<String, CachedValueType> cachedResults = null;

        if (listOfLists)
        {
            cachedResults = (Map<String, CachedValueType>) getCache().multiGetList(keys);
        }
        else
        {
            cachedResults = (Map<String, CachedValueType>) getCache().multiGet(keys);
        }

        List<CachedValueType> foundResults = new LinkedList<CachedValueType>();
        List<KeySuffixType> suffixesNotFound = new LinkedList<KeySuffixType>();

        // interpret the results - building a list of results and new requests from those missing
        for (KeySuffixType suffix : inKeySuffixes)
        {
            if (!cachedResults.containsKey(suffixToCacheKeyMap.get(suffix))
                    || cachedResults.get(suffixToCacheKeyMap.get(suffix)) == null)
            {
                // missing result - add the suffix to the new request
                suffixesNotFound.add(suffix);
            }
            else
            {
                // found the result - add it to the results
                foundResults.add(cachedResults.get(suffixToCacheKeyMap.get(suffix)));
            }
        }

        if (suffixesNotFound.size() == 0)
        {
            // complete response
            return new PartialMapperResponse<List<KeySuffixType>, List<CachedValueType>>(foundResults);
        }
        else
        {
            // partial response
            return new PartialMapperResponse<List<KeySuffixType>, //
            List<CachedValueType>>(foundResults, suffixesNotFound);
        }
    }
}
