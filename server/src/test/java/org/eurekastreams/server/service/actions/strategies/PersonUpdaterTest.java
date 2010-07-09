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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.execution.profile.UpdatePersonDisplayNameCaches;
import org.eurekastreams.server.domain.EntityTestHelper;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the create person strategy.
 */
@SuppressWarnings("unchecked")
public class PersonUpdaterTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private PersonUpdater sut;

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
     * Person Mapper Mock.
     */
    private PersonMapper personMapperMock = context.mock(PersonMapper.class);

    /**
     * {@link Organization}.
     */
    private Organization orgMock = context.mock(Organization.class);

    /**
     * Class to get the task actions for queue updates when display name changes.
     */
    private UpdatePersonDisplayNameCaches updatePersonCachesMock = context.mock(UpdatePersonDisplayNameCaches.class);

    /**
     * Setup fixtures.
     */
    @Before
    public final void setUp()
    {
        sut = new PersonUpdater(personMapperMock, updatePersonCachesMock);
    }

    /**
     * Test the get method.
     */
    @Test
    public final void testGet()
    {
        final Person testPerson = context.mock(Person.class);

        userActionRequests = new ArrayList<UserActionRequest>();

        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getUserActionRequests();
                will(returnValue(userActionRequests));

                oneOf(personMapperMock).findByAccountId("accountid");
                will(returnValue(testPerson));

                allowing(testPerson).getDisplayName();
                will(returnValue("displayName"));

                oneOf(testPerson).getParentOrganization();
                will(returnValue(orgMock));

                oneOf(orgMock).getShortName();
                will(returnValue("orgShortName"));
            }
        });

        Map<String, Serializable> fields = new HashMap<String, Serializable>();
        fields.put("accountId", "accountid");

        sut.get(null, fields);
    }

    /**
     * Test the persist method with the same display name.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public final void testPersistWithSameDisplayName() throws Exception
    {
        final Person testPerson = new Person("accountid", "First", "Middle", "Last", "Preferred");

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
        formData.put(PersonUpdater.ORIGINAL_DISPLAY_NAME_KEY, "Preferred Last");

        context.checking(new Expectations()
        {
            {
                oneOf(personMapperMock).flush();
            }
        });

        sut.persist(taskHandlerActionContext, formData, testPerson);
        context.assertIsSatisfied();
    }

    /**
     * Test the persist method with a new display name.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public final void testPersistWithChangingDisplayName() throws Exception
    {
        final String originalDisplayedName = "Preferred";
        final Long personId = 89348L;

        final Person testPerson = new Person("accountid", "First", "Middle", "Last", "New-Preferred");
        EntityTestHelper.setPersonId(testPerson, personId);

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
        formData.put(PersonUpdater.ORIGINAL_DISPLAY_NAME_KEY, originalDisplayedName + " Last");

        final List<UserActionRequest> getUpdateCacheRequests = new ArrayList<UserActionRequest>();
        final Principal userPrinicpal = context.mock(Principal.class);

        final List<UserActionRequest> taskActionRequests = new ArrayList<UserActionRequest>();
        final UserActionRequest cacheUpdatingActionRequest = new UserActionRequest("foo", null, null);
        taskActionRequests.add(cacheUpdatingActionRequest);

        context.checking(new Expectations()
        {
            {
                oneOf(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                oneOf(actionContext).getPrincipal();
                will(returnValue(userPrinicpal));

                oneOf(updatePersonCachesMock).getUpdateCacheRequests(with(userPrinicpal), with(personId));
                will(returnValue(getUpdateCacheRequests));

                oneOf(taskHandlerActionContext).getUserActionRequests();
                will(returnValue(taskActionRequests));

                oneOf(personMapperMock).flush();
            }
        });

        sut.persist(taskHandlerActionContext, formData, testPerson);

        assertEquals(1, taskActionRequests.size());
        assertSame(cacheUpdatingActionRequest, taskActionRequests.get(0));

        context.assertIsSatisfied();
    }
}
