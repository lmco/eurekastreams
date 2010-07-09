/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.execution.CreatePersonActionFactory;
import org.eurekastreams.server.action.execution.PersistResourceExecution;
import org.eurekastreams.server.action.execution.PersonLookupUtilityStrategy;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverser;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverserBuilder;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the CreateOrganizationAction.
 */
public class OrganizationPopulatorTest
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
     * The current user's account id.
     */
    private String accountId = "sdlkfjsdlfjs";

    /**
     * Mocked person lookup by group.
     */
    private PersonLookupUtilityStrategy groupLookup = context
            .mock(PersonLookupUtilityStrategy.class, "lookup by group");

    /**
     * task handler action context.
     */
    private TaskHandlerActionContext<PrincipalActionContext> taskHandlerActionContext = context
            .mock(TaskHandlerActionContext.class);

    /**
     * Action context.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);
    /**
     * Collection to hold action requests queued up for async processing.
     */
    private List<UserActionRequest> userActionRequests = new ArrayList<UserActionRequest>();

    /**
     * Current user principal.
     */
    private Principal userPrincipal = context.mock(Principal.class);
    /**
     * Mocked person lookup by group.
     */
    private PersonLookupUtilityStrategy attribLookup = context.mock(PersonLookupUtilityStrategy.class,
            "lookup by attrib");

    /**
     * The mock org mapper to be used by the action.
     */
    private OrganizationMapper orgMapperMock = context.mock(OrganizationMapper.class);

    /**
     * The mock org mapper to be used by the action.
     */
    private PersonMapper personMapperMock = context.mock(PersonMapper.class);

    /**
     * The mock CreatePersonActionFactory to be used.
     */
    private CreatePersonActionFactory personActionFactoryMock = context.mock(CreatePersonActionFactory.class);

    /**
     * The mock of the organization hierachy traverser builder.
     */
    private OrganizationHierarchyTraverserBuilder orgTraverserBuilderMock = context
            .mock(OrganizationHierarchyTraverserBuilder.class);

    /**
     * The organization populator.
     */
    private OrganizationPopulator sut;

    /**
     * The organization populator; the system under test.
     */
    private HashMap<String, Serializable> personDataMock = context.mock(HashMap.class);

    /**
     * The persist resource action mock.
     */
    private PersistResourceExecution<Person> persistResourceActionMock = context.mock(PersistResourceExecution.class);

    /**
     * A person mock; existing person.
     */
    private Person person1 = context.mock(Person.class, "person1");

    /**
     * A person mock; existing person.
     */
    private Person person2 = context.mock(Person.class, "person2");

    /**
     * A person mock; new person.
     */
    private Person person3 = context.mock(Person.class, "person3");

    /**
     * A person mock; new person.
     */
    private Person person4 = context.mock(Person.class, "person4");

    /**
     * The mock Organization to be used as the Root Organization.
     */
    private Organization rootOrganizationMock = context.mock(Organization.class, "rootOrganization");

    /**
     * The mock Organization to be used as a level 1 organization.
     */
    private Organization l1OrganizationMock = context.mock(Organization.class, "l1Organization");

    /**
     * The mock Organization to be used as a level 2 a organization.
     */
    private Organization l2aOrganizationMock = context.mock(Organization.class, "l2aOrganization");

    /**
     * The mock Organization to be used as a level 2 b organization.
     */
    private Organization l2bOrganizationMock = context.mock(Organization.class, "l2bOrganization");

    /**
     * The mock Organization to be used as the created organization.
     */
    private Organization createdOrganizationMock = context.mock(Organization.class, "createdOrganization");

    /**
     * A list of people.
     */
    private List<Person> people = new LinkedList<Person>();

    /**
     * Org traverser built by the org traverser builder.
     */
    private final OrganizationHierarchyTraverser orgTraverser = context.mock(OrganizationHierarchyTraverser.class);

    /**
     *
     * @throws Exception
     *             not expected
     */
    @Before
    public final void setUp() throws Exception
    {
        people.add(person1);
        people.add(person2);
        people.add(person3);
        people.add(person4);

        userActionRequests = new ArrayList<UserActionRequest>();

        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getUserActionRequests();
                will(returnValue(userActionRequests));

                allowing(actionContext).getPrincipal();
                will(returnValue(userPrincipal));

                allowing(userPrincipal).getAccountId();
                will(returnValue(accountId));

                one(orgTraverserBuilderMock).getOrganizationHierarchyTraverser();
                will(returnValue(orgTraverser));

                oneOf(personActionFactoryMock).getCreatePersonAction(with(any(PersonMapper.class)),
                        with(any(UpdaterStrategy.class)));
                will(returnValue(persistResourceActionMock));

                allowing(persistResourceActionMock).execute(with(any(TaskHandlerActionContext.class)));

                // Setup the person objects
                allowing(person1).getAccountId();
                will(returnValue("joe"));
                allowing(person2).getAccountId();
                will(returnValue("pete"));
                allowing(person3).getAccountId();
                will(returnValue("al"));
                allowing(person4).getAccountId();
                will(returnValue("bill"));

                // Setup the organization objects
                // Setup the organization Ids
                allowing(rootOrganizationMock).getId();
                will(returnValue(1L));
                allowing(l1OrganizationMock).getId();
                will(returnValue(2L));
                allowing(l2aOrganizationMock).getId();
                will(returnValue(3L));
                allowing(l2bOrganizationMock).getId();
                will(returnValue(4L));

                // Setup the organization parent organizations
                allowing(orgMapperMock).getRootOrganization();
                will(returnValue(rootOrganizationMock));
                allowing(l1OrganizationMock).getParentOrganization();
                will(returnValue(rootOrganizationMock));
                allowing(l2aOrganizationMock).getParentOrganization();
                will(returnValue(l1OrganizationMock));
                allowing(l2bOrganizationMock).getParentOrganization();
                will(returnValue(l1OrganizationMock));

                // Setup the created organization parent organization
                allowing(createdOrganizationMock).getParentOrganization();
                will(returnValue(l2aOrganizationMock));

                // Setup the placement of the people in organizations
                allowing(person1).getParentOrganization();
                will(returnValue(l1OrganizationMock));
                allowing(person2).getParentOrganization();
                will(returnValue(l2aOrganizationMock));
                allowing(person4).getParentOrganization();
                will(returnValue(rootOrganizationMock));

                // Finding the people
                oneOf(personMapperMock).findByAccountId("joe");
                will(returnValue(person1));
                oneOf(personMapperMock).findByAccountId("pete");
                will(returnValue(person2));
                oneOf(personMapperMock).findByAccountId("al");
                will(returnValue(null));
                oneOf(personMapperMock).findByAccountId("bill");
                will(returnValue(person4));

                // Move person2 since the created organization is a child organization of person2's parent organization
                oneOf(person2).setParentOrganization(createdOrganizationMock);

                // Move person4 since person4 is assigned to the Root Organization
                oneOf(person4).setParentOrganization(createdOrganizationMock);

                // We will not move person1 since person1's parent organization is not the Root Organization and it
                // is not the parent organization of the created organizations

                // Set the related organizations for all three existing people
                oneOf(person1).addRelatedOrganization(createdOrganizationMock);
                oneOf(person2).addRelatedOrganization(createdOrganizationMock);
                oneOf(person4).addRelatedOrganization(createdOrganizationMock);

                // Set the properties of person3 (new person)
                oneOf(person3).getProperties(Boolean.FALSE);
                will(returnValue(personDataMock));
                oneOf(personDataMock).put("organization", createdOrganizationMock);

                // Update the organization statistics
                oneOf(orgMapperMock).updateOrganizationStatistics(orgTraverser);
            }
        });

        sut = new OrganizationPopulator(orgMapperMock, personMapperMock, groupLookup, attribLookup,
                personActionFactoryMock, orgTraverserBuilderMock);
    }

    /**
     * Build an organization based on the input form being fully filled out with valid data.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void populateWithGroupLookupWithoutException() throws Exception
    {

        context.checking(new Expectations()
        {
            {
                oneOf(groupLookup).getPeople(with(any(String.class)), with(any(Integer.class)));
                will(returnValue(people));

                one(orgTraverser).traverseHierarchy(createdOrganizationMock);

                one(orgTraverser).traverseHierarchy(person2);
                one(orgTraverser).traverseHierarchy(person4);

                oneOf(person1).setAccountLocked(false);
                oneOf(person2).setAccountLocked(false);
                oneOf(person4).setAccountLocked(false);
            }
        });

        sut.populate("LDAPgrouphere", createdOrganizationMock, taskHandlerActionContext);
        context.assertIsSatisfied();
    }

    /**
     * Build an organization based on the input form being fully filled out with valid data.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void populateWithAttribLookupWithoutException() throws Exception
    {

        context.checking(new Expectations()
        {
            {
                oneOf(attribLookup).getPeople(with(any(String.class)), with(any(Integer.class)));
                will(returnValue(people));

                one(orgTraverser).traverseHierarchy(createdOrganizationMock);

                one(orgTraverser).traverseHierarchy(person2);
                one(orgTraverser).traverseHierarchy(person4);

                oneOf(person1).setAccountLocked(false);
                oneOf(person2).setAccountLocked(false);
                oneOf(person4).setAccountLocked(false);

            }
        });

        sut.populate("LDAP=grouphere", createdOrganizationMock, taskHandlerActionContext);
        context.assertIsSatisfied();
    }

}
