/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.stream;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.testhelpers.SimpleMemoryCache;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract test class for mappers tests that need to clear the cache before the test is executed. 
 */
public abstract class CachedMapperTest extends MapperTest
{
    /**
     * Cache.
     */
    @Autowired
    private Cache cache;    
    
    /**
     * Clear cache.
     */
    @Before
    public void instanceSetup()
    {
        ((SimpleMemoryCache) cache).clear();
    }

    /**
     * @return the cache
     */
    public Cache getCache()
    {
        return cache;
    }
}
