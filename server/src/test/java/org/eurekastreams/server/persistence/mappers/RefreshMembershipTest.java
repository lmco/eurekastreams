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
package org.eurekastreams.server.persistence.mappers;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.action.execution.CreatePersonActionFactory;
import org.eurekastreams.server.action.execution.PersistResourceExecution;
import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.service.actions.strategies.EmailerFactory;
import org.eurekastreams.server.service.actions.strategies.PersonLookupStrategy;
import org.eurekastreams.server.service.actions.strategies.ReflectiveUpdater;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the Membership Refresh mapper.
 */
@SuppressWarnings("unchecked")
public class RefreshMembershipTest
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
     * The system under test.
     */
    private RefreshMembership sut;

    /**
     * Find system settings mapper.
     */
    private final FindSystemSettings settingsMapper = context.mock(FindSystemSettings.class);

    /**
     * The mock Organization Mapper to use.
     */
    private OrganizationMapper organizationMapperMock = context.mock(OrganizationMapper.class);

    /**
     * The mock org mapper to be used by the action.
     */
    private PersonMapper personMapperMock = context.mock(PersonMapper.class);

    /**
     * The mock system settings class for looking up criteria.
     */
    private SystemSettings settingsMock = context.mock(SystemSettings.class);

    /**
     * The mock root Organization.
     */
    private Organization rootOrganizationMock = context.mock(Organization.class);

    /**
     * The mock to do people lookups.
     */
    private PersonLookupStrategy lookupMock = context.mock(PersonLookupStrategy.class);

    /**
     * The mock persistence action.
     */
    private PersistResourceExecution<Person> persistResourceActionMock = context.mock(PersistResourceExecution.class);

    /**
     * The mock create person factory.
     */
    private CreatePersonActionFactory createPersonActionFactoryMock = context.mock(CreatePersonActionFactory.class);

    /**
     * The mock emailer.
     */
    private EmailerFactory emailerMock = context.mock(EmailerFactory.class);

    /** Mock email message. */
    private MimeMessage messageMock = context.mock(MimeMessage.class);

    /**
     * The mock entity manager.
     */
    private EntityManager entityManagerMock = context.mock(EntityManager.class);

    /**
     * The mocked query object.
     */
    private Query queryMock = context.mock(Query.class);

    /**
     * A person mock.
     */
    private Person personMock = context.mock(Person.class);

    /**
     * List of people to be used during the test.
     */
    List<Person> people;

    /**
     * The list of people ids.
     */
    List<Long> peopleIds;

    /**
     * A different list of people ids.
     */
    List<Long> peopleIds2;

    /**
     * Fake person id for the test.
     */
    static final long PERSON_ID = 123L;

    /**
     * Anoter fake person id for the test.
     */
    static final long PERSON2_ID = 456L;

    /**
     * Fake baseUrl for the test.
     */
    static final String BASE_URL = "localhost:8080";

    /**
     * Test group name.
     */
    static final String TEST_GROUP = "domainacct\\ipe.usecure.smp.team";

    /**
     * Test attribute name.
     */
    static final String TEST_ATTRIBUTE = "samaccountname=t-es*";

    /**
     * Test account id.
     */
    static final String TEST_ACCOUNT = "testaccount";

    /**
     * Test email address.
     */
    static final String TEST_EMAIL = "somebody@example.com";

    /**
     * Test membership criterion.
     */
    static final String TEST_CRITERION = "testCriterion";

    /**
     * Setup for the test.
     */
    @Before
    public final void setUp()
    {
        sut = new RefreshMembership(settingsMapper, organizationMapperMock, personMapperMock, lookupMock, lookupMock,
                createPersonActionFactoryMock, emailerMock, BASE_URL);

        sut.setEntityManager(entityManagerMock);

        people = new ArrayList<Person>();
        people.add(personMock);

        peopleIds = new ArrayList<Long>();
        peopleIds.add(PERSON_ID);

        peopleIds2 = new ArrayList<Long>();
        peopleIds2.add(PERSON2_ID);
    }

    /**
     * Tests the two types of criteria (person and group) with the member having a locked account.
     *
     * @throws Exception
     *             on error.
     */
    @Test
    public final void testExecuteLocked() throws Exception
    {
        MembershipCriteria criteria1 = new MembershipCriteria();
        criteria1.setCriteria(TEST_GROUP);

        MembershipCriteria criteria2 = new MembershipCriteria();
        criteria1.setCriteria(TEST_ATTRIBUTE);

        final List<MembershipCriteria> membershipCriteria = new ArrayList<MembershipCriteria>();
        membershipCriteria.add(criteria1);
        membershipCriteria.add(criteria2);

        context.checking(new Expectations()
        {
            {
                oneOf(createPersonActionFactoryMock).getCreatePersonAction(with(any(PersonMapper.class)),
                        with(any(ReflectiveUpdater.class)));
                will(returnValue(persistResourceActionMock));

                oneOf(settingsMapper).execute(null);
                will(returnValue(settingsMock));

                oneOf(settingsMock).getMembershipCriteria();
                will(returnValue(membershipCriteria));

                oneOf(settingsMock).getSendWelcomeEmails();
                will(returnValue(Boolean.FALSE));

                exactly(2).of(lookupMock).findPeople(with(any(String.class)), with(any(Integer.class)));
                will(returnValue(people));

                exactly(2).of(personMapperMock).findByAccountId(with(any(String.class)));
                will(returnValue(personMock));

                exactly(2).of(personMock).getAccountId();
                will(returnValue(TEST_ACCOUNT));

                exactly(2).of(personMock).getId();
                will(returnValue(PERSON_ID));

                exactly(2).of(personMock).isAccountLocked();
                will(returnValue(true));

                exactly(2).of(personMock).setAccountLocked(false);

                oneOf(entityManagerMock).createQuery(with(any(String.class)));
                will(returnValue(queryMock));

                oneOf(queryMock).getResultList();
                will(returnValue(peopleIds));

                oneOf(personMapperMock).flush();
            }
        });

        sut.execute();
        context.assertIsSatisfied();
    }

    /**
     * Tests a criteria with the member having a account that is not locked.
     *
     * @throws Exception
     *             on error.
     */
    @Test
    public final void testExecuteNotLocked() throws Exception
    {
        MembershipCriteria criteria = new MembershipCriteria();
        criteria.setCriteria(TEST_ATTRIBUTE);

        final List<MembershipCriteria> membershipCriteria = new ArrayList<MembershipCriteria>();
        membershipCriteria.add(criteria);

        context.checking(new Expectations()
        {
            {
                oneOf(createPersonActionFactoryMock).getCreatePersonAction(with(any(PersonMapper.class)),
                        with(any(ReflectiveUpdater.class)));
                will(returnValue(persistResourceActionMock));

                oneOf(settingsMapper).execute(null);
                will(returnValue(settingsMock));

                oneOf(settingsMock).getMembershipCriteria();
                will(returnValue(membershipCriteria));

                oneOf(settingsMock).getSendWelcomeEmails();
                will(returnValue(Boolean.FALSE));

                oneOf(lookupMock).findPeople(with(any(String.class)), with(any(Integer.class)));
                will(returnValue(people));

                oneOf(personMapperMock).findByAccountId(with(any(String.class)));
                will(returnValue(personMock));

                oneOf(personMock).getAccountId();
                will(returnValue(TEST_ACCOUNT));

                oneOf(personMock).getId();
                will(returnValue(PERSON_ID));

                oneOf(personMock).isAccountLocked();
                will(returnValue(false));

                oneOf(entityManagerMock).createQuery(with(any(String.class)));
                will(returnValue(queryMock));

                oneOf(queryMock).getResultList();
                will(returnValue(peopleIds));

                oneOf(personMapperMock).flush();
            }
        });

        sut.execute();
        context.assertIsSatisfied();
    }

    /**
     * Tests a criteria with the member needing to be added to the system.
     *
     * @throws Exception
     *             on error.
     */
    @Test
    public final void testExecuteNewUser() throws Exception
    {
        MembershipCriteria criteria = new MembershipCriteria();
        criteria.setCriteria(TEST_GROUP);

        final List<MembershipCriteria> membershipCriteria = new ArrayList<MembershipCriteria>();
        membershipCriteria.add(criteria);

        context.checking(new Expectations()
        {
            {
                oneOf(createPersonActionFactoryMock).getCreatePersonAction(with(any(PersonMapper.class)),
                        with(any(ReflectiveUpdater.class)));
                will(returnValue(persistResourceActionMock));

                oneOf(organizationMapperMock).getRootOrganization();
                will(returnValue(rootOrganizationMock));

                oneOf(settingsMapper).execute(null);
                will(returnValue(settingsMock));

                oneOf(settingsMock).getMembershipCriteria();
                will(returnValue(membershipCriteria));

                oneOf(settingsMock).getSendWelcomeEmails();
                will(returnValue(Boolean.TRUE));

                oneOf(lookupMock).findPeople(with(any(String.class)), with(any(Integer.class)));
                will(returnValue(people));

                oneOf(personMock).getAccountId();
                will(returnValue(TEST_ACCOUNT));

                oneOf(personMapperMock).findByAccountId(with(any(String.class)));
                will(returnValue(null));

                oneOf(personMock).getProperties(Boolean.FALSE);

                oneOf(persistResourceActionMock).execute(with(any(TaskHandlerActionContext.class)));
                will(returnValue(personMock));

                oneOf(personMock).getEmail();
                will(returnValue(TEST_EMAIL));

                oneOf(personMock).getAccountId();
                will(returnValue(TEST_ACCOUNT));

                oneOf(emailerMock).createMessage();
                will(returnValue(messageMock));
                oneOf(emailerMock).setTo(with(same(messageMock)), with(equal(TEST_EMAIL)));
                oneOf(emailerMock).setSubject(with(same(messageMock)), with(any(String.class)));
                oneOf(emailerMock).setHtmlBody(with(same(messageMock)), with(any(String.class)));
                oneOf(emailerMock).sendMail(with(same(messageMock)));

                oneOf(personMock).getId();
                will(returnValue(PERSON_ID));

                oneOf(entityManagerMock).createQuery(with(any(String.class)));
                will(returnValue(queryMock));

                oneOf(queryMock).getResultList();
                will(returnValue(peopleIds));

                oneOf(personMapperMock).flush();
            }
        });

        sut.execute();
        context.assertIsSatisfied();
    }

    /**
     * Tests a criteria with a user that is no longer a member and needs to be locked.
     *
     * @throws Exception
     *             on error.
     */
    @Test
    public final void testExecuteLockingAccount() throws Exception
    {
        final List<MembershipCriteria> membershipCriteria = new ArrayList<MembershipCriteria>();
        MembershipCriteria criterion = new MembershipCriteria();
        criterion.setCriteria(TEST_CRITERION);
        membershipCriteria.add(criterion);

        context.checking(new Expectations()
        {
            {
                oneOf(createPersonActionFactoryMock).getCreatePersonAction(with(any(PersonMapper.class)),
                        with(any(ReflectiveUpdater.class)));
                will(returnValue(persistResourceActionMock));

                oneOf(settingsMapper).execute(null);
                will(returnValue(settingsMock));

                oneOf(settingsMock).getMembershipCriteria();
                will(returnValue(membershipCriteria));

                oneOf(settingsMock).getSendWelcomeEmails();
                will(returnValue(Boolean.FALSE));

                oneOf(lookupMock).findPeople(TEST_CRITERION, Integer.MAX_VALUE);
                will(returnValue(people));

                oneOf(personMock).getAccountId();
                will(returnValue(TEST_ACCOUNT));

                oneOf(personMapperMock).findByAccountId(with(any(String.class)));
                will(returnValue(personMock));

                oneOf(personMock).getId();
                will(returnValue(PERSON_ID));

                oneOf(personMock).isAccountLocked();
                will(returnValue(false));

                oneOf(entityManagerMock).createQuery(with(any(String.class)));
                will(returnValue(queryMock));

                oneOf(queryMock).getResultList();
                will(returnValue(peopleIds2));

                oneOf(personMapperMock).findById(PERSON2_ID);
                will(returnValue(personMock));

                oneOf(personMock).setAccountLocked(true);

                oneOf(personMock).getAccountId();
                will(returnValue(TEST_ACCOUNT));

                oneOf(personMapperMock).flush();
            }
        });

        sut.execute();
        context.assertIsSatisfied();
    }

    /**
     * Tests errors notifying the user.
     *
     * @throws Exception
     *             on error.
     */
    @Test
    public final void testNotifyUserException() throws Exception
    {
        final Exception ex = new MessagingException();

        context.checking(new Expectations()
        {
            {
                oneOf(emailerMock).createMessage();
                will(returnValue(messageMock));
                oneOf(emailerMock).setTo(with(same(messageMock)), with(equal(TEST_EMAIL)));
                oneOf(emailerMock).setSubject(with(same(messageMock)), with(any(String.class)));
                oneOf(emailerMock).setHtmlBody(with(same(messageMock)), with(any(String.class)));
                oneOf(emailerMock).sendMail(with(same(messageMock)));
                will(throwException(ex));
            }
        });

        sut.notifyUser(TEST_EMAIL, TEST_ACCOUNT);
        context.assertIsSatisfied();
    }
}
