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
package org.eurekastreams.server.persistence.mappers.cache;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.EntityTestHelper;
import org.eurekastreams.server.domain.OrganizationChild;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByIds;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for PopulatePeopleWithSkeletonParentOrgsCacheMapper.
 */
public class PopulatePeopleWithSkeletonParentOrgsCacheMapperTest
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
     * Mocked GetOrganizationsByIds mapper.
     */
    private GetOrganizationsByIds orgModelViewCacheMock = context.mock(GetOrganizationsByIds.class);

    /**
     * System under test.
     */
    private PopulateOrgChildWithSkeletonParentOrgsCacheMapper
    // line break
    sut = new PopulateOrgChildWithSkeletonParentOrgsCacheMapper(orgModelViewCacheMock);

    /**
     * Test populatePersonParentOrgSkeleton.
     */
    @Test
    public void testPopulatePersonParentOrgSkeleton()
    {
        final Long org1Id = 38L;
        final String org1BannerId = "slkdj33f";
        final String org1ShortName = "SHORT1";
        final String org1Name = "NAME1";

        Person p1 = new Person();
        EntityTestHelper.setPersonParentOrgId(p1, org1Id);

        final List<OrganizationModelView> cachedOrgModelViews = new ArrayList<OrganizationModelView>();

        final OrganizationModelView orgMv1 = new OrganizationModelView();
        orgMv1.setEntityId(org1Id);
        orgMv1.setBannerId(org1BannerId);
        orgMv1.setShortName(org1ShortName);
        orgMv1.setName(org1Name);

        cachedOrgModelViews.add(orgMv1);

        final ArrayList<Long> expectedOrgIds = new ArrayList<Long>();
        expectedOrgIds.add(org1Id);

        // assert that the sut will ask for the private groups
        context.checking(new Expectations()
        {
            {
                oneOf(orgModelViewCacheMock).execute(with(equal(expectedOrgIds)));
                will(returnValue(cachedOrgModelViews));
            }
        });

        // sut
        sut.populateParentOrgSkeleton(p1);

        // assert
        assertEquals((long) org1Id, p1.getParentOrganization().getId());
        assertEquals(org1BannerId, p1.getParentOrganization().getBannerId());
        assertEquals(org1Name, p1.getParentOrganization().getName());
        assertEquals(org1ShortName.toLowerCase(), p1.getParentOrganization().getShortName());
    }

    /**
     * Test populatePeopleParentOrgSkeletons.
     */
    @Test
    public void testPopulatePeopleParentOrgSkeletons()
    {
        final Long org1Id = 38L;
        final String org1BannerId = "slkdj33f";
        final String org1ShortName = "SHORT1";
        final String org1Name = "NAME1";

        final Long org2Id = 88L;
        final String org2BannerId = "slkdsdddjf";
        final String org2ShortName = "SHORT2";
        final String org2Name = "NAME2";

        Person p1 = new Person();
        Person p2 = new Person();

        EntityTestHelper.setPersonParentOrgId(p1, org1Id);
        EntityTestHelper.setPersonParentOrgId(p2, org2Id);

        ArrayList<OrganizationChild> people = new ArrayList<OrganizationChild>();
        people.add(p1);
        people.add(p2);

        final ArrayList<Long> expectedOrgIds = new ArrayList<Long>();
        expectedOrgIds.add(org1Id);
        expectedOrgIds.add(org2Id);

        final List<OrganizationModelView> cachedOrgModelViews = new ArrayList<OrganizationModelView>();

        final OrganizationModelView orgMv1 = new OrganizationModelView();
        orgMv1.setEntityId(org1Id);
        orgMv1.setBannerId(org1BannerId);
        orgMv1.setShortName(org1ShortName);
        orgMv1.setName(org1Name);

        final OrganizationModelView orgMv2 = new OrganizationModelView();
        orgMv2.setEntityId(org2Id);
        orgMv2.setBannerId(org2BannerId);
        orgMv2.setShortName(org2ShortName);
        orgMv2.setName(org2Name);

        cachedOrgModelViews.add(orgMv1);
        cachedOrgModelViews.add(orgMv2);

        // assert that the sut will ask for the private groups
        context.checking(new Expectations()
        {
            {
                oneOf(orgModelViewCacheMock).execute(with(equal(expectedOrgIds)));
                will(returnValue(cachedOrgModelViews));
            }
        });

        // sut
        sut.populateParentOrgSkeletons(people);

        // assert
        assertEquals((long) org1Id, p1.getParentOrganization().getId());
        assertEquals(org1BannerId, p1.getParentOrganization().getBannerId());
        assertEquals(org1Name, p1.getParentOrganization().getName());
        assertEquals(org1ShortName.toLowerCase(), p1.getParentOrganization().getShortName());

        assertEquals((long) org2Id, p2.getParentOrganization().getId());
        assertEquals(org2BannerId, p2.getParentOrganization().getBannerId());
        assertEquals(org2Name, p2.getParentOrganization().getName());
        assertEquals(org2ShortName.toLowerCase(), p2.getParentOrganization().getShortName());
    }
}
