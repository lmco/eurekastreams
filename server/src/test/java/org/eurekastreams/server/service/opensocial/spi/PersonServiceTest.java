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
package org.eurekastreams.server.service.opensocial.spi;

import static junit.framework.Assert.assertNotNull;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.testing.FakeGadgetToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.opensocial.spi.UserId.Type;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.exceptions.GeneralException;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.eurekastreams.server.action.principal.PrincipalPopulatorTransWrapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * This class performs the test for the implementation of the Shindig PersonService interface.
 *
 */
public class PersonServiceTest
{

    /**
     * Object that is being tested.
     */
    private PersonServiceImpl sut;

    /**
     * List of people expected to be returned from the search for users by opensocial id.
     */
    private final List<org.eurekastreams.server.domain.Person> people = // \n
    new LinkedList<org.eurekastreams.server.domain.Person>();

    /**
     * This is a fake security token taken from Shindig for testing.
     */
    private static final SecurityToken FAKETOKEN = new FakeGadgetToken();

    /**
     * Constant string describing the opensocial id for a test user.
     */
    private static final String USERID_ONE = "123456";

    /**
     * Constant string describing the opensocial id for a test user.
     */
    private static final String USERID_TWO = "654321";

    /**
     * Constant string describing the id for a test group.
     */
    private static final String GROUPID = "654321";

    /**
     * Base url for creating user profile urls.
     */
    private static final String BASE_URL = "http://localhost:8080";

    /**
     * TLD for user account.
     */
    private static final String TLD = "example.com";

    /**
     * A test UserId object to be used during the tests.
     */
    private final UserId testId = new UserId(Type.userId, USERID_ONE);

    /**
     * A test UserId object to be used during the tests.
     */
    private final UserId testId2 = new UserId(Type.userId, USERID_TWO);

    /**
     * A test UserId object with no id specified. Yes, this seems odd, but "null" is what is passed in when the id is
     * not supplied from the container side.
     */
    private final UserId testNullId = new UserId(Type.userId, "null");
    /**
     * A test GroupId object to be used during the tests.
     */
    private final GroupId testGroupId = new GroupId(GroupId.Type.self, GROUPID);

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
     * The mock action to be used.
     */
    private final ServiceAction getPersonAction = context.mock(ServiceAction.class, "getPersonAction");

    /**
     * Mocked instance of the getPeopleAction.
     */
    private final ServiceAction getPeopleAction = context.mock(ServiceAction.class, "getPeopleAction");

    /**
     * Service Action Controller.
     */
    private final ServiceActionController serviceActionController = context.mock(ServiceActionController.class);

    /**
     * Principal populator.
     */
    private final PrincipalPopulatorTransWrapper principalPopulator = context
            .mock(PrincipalPopulatorTransWrapper.class);

    /**
     * {@link Principal}.
     */
    private final Principal principal = context.mock(Principal.class);

    /**
     * The Person object needs to be fully qualified because Shindig has a Person object as well.
     */
    private final org.eurekastreams.server.domain.Person eurekastreamsPerson = context
            .mock(org.eurekastreams.server.domain.Person.class);

    /**
     * Prepare the test.
     */
    @Before
    public void setUp()
    {
        sut = new PersonServiceImpl(getPersonAction, getPeopleAction, principalPopulator, serviceActionController,
                BASE_URL, TLD);
    }

    /**
     * Test the getPerson method in the PersonService implementation.
     *
     * @throws Exception
     *             - covers all exceptions
     */
    @Test
    public void testGetPerson() throws Exception
    {
        final Long testPersonId = new Long(123);
        final String testPersonAvatarId = "24234";
        final String testPersonAccountId = "joeyd";

        // Set up expectations
        context.checking(new Expectations()
        {
            {
                allowing(principalPopulator).getPrincipal(USERID_ONE);
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue(testPersonAccountId));

                allowing(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(returnValue(eurekastreamsPerson));

                oneOf(eurekastreamsPerson).getLastName();
                will(returnValue("validuser"));

                oneOf(eurekastreamsPerson).getOpenSocialId();
                will(returnValue("opensocialid-23432432-4324"));

                oneOf(eurekastreamsPerson).getFirstName();
                will(returnValue("iama"));

                oneOf(eurekastreamsPerson).getPreferredName();
                will(returnValue("iama"));

                oneOf(eurekastreamsPerson).getBiography();
                will(returnValue("my bio"));

                oneOf(eurekastreamsPerson).getId();
                will(returnValue(testPersonId.longValue()));

                oneOf(eurekastreamsPerson).getAvatarId();
                will(returnValue(testPersonAvatarId));

                allowing(eurekastreamsPerson).getAccountId();
                will(returnValue(testPersonAccountId));
            }
        });

        Person testPerson = sut.getPerson(testId, Person.Field.DEFAULT_FIELDS, FAKETOKEN).get();

        assertNotNull("Person is not found", testPerson);

        context.assertIsSatisfied();
    }

