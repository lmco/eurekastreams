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

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.PersonLookupRequest;
import org.eurekastreams.server.domain.Person;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for PersonLookupExecution class.
 */
public class PersonLookupExecutionTest
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
    private PersonLookupExecution sut;

    /**
     * {@link PersonLookupUtilityStrategy} mock.
     */
    private PersonLookupUtilityStrategy lookupStrategy = context.mock(PersonLookupUtilityStrategy.class);

    /**
     * {@link PrincipalActionContext} mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * {@link PersonLookupRequest} mock.
     */
    private PersonLookupRequest params = context.mock(PersonLookupRequest.class);

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
        sut = new PersonLookupExecution(lookupStrategy);
    }

    /**
     * Test performing the action.
     */
    @Test
    public final void testPerformAction()
    {

        final List<Person> people = new ArrayList<Person>();

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(params));

                allowing(params).getQueryString();
                will(returnValue(QUERY_STRING));

                allowing(params).getMaxResults();
                will(returnValue(MAX_RESULTS));

                oneOf(lookupStrategy).getPeople(QUERY_STRING, MAX_RESULTS);
                will(returnValue(people));
            }
        });

        assertEquals(people, sut.execute(actionContext));
        context.assertIsSatisfied();
    }

}
