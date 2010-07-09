/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test fixture for StreamCacheLoader.
 */
public class StreamCacheLoaderTest extends CachedMapperTest
{
    /**
     * Test org id.
     */
    private static final Long ORG_1_TEST_ID = 5000L;

    /**
     * Test org id.
     */
    private static final Long ORG_2_TEST_ID = 5001L;

    /**
     * Test org id.
     */
    private static final Long ORG_3_TEST_ID = 5002L;

    /**
     * Test org id.
     */
    private static final Long ORG_4_TEST_ID = 5003L;

    /**
     * System under test.
     */
    @Autowired
    private StreamCacheLoader streamCacheLoader;

    /**
     * Test initialize.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testInitialize()
    {
        Cache cache = streamCacheLoader.getCache();

        assertNull(cache.get(CacheKeys.COMPOSITE_STREAM_BY_ID + ORG_3_TEST_ID));
        assertNull(cache.get(CacheKeys.COMPOSITE_STREAM_SEARCH_BY_ID + "1"));

        assertNull(cache.getList(CacheKeys.STARRED_BY_PERSON_ID + "42"));
        assertNull(cache.getList(CacheKeys.STARRED_BY_PERSON_ID + "98"));
        assertNull(cache.getList(CacheKeys.STARRED_BY_PERSON_ID + "142"));

        assertNull(cache.get(CacheKeys.STREAM_BY_ID + "4"));
        assertNull(cache.get(CacheKeys.STREAM_BY_ID + "878"));
        assertNull(cache.get(CacheKeys.STREAM_BY_ID + "837433"));
        assertNull(cache.get(CacheKeys.STREAM_BY_ID + "347"));

        // Everyone stream
        assertNull(cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + ORG_3_TEST_ID));

        // Core Streams
        assertNull(cache.getList(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE));
        assertNull(cache.getList(CacheKeys.CORE_STREAMVIEW_ID_PARENTORG));
        assertNull(cache.getList(CacheKeys.CORE_STREAMVIEW_ID_PEOPLEFOLLOW));
        assertNull(cache.getList(CacheKeys.CORE_STREAMVIEW_ID_STARRED));

        // Composite streams for a user
        assertNull(cache.getList(CacheKeys.COMPOSITE_STREAM_IDS_BY_PERSON_ID + "99"));

        // Personal composite stream
        assertNull(cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + "4"));
        assertNull(cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + "8"));

        // Parent Org composite stream for smithers
        //assertNull(cache.get(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + "3"));

        streamCacheLoader.initialize();

        assertNotNull(cache.get(CacheKeys.COMPOSITE_STREAM_BY_ID + ORG_3_TEST_ID));
        assertNotNull(cache.get(CacheKeys.COMPOSITE_STREAM_SEARCH_BY_ID + "1"));

        assertNotNull(cache.getList(CacheKeys.STARRED_BY_PERSON_ID + "42"));
        assertNotNull(cache.getList(CacheKeys.STARRED_BY_PERSON_ID + "98"));
        assertNotNull(cache.getList(CacheKeys.STARRED_BY_PERSON_ID + "142"));

        assertNotNull(cache.get(CacheKeys.STREAM_BY_ID + "4"));
        assertNotNull(cache.get(CacheKeys.STREAM_BY_ID + "878"));
        assertNotNull(cache.get(CacheKeys.STREAM_BY_ID + "837433"));
        assertNotNull(cache.get(CacheKeys.STREAM_BY_ID + "347"));

        List<Long> allIds = cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + ORG_3_TEST_ID);
        assertNotNull(allIds);
        assertEquals(5, allIds.size());

        // Core Streams
        assertNotNull(cache.get(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE));
        assertEquals(cache.get(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE), ORG_3_TEST_ID);

        assertNotNull(cache.get(CacheKeys.CORE_STREAMVIEW_ID_PARENTORG));
        assertEquals(cache.get(CacheKeys.CORE_STREAMVIEW_ID_PARENTORG), ORG_2_TEST_ID);

        assertNotNull(cache.get(CacheKeys.CORE_STREAMVIEW_ID_PEOPLEFOLLOW));
        assertEquals(cache.get(CacheKeys.CORE_STREAMVIEW_ID_PEOPLEFOLLOW), ORG_1_TEST_ID);

        assertNotNull(cache.get(CacheKeys.CORE_STREAMVIEW_ID_STARRED));
        assertEquals(cache.get(CacheKeys.CORE_STREAMVIEW_ID_STARRED), ORG_4_TEST_ID);

        List<Long> personStream = cache.getList(CacheKeys.COMPOSITE_STREAM_IDS_BY_PERSON_ID + "98");
        assertNotNull(personStream);
        assertEquals(2, personStream.size());

        List<Long> personalStream1 = cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + "4");
        assertNotNull(personalStream1);
        assertEquals(2, personalStream1.size());

        List<Long> personalStream2 = cache.getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + "8");
        assertNotNull(personalStream2);
        assertEquals(0, personalStream2.size());

        //List<Long> orgStream = (List<Long>) cache.get(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + "3");
        //assertNotNull(orgStream);
        //assertEquals(2, orgStream.size());
    }
}
