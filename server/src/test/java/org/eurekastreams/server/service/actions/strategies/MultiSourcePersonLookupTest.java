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

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.eurekastreams.server.domain.Person;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the domain person lookup.
 */
public class MultiSourcePersonLookupTest
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
    private MultiSourcePersonLookup sut;

    /**
     * Mock person primary lookup strategy.
     */
    private PersonLookupStrategy primaryMock = context.mock(PersonLookupStrategy.class, "Primary");

    /**
     * Mock person secondary lookup strategy.
     */
    private PersonLookupStrategy secondaryMock = context.mock(PersonLookupStrategy.class, "Secondary");

    /**
     * Setup text fixtures.
     */
    @Before
    public final void setUp()
    {
        sut = new MultiSourcePersonLookup(primaryMock, secondaryMock);
    }

    /**
     * Tests looking up users and merging.
     *
     * @throws NamingException
     *             not expected
     */
    @Test
    public final void testLookup() throws NamingException
    {
        final List<Person> primaryList = new ArrayList<Person>();
        final List<Person> secondaryList = new ArrayList<Person>();

        primaryList.add(new Person("lastf", "Primary", "M", "Last", "F"));
        primaryList.add(new Person("other", "other", "M", "Last", "F"));

        secondaryList.add(new Person("lastf", "Secondar", "M", "Last", "F"));

        context.checking(new Expectations()
        {
            {
                oneOf(primaryMock).findPeople("searchString", 0);
                will(returnValue(primaryList));

                oneOf(secondaryMock).findPeople("searchString", 0);
                will(returnValue(secondaryList));
            }
        });

        List<Person> results = sut.findPeople("searchString", 0);

        assertEquals(2, results.size());

        context.assertIsSatisfied();
    }

    /**
     * Tests looking up users with primary and secondary returning null.
     *
     * @throws NamingException
     *             not expected
     */
    @Test
    public final void testLookupNull() throws NamingException
    {
        context.checking(new Expectations()
        {
            {
                oneOf(primaryMock).findPeople("searchString", 0);
                will(returnValue(null));
                oneOf(secondaryMock).findPeople("searchString", 0);
                will(returnValue(null));
            }
        });

        sut.findPeople("searchString", 0);

        context.assertIsSatisfied();
    }
}
