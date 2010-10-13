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

import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.profile.UpdateOrganizationNameRequest;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetRecursiveOrgCoordinators;
import org.eurekastreams.server.persistence.mappers.cache.ClearPrivateGroupIdsViewableByCoordinatorCacheOnOrgUpdate;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the CreateOrganizationAction.
 */
public class OrganizationUpdaterTest
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
     * Person id of the user.
     */
    private static final long USER_PERSON_ID = 382717L;

    /**
     * Mocked person for the user.
     */
    private Person userPerson = context.mock(Person.class);

    /**
     * The mock org mapper to be used by the action.
     */
    private OrganizationMapper orgMapperMock = context.mock(OrganizationMapper.class);

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
     * Principal.
     */
    private Principal userPrincipal = context.mock(Principal.class);

    /**
     * The mock person mapper to be used by the action.
     */
    private PersonMapper personMapperMock = context.mock(PersonMapper.class);

    /**
     * The mock cached org mapper to be used by the action.
     */
    private DomainMapper<List<Long>, List<OrganizationModelView>> cachedOrgMapperMock = context
            .mock(DomainMapper.class);

    /**
     * Mapper to get all the coordinators of an org, traversing up the tree.
     */
    private GetRecursiveOrgCoordinators orgPermissionsChecker = context.mock(GetRecursiveOrgCoordinators.class);

    /**
     * Mocked ClearActivityStreamSearchStringCacheOnOrgUpdate.
     */
    private ClearPrivateGroupIdsViewableByCoordinatorCacheOnOrgUpdate
    // line break
    orgCoordinatorActivityStreamSearchStringClearer = context
            .mock(ClearPrivateGroupIdsViewableByCoordinatorCacheOnOrgUpdate.class);

    /**
     * The subject under test.
     */
    private OrganizationUpdater sut;

    /**
     * Setup sut before each test.
     */
    @Before
    public void setup()
    {
        sut = new OrganizationUpdater(orgMapperMock, personMapperMock, cachedOrgMapperMock,
                orgCoordinatorActivityStreamSearchStringClearer);

        userActionRequests = new ArrayList<UserActionRequest>();

        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getUserActionRequests();
                will(returnValue(userActionRequests));

                allowing(actionContext).getPrincipal();
                will(returnValue(userPrincipal));

                allowing(userPrincipal).getId();
                will(returnValue(USER_PERSON_ID));
            }
        });
    }

    /**
     * Build an organization based on the input form being fully filled out with valid data.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void getSuccess() throws Exception
    {
        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        final long id = 1L;
        formData.put("id", Long.toString(id));
        final Organization expectedOrg = new Organization("newOrg", "newOrg");
        final String accessingUser = "jschmoe";
        expectedOrg.addCoordinator(new Person(accessingUser, "b", "c", "d", "e"));

        context.checking(new Expectations()
        {
            {
                oneOf(orgMapperMock).findById(with(any(Long.class)));
                will(returnValue(expectedOrg));

                oneOf(orgCoordinatorActivityStreamSearchStringClearer).execute(id);
            }
        });

        try
        {
            Organization returnedOrg = sut.get(taskHandlerActionContext, formData);
            context.assertIsSatisfied();
            assertEquals(expectedOrg, returnedOrg);
        }
        catch (Exception e)
        {
            fail(e + ": something bad happened while getting");
        }

    }

    /**
     * Build an organization based on the input form being fully filled out with valid data.
     *
     * @throws Exception
     *             not expected
     */
    @SuppressWarnings("unchecked")
    @Test
    public void persistSuccess() throws Exception
    {
        final Organization newOrg = new Organization("newOrg", "newOrg");
        newOrg.setCapabilities(Arrays.asList(new BackgroundItem("banana", BackgroundItemType.CAPABILITY)));
        newOrg.setCoordinators(new HashSet<Person>());

        final List<Person> attribMembers = new ArrayList<Person>();
        attribMembers.add(new Person("id", "Homer", "Jay", "Simpson", "Homey"));
        attribMembers.add(new Person("id", "Max", "X", "Power", "Homer"));

        final List<Person> groupMembers = new ArrayList<Person>();
        groupMembers.add(new Person("id", "Homer", "Jay", "Simpson", "Homey"));
        groupMembers.add(new Person("id", "Max", "X", "Power", "Homer"));

        final long id = 1L;
        String newName = "NEW org name here";

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("id", Long.toString(id));
        formData.put("name", newName);
        formData.put("membershipcriteria", "somegroup");
        formData.put("keywords", " \t ,,, pathologically   horribly,,\tformatted,,keyword\tlist  \t");

        final List<OrganizationModelView> orgs = new ArrayList<OrganizationModelView>();
        OrganizationModelView org = new OrganizationModelView();
        org.setName("newOrg");
        orgs.add(org);

        context.checking(new Expectations()
        {
            {
                oneOf(cachedOrgMapperMock).execute(with(any(List.class)));
                will(returnValue(orgs));

                allowing(orgMapperMock).flush();
                allowing(personMapperMock).findByAccountId(with(any(String.class)));
                allowing(personMapperMock).flush();
            }
        });

        assertEquals(0, userActionRequests.size());

        sut.persist(taskHandlerActionContext, formData, newOrg);
        context.assertIsSatisfied();

        assertEquals(2, userActionRequests.size());
        assertEquals("updateCachedOrganizationNameAction", userActionRequests.get(0).getActionKey());
        assertEquals("newOrg", ((UpdateOrganizationNameRequest) userActionRequests.get(0).getParams())
                .getNewOrganizationName());

        assertEquals("organizationCacheUpdaterAsyncAction", userActionRequests.get(1).getActionKey());

        // TODO develop some nice helpers to make this cleaner
        assertTrue(Matchers.hasItem(
                equalInternally(new BackgroundItem("pathologically horribly", BackgroundItemType.CAPABILITY))).matches(
                newOrg.getCapabilities()));
        assertTrue(Matchers.hasItem(equalInternally(new BackgroundItem("formatted", BackgroundItemType.CAPABILITY)))
                .matches(newOrg.getCapabilities()));
        assertTrue(Matchers.hasItem(equalInternally(new BackgroundItem("keyword list", BackgroundItemType.CAPABILITY)))
                .matches(newOrg.getCapabilities()));
        assertFalse(Matchers.hasItem(equalInternally(new BackgroundItem("banana", BackgroundItemType.CAPABILITY)))
                .matches(newOrg.getCapabilities()));

    }
}
