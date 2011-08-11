/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import org.eurekastreams.server.persistence.mappers.chained.RefreshStrategy;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Generic mapper to set multiple keyed single values in cache at one time. The keys come from the list of requests and
 * the values come from the list of responses. IMPORTANT: There MUST be a 1-to-1 correspondence between the requests and
 * the responses (rqst[i] must go with resp[i]) - both order and placeholders for missing values - else keys will be
 * associated with the wrong values in cache!
 *
 * @param <TRequestType>
 *            Request type; toString must return the value used in the cache key.
 */
public class MultiSetKeyedSingleValueCacheMapper<TRequestType> extends CachedDomainMapper implements
        RefreshStrategy<List<TRequestType>, List<Long>>
{
    /** Prefix on the cache key. */
    private final String keyPrefix;

    /**
     * Constructor.
     *
     * @param inKeyPrefix
     *            Prefix on the cache key.
     */
    public MultiSetKeyedSingleValueCacheMapper(final String inKeyPrefix)
    {
        keyPrefix = inKeyPrefix;
    }

    /**
     * {@inheritDoc}
     */
    public void refresh(final List<TRequestType> inRequest, final List<Long> inResponse)
    {
        for (int i = 0; i < inRequest.size() && i < inResponse.size(); i++)
        {
            getCache().set(keyPrefix + inRequest.get(i).toString(), inResponse.get(i));
        }

    }
}
