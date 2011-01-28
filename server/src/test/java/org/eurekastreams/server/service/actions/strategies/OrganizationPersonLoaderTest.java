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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for OrganizationPersonLoader class.
 * 
 */
public class OrganizationPersonLoaderTest
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
    private OrganizationPersonLoader sut;

    /**
     * PersonDTO DAO.
     */
    private final DomainMapper<List<Long>, List<PersonModelView>> personDAO = context.mock(DomainMapper.class,
            "personDAO");

    /**
     * Organization mock.
     */
    private Organization organization = context.mock(Organization.class);

    /**
     * Test.
     */
    @Test
    public void testNoIds()
    {
        sut = new TesterEmptyIds(personDAO);

        context.checking(new Expectations()
        {
            {
                never(personDAO).execute(with(any(List.class)));
            }
        });

        sut.loadOrganization(organization);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testIds()
    {
        sut = new TesterWithIds(personDAO);
        new ArrayList<Long>(Arrays.asList(2L));

        final List<PersonModelView> people = new ArrayList<PersonModelView>();

        context.checking(new Expectations()
        {
            {
                allowing(personDAO).execute(new ArrayList<Long>(Arrays.asList(2L)));
                will(returnValue(people));
            }
        });

        sut.loadOrganization(organization);
        context.assertIsSatisfied();

    }

    /**
     * Inner class that extends abstract for testing.
     * 
     */
    public class TesterEmptyIds extends OrganizationPersonLoader
    {

        /**
         * Constructor.
         * 
         * @param inPersonDAO
         *            Person DAO.
         */
        public TesterEmptyIds(final DomainMapper<List<Long>, List<PersonModelView>> inPersonDAO)
        {
            super(inPersonDAO);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Set<Long> getPersonIds(final Organization inOrganization)
        {
            return new HashSet<Long>(0);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void setPeopleInOrganization(final Organization inOrganization, final Set<Person> inPeople)
        {
            // do nothing
        }

    }

    /**
     * Inner class that extends abstract for testing.
     * 
     */
    public class TesterWithIds extends OrganizationPersonLoader
    {

        /**
         * Constructor.
         * 
         * @param inPersonDAO
         *            Person DAO.
         */
        public TesterWithIds(final DomainMapper<List<Long>, List<PersonModelView>> inPersonDAO)
        {
            super(inPersonDAO);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Set<Long> getPersonIds(final Organization inOrganization)
        {
            return new HashSet<Long>(Arrays.asList(2L));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void setPeopleInOrganization(final Organization inOrganization, final Set<Person> inPeople)
        {
            // do nothing
        }

    }

}
