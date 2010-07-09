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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests ExcludeSelfMapperDecorator.
 */
public class ExcludeSelfMapperDecoratorTest
{
    /** Test data. */
    private static final Long SELF_ID = 99L;

    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: mapper. */
    private DomainMapper<Long, List<Long>> mapper = context.mock(DomainMapper.class);

    /** SUT. */
    private ExcludeSelfMapperDecorator sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new ExcludeSelfMapperDecorator(mapper);
    }

    /**
     * Tests nominal case.
     */
    @Test
    public void testExecute()
    {
        final List<Long> list = Arrays.asList(1L, 2L, SELF_ID, 3L, 4L);

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute(SELF_ID);
                will(returnValue(list));
            }
        });

        List<Long> resultList = sut.execute(SELF_ID);

        context.assertIsSatisfied();
        assertEquals(4, resultList.size());
        for (int i = 0; i < 4; i++)
        {
            assertEquals((Long) (long) (i + 1), resultList.get(i));
        }
    }

    /**
     * Tests list with no self id.
     */
    @Test
    public void testExecuteNoSelf()
    {
        final List<Long> list = Arrays.asList(1L, 2L, 3L, 4L);

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute(SELF_ID);
                will(returnValue(list));
            }
        });

        List<Long> resultList = sut.execute(SELF_ID);

        context.assertIsSatisfied();
        assertEquals(4, resultList.size());
        for (int i = 0; i < 4; i++)
        {
            assertEquals((Long) (long) (i + 1), resultList.get(i));
        }
    }

    /**
     * Tests empty list.
     */
    @Test
    public void testExecuteEmpty()
    {
        final List<Long> list = Arrays.asList(1L, 2L, 3L, 4L);

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute(SELF_ID);
                will(returnValue(Collections.EMPTY_LIST));
            }
        });

        List<Long> resultList = sut.execute(SELF_ID);

        context.assertIsSatisfied();
        assertTrue(resultList.isEmpty());
    }
}
