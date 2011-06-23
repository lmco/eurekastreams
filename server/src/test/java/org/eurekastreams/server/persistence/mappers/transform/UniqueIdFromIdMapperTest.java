/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eurekastreams.server.domain.Identifiable;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests UniqueIdFromIdMapper.
 */
public class UniqueIdFromIdMapperTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** The DAO to use to get the object in question. */
    private final DomainMapper<Long, Identifiable> dao = context.mock(DomainMapper.class);

    /** SUT. */
    private DomainMapper<Long, String> sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new UniqueIdFromIdMapper(dao);
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecute()
    {
        final Identifiable item = context.mock(Identifiable.class);
        context.checking(new Expectations()
        {
            {
                allowing(dao).execute(9L);
                will(returnValue(item));

                allowing(item).getUniqueId();
                will(returnValue("ABC"));
            }
        });

        String result = sut.execute(9L);
        context.assertIsSatisfied();
        assertEquals("ABC", result);
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteUnknown()
    {
        context.checking(new Expectations()
        {
            {
                allowing(dao).execute(9L);
                will(returnValue(null));
            }
        });

        String result = sut.execute(9L);
        context.assertIsSatisfied();
        assertNull(result);
    }
}
