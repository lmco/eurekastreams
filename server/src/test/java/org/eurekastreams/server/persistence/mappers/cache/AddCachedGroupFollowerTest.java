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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.cache.testhelpers.SimpleMemoryCache;
import org.eurekastreams.server.persistence.mappers.stream.GetFollowedGroupIds;
import org.eurekastreams.server.persistence.mappers.stream.GetGroupFollowerIds;
import org.eurekastreams.server.persistence.strategies.DomainGroupQueryStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Class responsible for testing the AddCachedGroupFollower class.
 */
public class AddCachedGroupFollowerTest extends MapperTest
{
    /**
     * Cache fed into the loader.
     */
    private Cache cache;

    /**
     * Local instance of GetFollowedGroupids for test.
     */
    private GetFollowedGroupIds followedGroupIdsMapper;

    /**
     * Local instance of GetGroupFollowerIds for test.
     */
    private GetGroupFollowerIds followerIdsMapper;

    /**
     * System under test.
     */
    private AddCachedGroupFollower mapper;

    /**
     * Cache loader for person.
     */
    private DomainGroupCacheLoader domainGroupCacheLoader;

    /**
     * Test person id from data set.
     */
    private static final Long TEST_PERSON_ID_1 = new Long(98L);

    /**
     * Test person id from data set.
     */
    private static final Long TEST_PERSON_ID_2 = new Long(99L);

    /**
     * Test person id from data set.
     */
    private static final Long TEST_PERSON_ID_3 = new Long(142L);

    /**
     * Test person id from data set.
     */
    private static final Long TEST_PERSON_ID_4 = new Long(42L);

    /**
     * Test person id from data set.
     */
    private static final Long TEST_GROUP_ID_1 = new Long(1L);

    /**
     * Setup method to prepare the test suite.
     */
    @Before
    public void setup()
    {
        cache = new SimpleMemoryCache();

        domainGroupCacheLoader = new DomainGroupCacheLoader(new DomainGroupQueryStrategy());
        domainGroupCacheLoader.setCache(cache);
        domainGroupCacheLoader.setEntityManager(getEntityManager());
        domainGroupCacheLoader.initialize();

        followedGroupIdsMapper = new GetFollowedGroupIds();
        followedGroupIdsMapper.setCache(cache);
        followedGroupIdsMapper.setEntityManager(getEntityManager());

        followerIdsMapper = new GetGroupFollowerIds();
        followerIdsMapper.setCache(cache);
        followerIdsMapper.setEntityManager(getEntityManager());

        mapper = new AddCachedGroupFollower(followedGroupIdsMapper, followerIdsMapper);
        mapper.setCache(cache);
        mapper.setEntityManager(getEntityManager());

    }

    /**
     * Tear-down method.
     */
    @After
    public void tearDown()
    {
        getEntityManager().clear();
    }

    /**
     * Test adding group followers.
     */
    @Test
    public void testExecute()
    {
        // Retrieve the list of users following group1.
        List<Long> groupFollowerIds = cache.getList(CacheKeys.FOLLOWERS_BY_GROUP + TEST_GROUP_ID_1);

        assertEquals(3, groupFollowerIds.size());
        // Assert that smithers, mrburns, and fordp are all following group 1.
        assertTrue(groupFollowerIds.contains(TEST_PERSON_ID_1));
        assertTrue(groupFollowerIds.contains(TEST_PERSON_ID_2));
        assertTrue(groupFollowerIds.contains(TEST_PERSON_ID_4));

        // Retrieve the list of groups that fordp2 is following. This should be none.
        List<Long> groupIdsFollowing = cache.getList(CacheKeys.GROUPS_FOLLOWED_BY_PERSON + TEST_PERSON_ID_3);

        assertNull(groupIdsFollowing);

        // Add fordp2 as a follower to group 1.
        mapper.execute(TEST_PERSON_ID_3, TEST_GROUP_ID_1);

        // Test that fordp2 was added to the followers of the group.
        List<Long> updatedGroupFollowerIds = cache.getList(CacheKeys.FOLLOWERS_BY_GROUP + TEST_GROUP_ID_1);

        assertEquals(4, updatedGroupFollowerIds.size());
        // Assert that smithers, mrburns, fordp, and fordp2 are all following group 1.
        assertTrue(updatedGroupFollowerIds.contains(TEST_PERSON_ID_1));
        assertTrue(updatedGroupFollowerIds.contains(TEST_PERSON_ID_2));
        assertTrue(updatedGroupFollowerIds.contains(TEST_PERSON_ID_3));
        assertTrue(groupFollowerIds.contains(TEST_PERSON_ID_4));

        List<Long> updatedGroupIdsFollowing = cache.getList(CacheKeys.GROUPS_FOLLOWED_BY_PERSON + TEST_PERSON_ID_3);

        assertEquals(1, updatedGroupIdsFollowing.size());
        // Assert that fordp2 is now following group1.
        assertTrue(updatedGroupIdsFollowing.contains(TEST_GROUP_ID_1));
    }

    /**
     * Test adding group followers.
     */
    @Test
    public void testExecuteAddFollowerWhoIsAlreadyAdded()
    {
        // Retrieve the list of users following group1.
        List<Long> groupFollowerIds = cache.getList(CacheKeys.FOLLOWERS_BY_GROUP + TEST_GROUP_ID_1);

        assertEquals(3, groupFollowerIds.size());
        // Assert that smithers, mrburns, and fordp are all following group 1.
        assertTrue(groupFollowerIds.contains(TEST_PERSON_ID_1));
        assertTrue(groupFollowerIds.contains(TEST_PERSON_ID_2));
        assertTrue(groupFollowerIds.contains(TEST_PERSON_ID_4));

        List<Long> groupIdsFollowing = cache.getList(CacheKeys.GROUPS_FOLLOWED_BY_PERSON + TEST_PERSON_ID_1);

        assertEquals(1, groupIdsFollowing.size());
        // Assert that smithers is following group1.
        assertTrue(groupIdsFollowing.contains(TEST_GROUP_ID_1));

        // Add smithers as a follower to group 1.
        mapper.execute(TEST_PERSON_ID_1, TEST_GROUP_ID_1);

        // Test that the followers of the group are unchanged.
        List<Long> updatedGroupFollowerIds = cache.getList(CacheKeys.FOLLOWERS_BY_GROUP + TEST_GROUP_ID_1);

        assertEquals(3, updatedGroupFollowerIds.size());
        // Assert that smithers, mrburns, and fordp are all following group 1.
        assertTrue(groupFollowerIds.contains(TEST_PERSON_ID_1));
        assertTrue(groupFollowerIds.contains(TEST_PERSON_ID_2));
        assertTrue(groupFollowerIds.contains(TEST_PERSON_ID_4));

        List<Long> updatedGroupIdsFollowing = cache.getList(CacheKeys.GROUPS_FOLLOWED_BY_PERSON + TEST_PERSON_ID_1);

        assertEquals(1, updatedGroupIdsFollowing.size());
        // Assert that smithers is still following group1.
        assertTrue(updatedGroupIdsFollowing.contains(TEST_GROUP_ID_1));
    }
}
