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

import static org.junit.Assert.assertSame;

import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.cache.testhelpers.SimpleMemoryCache;
import org.junit.Test;

/**
 * Test fixture for RefreshSystemSettingsCacheMapperImpl.
 */
public class RefreshSystemSettingsCacheMapperImplTest
{
    /**
     * Test refreshing cache.
     */
    @Test
    public void testRefresh()
    {
        // system under test
        RefreshSystemSettingsCacheMapperImpl sut = new RefreshSystemSettingsCacheMapperImpl();
        SimpleMemoryCache cache = new SimpleMemoryCache();
        sut.setCache(cache);

        // settings we're storing in cache
        SystemSettings settings = new SystemSettings();

        // perform system under test
        sut.refresh(settings);

        // make sure the settings were stored in cache
        assertSame(settings, cache.get(CacheKeys.SYSTEM_SETTINGS));
    }
}
