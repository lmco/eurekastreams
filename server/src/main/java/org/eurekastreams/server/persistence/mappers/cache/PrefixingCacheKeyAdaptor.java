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

import java.util.Collections;
import java.util.Set;

import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Adaptor for DeleteCacheKeys (and similar mappers) which allows the keyname-based mapper to be used where a mapper
 * that just takes an ID would be used.
 */
public class PrefixingCacheKeyAdaptor implements DomainMapper<Object, Boolean>
{
    /** Prefix to add to create key name. */
    private final String prefix;

    /** Mapper being adapted. */
    private final DomainMapper<Set<String>, Boolean> wrappedMapper;

    /**
     * Constructor.
     * 
     * @param inPrefix
     *            Prefix to add to create key name.
     * @param inWrappedMapper
     *            Mapper being adapted.
     */
    public PrefixingCacheKeyAdaptor(final String inPrefix, final DomainMapper<Set<String>, Boolean> inWrappedMapper)
    {
        prefix = inPrefix;
        wrappedMapper = inWrappedMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean execute(final Object inRequest)
    {
        return wrappedMapper.execute(Collections.singleton(prefix + inRequest));
    }
}
