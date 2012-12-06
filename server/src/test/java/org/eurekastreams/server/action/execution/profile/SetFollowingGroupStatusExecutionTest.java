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

import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;
import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.action.ActionTestHelper;
import org.eurekastreams.server.action.execution.stream.PostActivityExecutionStrategy;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.profile.RequestForGroupMembershipRequest;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusByGroupCreatorRequest;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.AddCachedGroupFollower;
import org.eurekastreams.server.persistence.mappers.db.DeleteRequestForGroupMembership;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.testing.TestContextCreator;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Test class for the {@link SetFollowingGroupStatusExecution} class.
 * 
 */
public class SetFollowingGroupStatusExecutionTest
{
    /** Test data. */
    private static final long FOLLOWER_ID = 1L;

    /** Test data. */
    private static final String FOLLOWER_ACCOUNT = "jdoe";

    /** Test data. */
    private static final long ACTOR_ID = 1010L;

    /** Test data. */
    private static final String ACTOR_ACCOUNT = "smith";

    /** Test data. */
    private static final String GROUP_UNIQUEID = "thegroup";

    /** Test data. */
    private static final long GROUP_ID = 1000L;

    /** Test data. */
    private static final String GROUP_NAME = "The Group";

    /**
     * System under test.
     */
    private SetFollowingGroupStatusExecution sut;

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
     * Mock instance of GetDomainGroupsByShortNames.
     */
    private final GetDomainGroupsByShortNames groupByShortNameMapperMock = context
            .mock(GetDomainGroupsByShortNames.class);

    /** Mapper to get person by id (for getting account id). */
    private final DomainMapper<Long, PersonModelView> getPersonByIdMapper = context.mock(DomainMapper.class,
            "getPersonByIdMapper");

    /**
     * Mapper to get the person id from an account id.
     */
    private final DomainMapper<String, Long> getPersonIdFromAccountIdMapper = context.mock(DomainMapper.class,
            "getPersonIdFromAccountIdMapper");

    /**
     * Mock instance of DomainGroupMapper.
     */
    private final DomainGroupMapper groupMapperMock = context.mock(DomainGroupMapper.class);

    /**
     * Mock instance of AddCachedGroupFollower.
     */
    private final AddCachedGroupFollower addCachedGroupFollowerMapperMock = context.mock(AddCachedGroupFollower.class);

    /**
     * Mock instance of GetGroupFollowerIds.
     */
    private final DomainMapper<Long, List<Long>> groupFollowerIdsMapperMock = context.mock(DomainMapper.class,
            "groupFollowerIdsMapperMock");

    /**
     * Mocked principal object.
     */
    private final Principal principalMock = context.mock(Principal.class);

    /** Mapper to remove group access requests. */
    private final DeleteRequestForGroupMembership deleteRequestForGroupMembershipMapper = context
            .mock(DeleteRequestForGroupMembership.class);

    /**
     * Delete cache key mapper.
     */
    private final DomainMapper<Set<String>, Boolean> deleteCacheKeyMapper = context.mock(DomainMapper.class,
            "deleteCacheKeyMapper");

    /**
     * Post an activity.
     */
    private final PostActivityExecutionStrategy postActivity = context.mock(PostActivityExecutionStrategy.class);

    /** Fixture: group. */
    private final DomainGroupModelView group = context.mock(DomainGroupModelView.class, "group");

    /** Fixture: follower ids (a mock to insure SUT doesn't alter it). */
    private final List<Long> followerIds = context.mock(List.class, "followerIds");

    /** Fixture: principal for user requesting the action. */
    private Principal principal;

    /**
     * Method to setup the System Under Test.
     */
    @Before
    public void setUp()
    {
        principal = TestContextCreator.createPrincipal(ACTOR_ACCOUNT, ACTOR_ID);

        sut = new SetFollowingGroupStatusExecution(groupByShortNameMapperMock, getPersonByIdMapper,
                getPersonIdFromAccountIdMapper, groupMapperMock, addCachedGroupFollowerMapperMock,
                groupFollowerIdsMapperMock, deleteRequestForGroupMembershipMapper, postActivity, deleteCacheKeyMapper);

        context.checking(new Expectations()
        {
            {
                allowing(group).getName();
                will(returnValue(GROUP_NAME));
                allowing(group).getEntityId();
                will(returnValue(GROUP_ID));
                allowing(followerIds).size();
                will(returnValue(5));
            }
        });
    }

