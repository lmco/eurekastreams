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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

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
public class DeleteCachedCompositeStreamSearchByIdTest extends CachedMapperTest
{
    /**
     * System under test.
     */
    @Autowired
    DeleteCachedCompositeStreamSearchById sut;
    
    /**
     * Cache.
     */
    @Autowired
    Cache cache;
    
    /**
     * User id used in tests.
     */
    private static final long PERSON_ID = 999L;
    
    /**
     * CompositeStream id used in tests.
     */
    private static final long COMPOSITE_STREAM_SEARCH_ID = 99L;
    
    /**
     * Setup method.
     */
    @SuppressWarnings("unchecked")
    @Before
    public void setup()
    {
        // create a composite stream list for the person with 1 composite stream id.
        List<Long> compositeStreamSearchIdsList = new ArrayList<Long>();
        compositeStreamSearchIdsList.add(COMPOSITE_STREAM_SEARCH_ID);

        // set list in the cache for the person.
        cache.setList(CacheKeys.COMPOSITE_STREAM_SEARCH_IDS_BY_PERSON_ID + PERSON_ID, compositeStreamSearchIdsList);

        // assert the list containing 1 composite stream id is in the cache.
        List<Long> result = 
            cache.getList(CacheKeys.COMPOSITE_STREAM_SEARCH_IDS_BY_PERSON_ID + PERSON_ID);
        assertEquals(1, result.size());
    }
    
    /**
     * test.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute()
    {
        sut.execute(PERSON_ID, COMPOSITE_STREAM_SEARCH_ID);

        //assert that cache is empty for compositeStream .
        assertEquals(0, 
                (cache.getList(CacheKeys.COMPOSITE_STREAM_SEARCH_IDS_BY_PERSON_ID + PERSON_ID)).size());
    }

}
