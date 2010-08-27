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

import org.eurekastreams.server.persistence.mappers.chained.RefreshStrategy;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Generic mapper to retrieve single values named using the input request.
 *
 * @param <TRequestType>
 *            Request type; toString must return the value used in the cache key.
 * @param <TResponseType>
 *            Data type returned by fetch mappers and hence used to refresh.
 */
public class SetKeyedSingleValueCacheMapper<TRequestType, TResponseType> extends CachedDomainMapper implements
        RefreshStrategy<TRequestType, TResponseType>
{
    /** Prefix on the cache key. */
    private String keyPrefix;

    /**
     * Constructor.
     *
     * @param inKeyPrefix
     *            Prefix on the cache key.
     */
    public SetKeyedSingleValueCacheMapper(final String inKeyPrefix)
    {
        keyPrefix = inKeyPrefix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh(final TRequestType inRequest, final TResponseType inResponse)
    {
        getCache().set(keyPrefix + inRequest.toString(), inResponse);
    }
}
