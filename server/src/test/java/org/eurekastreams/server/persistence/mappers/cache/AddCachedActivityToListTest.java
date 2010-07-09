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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.List;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.AddCachedActivityToListRequest;
import org.eurekastreams.server.persistence.mappers.stream.CompositeStreamActivityIdsMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is responsible for testing the AddCachedActivityToList Sync block.
 *
 */
public class AddCachedActivityToListTest extends MapperTest
{
    /**
     * Test instance.
     */
    @Autowired
    private CompositeStreamActivityIdsMapper streamActivityIdsMapper;

    /**
     * Test instance.
     */
    @Autowired
    private StreamCacheLoader streamLoader;

    /**
     * System under test, allow this to be autowired.
     */
    @Autowired
    private AddCachedActivityToList sut;

    /**
     * Specific setup steps to prep for the tests.
     */
    @Before
    public void setup()
    {
        // This will prepopulate the streams with what is in the test db dataset.
        streamLoader.initialize();
    }

    /**
     * Test the successful execution of the action. - Add a single person's activity to an already empty list.
     */
    @Test
    public void testExecute()
    {
        final long listId = 187L;
        final long listOwnerId = 99L; // mrburns
        final long activityOwnerId = 98L; // smithers
        final long testActivityId1 = 6789L;
        final long testActivityId2 = 6790L;

        AddCachedActivityToListRequest request =
                new AddCachedActivityToListRequest(listId, listOwnerId, activityOwnerId);

        List<Long> preUpdateStream = streamActivityIdsMapper.execute(listId, listOwnerId);
        assertEquals(0, preUpdateStream.size());

        sut.execute(request);

        List<Long> postUpdateStream = streamActivityIdsMapper.execute(listId, listOwnerId);
        assertEquals(1, postUpdateStream.size());
        // Assert that only the newest activity was added to the list.
        assertFalse(postUpdateStream.contains(testActivityId1));
        assertTrue(postUpdateStream.contains(testActivityId2));
    }

    /**
     * Test the successful execution of the action. - Add a single person's activity to a list with an activity in it
     * already.
     */
    @Test
    public void testExecuteAnotherUser()
    {
        final long listId = 187L;
        final long listOwnerId = 99L; // mrburns
        final long activityOwnerId = 42L; // fordp
        final long testActivityId2 = 6790L; // smithers owner - newest
        final long testActivityId3 = 6792L; // fordp2 actor posted to fordp's stream

        AddCachedActivityToListRequest request =
                new AddCachedActivityToListRequest(listId, listOwnerId, activityOwnerId);

        List<Long> preUpdateStream = streamActivityIdsMapper.execute(listId, listOwnerId);
        assertEquals(1, preUpdateStream.size());
        assertTrue(preUpdateStream.contains(testActivityId2));

        sut.execute(request);

        List<Long> postUpdateStream = streamActivityIdsMapper.execute(listId, listOwnerId);
        assertEquals(2, postUpdateStream.size());
        // Assert that the newest activity was added to the list.
        assertTrue(postUpdateStream.contains(testActivityId2));
        assertTrue(postUpdateStream.contains(testActivityId3));

        // Ensure that the activities are in the right order.
        assertEquals(testActivityId3, postUpdateStream.get(0).longValue());
        assertEquals(testActivityId2, postUpdateStream.get(1).longValue());
    }

    /**
     * Test the successful execution of the action. - Add a single person's activity to an already empty list.
     */
    @Test
    public void testExecuteAThirdUserEnsureOrdering()
    {
        final long listId = 187L;
        final long listOwnerId = 99L; // mrburns
        final long activityOwnerId = 142L; // fordp2
        final long testActivityId2 = 6790L; // smithers owner - newest
        final long testActivityId3 = 6792L; // fordp2 actor posted to fordp's stream
        final long testActivityId4 = 6791L; // fordp actor posted to fordp2's stream

        AddCachedActivityToListRequest request =
                new AddCachedActivityToListRequest(listId, listOwnerId, activityOwnerId);

        List<Long> preUpdateStream = streamActivityIdsMapper.execute(listId, listOwnerId);
        assertEquals(2, preUpdateStream.size());
        assertTrue(preUpdateStream.contains(testActivityId2));
        assertTrue(preUpdateStream.contains(testActivityId3));

        // Ensure that the activities are in the right order.
        assertEquals(testActivityId3, preUpdateStream.get(0).longValue());
        assertEquals(testActivityId2, preUpdateStream.get(1).longValue());

        sut.execute(request);

        List<Long> postUpdateStream = streamActivityIdsMapper.execute(listId, listOwnerId);
        assertEquals(3, postUpdateStream.size());
        // Assert that the newest activity was added to the list.
        assertTrue(postUpdateStream.contains(testActivityId2));
        assertTrue(postUpdateStream.contains(testActivityId3));
        assertTrue(postUpdateStream.contains(testActivityId4));

        // Ensure that the activities are in the right order.
        assertEquals(testActivityId3, postUpdateStream.get(0).longValue());
        assertEquals(testActivityId4, postUpdateStream.get(1).longValue());
        assertEquals(testActivityId2, postUpdateStream.get(2).longValue());
    }
}
