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

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByIds;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link GetBannerIdByParentOrganizationStrategy} class.
 *
 */
public class GetBannerIdByParentOrganizationStrategyTest
{
    /**
     * System under test.
     */
    private GetBannerIdByParentOrganizationStrategy sut;

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
     * Mocked instance of the {@link GetOrganizationsByIds} mapper.
     */
    private GetOrganizationsByIds orgMapperMock = context.mock(GetOrganizationsByIds.class);

    /**
     * mapper to get all parent org ids for an org id.
     */
    private DomainMapper<Long, List<Long>> recursiveParentOrgIdsMock = context.mock(DomainMapper.class);

    /**
     * Mocked instance of the FindByIdMapper.
     */
    private FindByIdMapper findByIdMapperMock = context.mock(FindByIdMapper.class);

    /**
     * Mocked instance of the OrganizationModelView.
     */
    private OrganizationModelView orgModelViewMock = context.mock(OrganizationModelView.class);

    /**
     * Mocked instance of the Organization.
     */
    private Organization orgMock = context.mock(Organization.class);

    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new GetBannerIdByParentOrganizationStrategy(
                orgMapperMock, recursiveParentOrgIdsMock, findByIdMapperMock, "Organization");
    }

    /**
     * Test the successful path through the strategy with the direct parent passed in having a
     * banner id.
     */
    @Test
    public void testGetBannerWithDirectParentBannerId()
    {

        context.checking(new Expectations()
        {
            {
                oneOf(orgMapperMock).execute(1L);
                will(returnValue(orgModelViewMock));

                oneOf(orgModelViewMock).getBannerId();
                will(returnValue("testBanner"));

                oneOf(orgMock).setBannerId("testBanner");

                oneOf(orgModelViewMock).getEntityId();
                will(returnValue(2L));

                oneOf(orgMock).setBannerEntityId(2L);

                oneOf(orgMock).getBannerId();
                will(returnValue("testBanner"));
            }
        });

        sut.getBannerId(1L, orgMock);

        context.assertIsSatisfied();
    }

    /**
     * Test the succesful path through the strategy where the direct parent does not have the
     * banner id configured but another parent org further up the tree does.
     */
    @Test
    public void testGetBannerWithParentHierarchyBannerId()
    {
        final List<Long> parentOrgIds = new ArrayList<Long>();
        parentOrgIds.add(new Long(2L));
        parentOrgIds.add(new Long(3L));

        final List<OrganizationModelView> parentOrgs = new ArrayList<OrganizationModelView>();
        OrganizationModelView org1 = new OrganizationModelView();
        org1.setBannerId(null);
        parentOrgs.add(org1);
        OrganizationModelView org2 = new OrganizationModelView();
        org2.setBannerId("goodBanner");
        parentOrgs.add(org2);

        context.checking(new Expectations()
        {
            {
                oneOf(orgMapperMock).execute(1L);
                will(returnValue(orgModelViewMock));

                oneOf(orgModelViewMock).getBannerId();
                will(returnValue(null));

                oneOf(orgMock).setBannerId(null);

                oneOf(orgModelViewMock).getEntityId();
                will(returnValue(2L));

                oneOf(orgMock).setBannerEntityId(2L);

                oneOf(orgMock).getBannerId();
                will(returnValue(null));

                oneOf(recursiveParentOrgIdsMock).execute(1L);
                will(returnValue(parentOrgIds));

                oneOf(orgMapperMock).execute(parentOrgIds);
                will(returnValue(parentOrgs));

                allowing(orgModelViewMock).getBannerId();

                oneOf(orgMock).setBannerId("goodBanner");

                oneOf(orgMock).setBannerEntityId(with(any(Long.class)));

                allowing(orgMock).getBannerId();
            }
        });

        sut.getBannerId(1L, orgMock);

        context.assertIsSatisfied();
    }

    /**
     * Test the succesful path through the strategy where no parent has a configured
     * banner id.
     */
    @Test
    public void testGetBannerWithNullParentHierarchyBannerId()
    {
        final List<Long> parentOrgIds = new ArrayList<Long>();
        parentOrgIds.add(new Long(2L));
        parentOrgIds.add(new Long(3L));

        final List<OrganizationModelView> parentOrgs = new ArrayList<OrganizationModelView>();
        OrganizationModelView org1 = new OrganizationModelView();
        org1.setBannerId(null);
        parentOrgs.add(org1);
        OrganizationModelView org2 = new OrganizationModelView();
        org2.setBannerId(null);
        parentOrgs.add(org2);

        context.checking(new Expectations()
        {
            {
                oneOf(orgMapperMock).execute(1L);
                will(returnValue(orgModelViewMock));

                oneOf(orgModelViewMock).getBannerId();
                will(returnValue(null));

                oneOf(orgMock).setBannerId(null);


                oneOf(orgModelViewMock).getEntityId();
                will(returnValue(2L));

                oneOf(orgMock).setBannerEntityId(2L);

                oneOf(orgMock).getBannerId();
                will(returnValue(null));

                oneOf(recursiveParentOrgIdsMock).execute(1L);
                will(returnValue(parentOrgIds));

                oneOf(orgMapperMock).execute(parentOrgIds);
                will(returnValue(parentOrgs));

                allowing(orgModelViewMock).getBannerId();
                will(returnValue(null));

                allowing(orgMock).setBannerId(null);

                allowing(orgMock).getBannerId();
                will(returnValue(null));

                oneOf(orgMock).setBannerEntityId(null);
            }
        });

        sut.getBannerId(1L, orgMock);

        context.assertIsSatisfied();
    }

    /**
     * Test the second method to retrieving the BannerId works successfully.
     */
    @Test
    public void testGetBannerWithEntityId()
    {
        final List<Long> parentOrgIds = new ArrayList<Long>();
        parentOrgIds.add(new Long(2L));
        parentOrgIds.add(new Long(3L));

        final List<OrganizationModelView> parentOrgs = new ArrayList<OrganizationModelView>();
        OrganizationModelView org1 = new OrganizationModelView();
        org1.setBannerId(null);
        parentOrgs.add(org1);
        OrganizationModelView org2 = new OrganizationModelView();
        org2.setBannerId(null);
        parentOrgs.add(org2);

        context.checking(new Expectations()
        {
            {
                oneOf(findByIdMapperMock).execute(with(any(FindByIdRequest.class)));
                will(returnValue(orgMock));

                oneOf(orgMock).getParentOrgId();
                will(returnValue(1L));

                oneOf(orgMapperMock).execute(1L);
                will(returnValue(orgModelViewMock));

                oneOf(orgModelViewMock).getBannerId();
                will(returnValue(null));

                oneOf(orgMock).setBannerId(null);

                oneOf(orgModelViewMock).getEntityId();
                will(returnValue(2L));

                oneOf(orgMock).setBannerEntityId(2L);

                oneOf(orgMock).getBannerId();
                will(returnValue(null));

                oneOf(recursiveParentOrgIdsMock).execute(1L);
                will(returnValue(parentOrgIds));

                oneOf(orgMapperMock).execute(parentOrgIds);
                will(returnValue(parentOrgs));

                allowing(orgModelViewMock).getBannerId();
                will(returnValue(null));

                allowing(orgMock).setBannerId(null);

                allowing(orgMock).getBannerId();
                will(returnValue(null));

                oneOf(orgMock).setBannerEntityId(null);
            }
        });

        sut.getBannerId(orgMock, 2L);

        context.assertIsSatisfied();
    }
}
