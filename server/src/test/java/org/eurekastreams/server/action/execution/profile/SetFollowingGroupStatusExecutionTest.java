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
package org.eurekastreams.server.action.execution.profile;

import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.profile.RequestForGroupMembershipRequest;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusByGroupCreatorRequest;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.mappers.cache.AddCachedGroupFollower;
import org.eurekastreams.server.persistence.mappers.db.DeleteRequestForGroupMembership;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetGroupFollowerIds;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the {@link SetFollowingGroupStatusExecution} class.
 * 
 */
public class SetFollowingGroupStatusExecutionTest
{
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

    /**
     * Mock instance of GetPeopleByAccountIds.
     */
    private final GetPeopleByAccountIds personMapperMock = context.mock(GetPeopleByAccountIds.class);

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
    private final GetGroupFollowerIds groupFollowerIdsMapperMock = context.mock(GetGroupFollowerIds.class);

    /**
     * Mocked principal object.
     */
    private final Principal princpalMock = context.mock(Principal.class);

    /** Mapper to remove group access requests. */
    private DeleteRequestForGroupMembership deleteRequestForGroupMembershipMapper = context
            .mock(DeleteRequestForGroupMembership.class);

    /**
     * Method to setup the System Under Test.
     */
    @Before
    public void setUp()
    {
        sut = new SetFollowingGroupStatusExecution(groupByShortNameMapperMock, personMapperMock, groupMapperMock,
                addCachedGroupFollowerMapperMock, groupFollowerIdsMapperMock, deleteRequestForGroupMembershipMapper);
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
        final PersonModelView testFollower = new PersonModelView();
        testFollower.setEntityId(1L);

        final DomainGroupModelView testTarget = new DomainGroupModelView();
        testTarget.setEntityId(2L);

        final List<Long> targetFollowerIds = new ArrayList<Long>(5);

        final RequestForGroupMembershipRequest delRequest = new RequestForGroupMembershipRequest(2L, 1L);

        context.checking(new Expectations()
        {
            {
                oneOf(personMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testFollower));

                oneOf(groupByShortNameMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testTarget));

                oneOf(groupMapperMock).addFollower(1L, 2L);

                oneOf(addCachedGroupFollowerMapperMock).execute(1L, 2L);

                oneOf(groupFollowerIdsMapperMock).execute(2L);
                will(returnValue(targetFollowerIds));

                oneOf(deleteRequestForGroupMembershipMapper).execute(with(equalInternally(delRequest)));
            }
        });

        SetFollowingStatusRequest currentRequest = new SetFollowingStatusRequest("ntaccount", "groupshortname",
                EntityType.GROUP, false, Follower.FollowerStatus.FOLLOWING);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, princpalMock);
        TaskHandlerActionContext currentTaskHandlerActionContext = new TaskHandlerActionContext<PrincipalActionContext>(
                currentContext, new ArrayList<UserActionRequest>());
        sut.execute(currentTaskHandlerActionContext);

        context.assertIsSatisfied();
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
        testFollower.setEntityId(1L);

        final DomainGroupModelView testTarget = new DomainGroupModelView();
        testTarget.setEntityId(2L);

        final List<Long> targetFollowerIds = new ArrayList<Long>(5);

        final RequestForGroupMembershipRequest delRequest = new RequestForGroupMembershipRequest(2L, 1L);

        context.checking(new Expectations()
        {
            {
                oneOf(groupMapperMock).addFollower(1L, 2L);

                oneOf(addCachedGroupFollowerMapperMock).execute(1L, 2L);

                oneOf(groupFollowerIdsMapperMock).execute(2L);
                will(returnValue(targetFollowerIds));

                oneOf(deleteRequestForGroupMembershipMapper).execute(with(equalInternally(delRequest)));
            }
        });

        SetFollowingStatusByGroupCreatorRequest currentRequest = new SetFollowingStatusByGroupCreatorRequest(1L, 2L,
                Follower.FollowerStatus.FOLLOWING);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, princpalMock);
        TaskHandlerActionContext currentTaskHandlerActionContext = new TaskHandlerActionContext<PrincipalActionContext>(
                currentContext, new ArrayList<UserActionRequest>());
        sut.execute(currentTaskHandlerActionContext);

        context.assertIsSatisfied();
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
        final PersonModelView testFollower = new PersonModelView();
        testFollower.setEntityId(1L);

        final DomainGroupModelView testTarget = new DomainGroupModelView();
        testTarget.setEntityId(2L);

        final List<Long> targetFollowerIds = new ArrayList<Long>(5);

        context.checking(new Expectations()
        {
            {
                oneOf(personMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testFollower));

                oneOf(groupByShortNameMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testTarget));

                oneOf(groupMapperMock).removeFollower(1L, 2L);

                oneOf(groupFollowerIdsMapperMock).execute(2L);
                will(returnValue(targetFollowerIds));
            }
        });

        SetFollowingStatusRequest currentRequest = new SetFollowingStatusRequest("ntaccount", "groupshortname",
                EntityType.GROUP, false, Follower.FollowerStatus.NOTFOLLOWING);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, princpalMock);
        TaskHandlerActionContext currentTaskHandlerActionContext = new TaskHandlerActionContext<PrincipalActionContext>(
                currentContext, new ArrayList<UserActionRequest>());
        sut.execute(currentTaskHandlerActionContext);

        context.assertIsSatisfied();
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
        final PersonModelView testFollower = new PersonModelView();
        testFollower.setEntityId(1L);

        final DomainGroupModelView testTarget = new DomainGroupModelView();
        testTarget.setEntityId(2L);

        final List<Long> targetFollowerIds = new ArrayList<Long>(5);

        context.checking(new Expectations()
        {
            {
                oneOf(personMapperMock).fetchUniqueResult(with(any(String.class)));
                will(throwException(new Exception("BAD")));
            }
        });

        SetFollowingStatusRequest currentRequest = new SetFollowingStatusRequest("ntaccount", "groupshortname",
                EntityType.GROUP, false, Follower.FollowerStatus.FOLLOWING);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, princpalMock);
        TaskHandlerActionContext currentTaskHandlerActionContext = new TaskHandlerActionContext<PrincipalActionContext>(
                currentContext, new ArrayList<UserActionRequest>());
        sut.execute(currentTaskHandlerActionContext);

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
        final PersonModelView testFollower = new PersonModelView();
        testFollower.setEntityId(1L);

        final DomainGroupModelView testTarget = new DomainGroupModelView();
        testTarget.setEntityId(2L);

        final List<Long> targetFollowerIds = new ArrayList<Long>(5);

        context.checking(new Expectations()
        {
            {
                oneOf(personMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testFollower));

                oneOf(groupByShortNameMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testTarget));

                oneOf(groupFollowerIdsMapperMock).execute(2L);
                will(returnValue(targetFollowerIds));
            }
        });

        SetFollowingStatusRequest currentRequest = new SetFollowingStatusRequest("ntaccount", "groupshortname",
                EntityType.GROUP, false, Follower.FollowerStatus.NOTSPECIFIED);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, princpalMock);
        TaskHandlerActionContext currentTaskHandlerActionContext = new TaskHandlerActionContext<PrincipalActionContext>(
                currentContext, new ArrayList<UserActionRequest>());
        sut.execute(currentTaskHandlerActionContext);

        context.assertIsSatisfied();
    }
}
