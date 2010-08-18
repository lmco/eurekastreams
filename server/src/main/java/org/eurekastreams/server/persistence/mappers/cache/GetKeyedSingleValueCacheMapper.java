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
 * Generic mapper to retrieve single values named using the input request.
 * 
 * @param <TRequestType>
 *            Request type; toString must return the value used in the cache key.
 * @param <TReturnType>
 *            Return type.
 */
public class GetKeyedSingleValueCacheMapper<TRequestType, TReturnType> extends CachedDomainMapper implements
        DomainMapper<TRequestType, TReturnType>
{
    /** Prefix on the cache key. */
    private String keyPrefix;

    /**
     * Constructor.
     * 
     * @param inKeyPrefix
     *            Prefix on the cache key.
     */
    public GetKeyedSingleValueCacheMapper(final String inKeyPrefix)
    {
        keyPrefix = inKeyPrefix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TReturnType execute(final TRequestType inRequest)
    {
        return (TReturnType) getCache().get(keyPrefix + inRequest.toString());
    }
}
