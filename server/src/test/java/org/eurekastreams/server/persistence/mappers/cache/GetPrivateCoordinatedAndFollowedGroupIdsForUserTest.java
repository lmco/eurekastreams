/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetPrivateCoordinatedAndFollowedGroupIdsForUser.
 */
public class GetPrivateCoordinatedAndFollowedGroupIdsForUserTest extends CachedMapperTest
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
     * Mapper to get all private group ids that a user can view with org or group coordinator access.
     */
    private final DomainMapper<Long, Set<Long>> getPrivateGroupIdsMapper = context.mock(DomainMapper.class,
            "getPrivateGroupIdsMapper");

    /**
     * Mapper to get the group ids followed by a person.
     */
    private final DomainMapper<Long, List<Long>> getFollowedGroupIdsMapper = context.mock(DomainMapper.class,
            "getFollowedGroupIdsMapper");

    /**
     * Mapper to get the group ids.
     */
    private final GetPrivateCoordinatedAndFollowedGroupIdsForUser sut = // \n
    new GetPrivateCoordinatedAndFollowedGroupIdsForUser(getPrivateGroupIdsMapper, getFollowedGroupIdsMapper);

    /**
     * Person id for testing.
     */
    final Long personId = 2382L;

    /**
     * Group id for testing.
     */
    final Long groupId1 = 11111L;

    /**
     * Group id for testing.
     */
    final Long groupId2 = 22222L;

    /**
     * Cache key for followed group ids of the test person.
     */
    private final String keyFollowed = CacheKeys.GROUPS_FOLLOWED_BY_PERSON + personId;

    /**
     * Cache key for private group ids of the test person.
     */
    private final String keyPrivate = CacheKeys.PRIVATE_GROUP_IDS_VIEWABLE_BY_PERSON_AS_COORDINATOR + personId;

    /**
     * ArrayList of followed group ids - either to be put in cache or returned by mocked mapper.
     */
    private final ArrayList<Long> followedGroupIds = new ArrayList<Long>();

    /**
     * Set of private group ids - either to be put in cache or returned by mocked mapper.
     */
    private final Set<Long> privateGroupIds = new HashSet<Long>();

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut.setCache(getCache());
        followedGroupIds.add(groupId1);
        privateGroupIds.add(groupId2);
    }

    /**
     * Test executing when both lists are found in cache.
     */
    @Test
    public void testExecuteWhenBothListsInCache()
    {
        // set both lists in cache
        getCache().setList(keyFollowed, followedGroupIds);
        getCache().set(keyPrivate, privateGroupIds);

        // no assertions necessary - the mocked mappers shouldn't be touched

        // perform SUT
        Set<Long> results = sut.execute(personId);

        // verify
        assertResultsContainGroups(results);

        context.assertIsSatisfied();
    }

    /**
     * Test executing when just the followed list is in cache.
     */
    @Test
    public void testExecuteWhenFollowedInCache()
    {
        // stick the followed list in cache
        getCache().setList(keyFollowed, followedGroupIds);

        // assert that the sut will ask for the private groups
        context.checking(new Expectations()
        {
            {
                oneOf(getPrivateGroupIdsMapper).execute(personId);
                will(returnValue(privateGroupIds));
            }
        });

        // perform SUT
        Set<Long> results = sut.execute(personId);

        // verify
        assertResultsContainGroups(results);
        context.assertIsSatisfied();
    }

    /**
     * Test executing when just the private list is in cache.
     */
    @Test
    public void testExecuteWhenPrivateListInCache()
    {
        // stick the private list in cache
        getCache().set(keyPrivate, privateGroupIds);

        // assert that the sut will ask for the private groups
        context.checking(new Expectations()
        {
            {
                oneOf(getFollowedGroupIdsMapper).execute(personId);
                will(returnValue(followedGroupIds));
            }
        });

        // perform SUT
        Set<Long> results = sut.execute(personId);

        // verify
        assertResultsContainGroups(results);
        context.assertIsSatisfied();
    }

    /**
     * Test executing when just the neither list is in cache.
     */
    @Test
    public void testExecuteWhenNeitherListInCache()
    {
        // assert that the sut will ask for the private groups
        context.checking(new Expectations()
        {
            {
                oneOf(getFollowedGroupIdsMapper).execute(personId);
                will(returnValue(followedGroupIds));

                oneOf(getPrivateGroupIdsMapper).execute(personId);
                will(returnValue(privateGroupIds));
            }
        });

        // perform SUT
        Set<Long> results = sut.execute(personId);

        // verify
        assertResultsContainGroups(results);
        context.assertIsSatisfied();
    }

    /**
     * Common assertion that groupId1 and groupId2 are in inResults.
     *
     * @param inResults
     *            results to check
     */
    private void assertResultsContainGroups(final Set<Long> inResults)
    {
        assertEquals(2, inResults.size());
        assertTrue(inResults.contains(groupId1));
        assertTrue(inResults.contains(groupId2));
    }
}
