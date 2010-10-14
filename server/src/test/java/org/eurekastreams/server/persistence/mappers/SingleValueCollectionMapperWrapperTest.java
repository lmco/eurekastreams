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

import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for ingleValueCollectionMapperWrapper.
 */
public class SingleValueCollectionMapperWrapperTest
{
    /**
     * mock context.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mapper to wrap.
     */
    private final DomainMapper<List<String>, List<String>> wrappedMapper = context.mock(DomainMapper.class);

    /**
     * Test execute with no results returning null.
     */
    @Test
    public void testExecuteReturningNullFromNoResults()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(wrappedMapper).execute(with(any(List.class)));
                will(returnValue(new ArrayList<String>()));
            }
        });
        assertNull(new SingleValueCollectionMapperWrapper(wrappedMapper, true).execute("FOO"));
        context.assertIsSatisfied();
    }

    /**
     * Test execute with no results returning null.
     */
    @Test(expected = RuntimeException.class)
    public void testExecuteThrowsExceptionFromNoResults()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(wrappedMapper).execute(with(any(List.class)));
                will(returnValue(new ArrayList<String>()));
            }
        });
        assertNull(new SingleValueCollectionMapperWrapper(wrappedMapper, false).execute("FOO"));
        context.assertIsSatisfied();
    }

    /**
     * Test execute with no results returning null.
     */
    @Test
    public void testExecuteReturningNullFromMultipleResults()
    {
        final ArrayList<String> results = new ArrayList<String>();
        results.add("HI");
        results.add("THERE");

        context.checking(new Expectations()
        {
            {
                oneOf(wrappedMapper).execute(with(any(List.class)));
                will(returnValue(results));
            }
        });
        assertNull(new SingleValueCollectionMapperWrapper(wrappedMapper, true).execute("FOO"));
        context.assertIsSatisfied();
    }

    /**
     * Test execute with no results returning null.
     */
    @Test(expected = RuntimeException.class)
    public void testExecuteThrowsExceptionFromMultipleResults()
    {
        final ArrayList<String> results = new ArrayList<String>();
        results.add("HI");
        results.add("THERE");

        context.checking(new Expectations()
        {
            {
                oneOf(wrappedMapper).execute(with(any(List.class)));
                will(returnValue(results));
            }
        });
        assertNull(new SingleValueCollectionMapperWrapper(wrappedMapper, false).execute("FOO"));
        context.assertIsSatisfied();
    }
}
