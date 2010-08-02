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
package org.eurekastreams.server.action.execution.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.eurekastreams.server.service.actions.strategies.PersonLookupStrategy;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for PersonSourceLDAP.
 *
 */
public class PersonSourceLDAPTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * The settings mapper.
     */
    private DomainMapper<MapperRequest, SystemSettings> settingsMapper = context.mock(DomainMapper.class);

    /**
     * Group lookup strategy.
     */
    private PersonLookupStrategy groupLookupStrategy = context.mock(PersonLookupStrategy.class, "group");

    /**
     * Attribute-based lookup strategy.
     */
    private PersonLookupStrategy attributeLookupStrategy = context.mock(PersonLookupStrategy.class, "attrib");

    /**
     * System under test.
     */
    private PersonSourceLDAP sut = new PersonSourceLDAP(settingsMapper, groupLookupStrategy, attributeLookupStrategy);

    /**
     * {@link SystemSettings}.
     */
    private SystemSettings settings = context.mock(SystemSettings.class);

    /**
     * {@link MembershipCriteria}.
     */
    private MembershipCriteria membershipCriteria = context.mock(MembershipCriteria.class);

    /**
     * List of criteria.
     */
    private List<MembershipCriteria> criteriaList = // \n
    new ArrayList<MembershipCriteria>(Arrays.asList(membershipCriteria));

    /**
     * Test.
     */
    @Test
    public void testAttribute()
    {
        context.checking(new Expectations()
        {
            {
                allowing(settingsMapper).execute(null);
                will(returnValue(settings));

                allowing(settings).getMembershipCriteria();
                will(returnValue(criteriaList));

                allowing(membershipCriteria).getCriteria();
                will(returnValue("="));

                allowing(attributeLookupStrategy).findPeople("=", Integer.MAX_VALUE);
                will(returnValue(new ArrayList<Person>()));
            }
        });

        sut.getPeople();

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testGroup()
    {
        context.checking(new Expectations()
        {
            {
                allowing(settingsMapper).execute(null);
                will(returnValue(settings));

                allowing(settings).getMembershipCriteria();
                will(returnValue(criteriaList));

                allowing(membershipCriteria).getCriteria();
                will(returnValue("blah"));

                allowing(groupLookupStrategy).findPeople("blah", Integer.MAX_VALUE);
                will(returnValue(new ArrayList<Person>()));
            }
        });

        sut.getPeople();

        context.assertIsSatisfied();
    }

}
