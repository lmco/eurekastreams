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

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.request.profile.GetFollowersFollowingRequest;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.mappers.GetRecursiveOrgCoordinators;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetFollowersAuthorizationStrategy.
 */
public class GetFollowersAuthorizationStrategyTest
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
     * The mapper that will check security stuff.
     */
    private final DomainGroupMapper groupMapperMock = context.mock(DomainGroupMapper.class);

    /** Fixture: org permission checker. */
    private GetRecursiveOrgCoordinators orgPermChecker = context.mock(GetRecursiveOrgCoordinators.class);

    /**
     * Mocked PrincipalActionContext.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Principal mock.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * Mocked request object.
     */
    private GetFollowersFollowingRequest getFollowersRequest = context.mock(GetFollowersFollowingRequest.class);

    /**
     * The mapper that will check security stuff.
     */
    private final DomainGroup groupMock = context.mock(DomainGroup.class);

    /**
     * System under test.
     */
    private GetFollowersAuthorizationStrategy sut;

    /**
     * Org short name.
     */
    private static final String GROUP_SHORT_NAME = "groupname";

    /**
     * current user.
     */
    private static final String CURRENT_USER = "currentuser";

    /** Test data. */
    private static final long CURRENT_USER_ID = 2468L;

    /**
     * Start index.
     */
    private static final Integer START_INDEX = 382;

    /**
     * End index.
     */
    private static final Integer END_INDEX = 391;

    /**
     * Person entity type.
     */
    private static final EntityType PERSON_ENTITY_TYPE = EntityType.PERSON;

    /**
     * Group entity type.
     */
    private static final EntityType GROUP_ENTITY_TYPE = EntityType.GROUP;

    /** Test data. */
    private static final long PARENT_ORG_ID = 97531L;

    /**
     * request to authorize.
     */
    // private GetPendingGroupsRequest request = new GetPendingGroupsRequest(orgShortName, startIndex, endIndex);
    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new GetFollowersAuthorizationStrategy(groupMapperMock, orgPermChecker);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue(CURRENT_USER));

                allowing(principal).getId();
                will(returnValue(CURRENT_USER_ID));
            }
        });
    }

    /**
     * Perform the action security test given the entity type of person as access granted.
     *
     * @throws Exception
     *             shouldn't happen.
     */
    @Test
    public final void performSecurityTestPersonEntity() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(getFollowersRequest));

                oneOf(getFollowersRequest).getEntityType();
                will(returnValue(PERSON_ENTITY_TYPE));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Set up expectations common to all group tests.
     */
    private void setupCommonGroupExpectations()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(getFollowersRequest));

                oneOf(getFollowersRequest).getEntityId();
                will(returnValue(GROUP_SHORT_NAME));

                oneOf(getFollowersRequest).getEntityType();
                will(returnValue(GROUP_ENTITY_TYPE));

                oneOf(groupMapperMock).findByShortName(GROUP_SHORT_NAME);
                will(returnValue(groupMock));
            }
        });
    }

    /**
     * Perform the action security test given the entity type of group as access granted. Not a public group or a
     * coordinator, is following.
     *
     * @throws Exception
     *             shouldn't happen.
     */
    @Test
    public final void performSecurityTestGroupEntityIsFollowing() throws Exception
    {
        setupCommonGroupExpectations();
        context.checking(new Expectations()
        {
            {
                oneOf(groupMock).isPublicGroup();
                will(returnValue(false));

                oneOf(groupMock).isCoordinator(CURRENT_USER);
                will(returnValue(false));

                oneOf(groupMapperMock).isFollowing(CURRENT_USER, GROUP_SHORT_NAME);
                will(returnValue(true));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Perform the action security test given the entity type of group as access granted. A public group.
     *
     * @throws Exception
     *             shouldn't happen.
     */
    @Test
    public final void performSecurityTestGroupEntityPublicGroup() throws Exception
    {
        setupCommonGroupExpectations();
        context.checking(new Expectations()
        {
            {
                oneOf(groupMock).isPublicGroup();
                will(returnValue(true));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Perform the action security test given the entity type of group as access granted. Not a public group. Is a
     * coordinator.
     *
     * @throws Exception
     *             shouldn't happen.
     */
    @Test
    public final void performSecurityTestGroupEntityIsCoordinator() throws Exception
    {
        setupCommonGroupExpectations();
        context.checking(new Expectations()
        {
            {
                oneOf(groupMock).isPublicGroup();
                will(returnValue(false));

                oneOf(groupMock).isCoordinator(CURRENT_USER);
                will(returnValue(true));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Perform the action security test given the entity type of group as access granted. Not a public group. Is a
     * coordinator.
     *
     * @throws Exception
     *             shouldn't happen.
     */
    @Test
    public final void performSecurityTestGroupEntityIsOrgCoordinator() throws Exception
    {
        setupCommonGroupExpectations();
        context.checking(new Expectations()
        {
            {
                oneOf(groupMock).isPublicGroup();
                will(returnValue(false));

                oneOf(groupMock).isCoordinator(CURRENT_USER);
                will(returnValue(false));

                allowing(groupMock).getParentOrgId();
                will(returnValue(PARENT_ORG_ID));

                oneOf(groupMapperMock).isFollowing(CURRENT_USER, GROUP_SHORT_NAME);
                will(returnValue(false));

                oneOf(orgPermChecker).isOrgCoordinatorRecursively(CURRENT_USER_ID, PARENT_ORG_ID);
                will(returnValue(true));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Perform the action security test given the entity type of group as access denied. No a public group, not a
     * coordinator, not following.
     *
     * @throws Exception
     *             shouldn't happen.
     */
    @Test(expected = AuthorizationException.class)
    public final void performSecurityTestFail() throws Exception
    {
        setupCommonGroupExpectations();
        context.checking(new Expectations()
        {
            {
                oneOf(groupMock).isPublicGroup();
                will(returnValue(false));

                oneOf(groupMock).isCoordinator(CURRENT_USER);
                will(returnValue(false));

                oneOf(groupMapperMock).isFollowing(CURRENT_USER, GROUP_SHORT_NAME);
                will(returnValue(false));

                allowing(groupMock).getParentOrgId();
                will(returnValue(PARENT_ORG_ID));

                oneOf(orgPermChecker).isOrgCoordinatorRecursively(CURRENT_USER_ID, PARENT_ORG_ID);
                will(returnValue(false));
            }
        });

        sut.authorize(actionContext);
    }

    /**
     * Perform the action security test for an entity type of neither person nor group.
     *
     * @throws Exception
     *             shouldn't happen.
     */
    @Test
    public final void performSecurityTestOtherEntity() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(getFollowersRequest));

                oneOf(getFollowersRequest).getEntityType();
                will(returnValue(EntityType.ORGANIZATION));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }
}
