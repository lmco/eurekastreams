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
package org.eurekastreams.server.persistence.mappers.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests GetListsContainingActivities class.
 */
public class GetListsContainingActivitiesTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    GetListsContainingActivities sut;

    /**
     * Test activity id.
     */
    private static final Long TEST_ACTIVITY_ID_1 = 6789L;

    /**
     * Test activity id.
     */
    private static final Long TEST_ACTIVITY_ID_2 = 6791L;

    /**
     * Tests execute method.
     */
    @Test
    public void testExecute()
    {
        final int keySize = 11;
        final int author1 = 98;
        final int author2 = 42;
        //Follower id of author 1
        final int follower1 = 99;
        //Follower id of author 2
        final int follower2 = 142;

        List<Long> activityIds = new ArrayList<Long>();
        activityIds.add(TEST_ACTIVITY_ID_1);
        activityIds.add(TEST_ACTIVITY_ID_2);

        List<String> keys = sut.execute(activityIds);
        assertEquals(keySize, keys.size());

        // everyone stream
        //TODO: assertTrue(keys.contains(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + everyone));

        // followers of the authors of the two activities
        assertTrue(keys.contains(CacheKeys.ACTIVITIES_BY_FOLLOWING + follower1));
        assertTrue(keys.contains(CacheKeys.ACTIVITIES_BY_FOLLOWING + follower2));

        // authors of the two activities following themselves.
        assertTrue(keys.contains(CacheKeys.ACTIVITIES_BY_FOLLOWING + author1));
        assertTrue(keys.contains(CacheKeys.ACTIVITIES_BY_FOLLOWING + author2));
    }
}
