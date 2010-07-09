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
package org.eurekastreams.server.service.filters;

import java.io.Serializable;
import java.util.HashMap;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.AnonymousClassInterceptor;
import org.eurekastreams.server.action.execution.PersistResourceExecution;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.service.actions.strategies.ldap.LdapToPersonMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the LDAP person persister.
 */
public class LdapPersonPersisterTest
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
    private LdapPersonPersister sut;

    /**
     * Person mapper.
     */
    private PersonMapper jpaPersonMapper = context.mock(PersonMapper.class);

    /**
     * LDAP to person mapper.
     */
    private LdapToPersonMapper personLdapMapper = context.mock(LdapToPersonMapper.class);

    /**
     * Intercepts person hashmap.
     */
    final AnonymousClassInterceptor<HashMap<String, Person>> personMapInt
    // line break
    = new AnonymousClassInterceptor<HashMap<String, Person>>();

    /**
     * Persist person action.
     */
    private PersistResourceExecution<Person> persistResourceExecution = context.mock(PersistResourceExecution.class);

    /**
     * User name.
     */
    private String userName = "someuser";

    /**
     * Attribs mock.
     */
    private Attributes attrs = context.mock(Attributes.class);

    /**
     * Setup test fixtures.
     */
    @Before
    public final void setUp()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(personLdapMapper).setPeople(with(any(HashMap.class)));
                will(personMapInt);
            }
        });

        sut = new LdapPersonPersister(userName, jpaPersonMapper, personLdapMapper, persistResourceExecution);
    }

    /**
     * Tests with no results from LDAP.
     *
     * @throws NamingException
     *             shouldn't happen.
     */
    @Test
    public final void testNoResults() throws NamingException
    {
        personMapInt.getObject().clear();

        context.checking(new Expectations()
        {
            {
                oneOf(personLdapMapper).mapFromAttributes(attrs);
            }
        });

        sut.mapFromAttributes(attrs);

        context.assertIsSatisfied();
    }

    /**
     * Tests with user already in the db.
     *
     * @throws NamingException
     *             shouldn't happen.
     */
    @Test
    public final void testUserExists() throws NamingException
    {
        final Person person = context.mock(Person.class);

        personMapInt.getObject().put(userName, person);

        context.checking(new Expectations()
        {
            {
                oneOf(personLdapMapper).mapFromAttributes(attrs);

                oneOf(jpaPersonMapper).findByAccountId(userName);
                will(returnValue(person));
            }
        });

        sut.mapFromAttributes(attrs);

        context.assertIsSatisfied();
    }

    /**
     * Tests creating the user.
     *
     * @throws Exception
     *             shouldn't happen.
     */
    @Test
    public final void testCreateUser() throws Exception
    {
        final Person person = context.mock(Person.class);

        final HashMap<String, Serializable> properties = new HashMap<String, Serializable>();
        final Serializable[] theParams = { properties };

        personMapInt.getObject().put(userName, person);

        context.checking(new Expectations()
        {
            {
                oneOf(personLdapMapper).mapFromAttributes(attrs);

                oneOf(jpaPersonMapper).findByAccountId(userName);
                will(returnValue(null));

                oneOf(person).getProperties(false);
                will(returnValue(properties));

                oneOf(persistResourceExecution).execute(with(any(TaskHandlerActionContext.class)));
            }
        });

        sut.mapFromAttributes(attrs);

        context.assertIsSatisfied();
    }

    /**
     * Tests creating the user with an exception.
     *
     * @throws Exception
     *             shouldn't happen.
     */
    @Test
    public final void testCreateUserWithException() throws Exception
    {
        final Person person = context.mock(Person.class);

        final HashMap<String, Serializable> properties = new HashMap<String, Serializable>();

        personMapInt.getObject().put(userName, person);

        context.checking(new Expectations()
        {
            {
                oneOf(personLdapMapper).mapFromAttributes(attrs);

                oneOf(jpaPersonMapper).findByAccountId(userName);
                will(returnValue(null));

                oneOf(person).getProperties(false);
                will(returnValue(properties));

                oneOf(persistResourceExecution).execute(with(any(TaskHandlerActionContext.class)));
            }
        });

        sut.mapFromAttributes(attrs);

        context.assertIsSatisfied();
    }
}
