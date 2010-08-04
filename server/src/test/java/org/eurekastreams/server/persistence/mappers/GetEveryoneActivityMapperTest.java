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
package org.eurekastreams.server.persistence.mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for the get everyone activity mapper.
 *
 */
public class GetEveryoneActivityMapperTest extends CachedMapperTest
{
    /**
     * Test org id.
     */
    private static final Long ORG_3_TEST_ID = 5002L;

    /**
     * System under test.
     */
    @Autowired
    private GetEveryoneActivityMapper sut;


    /**
     * Get everything from the DB and populate cache.
     */
    @Test
    public void executeGettingFromDB()
    {
       Cache cache = getCache();

       assertNull(cache.getList(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE));
       assertNull(cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + ORG_3_TEST_ID));

       List<Long> ids = sut.execute(null);

       List<Long> allIds = cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + ORG_3_TEST_ID);
       assertNotNull(allIds);
       assertEquals(5, allIds.size());

       assertEquals(ids, allIds);

       assertNotNull(cache.get(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE));
       assertEquals(cache.get(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE), ORG_3_TEST_ID);
    }

    /**
     * Populate cache first (with different data, to confirm). Don't go to the DB.
     */
    @Test
    public void executeGettingAllFromCache()
    {
       Cache cache = getCache();

       assertNull(cache.getList(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE));
       assertNull(cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + 1L));

       cache.set(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE, 1L);

       List<Long> cachedIds = new ArrayList<Long>();
       cachedIds.add(3L);

       cache.setList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + 1L, cachedIds);

       List<Long> ids = sut.execute(null);

       List<Long> allIds = cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + 1L);
       assertNotNull(allIds);
       assertEquals(1, allIds.size());

       assertEquals(ids, allIds);
       assertEquals(ids, cachedIds);

       assertNotNull(cache.get(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE));
       assertEquals(cache.get(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE), 1L);
    }

    /**
     * Put just the ID in cache, get the list from the DB.
     */
    @Test
    public void executeGettingJustIdFromCache()
    {
       Cache cache = getCache();

       assertNull(cache.getList(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE));
       assertNull(cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + 1L));

       cache.set(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE, 1L);

       List<Long> ids = sut.execute(null);

       List<Long> allIds = cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + 1L);
       assertNotNull(allIds);
       assertEquals(5, allIds.size());

       assertEquals(ids, allIds);

       assertNotNull(cache.get(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE));
       assertEquals(cache.get(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE), 1L);
    }

    /**
     * Put just the list in cache, get the ID from the DB.
     */
    @Test
    public void executeGettingJustActivityFromCache()
    {
       Cache cache = getCache();

       assertNull(cache.getList(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE));
       assertNull(cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + ORG_3_TEST_ID));

       List<Long> cachedIds = new ArrayList<Long>();
       cachedIds.add(3L);

       cache.setList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + ORG_3_TEST_ID, cachedIds);

       List<Long> ids = sut.execute(null);

       List<Long> allIds = cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + ORG_3_TEST_ID);
       assertNotNull(allIds);
       assertEquals(1, allIds.size());

       assertEquals(ids, allIds);
       assertEquals(ids, cachedIds);

       assertNotNull(cache.get(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE));
       assertEquals(cache.get(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE), ORG_3_TEST_ID);
    }
}
