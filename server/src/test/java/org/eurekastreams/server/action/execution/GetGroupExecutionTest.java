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
package org.eurekastreams.server.action.execution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.DomainGroupEntity;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.RestrictedDomainGroup;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.cache.PopulateOrgChildWithSkeletonParentOrgsCacheMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetGroupExecution class.
 * 
 */
public class GetGroupExecutionTest
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
     * Username for tests.
     */
    private static final String USERNAME = "username";

    /**
     * User id for tests.
     */
    private static final Long USER_ID = 1L;

    /**
     * Subject under test.
     */
    private GetGroupExecution sut;

    /**
     * {@link PopulateOrgChildWithSkeletonParentOrgsCacheMapper}.
     */
    private PopulateOrgChildWithSkeletonParentOrgsCacheMapper orgChildSkeletonParentOrgPopulator = context
            .mock(PopulateOrgChildWithSkeletonParentOrgsCacheMapper.class);

    /**
     * A short name to look up. Arbitrary.
     */
    private static final String GROUP_SHORT_NAME = "shortName";

    /**
     * Group id.
     */
    private static final Long GROUP_ID = 5L;

    /**
     * {@link DomainGroupMapper}.
     */
    private DomainGroupMapper mapper = context.mock(DomainGroupMapper.class);

    /**
     *{@link DomainGroup}.
     */
    private DomainGroup group = context.mock(DomainGroup.class);

    /**
     * Mock set for list of coordinator ids.
     */
    private Set<Long> coordinatorIds = context.mock(HashSet.class);

    /**
     * {@link PrincipalActionContext} mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * {@link Principal} mock.
     */
    private Principal actionContextPrincipal = context.mock(Principal.class);

    /**
     * {@link GetAllPersonIdsWhoHaveGroupCoordinatorAccess}.
     */
    private GetAllPersonIdsWhoHaveGroupCoordinatorAccess getAllPersonIdsWhoHaveGroupCoordinatorAccess = context
            .mock(GetAllPersonIdsWhoHaveGroupCoordinatorAccess.class);

    /**
     * Mocked mapper for retrieving the banner id.
     */
    private GetBannerIdByParentOrganizationStrategy getBannerIdMapperMock = context
            .mock(GetBannerIdByParentOrganizationStrategy.class);

    /**
     * {@link GetGroupFollowerIds}.
     */
    private DomainMapper<Long, List<Long>> getGroupFollowerIdsMapper = context.mock(DomainMapper.class,
            "getGroupFollowerIdsMapper");

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new GetGroupExecution(mapper, orgChildSkeletonParentOrgPopulator,
                getAllPersonIdsWhoHaveGroupCoordinatorAccess, getBannerIdMapperMock, getGroupFollowerIdsMapper);
    }

    /**
     * Test the performAction method with an existing group.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public void performAction() throws Exception
    {
        setupGroupExpectations();

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(GROUP_SHORT_NAME));

                allowing(actionContext).getPrincipal();
                will(returnValue(actionContextPrincipal));

                allowing(actionContextPrincipal).getAccountId();
                will(returnValue(USERNAME));

                oneOf(group).isPublicGroup();
                will(returnValue(true));

                oneOf(group).getCapabilities();

                oneOf(orgChildSkeletonParentOrgPopulator).populateParentOrgSkeletons(with(any(Collection.class)));

                oneOf(group).getId();

                oneOf(group).setBannerEntityId(with(any(Long.class)));

                oneOf(group).getBannerId();
                will(returnValue(null));

                oneOf(group).getParentOrganization();

                oneOf(getBannerIdMapperMock).getBannerId(with(any(Long.class)), with(any(DomainGroup.class)));
            }
        });

        DomainGroupEntity actual = sut.execute(actionContext);

        assertEquals(group, actual);

        context.assertIsSatisfied();
    }

    /**
     * Test the performAction method with a group that is not in the database.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public void performActionWithUnknownGroup() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(GROUP_SHORT_NAME));

                allowing(actionContext).getPrincipal();
                will(returnValue(actionContextPrincipal));

                allowing(actionContextPrincipal).getAccountId();
                will(returnValue(USERNAME));

                oneOf(mapper).findByShortName(GROUP_SHORT_NAME);
                will(returnValue(null));
            }
        });

        DomainGroupEntity actual = sut.execute(actionContext);

        assertNull(actual);

        context.assertIsSatisfied();
    }

    /**
     * Try to view a private group where the user is a coordinator.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public void performActionViewPrivateAsCoordinator() throws Exception
    {
        setupGroupExpectations();

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(GROUP_SHORT_NAME));

                allowing(actionContext).getPrincipal();
                will(returnValue(actionContextPrincipal));

                allowing(actionContextPrincipal).getAccountId();
                will(returnValue(USERNAME));

                oneOf(group).isPublicGroup();
                will(returnValue(false));

                allowing(group).getId();
                will(returnValue(GROUP_ID));

                allowing(getAllPersonIdsWhoHaveGroupCoordinatorAccess).execute(GROUP_ID);
                will(returnValue(coordinatorIds));

                allowing(actionContextPrincipal).getId();
                will(returnValue(USER_ID));

                allowing(coordinatorIds).contains(USER_ID);
                will(returnValue(true));

                oneOf(group).getCapabilities();

                oneOf(orgChildSkeletonParentOrgPopulator).populateParentOrgSkeletons(with(any(Collection.class)));

                oneOf(group).setBannerEntityId(with(any(Long.class)));

                oneOf(group).getBannerId();
                will(returnValue(null));

                oneOf(group).getParentOrganization();

                oneOf(getBannerIdMapperMock).getBannerId(with(any(Long.class)), with(any(DomainGroup.class)));
            }
        });

        DomainGroupEntity actual = sut.execute(actionContext);

        assertEquals(group, actual);

        context.assertIsSatisfied();
    }

    /**
     * Try to view a private group where the user is a follower of the group.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public void performActionViewPrivateAsFollower() throws Exception
    {
        setupGroupExpectations();

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(GROUP_SHORT_NAME));

                allowing(actionContext).getPrincipal();
                will(returnValue(actionContextPrincipal));

                allowing(actionContextPrincipal).getAccountId();
                will(returnValue(USERNAME));

                oneOf(group).isPublicGroup();
                will(returnValue(false));

                allowing(group).getId();
                will(returnValue(GROUP_ID));

                allowing(getAllPersonIdsWhoHaveGroupCoordinatorAccess).execute(GROUP_ID);
                will(returnValue(coordinatorIds));

                allowing(actionContextPrincipal).getId();
                will(returnValue(USER_ID));

                allowing(coordinatorIds).contains(USER_ID);
                will(returnValue(false));

                oneOf(getGroupFollowerIdsMapper).execute(GROUP_ID);
                will(returnValue(Collections.singletonList(USER_ID)));

                oneOf(group).getCapabilities();

                oneOf(orgChildSkeletonParentOrgPopulator).populateParentOrgSkeletons(with(any(Collection.class)));

                oneOf(group).setBannerEntityId(with(any(Long.class)));

                oneOf(group).getBannerId();
                will(returnValue(null));

                oneOf(group).getParentOrganization();

                oneOf(getBannerIdMapperMock).getBannerId(with(any(Long.class)), with(any(DomainGroup.class)));
            }
        });

        DomainGroupEntity actual = sut.execute(actionContext);

        assertEquals(group, actual);

        context.assertIsSatisfied();
    }

    /**
     * Attempt to view a private group by someone who isn't allowed.
     * 
     * @throws Exception
     *             should throw AuthorizationException
     */
    @Test
    public void performActionNotAllowedToSee() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(GROUP_SHORT_NAME));

                allowing(actionContext).getPrincipal();
                will(returnValue(actionContextPrincipal));

                allowing(actionContextPrincipal).getAccountId();
                will(returnValue(USERNAME));

                oneOf(mapper).findByShortName(GROUP_SHORT_NAME);
                will(returnValue(group));

                oneOf(group).isPublicGroup();
                will(returnValue(false));

                allowing(group).getId();
                will(returnValue(GROUP_ID));

                allowing(getAllPersonIdsWhoHaveGroupCoordinatorAccess).execute(GROUP_ID);
                will(returnValue(coordinatorIds));

                allowing(actionContextPrincipal).getId();
                will(returnValue(USER_ID));

                allowing(coordinatorIds).contains(USER_ID);
                will(returnValue(false));

                allowing(group).getShortName();
                will(returnValue(GROUP_SHORT_NAME));

                oneOf(getGroupFollowerIdsMapper).execute(GROUP_ID);
                will(returnValue(new ArrayList<Long>()));

                // for cloning the group to a restricted group
                allowing(group).getId();

                allowing(group).getBannerId();

                allowing(group).getName();

                allowing(group).getParentOrganization();
                oneOf(group).getAvatarId();
                oneOf(group).getAvatarCropY();
                oneOf(group).getAvatarCropX();
                oneOf(group).getAvatarCropSize();
            }
        });

        DomainGroupEntity actual = sut.execute(actionContext);

        assertTrue(actual instanceof RestrictedDomainGroup);

        context.assertIsSatisfied();
    }

    /**
     * Utility method to set up expectations for tests where a group gets returned, since there are a bunch of
     * expectations related to making sure fields get loaded up.
     */
    private void setupGroupExpectations()
    {
        final Organization parentOrg = context.mock(Organization.class, "parent");
        final long parentOrgId = 634L;
        final Organization parent2Org = context.mock(Organization.class, "gramps");
        final long parent2OrgId = 983L;

        final Set<Person> coordinators = new HashSet<Person>();
        final Person coordinator = context.mock(Person.class);
        coordinators.add(coordinator);

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).findByShortName(GROUP_SHORT_NAME);
                will(returnValue(group));

                allowing(group).getShortName();
                will(returnValue(GROUP_SHORT_NAME));

                oneOf(group).getParentOrganization();
                will(returnValue(parentOrg));

                oneOf(group).getCoordinators();
                will(returnValue(coordinators));

                allowing(coordinator).getParentOrganization();
                will(returnValue(parentOrg));

                allowing(parentOrg).getId();
                will(returnValue(parentOrgId));

                allowing(parent2Org).getId();
                will(returnValue(parent2OrgId));

                allowing(parentOrg).getParentOrganization();
                will(returnValue(parent2Org));

                allowing(parent2Org).getParentOrganization();
                will(returnValue(parent2Org));
            }
        });
    }

}
