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
package org.eurekastreams.server.persistence.mappers.stream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * Tests deleting a composite stream from the cache.
 */
@TransactionConfiguration(defaultRollback = false)
public class UpdateCachedCompositeStreamTest extends CachedMapperTest
{
    /**
     * System under test.
     */
    @Autowired
    UpdateCachedCompositeStream sut;

    /**
     * Cache.
     */
    @Autowired
    Cache cache;

    /**
     * CompositeStream id used in tests.
     */
    private static final long COMPOSITE_STREAM_ID = 1L;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        cache.delete(CacheKeys.COMPOSITE_STREAM_BY_ID + COMPOSITE_STREAM_ID);
        // check for the composite stream object in the cache.
        StreamView resultStreamView = (StreamView) cache.get(CacheKeys.COMPOSITE_STREAM_BY_ID + COMPOSITE_STREAM_ID);
        assertNull(resultStreamView);
    }

    /**
     * Test that the cache with no entry has the entry populated.
     */
    @Test
    public void testExecuteMissingFromCache()
    {
        StreamView resultStreamView = (StreamView) cache.get(CacheKeys.COMPOSITE_STREAM_BY_ID + COMPOSITE_STREAM_ID);
        assertNull(resultStreamView);

        sut.execute(COMPOSITE_STREAM_ID);

        StreamView updateResultStreamView =
                (StreamView) cache.get(CacheKeys.COMPOSITE_STREAM_BY_ID + COMPOSITE_STREAM_ID);
        assertNotNull(updateResultStreamView);
    }

    /**
     * Test that an entry exists in cache for this composite stream before and after.
     */
    @Test
    public void testExecuteEntryInCache()
    {
        // Warm up the cache by requesting the list.
        sut.execute(COMPOSITE_STREAM_ID);

        // Verify that the list exists in cache.
        StreamView resultStreamView = (StreamView) cache.get(CacheKeys.COMPOSITE_STREAM_BY_ID + COMPOSITE_STREAM_ID);
        assertNotNull(resultStreamView);
        // Change the name of the list and put it back in cache.
        resultStreamView.setName("TestName");
        cache.set(CacheKeys.COMPOSITE_STREAM_BY_ID + COMPOSITE_STREAM_ID, resultStreamView);

        sut.execute(COMPOSITE_STREAM_ID);

        // Pull the list from cache.
        StreamView updateResultStreamView =
                (StreamView) cache.get(CacheKeys.COMPOSITE_STREAM_BY_ID + COMPOSITE_STREAM_ID);
        assertNotNull(updateResultStreamView);
        // Verify that the name has not been updated indicating that
        // this list has not been updated because it was already found in cache.
        assertTrue(updateResultStreamView.getName().equals("TestName"));
    }

}
