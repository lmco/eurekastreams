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
package org.eurekastreams.server.action.execution.profile;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.AddCachedPersonFollower;
import org.eurekastreams.server.testing.TestContextCreator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link SetFollowingPersonStatusExecution} class.
 *
 */
public class SetFollowingPersonStatusExecutionTest
{
    /**
     * System under test.
     */
    private SetFollowingPersonStatusExecution sut;

    /**
     * Setup the mocking context for this test class.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mock instance of PersonMapper.
     */
    private final PersonMapper personMapperMock = context.mock(PersonMapper.class);

    /**
     * Mapper to get id from account id.
     */
    private final DomainMapper<String, Long> getPersonIdByAccountIdMapper = context.mock(DomainMapper.class,
            "getPersonIdByAccountIdMapper");

    /**
     * Mock instance of AddCachedPersonFollower.
     */
    private final AddCachedPersonFollower addCachedMapperMock = context.mock(AddCachedPersonFollower.class,
            "addCachedMapperMock");

    /**
     * Mock instance of GetFollowerIds.
     */
    private final DomainMapper<Long, List<Long>> followerIdsMapperMock = context.mock(DomainMapper.class,
            "followerIdsMapperMock");

    /**
     * Mock instance of the principal object.
     */
    private final Principal principalMock = context.mock(Principal.class);

    /**
     * Method that prepares the sut.
     */
    @Before
    public void setUp()
    {
        sut = new SetFollowingPersonStatusExecution(personMapperMock, getPersonIdByAccountIdMapper,
                addCachedMapperMock, followerIdsMapperMock);
    }

    /**
     * Test to ensure that following a person works.
     *
     * @throws Exception
     *             - on error.
     */
    @Test
    public void testSetFollowing() throws Exception
    {
        final String followerAccountId = "ntAccount";
        final String followedAccountId = "followingntaccount";
        final Long followerId = 1L;
        final Long followedId = 2L;

        final List<Long> targetFollowerIds = new ArrayList<Long>(5);

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonIdByAccountIdMapper).execute(followerAccountId);
                will(returnValue(followerId));

                oneOf(getPersonIdByAccountIdMapper).execute(followedAccountId);
                will(returnValue(followedId));

                oneOf(personMapperMock).addFollower(1L, 2L);

                oneOf(addCachedMapperMock).execute(1L, 2L);

                oneOf(followerIdsMapperMock).execute(2L);
                will(returnValue(targetFollowerIds));
            }
        });

        SetFollowingStatusRequest currentRequest = new SetFollowingStatusRequest(followerAccountId, followedAccountId,
                EntityType.PERSON, false, Follower.FollowerStatus.FOLLOWING);
        sut.execute(TestContextCreator.createTaskHandlerContextWithPrincipal(currentRequest, principalMock));

        context.assertIsSatisfied();
    }

    /**
     * Test to ensure that the RemoveFollowing works.
     *
     * @throws Exception
     *             - on error.
     */
    @Test
    public void testRemoveFollowing() throws Exception
    {
        final String followerAccountId = "ntAccount";
        final String followedAccountId = "followingntaccount";
        final Long followerId = 1L;
        final Long followedId = 2L;

        final List<Long> targetFollowerIds = new ArrayList<Long>(5);

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonIdByAccountIdMapper).execute(followerAccountId);
                will(returnValue(followerId));

                oneOf(getPersonIdByAccountIdMapper).execute(followedAccountId);
                will(returnValue(followedId));

                oneOf(personMapperMock).removeFollower(1L, 2L);

                oneOf(followerIdsMapperMock).execute(2L);
                will(returnValue(targetFollowerIds));
            }
        });

        SetFollowingStatusRequest currentRequest = new SetFollowingStatusRequest(followerAccountId, followedAccountId,
                EntityType.PERSON, false, Follower.FollowerStatus.NOTFOLLOWING);
        sut.execute(TestContextCreator.createTaskHandlerContextWithPrincipal(currentRequest, principalMock));

        context.assertIsSatisfied();
    }

    /**
     * Test an exception.
     *
     * @throws Exception
     *             expected.
     */
    @Test(expected = Exception.class)
    public void testSetFollowingError() throws Exception
    {
        final String followerAccountId = "ntAccount";
        final String followedAccountId = "followingntaccount";

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonIdByAccountIdMapper).execute(followedAccountId);
                will(throwException(new RuntimeException("NO.")));
            }
        });

        SetFollowingStatusRequest currentRequest = new SetFollowingStatusRequest(followerAccountId, followedAccountId,
                EntityType.PERSON, false, Follower.FollowerStatus.FOLLOWING);
        sut.execute(TestContextCreator.createTaskHandlerContextWithPrincipal(currentRequest, principalMock));

        context.assertIsSatisfied();
    }

    /**
     * Test an unexpected following status.
     *
     * @throws Exception
     *             - on error.
     */
    @Test
    public void testOtherFollowing() throws Exception
    {
        final String followerAccountId = "ntAccount";
        final String followedAccountId = "followingntaccount";
        final Long followerId = 1L;
        final Long followedId = 2L;

        final List<Long> targetFollowerIds = new ArrayList<Long>(5);

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonIdByAccountIdMapper).execute(followerAccountId);
                will(returnValue(followerId));

                oneOf(getPersonIdByAccountIdMapper).execute(followedAccountId);
                will(returnValue(followedId));

                oneOf(followerIdsMapperMock).execute(2L);
                will(returnValue(targetFollowerIds));
            }
        });

        SetFollowingStatusRequest currentRequest = new SetFollowingStatusRequest(followerAccountId, followedAccountId,
                EntityType.PERSON, false, Follower.FollowerStatus.NOTSPECIFIED);
        sut.execute(TestContextCreator.createTaskHandlerContextWithPrincipal(currentRequest, principalMock));

        context.assertIsSatisfied();
    }
}
