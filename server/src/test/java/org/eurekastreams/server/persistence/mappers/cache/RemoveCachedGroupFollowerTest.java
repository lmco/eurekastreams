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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.cache.testhelpers.SimpleMemoryCache;
import org.eurekastreams.server.persistence.strategies.DomainGroupQueryStrategy;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * This class is responsible for testing the RemoveCachedGroupFollower class.
 *
 */
public class RemoveCachedGroupFollowerTest extends MapperTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Cache fed into the loader.
     */
    private Cache cache;

    /**
     * System under test.
     */
    private RemoveCachedGroupFollower mapper;

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
     * Mapper to remove domain group from cache.
     */
    private RemoveDomainGroupFromCacheMapper removeDomainGroupFromCacheMapper;

    /**
     * Setup method to prepare the test suite.
     */
    @Before
    public void setup()
    {
        cache = new SimpleMemoryCache();

        removeDomainGroupFromCacheMapper = context.mock(RemoveDomainGroupFromCacheMapper.class);
        domainGroupCacheLoader = new DomainGroupCacheLoader(new DomainGroupQueryStrategy(),
                removeDomainGroupFromCacheMapper);
        domainGroupCacheLoader.setCache(cache);
        domainGroupCacheLoader.setEntityManager(getEntityManager());
        domainGroupCacheLoader.initialize();

        mapper = new RemoveCachedGroupFollower();
        mapper.setCache(cache);
        mapper.setEntityManager(getEntityManager());

    }

    /**
     * Test removing group followers.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute()
    {
        // Retrieve the list of users following group1.
        List<Long> groupFollowerIds = cache.getList(CacheKeys.FOLLOWERS_BY_GROUP + TEST_GROUP_ID_1);

        assertEquals(3, groupFollowerIds.size());
        // Assert that smithers, mrburns, and fordp are following group 1.
        assertTrue(groupFollowerIds.contains(TEST_PERSON_ID_1));
        assertTrue(groupFollowerIds.contains(TEST_PERSON_ID_2));
        assertTrue(groupFollowerIds.contains(TEST_PERSON_ID_4));

        // Retrieve the list of groups that fordp2 is following. This should be none.
        List<Long> groupIdsFollowing = cache.getList(CacheKeys.GROUPS_FOLLOWED_BY_PERSON + TEST_PERSON_ID_1);

        assertEquals(1, groupIdsFollowing.size());
        // Assert that fordp2 is now following group1.
        assertTrue(groupIdsFollowing.contains(TEST_GROUP_ID_1));

        // Remove smithers as a follower to group 1.
        mapper.execute(TEST_PERSON_ID_1, TEST_GROUP_ID_1);

        // Test that smithers was removed from the followers of the group.
        List<Long> updatedGroupFollowerIds = cache.getList(CacheKeys.FOLLOWERS_BY_GROUP + TEST_GROUP_ID_1);

        assertEquals(2, updatedGroupFollowerIds.size());
        // Assert that mrburns and fordp are the only people following group 1.
        assertTrue(groupFollowerIds.contains(TEST_PERSON_ID_2));
        assertTrue(groupFollowerIds.contains(TEST_PERSON_ID_4));

        List<Long> updatedGroupIdsFollowing = cache.getList(CacheKeys.GROUPS_FOLLOWED_BY_PERSON + TEST_PERSON_ID_1);

        assertEquals(0, updatedGroupIdsFollowing.size());
    }

    /**
     * Test removing group followers.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteRemovingFollowerNotInList()
    {
        // Retrieve the list of users following group1.
        List<Long> groupFollowerIds = cache.getList(CacheKeys.FOLLOWERS_BY_GROUP + TEST_GROUP_ID_1);

        assertEquals(3, groupFollowerIds.size());
        // Assert that fordp2 is not following group 1.
        assertFalse(groupFollowerIds.contains(TEST_PERSON_ID_3));

        // Retrieve the list of groups that fordp2 is following. This should be none.
        List<Long> groupIdsFollowing = cache.getList(CacheKeys.GROUPS_FOLLOWED_BY_PERSON + TEST_PERSON_ID_3);

        assertNull(groupIdsFollowing);

        // Remove fordp2 as a follower to group 1.
        mapper.execute(TEST_PERSON_ID_3, TEST_GROUP_ID_1);

        // Test that fordp2 is still not amoung the followers of the group.
        List<Long> updatedGroupFollowerIds = cache.getList(CacheKeys.FOLLOWERS_BY_GROUP + TEST_GROUP_ID_1);

        assertEquals(3, updatedGroupFollowerIds.size());
        // Assert that fordp2 is still not a follower of group 1.
        assertFalse(groupFollowerIds.contains(TEST_PERSON_ID_3));

        List<Long> updatedGroupIdsFollowing = cache.getList(CacheKeys.GROUPS_FOLLOWED_BY_PERSON + TEST_PERSON_ID_3);

        assertNull(updatedGroupIdsFollowing);
    }
}
