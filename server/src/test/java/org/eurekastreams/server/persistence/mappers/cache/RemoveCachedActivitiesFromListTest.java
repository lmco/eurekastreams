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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.requests.RemoveCachedActivitiesFromListRequest;
import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.eurekastreams.server.persistence.mappers.stream.CompositeStreamActivityIdsMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetStreamByOwnerId;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test class for the RemoveCachedActivitiesFromList mapper.
 *
 */
public class RemoveCachedActivitiesFromListTest extends CachedMapperTest
{
    /**
     * Test instance.
     */
    @Autowired
    private StreamCacheLoader streamLoader;

    /**
     * Mock instance of the Cache object.
     */
    @Autowired
    private Cache testCache;

    /**
     * Mock instance of the CompositeStreamActivityIdsMapper.
     */
    @Autowired
    private CompositeStreamActivityIdsMapper activityIdsMapper;

    /**
     * Local instance of the GetStreamByOwnerId mapper to be used in tests.
     */
    private GetStreamByOwnerId streamMapper;

    /**
     * System under test representing a custom list.
     */
    private RemoveCachedActivitiesFromList sutCustomList;

    /**
     * System under test representing a following list.
     */
    private RemoveCachedActivitiesFromList sutFollowingList;

    /**
     * Test stream scope id.
     */
    private static final Long TEST_STREAM_SCOPE_ID = 187L;

    /**
     * Test stream scope owner id.
     */
    private static final Long TEST_STREAM_SCOPE_OWNER_ID = 99L;

    /**
     * Test activities owner id.
     */
    private static final Long TEST_ACTIVITIES_OWNER_ID = 98L;

    /**
     * Test activity id.
     */
    private static final Long TEST_ACTIVITY_ID_1 = 6789L;

    /**
     * Test activity id.
     */
    private static final Long TEST_ACTIVITY_ID_2 = 6790L;

    /**
     * Test activity id.
     */
    private static final Long TEST_ACTIVITY_ID_3 = 6791L;

    /**
     * Test activity id.
     */
    private static final Long TEST_ACTIVITY_ID_4 = 6792L;

    /**
     * Prep method for test suite.
     */
    @Before
    public void setup()
    {
        streamLoader.initialize();

        streamMapper = new GetStreamByOwnerId(EntityType.PERSON);
        streamMapper.setEntityManager(getEntityManager());

        sutCustomList = new RemoveCachedActivitiesFromList(
                activityIdsMapper, streamMapper, CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM);
        sutCustomList.setCache(testCache);

        sutFollowingList = new RemoveCachedActivitiesFromList(
                activityIdsMapper, streamMapper, CacheKeys.ACTIVITIES_BY_FOLLOWING);
        sutFollowingList.setCache(testCache);
    }

    /**
     * Test successful removal of activities from a list.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveActivitiesFromList()
    {
        addContentToListForTest(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + TEST_STREAM_SCOPE_ID, false);
        RemoveCachedActivitiesFromListRequest request =
            new RemoveCachedActivitiesFromListRequest(
                    TEST_STREAM_SCOPE_ID, TEST_STREAM_SCOPE_OWNER_ID, TEST_ACTIVITIES_OWNER_ID);
        sutCustomList.execute(request);

        List<Long> testList =
            getCache().getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + TEST_STREAM_SCOPE_ID);

        //These first two activities should be removed.
        assertFalse(testList.contains(TEST_ACTIVITY_ID_1));
        assertFalse(testList.contains(TEST_ACTIVITY_ID_2));
        assertTrue(testList.contains(TEST_ACTIVITY_ID_3));
        assertTrue(testList.contains(TEST_ACTIVITY_ID_4));
    }

    /**
     * Test successful removal of activities from the following list.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveActivitiesFromFollowingList()
    {
        addContentToListForTest(CacheKeys.ACTIVITIES_BY_FOLLOWING + TEST_STREAM_SCOPE_OWNER_ID, true);
        RemoveCachedActivitiesFromListRequest request =
            new RemoveCachedActivitiesFromListRequest(
                    TEST_STREAM_SCOPE_OWNER_ID, TEST_STREAM_SCOPE_OWNER_ID, TEST_ACTIVITIES_OWNER_ID);
        sutFollowingList.execute(request);

        List<Long> testList =
            getCache().getList(CacheKeys.ACTIVITIES_BY_FOLLOWING + TEST_STREAM_SCOPE_OWNER_ID);

        //These first two activities should be removed.
        assertFalse(testList.contains(TEST_ACTIVITY_ID_1));
        assertFalse(testList.contains(TEST_ACTIVITY_ID_2));
        assertTrue(testList.contains(TEST_ACTIVITY_ID_3));
        assertTrue(testList.contains(TEST_ACTIVITY_ID_4));
    }

    /**
     * Helper method for loading up a cached list for testing.
     * @param inCacheKey - key to use when building the test cache lists.
     * @param isFollowingList flag if this is for following list or not.
     */
    @SuppressWarnings("unchecked")
    private void addContentToListForTest(final String inCacheKey, final boolean isFollowingList)
    {
    	ArrayList<Long> values = new ArrayList<Long>();

        values.add(TEST_ACTIVITY_ID_3);
        values.add(TEST_ACTIVITY_ID_4);

        // 2 activities are already populated in following list by Streamloader "warming the cache".
        if (!isFollowingList)
        {
            // cache warming never took place, set the list to cache
            values.add(TEST_ACTIVITY_ID_1);
            values.add(TEST_ACTIVITY_ID_2);
            getCache().setList(inCacheKey, values);
        }
        else
        {
        	for (Long value : values)
        	{
	            // cache warming took place so we can append to cache
	            getCache().addToTopOfList(inCacheKey, value);
        	}
        }

        List<Long> testList =
            getCache().getList(inCacheKey);

        assertEquals(4, testList.size());
        assertTrue(testList.contains(TEST_ACTIVITY_ID_1));
        assertTrue(testList.contains(TEST_ACTIVITY_ID_2));
        assertTrue(testList.contains(TEST_ACTIVITY_ID_3));
        assertTrue(testList.contains(TEST_ACTIVITY_ID_4));
    }
}
