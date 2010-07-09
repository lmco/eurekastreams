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
package org.eurekastreams.server.service.actions.strategies;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.cache.GetOrganizationLeaderIdsByOrgId;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByIds;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for OrganizationPersonLoaderLeaders class.
 * 
 */
public class OrganizationPersonLoaderLeadersTest
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
     * System under test.
     */
    private OrganizationPersonLoaderLeaders sut;

    /**
     * Organization leader id DAO.
     */
    private GetOrganizationLeaderIdsByOrgId organizationLeaderIdDAO = context
            .mock(GetOrganizationLeaderIdsByOrgId.class);

    /**
     * Person mapper mock.
     */
    private GetPeopleByIds getPeopleByIds = context.mock(GetPeopleByIds.class);

    /**
     * Organization mock.
     */
    private Organization organization = context.mock(Organization.class);

    /**
     * Pre-test setup.
     */
    @Before
    public void setUp()
    {
        sut = new OrganizationPersonLoaderLeaders(getPeopleByIds, organizationLeaderIdDAO);
    }

    /**
     * Test.
     */
    @Test
    public void testGetPersonIds()
    {
        final Set<Long> results = new HashSet<Long>(Arrays.asList(2L));
        context.checking(new Expectations()
        {
            {
                allowing(organization).getId();
                will(returnValue(1L));

                allowing(organizationLeaderIdDAO).execute(1L);
                will(returnValue(results));
            }
        });

        assertEquals(1, sut.getPersonIds(organization).size());
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testSetPeopleInOrganization()
    {
        final Set<Person> results = new HashSet<Person>(0);
        context.checking(new Expectations()
        {
            {
                allowing(organization).setLeaders(results);
            }
        });

        sut.setPeopleInOrganization(organization, results);
        context.assertIsSatisfied();
    }

}
