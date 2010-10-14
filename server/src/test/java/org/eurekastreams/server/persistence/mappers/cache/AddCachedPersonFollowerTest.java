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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eurekastreams.server.persistence.mappers.GetRelatedOrganizationIdsByPersonId;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.cache.testhelpers.SimpleMemoryCache;
import org.eurekastreams.server.persistence.mappers.db.GetFollowedPersonIdsForPersonByIdDbMapper;
import org.eurekastreams.server.persistence.mappers.db.GetFollowerPersonIdsForPersonByIdDbMapper;
import org.eurekastreams.server.persistence.strategies.PersonQueryStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This class is responsible for testing the AddCachedPersonFollower class.
 * 
 */
public class AddCachedPersonFollowerTest extends MapperTest
{
    /**
     * Cache fed into the loader.
     */
    private Cache cache;

    /**
     * Test instance of mapper to retrieve the user ids that a person is following.
     */
    private GetFollowedPersonIdsForPersonByIdDbMapper followedPersonIdsMapper;

    /**
     * Test instance of mapper to retrieve the user ids that are following a person.
     */
    private GetFollowerPersonIdsForPersonByIdDbMapper followerIdsMapper;

    /**
     * System under test.
     */
    private AddCachedPersonFollower sut;

    /**
     * Mapper to load the person cache and subsequently the follower lists.
     */
    private PersonCacheLoader personCacheLoader;

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
     * Mapper to get related org ids for people.
     */
    private GetRelatedOrganizationIdsByPersonId
    // line break
    getRelatedOrganizationIdsByPersonIdMapper = new GetRelatedOrganizationIdsByPersonId();

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        cache = new SimpleMemoryCache();

        getRelatedOrganizationIdsByPersonIdMapper.setEntityManager(getEntityManager());

        // Setup the cache loader to get dataset data into the cache.
        personCacheLoader = new PersonCacheLoader(new PersonQueryStrategy(), getRelatedOrganizationIdsByPersonIdMapper);
        personCacheLoader.setCache(cache);
        personCacheLoader.setEntityManager(getEntityManager());
        personCacheLoader.initialize();

        followedPersonIdsMapper = new GetFollowedPersonIdsForPersonByIdDbMapper();
        followedPersonIdsMapper.setEntityManager(getEntityManager());

        followerIdsMapper = new GetFollowerPersonIdsForPersonByIdDbMapper();
        followerIdsMapper.setEntityManager(getEntityManager());

        sut = new AddCachedPersonFollower(followedPersonIdsMapper, followerIdsMapper);
        sut.setCache(cache);
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Teardown method.
     */
    @After
    public void tearDown()
    {
        getEntityManager().clear();
    }

    /**
     * Method to test execute.
     */
    @Test
    public void testExecute()
    {
        // Retrieve the list of users following smithers.
        List<Long> personFollowerIds = cache.getList(CacheKeys.FOLLOWERS_BY_PERSON + TEST_PERSON_ID_1);
        assertEquals(3, personFollowerIds.size());

        assertTrue(personFollowerIds.contains(TEST_PERSON_ID_2));
        assertTrue(personFollowerIds.contains(TEST_PERSON_ID_3));

        // Retrieve the list of users fordp is following.
        List<Long> peopleFollowedIds = cache.getList(CacheKeys.PEOPLE_FOLLOWED_BY_PERSON + TEST_PERSON_ID_4);

        // always following himself
        assertEquals(1, peopleFollowedIds.size());

        sut.execute(TEST_PERSON_ID_4, TEST_PERSON_ID_1);

        // Retrieve the list of users following smithers, should have one more now.
        List<Long> updatedPersonFollowerIds = cache.getList(CacheKeys.FOLLOWERS_BY_PERSON + TEST_PERSON_ID_1);
        assertEquals(4, updatedPersonFollowerIds.size());

        assertTrue(updatedPersonFollowerIds.contains(TEST_PERSON_ID_2));
        assertTrue(updatedPersonFollowerIds.contains(TEST_PERSON_ID_3));
        assertTrue(updatedPersonFollowerIds.contains(TEST_PERSON_ID_4));

        // Retrieve the list of users fordp is following should be 2 now.
        List<Long> updatedPeopleFollowedIds = cache.getList(CacheKeys.PEOPLE_FOLLOWED_BY_PERSON + TEST_PERSON_ID_4);

        assertEquals(2, updatedPeopleFollowedIds.size());

        assertTrue(updatedPeopleFollowedIds.contains(TEST_PERSON_ID_1));
    }

    /**
     * Method to test execute.
     */
    @Test
    public void testExecuteFollowingAPersonAlreadyFollowed()
    {
        // Retrieve the list of users following smithers.
        List<Long> personFollowerIds = cache.getList(CacheKeys.FOLLOWERS_BY_PERSON + TEST_PERSON_ID_1);
        assertEquals(3, personFollowerIds.size());

        assertTrue(personFollowerIds.contains(TEST_PERSON_ID_2));
        assertTrue(personFollowerIds.contains(TEST_PERSON_ID_3));

        // Retrieve the list of users mrburns is following.
        List<Long> peopleFollowedIds = cache.getList(CacheKeys.PEOPLE_FOLLOWED_BY_PERSON + TEST_PERSON_ID_2);

        assertEquals(2, peopleFollowedIds.size());

        assertTrue(peopleFollowedIds.contains(TEST_PERSON_ID_1));

        sut.execute(TEST_PERSON_ID_2, TEST_PERSON_ID_1);

        // Retrieve the list of users following smithers, should be the same.
        List<Long> updatedPersonFollowerIds = cache.getList(CacheKeys.FOLLOWERS_BY_PERSON + TEST_PERSON_ID_1);
        assertEquals(3, updatedPersonFollowerIds.size());

        assertTrue(updatedPersonFollowerIds.contains(TEST_PERSON_ID_2));
        assertTrue(updatedPersonFollowerIds.contains(TEST_PERSON_ID_3));

        // Retrieve the list of users mrburns is following should still be 2.
        List<Long> updatedPeopleFollowedIds = cache.getList(CacheKeys.PEOPLE_FOLLOWED_BY_PERSON + TEST_PERSON_ID_2);

        assertEquals(2, updatedPeopleFollowedIds.size());

        assertTrue(updatedPeopleFollowedIds.contains(TEST_PERSON_ID_1));
    }
}
