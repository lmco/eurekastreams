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
package org.eurekastreams.server.action.execution;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.OrganizationChild;
import org.eurekastreams.server.persistence.mappers.cache.PopulateOrgChildWithSkeletonParentOrgsCacheMapper;
import org.eurekastreams.server.service.actions.strategies.PersonLookupStrategy;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for PersonLookupUtilityStrategy class.
 * 
 */
public class PersonLookupUtilityStrategyTest
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
    private PersonLookupUtilityStrategy sut;

    /**
     * {@link PersonLookupStrategy} mock.
     */
    private PersonLookupStrategy lookupStrategy = context.mock(PersonLookupStrategy.class);

    /**
     * {@link PopulateOrgChildWithSkeletonParentOrgsCacheMapper} mock.
     */
    private PopulateOrgChildWithSkeletonParentOrgsCacheMapper populateParentOrgDAO = context
            .mock(PopulateOrgChildWithSkeletonParentOrgsCacheMapper.class);

    /**
     * The maximum number of Person results to ask for.
     */
    private static final int MAX_RESULTS = 100;

    /**
     * Query string used for tests.
     */
    private static final String QUERY_STRING = "queryString";

    /**
     * Setup.
     */
    @Before
    public final void setUp()
    {
        sut = new PersonLookupUtilityStrategy(lookupStrategy, populateParentOrgDAO);
    }

    /**
     * Test performing the action.
     */
    @Test
    public final void testPerformAction()
    {

        final List<OrganizationChild> people = new ArrayList<OrganizationChild>();

        context.checking(new Expectations()
        {
            {
                oneOf(lookupStrategy).findPeople(QUERY_STRING, MAX_RESULTS);
                will(returnValue(people));

                oneOf(populateParentOrgDAO).populateParentOrgSkeletons(people);
            }
        });

        assertEquals(people, sut.getPeople(QUERY_STRING, MAX_RESULTS));
        context.assertIsSatisfied();
    }

}
