/*
 * Copyright (c) 2010-2013 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.SetPersonLockedStatusRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for RefreshPeopleExecution.
 * 
 */
public class RefreshPeopleExecutionTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Source of person information for users.
     */
    private final PersonSource source = context.mock(PersonSource.class);

    /**
     * Action key for create action to be called for new user.
     */
    private final String createPersonActionKey = null;

    /**
     * Action key for set status action to be called for users.
     */
    private final String lockPersonActionKey = null;

    /**
     * {@link GetNonLockedPersonIds}.
     */
    private final DomainMapper<Boolean, List<String>> personIdsByLockedStatusDAO = context.mock(DomainMapper.class,
            "personIdsByLockedStatusDAO");

    /**
     * The settings mapper.
     */
    private final DomainMapper<MapperRequest, SystemSettings> settingsMapper = context.mock(DomainMapper.class,
            "settingsMapper");

    /**
     * Person.
     */
    private final Person person1 = context.mock(Person.class, "person1");

    /**
     * Person.
     */
    private final Person person2 = context.mock(Person.class, "person2");

    /**
     * Person.
     */
    private final Person person3 = context.mock(Person.class, "person3");

    /**
     * {@link SystemSettings}.
     */
    private final SystemSettings settings = context.mock(SystemSettings.class);

    /**
     * {@link TaskHandlerActionContext}.
     */
    private final TaskHandlerActionContext actionContext = context.mock(TaskHandlerActionContext.class);

    /**
     * System under test.
     */
    private final RefreshPeopleExecution sut = new RefreshPeopleExecution(source, "create", "lock", "refresh",
            personIdsByLockedStatusDAO, settingsMapper);

    /**
     * Test.
     */
    @Test
    public void testCreate()
    {
        final Set<Person> people = new HashSet<Person>();
        final List<UserActionRequest> list = new ArrayList<UserActionRequest>();

        people.add(person1);
        people.add(person2);
        people.add(person3);

        context.checking(new Expectations()
        {
            {
                allowing(source).getPeople();
                will(returnValue(people));

                allowing(personIdsByLockedStatusDAO).execute(false);
                will(returnValue(new ArrayList<String>()));

                allowing(personIdsByLockedStatusDAO).execute(true);
                will(returnValue(new ArrayList<String>()));

                allowing(settingsMapper).execute(null);
                will(returnValue(settings));

                allowing(settings).getSendWelcomeEmails();
                will(returnValue(false));

                allowing(person3).getAccountId();
                will(returnValue("p3"));

                allowing(person2).getAccountId();
                will(returnValue("p2"));

                allowing(person1).getAccountId();
                will(returnValue("p1"));

                allowing(person3).getDisplayName();
                will(returnValue("p3"));

                allowing(person2).getDisplayName();
                will(returnValue("p2"));

                allowing(person1).getDisplayName();
                will(returnValue("p1"));

                allowing(actionContext).getUserActionRequests();
                will(returnValue(list));
            }
        });

        sut.execute(actionContext);

        // should be 3 creates
        assertEquals(3, list.size());
        assertEquals("create", list.get(0).getActionKey());

        context.assertIsSatisfied();
    }

    /**
     * Same as above test, only create disabled so should have no actions in queue list.
     */
    @Test
    public void testCreateWithCreateDisabled()
    {
        RefreshPeopleExecution tempSut = new RefreshPeopleExecution(source, "", "lock", "refresh",
                personIdsByLockedStatusDAO, settingsMapper);

        final Set<Person> people = new HashSet<Person>();
        final List<UserActionRequest> list = new ArrayList<UserActionRequest>();

        people.add(person1);
        people.add(person2);
        people.add(person3);

        context.checking(new Expectations()
        {
            {
                allowing(source).getPeople();
                will(returnValue(people));

                allowing(personIdsByLockedStatusDAO).execute(false);
                will(returnValue(new ArrayList<String>()));

                allowing(personIdsByLockedStatusDAO).execute(true);
                will(returnValue(new ArrayList<String>()));

                allowing(settingsMapper).execute(null);
                will(returnValue(settings));

                allowing(settings).getSendWelcomeEmails();
                will(returnValue(false));

                allowing(person3).getAccountId();
                will(returnValue("p3"));

                allowing(person2).getAccountId();
                will(returnValue("p2"));

                allowing(person1).getAccountId();
                will(returnValue("p1"));

                allowing(person3).getDisplayName();
                will(returnValue("p3"));

                allowing(person2).getDisplayName();
                will(returnValue("p2"));

                allowing(person1).getDisplayName();
                will(returnValue("p1"));

                allowing(actionContext).getUserActionRequests();
                will(returnValue(list));
            }
        });

        tempSut.execute(actionContext);

        assertEquals(0, list.size());

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testUnlock()
    {
        final Set<Person> people = new HashSet<Person>();
        final ArrayList<String> unlocked = new ArrayList(Arrays.asList("p1", "p2"));
        final ArrayList<String> locked = new ArrayList(Arrays.asList("p3"));
        final List<UserActionRequest> list = new ArrayList<UserActionRequest>();

        people.add(person1);
        people.add(person2);
        people.add(person3);

        context.checking(new Expectations()
        {
            {
                allowing(source).getPeople();
                will(returnValue(people));

                allowing(personIdsByLockedStatusDAO).execute(false);
                will(returnValue(unlocked));

                allowing(personIdsByLockedStatusDAO).execute(true);
                will(returnValue(locked));

                allowing(settingsMapper).execute(null);
                will(returnValue(settings));

                allowing(settings).getSendWelcomeEmails();
                will(returnValue(false));

                allowing(person3).getAccountId();
                will(returnValue("p3"));

                allowing(person2).getAccountId();
                will(returnValue("p2"));

                allowing(person1).getAccountId();
                will(returnValue("p1"));

                allowing(person3).getDisplayName();
                will(returnValue("p3"));

                allowing(person2).getDisplayName();
                will(returnValue("p2"));

                allowing(person1).getDisplayName();
                will(returnValue("p1"));

                allowing(actionContext).getUserActionRequests();
                will(returnValue(list));
            }
        });

        sut.execute(actionContext);

        assertEquals(4, list.size()); // 4 requests - refreshing p1, p2, and p3, and unlocking p3

        HashMap<String, UserActionRequest> listOfRequests = new HashMap<String, UserActionRequest>();
        listOfRequests.put(list.get(0).getActionKey(), list.get(0));
        listOfRequests.put(list.get(1).getActionKey(), list.get(1));
        listOfRequests.put(list.get(2).getActionKey(), list.get(2));
        listOfRequests.put(list.get(3).getActionKey(), list.get(3));
        assertTrue(listOfRequests.containsKey("lock"));
        assertTrue(listOfRequests.containsKey("refresh"));

        SetPersonLockedStatusRequest splsr = (SetPersonLockedStatusRequest) listOfRequests.get("lock").getParams();

        assertEquals(false, splsr.getLockedStatus());

        context.assertIsSatisfied();
    }

    /**
     * Same as above test, only lock/unlock disabled so should have no actions in queue list.
     */
    @Test
    public void testUnlockWithUnlockDisabled()
    {
        RefreshPeopleExecution tempSut = new RefreshPeopleExecution(source, "create", "", "refresh",
                personIdsByLockedStatusDAO, settingsMapper);

        final Set<Person> people = new HashSet<Person>();
        final ArrayList<String> unlocked = new ArrayList(Arrays.asList("p1", "p2"));
        final ArrayList<String> locked = new ArrayList(Arrays.asList("p3"));
        final List<UserActionRequest> list = new ArrayList<UserActionRequest>();

        people.add(person1);
        people.add(person2);
        people.add(person3);

        context.checking(new Expectations()
        {
            {
                allowing(source).getPeople();
                will(returnValue(people));

                allowing(personIdsByLockedStatusDAO).execute(false);
                will(returnValue(unlocked));

                allowing(personIdsByLockedStatusDAO).execute(true);
                will(returnValue(locked));

                allowing(settingsMapper).execute(null);
                will(returnValue(settings));

                allowing(settings).getSendWelcomeEmails();
                will(returnValue(false));

                allowing(person3).getAccountId();
                will(returnValue("p3"));

                allowing(person2).getAccountId();
                will(returnValue("p2"));

                allowing(person1).getAccountId();
                will(returnValue("p1"));

                allowing(person3).getDisplayName();
                will(returnValue("p3"));

                allowing(person2).getDisplayName();
                will(returnValue("p2"));

                allowing(person1).getDisplayName();
                will(returnValue("p1"));

                allowing(actionContext).getUserActionRequests();
                will(returnValue(list));
            }
        });

        tempSut.execute(actionContext);

        assertEquals(3, list.size());

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testLock()
    {
        final Set<Person> people = new HashSet<Person>();
        final ArrayList<String> unlocked = new ArrayList(Arrays.asList("p1", "p2", "p3"));
        final ArrayList<String> locked = new ArrayList();
        final List<UserActionRequest> list = new ArrayList<UserActionRequest>();

        people.add(person1);
        people.add(person2);

        context.checking(new Expectations()
        {
            {
                allowing(source).getPeople();
                will(returnValue(people));

                allowing(personIdsByLockedStatusDAO).execute(false);
                will(returnValue(unlocked));

                allowing(personIdsByLockedStatusDAO).execute(true);
                will(returnValue(locked));

                allowing(settingsMapper).execute(null);
                will(returnValue(settings));

                allowing(settings).getSendWelcomeEmails();
                will(returnValue(false));

                allowing(person3).getAccountId();
                will(returnValue("p3"));

                allowing(person2).getAccountId();
                will(returnValue("p2"));

                allowing(person1).getAccountId();
                will(returnValue("p1"));

                allowing(person3).getDisplayName();
                will(returnValue("p3"));

                allowing(person2).getDisplayName();
                will(returnValue("p2"));

                allowing(person1).getDisplayName();
                will(returnValue("p1"));

                allowing(actionContext).getUserActionRequests();
                will(returnValue(list));
            }
        });

        sut.execute(actionContext);

        assertEquals(3, list.size());
        boolean found = false;
        for (UserActionRequest uar : list)
        {
            if (uar.getActionKey() == "lock")
            {
                SetPersonLockedStatusRequest splsr = (SetPersonLockedStatusRequest) uar.getParams();
                assertEquals(true, splsr.getLockedStatus());
                found = true;
            }
        }
        assertTrue(found);
        context.assertIsSatisfied();
    }

    /**
     * Same as above test, only lock/unlock disabled so should have no actions in queue list.
     */
    @Test
    public void testLockWithLockDisabled()
    {
        RefreshPeopleExecution tempSut = new RefreshPeopleExecution(source, "create", "", "refresh",
                personIdsByLockedStatusDAO, settingsMapper);

        final Set<Person> people = new HashSet<Person>();
        final ArrayList<String> unlocked = new ArrayList(Arrays.asList("p1", "p2", "p3"));
        final ArrayList<String> locked = new ArrayList();
        final List<UserActionRequest> list = new ArrayList<UserActionRequest>();

        people.add(person1);
        people.add(person2);

        context.checking(new Expectations()
        {
            {
                allowing(source).getPeople();
                will(returnValue(people));

                allowing(personIdsByLockedStatusDAO).execute(false);
                will(returnValue(unlocked));

                allowing(personIdsByLockedStatusDAO).execute(true);
                will(returnValue(locked));

                allowing(settingsMapper).execute(null);
                will(returnValue(settings));

                allowing(settings).getSendWelcomeEmails();
                will(returnValue(false));

                allowing(person3).getAccountId();
                will(returnValue("p3"));

                allowing(person2).getAccountId();
                will(returnValue("p2"));

                allowing(person1).getAccountId();
                will(returnValue("p1"));

                allowing(person3).getDisplayName();
                will(returnValue("p3"));

                allowing(person2).getDisplayName();
                will(returnValue("p2"));

                allowing(person1).getDisplayName();
                will(returnValue("p1"));

                allowing(actionContext).getUserActionRequests();
                will(returnValue(list));
            }
        });

        tempSut.execute(actionContext);

        assertEquals(2, list.size());

        context.assertIsSatisfied();
    }
}
