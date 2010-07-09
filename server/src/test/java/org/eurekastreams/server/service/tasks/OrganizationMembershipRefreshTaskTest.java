/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.tasks;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverser;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverserBuilder;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.FindSystemSettings;
import org.eurekastreams.server.service.actions.strategies.OrganizationPopulator;

/**
 * Tests the Organization Membership Refresh class.
 * 
 */
public class OrganizationMembershipRefreshTaskTest
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
     * The system under test class.
     */
    private OrganizationMembershipRefreshTask sut;

    /**
     * The mock Organization Mapper to use.
     */
    private OrganizationMapper organizationMapperMock = context.mock(OrganizationMapper.class);

    /**
     * the mock Organiozation Populator class to use.
     */
    private OrganizationPopulator organizationPopulatorMock = context.mock(OrganizationPopulator.class);

    /**
     * The mock of the organization hierachy traverser builder.
     */
    private OrganizationHierarchyTraverserBuilder organizationTraverserBuilderMock = context
            .mock(OrganizationHierarchyTraverserBuilder.class);

    /**
     * Org traverser built by the org traverser builder.
     */
    private final OrganizationHierarchyTraverser organizationTraverser = context
            .mock(OrganizationHierarchyTraverser.class);

    /**
     * Find system settings mapper.
     */
    private final FindSystemSettings settingsMapper = context.mock(FindSystemSettings.class);

    /**
     * The mock org mapper to be used by the action.
     */
    private PersonMapper personMapperMock = context.mock(PersonMapper.class);

    /**
     * The mock Organization first tier.
     */
    private Organization rootOrganizationMock = context.mock(Organization.class, "rootOrgMock");

    /**
     * The mock Organization first tier.
     */
    private Organization org1Mock = context.mock(Organization.class, "org1Mock");

    /**
     * The mock Organization second tier.
     */
    private Organization org2aMock = context.mock(Organization.class, "org2aMock");

    /**
     * The mock Organization second tier.
     */
    private Organization org2bMock = context.mock(Organization.class, "org2bMock");

    /**
     * The mock Organization second tier.
     */
    private Organization org2cMock = context.mock(Organization.class, "org2cMock");

    /**
     * The mock Organization third tier first child of 2a.
     */
    private Organization org3a1Mock = context.mock(Organization.class, "org3a1Mock");

    /**
     * The mock Organization third tier second child of 2a.
     */
    private Organization org3a2Mock = context.mock(Organization.class, "org3a2Mock");

    /**
     * The mock Organization third tier first and only child of 2c.
     */
    private Organization org3c1Mock = context.mock(Organization.class, "org3c1Mock");

    /**
     * A person mock.
     */
    private Person person1Mock = context.mock(Person.class, "person1");

    /**
     * List of related Organizations mock.
     */
    @SuppressWarnings("unchecked")
    private List<Organization> relatedOrganizationsMock = context.mock(List.class);

    /**
     * 
     * @throws Exception
     *             not expected
     */
    @Before
    public final void setUp() throws Exception
    {
        final Sequence preOrderTreeTraversal = context.sequence("preOrderTreeTraversal");

        // Setup the Root organization children
        final List<Organization> rootChildOrganizations = new ArrayList<Organization>();
        rootChildOrganizations.add(org1Mock);
        // Setup the Org 1 children
        final List<Organization> org1ChildOrganizations = new ArrayList<Organization>();
        org1ChildOrganizations.add(org2aMock);
        org1ChildOrganizations.add(org2bMock);
        org1ChildOrganizations.add(org2cMock);
        // Setup the Org 2a children.
        final List<Organization> org2aChildOrganizations = new ArrayList<Organization>();
        org2aChildOrganizations.add(org3a1Mock);
        org2aChildOrganizations.add(org3a2Mock);

        // Setup the Org 2b children.
        final List<Organization> org2bChildOrganizations = new ArrayList<Organization>();

        // Setup the Org 2c children.
        final List<Organization> org2cChildOrganizations = new ArrayList<Organization>();
        org2cChildOrganizations.add(org3c1Mock);

        // Setup the Org 3a1 children.
        final List<Organization> org3a1ChildOrganizations = new ArrayList<Organization>();
        // Setup the Org 3a2 children.
        final List<Organization> org3a2ChildOrganizations = new ArrayList<Organization>();
        // Setup the Org 3c1 children.
        final List<Organization> org3c1ChildOrganizations = new ArrayList<Organization>();

        context.checking(new Expectations()
        {
            {
                // Allowing method executions build up the data for the test runs

                // Setup the person objects
                allowing(person1Mock).getAccountId();
                will(returnValue("bsmith"));

                // Setup the root organization
                allowing(organizationMapperMock).getRootOrganization();
                will(returnValue(rootOrganizationMock));

                // Formal expectations for all tests

                // The organization traverser builder
                oneOf(organizationTraverserBuilderMock).getOrganizationHierarchyTraverser();
                will(returnValue(organizationTraverser));

                // Purging each person of their related organizations
                oneOf(personMapperMock).purgeRelatedOrganizations();


                oneOf(settingsMapper).execute(null);
                
                oneOf(organizationMapperMock).updateOrganizationStatistics(organizationTraverser);
            }
        });

        // To provide coverage for the no parameter constructor method needed for Spring.
        sut = new OrganizationMembershipRefreshTask();

        // Actually constructor.
        sut = new OrganizationMembershipRefreshTask(organizationMapperMock, organizationPopulatorMock,
                personMapperMock, organizationTraverserBuilderMock, settingsMapper);
    }

    /**
     * This test has a person assigned to the Root organization with no related organizations. This person will not have
     * any operations performed on them.
     * 
     * @throws Exception
     *             not expected to occur.
     */
    @Test
    public final void testPersonWithRootOrgParentNoRelatedOrgs() throws Exception
    {
        final List<Person> orphanedPeople = new ArrayList<Person>();
        orphanedPeople.add(person1Mock);

        context.checking(new Expectations()
        {
            {
                oneOf(personMapperMock).findOrphanedPeople();
                will(returnValue(orphanedPeople));
                
                oneOf(person1Mock).setAccountLocked(true);

                oneOf(personMapperMock).flush();
            }

        });

        sut.execute();
        context.assertIsSatisfied();
    }
}