    /**
     * Test the successful SetFollowing method when a group is followed.
     * 
     * @throws Exception
     *             - on error.
     */
    @Test
    public void testSetFollowing() throws Exception
    {
        final RequestForGroupMembershipRequest delRequest = new RequestForGroupMembershipRequest(GROUP_ID, 1L);

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonIdFromAccountIdMapper).execute(with(any(String.class)));
                will(returnValue(FOLLOWER_ID));

                oneOf(groupByShortNameMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(group));

                oneOf(groupMapperMock).addFollower(FOLLOWER_ID, GROUP_ID);

                oneOf(addCachedGroupFollowerMapperMock).execute(1L, GROUP_ID);

                oneOf(deleteRequestForGroupMembershipMapper).execute(with(equalInternally(delRequest)));

                oneOf(deleteCacheKeyMapper).execute(Collections.singleton("Per:1"));

                oneOf(postActivity).execute(with(any(TaskHandlerActionContext.class)));

                oneOf(groupFollowerIdsMapperMock).execute(GROUP_ID);
                will(returnValue(followerIds));
            }
        });

        SetFollowingStatusRequest currentRequest = new SetFollowingStatusRequest("ntaccount", "groupshortname",
                EntityType.GROUP, false, Follower.FollowerStatus.FOLLOWING);
        TaskHandlerActionContext<PrincipalActionContext> actionContext = TestContextCreator
                .createTaskHandlerContextWithPrincipal(currentRequest, principal);
        Serializable result = sut.execute(actionContext);

        context.assertIsSatisfied();
        assertEquals(5, result);
        assertEquals(2, actionContext.getUserActionRequests().size());
    }

    /**
     * Test the successful SetFollowing method when a group is followed.
     * 
     * @throws Exception
     *             - on error.
     */
    @Test
    public void testSetFollowingWithDifferentRequestObject() throws Exception
    {
        final PersonModelView testFollower = new PersonModelView();
        testFollower.setEntityId(FOLLOWER_ID);

        final RequestForGroupMembershipRequest delRequest = new RequestForGroupMembershipRequest(GROUP_ID, 1L);

        final PersonModelView person = context.mock(PersonModelView.class);

        context.checking(new Expectations()
        {
            {
                allowing(getPersonByIdMapper).execute(FOLLOWER_ID);
                will(returnValue(person));

                allowing(person).getAccountId();

                oneOf(groupMapperMock).addFollower(FOLLOWER_ID, GROUP_ID);

                oneOf(addCachedGroupFollowerMapperMock).execute(1L, GROUP_ID);

                oneOf(deleteRequestForGroupMembershipMapper).execute(with(equalInternally(delRequest)));

                oneOf(deleteCacheKeyMapper).execute(Collections.singleton("Per:1"));

                oneOf(postActivity).execute(with(any(TaskHandlerActionContext.class)));

                oneOf(groupFollowerIdsMapperMock).execute(GROUP_ID);
                will(returnValue(followerIds));
            }
        });

        SetFollowingStatusByGroupCreatorRequest currentRequest = new SetFollowingStatusByGroupCreatorRequest(
                FOLLOWER_ID, GROUP_ID, Follower.FollowerStatus.FOLLOWING, "Group Name", "groupName", false);
        TaskHandlerActionContext<PrincipalActionContext> actionContext = TestContextCreator
                .createTaskHandlerContextWithPrincipal(currentRequest, principal);
        Serializable result = sut.execute(actionContext);

        context.assertIsSatisfied();
        assertEquals(5, result);
        assertEquals(2, actionContext.getUserActionRequests().size());
    }

    /**
     * Tests a user approving another to follow a private group.
     */
    @Test
    public void testFollowPrivateGroup()
    {
        SetFollowingStatusRequest request = new SetFollowingStatusRequest(FOLLOWER_ACCOUNT, GROUP_UNIQUEID,
                EntityType.GROUP, false, FollowerStatus.FOLLOWING);

        final RequestForGroupMembershipRequest mapperRequest1 = new RequestForGroupMembershipRequest(GROUP_ID,
                FOLLOWER_ID);

        context.checking(new Expectations()
        {
            {
                allowing(getPersonIdFromAccountIdMapper).execute(FOLLOWER_ACCOUNT);
                will(returnValue(FOLLOWER_ID));

                allowing(groupByShortNameMapperMock).fetchUniqueResult(GROUP_UNIQUEID);
                will(returnValue(group));

                oneOf(groupMapperMock).addFollower(FOLLOWER_ID, GROUP_ID);

                oneOf(addCachedGroupFollowerMapperMock).execute(FOLLOWER_ID, GROUP_ID);

                oneOf(deleteRequestForGroupMembershipMapper).execute(with(equalInternally(mapperRequest1)));
                will(returnValue(true));

                oneOf(deleteCacheKeyMapper).execute(Collections.singleton("Per:1"));

                oneOf(postActivity).execute(with(any(TaskHandlerActionContext.class)));

                oneOf(groupFollowerIdsMapperMock).execute(GROUP_ID);
                will(returnValue(followerIds));
            }
        });

        TaskHandlerActionContext<PrincipalActionContext> actionContext = TestContextCreator
                .createTaskHandlerContextWithPrincipal(request, principal);
        Serializable result = sut.execute(actionContext);

        context.assertIsSatisfied();
        assertEquals(5, result);

        ActionTestHelper.assertAsyncActionRequests(actionContext, "deleteCacheKeysAction",
                CreateNotificationsRequest.ACTION_NAME, CreateNotificationsRequest.ACTION_NAME);
    }

    /**
     * Test the setFollowing method when a group is unfollowed.
     * 
     * @throws Exception
     *             - on error.
     */
    @Test
    public void testRemoveFollowing() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                /*
                 * allowing(group).getCoordinators().size(); will(returnValue(2));
                 * 
                 * allowing(groupMapperMock).getGroupCoordinatorCount(); will(returnValue(2));
                 */

                oneOf(getPersonIdFromAccountIdMapper).execute(with(any(String.class)));
                will(returnValue(FOLLOWER_ID));

                oneOf(groupByShortNameMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(group));

                oneOf(groupMapperMock).isInputUserGroupCoordinator(with(FOLLOWER_ID), with(GROUP_ID));
                will(returnValue(false));

                oneOf(groupMapperMock).getGroupCoordinatorCount(with(GROUP_ID));
                will(returnValue(2));

                oneOf(groupMapperMock).removeFollower(FOLLOWER_ID, GROUP_ID);

                oneOf(groupFollowerIdsMapperMock).execute(GROUP_ID);
                will(returnValue(followerIds));
            }
        });

        SetFollowingStatusRequest currentRequest = new SetFollowingStatusRequest("ntaccount", "groupshortname",
                EntityType.GROUP, false, Follower.FollowerStatus.NOTFOLLOWING);
        TaskHandlerActionContext<PrincipalActionContext> actionContext = TestContextCreator
                .createTaskHandlerContextWithPrincipal(currentRequest, principal);
        Serializable result = sut.execute(actionContext);

        context.assertIsSatisfied();
        assertEquals(5, result);
        assertEquals(3, actionContext.getUserActionRequests().size());
    }

    /**
     * Test a failure. Try to remove the last Group Coordinator of a Group.
     * 
     * @throws ExecutionException
     *             - on error.
     */
    @Test(expected = ExecutionException.class)
    public void testRemoveLastGroupCoordinators() throws ExecutionException
    {
        context.checking(new Expectations()
        {
            {
                allowing(group).getCoordinators().size();
                will(returnValue(1));

                allowing(groupMapperMock).isInputUserGroupCoordinator(FOLLOWER_ID, GROUP_ID);
                will(returnValue(true));

                oneOf(getPersonIdFromAccountIdMapper).execute(with(any(String.class)));
                will(returnValue(FOLLOWER_ID));

                oneOf(groupByShortNameMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(group));

                oneOf(groupMapperMock).isInputUserGroupCoordinator(with(FOLLOWER_ID), with(GROUP_ID));
                will(returnValue(true));

                oneOf(groupMapperMock).getGroupCoordinatorCount(with(GROUP_ID));
                will(returnValue(1));

                oneOf(groupMapperMock).removeFollower(FOLLOWER_ID, GROUP_ID);

                oneOf(groupFollowerIdsMapperMock).execute(GROUP_ID);
                will(returnValue(followerIds));
            }
        });

        SetFollowingStatusRequest currentRequest = new SetFollowingStatusRequest("ntaccount", "groupshortname",
                EntityType.GROUP, false, Follower.FollowerStatus.NOTFOLLOWING);
        TaskHandlerActionContext<PrincipalActionContext> actionContext = TestContextCreator
                .createTaskHandlerContextWithPrincipal(currentRequest, principal);
        Serializable result = sut.execute(actionContext);
    }

    /**
     * Test a removal of a group coordinator.
     * 
     * @throws Exception
     *             - on error.
     */
    public void testRemoveGroupCoordinators() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(group).getCoordinators().size();
                will(returnValue(2));

                allowing(groupMapperMock).isInputUserGroupCoordinator(FOLLOWER_ID, GROUP_ID);
                will(returnValue(true));

                oneOf(getPersonIdFromAccountIdMapper).execute(with(any(String.class)));
                will(returnValue(FOLLOWER_ID));

                oneOf(groupByShortNameMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(group));

                oneOf(groupMapperMock).isInputUserGroupCoordinator(with(FOLLOWER_ID), with(GROUP_ID));
                will(returnValue(true));

                oneOf(groupMapperMock).getGroupCoordinatorCount(with(GROUP_ID));
                will(returnValue(1));

                oneOf(groupMapperMock).removeFollower(FOLLOWER_ID, GROUP_ID);

                oneOf(groupFollowerIdsMapperMock).execute(GROUP_ID);
                will(returnValue(followerIds));
            }
        });

        SetFollowingStatusRequest currentRequest = new SetFollowingStatusRequest("ntaccount", "groupshortname",
                EntityType.GROUP, false, Follower.FollowerStatus.NOTFOLLOWING);
        TaskHandlerActionContext<PrincipalActionContext> actionContext = TestContextCreator
                .createTaskHandlerContextWithPrincipal(currentRequest, principal);
        Serializable result = sut.execute(actionContext);
    }

    /**
     * Test a failure case.
     * 
     * @throws Exception
     *             - on error.
     */
    @Test(expected = Exception.class)
    public void testSetFollowingError() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(getPersonIdFromAccountIdMapper).execute(with(any(String.class)));
                will(throwException(new Exception("BAD")));
            }
        });

        SetFollowingStatusRequest currentRequest = new SetFollowingStatusRequest("ntaccount", "groupshortname",
                EntityType.GROUP, false, Follower.FollowerStatus.FOLLOWING);
        sut.execute(TestContextCreator.createTaskHandlerContextWithPrincipal(currentRequest, null, 0));

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
        context.checking(new Expectations()
        {
            {
                oneOf(getPersonIdFromAccountIdMapper).execute(with(any(String.class)));
                will(returnValue(FOLLOWER_ID));

                oneOf(groupByShortNameMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(group));

                oneOf(groupFollowerIdsMapperMock).execute(GROUP_ID);
                will(returnValue(followerIds));
            }
        });

        SetFollowingStatusRequest currentRequest = new SetFollowingStatusRequest("ntaccount", "groupshortname",
                EntityType.GROUP, false, Follower.FollowerStatus.NOTSPECIFIED);
        TaskHandlerActionContext<PrincipalActionContext> actionContext = TestContextCreator
                .createTaskHandlerContextWithPrincipal(currentRequest, principal);
        Serializable result = sut.execute(actionContext);

        context.assertIsSatisfied();
        assertEquals(5, result);
        assertEquals(0, actionContext.getUserActionRequests().size());
    }
}