    /**
     * Test forcing an Exception.
     *
     * @throws Exception
     *             - covers all exceptions.
     */
    @Test(expected = ProtocolException.class)
    public void testGetPersonException() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(principalPopulator).getPrincipal(USERID_ONE);
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue("foo"));

                allowing(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new Exception()));
            }
        });

        sut.getPerson(testId, Person.Field.DEFAULT_FIELDS, FAKETOKEN).get();

        context.assertIsSatisfied();
    }

    /**
     * currentUser Test forcing a NumberFormatException.
     *
     * @throws Exception
     *             - covers all exceptions.
     */
    @Test(expected = ProtocolException.class)
    public void testGetPersonNumberFormatException() throws Exception
    {
        context.checking(new Expectations()
        {
            {

                allowing(principalPopulator).getPrincipal(USERID_ONE);
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue("foo"));

                allowing(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new NumberFormatException()));
            }
        });

        sut.getPerson(testId, Person.Field.DEFAULT_FIELDS, FAKETOKEN).get();

        context.assertIsSatisfied();
    }

    /**
     * Test forcing a NumberFormatException.
     *
     * @throws Exception
     *             - covers all exceptions.
     */
    @Test(expected = ProtocolException.class)
    public void testGetPersonNullId() throws Exception
    {
        sut.getPerson(testNullId, Person.Field.DEFAULT_FIELDS, FAKETOKEN).get();
    }

    /**
     * Test stub for unimplemented method. This is necessary for code coverage and because all methods for Shindig need
     * to be implemented to not cause runtime errors even though we don't currently have implementations
     * for all methods
     * yet.
     *
     * @throws Exception
     *             - covers all exceptions
     */
    @Test
    public void testGetPeople() throws Exception
    {
        LinkedHashSet<UserId> userIdSet = new LinkedHashSet<UserId>();
        userIdSet.add(testId);
        userIdSet.add(testId2);
        CollectionOptions collOptions = new CollectionOptions();

        buildPeople();
        context.checking(new Expectations()
        {
            {

                allowing(principalPopulator).getPrincipal(with(any(String.class)));
                will(returnValue(principal));

                allowing(principal).getAccountId();

                allowing(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(returnValue(people));
            }
        });

        RestfulCollection<Person> testPeople = sut.getPeople(userIdSet, testGroupId, collOptions,
                Person.Field.DEFAULT_FIELDS, FAKETOKEN).get();

        assertNotNull("Collection of people is null", testPeople);

        context.assertIsSatisfied();
    }

    /**
     * This test exercises the GetPeople method of the OpenSocial implementation in Shindig. This test throws an
     * exception to test error handling.
     *
     * @throws Exception
     *             - on unhandled errors.
     */
    @Test(expected = ProtocolException.class)
    public void testGetPeopleThrowsException() throws Exception
    {
        LinkedHashSet<UserId> userIdSet = new LinkedHashSet<UserId>();
        userIdSet.add(testId);
        userIdSet.add(testId2);
        CollectionOptions collOptions = new CollectionOptions();

        context.checking(new Expectations()
        {
            {

                allowing(principalPopulator).getPrincipal(with(any(String.class)));
                will(returnValue(principal));

                allowing(principal).getAccountId();

                allowing(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new GeneralException()));
            }
        });

        sut.getPeople(userIdSet, testGroupId, collOptions, Person.Field.DEFAULT_FIELDS, FAKETOKEN).get();

        context.assertIsSatisfied();
    }

    /**
     * Build the basic collection of people that will be returned in the GetPeople tests.
     */
    private void buildPeople()
    {
        org.eurekastreams.server.domain.Person authorPerson = new org.eurekastreams.server.domain.Person();
        authorPerson.setOpenSocialId(USERID_ONE);
        org.eurekastreams.server.domain.Person subjectPerson = new org.eurekastreams.server.domain.Person();
        subjectPerson.setOpenSocialId(USERID_ONE);

        people.add(authorPerson);
        people.add(subjectPerson);
    }
}
