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
package org.eurekastreams.server.action.authorization.profile;

import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * This class is responsible for testing the {@link SetFollowingGroupStatusAuthorization} class.
 *
 */
public class SetFollowingGroupStatusAuthorizationTest
{
    /**
     * System under test.
     */
    private SetFollowingGroupStatusAuthorization sut;

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
     * Local instance of the GetDomainGroupsByShortName mapper.
     */
    private final GetDomainGroupsByShortNames groupMapperMock = context.mock(GetDomainGroupsByShortNames.class);

    /**
     * Local instance of the GetAllPersonIdsWhoHaveGroupCoordinatorAccess mapper.
     */
    private final GetAllPersonIdsWhoHaveGroupCoordinatorAccess groupCoordMapperMock = context
            .mock(GetAllPersonIdsWhoHaveGroupCoordinatorAccess.class);

    /**
     * Mocked instance of the Principal class.
     */
    private final Principal principalMock = context.mock(Principal.class);

    /**
     * Prepare the system under test.
     */
    @Before
    public void setUp()
    {
        sut = new SetFollowingGroupStatusAuthorization(groupMapperMock, groupCoordMapperMock);
    }

    /**
     * Test authorizing add a follower to a private group.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testAuthorizeAddFollowerPrivateGroup()
    {
        final SetFollowingStatusRequest request = new SetFollowingStatusRequest("1", "1", EntityType.GROUP, false,
                Follower.FollowerStatus.FOLLOWING);

        final DomainGroupModelView testGroup = new DomainGroupModelView();
        testGroup.setIsPublic(false);
        testGroup.setEntityId(1L);

        final Set<Long> groupCoordIds = new HashSet<Long>();
        groupCoordIds.add(1L);

        context.checking(new Expectations()
        {
            {
                oneOf(groupMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testGroup));

                oneOf(groupCoordMapperMock).execute(1L);
                will(returnValue(groupCoordIds));

                oneOf(principalMock).getId();
                will(returnValue(1L));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        sut.authorize(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Test authoring adding a follower to a private group where the person adding the follower is not a coordinator.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeAddFollowerPrivateGroupNotCoordinator()
    {
        final SetFollowingStatusRequest request = new SetFollowingStatusRequest("1", "1", EntityType.GROUP, false,
                Follower.FollowerStatus.FOLLOWING);

        final DomainGroupModelView testGroup = new DomainGroupModelView();
        testGroup.setIsPublic(false);
        testGroup.setEntityId(1L);

        final Set<Long> groupCoordIds = new HashSet<Long>();
        groupCoordIds.add(1L);

        context.checking(new Expectations()
        {
            {
                oneOf(groupMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testGroup));

                oneOf(groupCoordMapperMock).execute(1L);
                will(returnValue(groupCoordIds));

                oneOf(principalMock).getId();
                will(returnValue(2L));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        sut.authorize(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Test authorizing adding a follower to a public group.
     */
    @Test
    public void testAuthorizeAddFollowerPublicGroup()
    {
        final SetFollowingStatusRequest request = new SetFollowingStatusRequest("1", "1", EntityType.GROUP, false,
                Follower.FollowerStatus.FOLLOWING);

        final DomainGroupModelView testGroup = new DomainGroupModelView();
        testGroup.setIsPublic(true);
        testGroup.setEntityId(1L);

        context.checking(new Expectations()
        {
            {
                oneOf(groupMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testGroup));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        sut.authorize(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Test authorizing removing a follower from a private group.
     */
    @Test
    public void testAuthorizeRemoveFollowerPrivateGroup()
    {
        final SetFollowingStatusRequest request = new SetFollowingStatusRequest("1", "1", EntityType.GROUP, false,
                Follower.FollowerStatus.NOTFOLLOWING);

        final DomainGroupModelView testGroup = new DomainGroupModelView();
        testGroup.setIsPublic(false);
        testGroup.setEntityId(1L);

        final Set<Long> groupCoordIds = new HashSet<Long>();
        groupCoordIds.add(1L);

        context.checking(new Expectations()
        {
            {
                oneOf(groupMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testGroup));

                oneOf(groupCoordMapperMock).execute(1L);
                will(returnValue(groupCoordIds));

                oneOf(principalMock).getId();
                will(returnValue(1L));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        sut.authorize(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Test authorizing removing a follower from a private group when the user removing the follower is not a
     * coordinator.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeRemoveFollowerPrivateGroupNotCoordinator()
    {
        final SetFollowingStatusRequest request = new SetFollowingStatusRequest("1", "1", EntityType.GROUP, false,
                Follower.FollowerStatus.NOTFOLLOWING);

        final DomainGroupModelView testGroup = new DomainGroupModelView();
        testGroup.setIsPublic(false);
        testGroup.setEntityId(1L);

        final Set<Long> groupCoordIds = new HashSet<Long>();
        groupCoordIds.add(2L);

        context.checking(new Expectations()
        {
            {
                oneOf(groupMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testGroup));

                oneOf(groupCoordMapperMock).execute(1L);
                will(returnValue(groupCoordIds));

                oneOf(principalMock).getId();
                will(returnValue(1L));

                oneOf(principalMock).getAccountId();
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        sut.authorize(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Test authorizing removing a follower from a public group when the person removing the follower is not the
     * follower themselves.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeRemoveFollowerPublicGroupNotOwner()
    {
        final SetFollowingStatusRequest request = new SetFollowingStatusRequest("ntaccount", "groupshortname",
                EntityType.GROUP, false, Follower.FollowerStatus.NOTFOLLOWING);

        final DomainGroupModelView testGroup = new DomainGroupModelView();
        testGroup.setIsPublic(true);
        testGroup.setEntityId(1L);

        context.checking(new Expectations()
        {
            {
                oneOf(groupMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testGroup));

                oneOf(principalMock).getAccountId();
                will(returnValue("differentNtAccount"));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        sut.authorize(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Test authorizing removing a follower from a public group when the person removing the follower is not the
     * follower themselves.
     */
    @Test
    public void testAuthorizeRemoveFollowerPublicGroup()
    {
        final SetFollowingStatusRequest request = new SetFollowingStatusRequest("ntaccount", "groupshortname",
                EntityType.GROUP, false, Follower.FollowerStatus.NOTFOLLOWING);

        final DomainGroupModelView testGroup = new DomainGroupModelView();
        testGroup.setIsPublic(true);
        testGroup.setEntityId(1L);

        context.checking(new Expectations()
        {
            {
                oneOf(groupMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testGroup));

                oneOf(principalMock).getAccountId();
                will(returnValue("ntaccount"));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        sut.authorize(currentContext);

        context.assertIsSatisfied();
    }
}
