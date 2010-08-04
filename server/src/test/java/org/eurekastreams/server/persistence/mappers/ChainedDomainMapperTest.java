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
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for ChainedDomainMapper.
 */
public class ChainedDomainMapperTest
{
    /**
     * Context for mocking.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * First domain mapper.
     */
    private DomainMapper<Object, Object> firstDomainMapper = context.mock(DomainMapper.class, "firstDM");

    /**
     * Second domain mapper.
     */
    private DomainMapper<Object, Object> secondDomainMapper = context.mock(DomainMapper.class, "secondDM");

    /**
     * Third domain mapper.
     */
    private DomainMapper<Object, Object> thirdDomainMapper = context.mock(DomainMapper.class, "thirdDM");

    /**
     * First domain mapper.
     */
    private RefreshDataSourceMapper<Object, Object> firstRefreshMapper = context.mock(RefreshDataSourceMapper.class, "firstRM");

    /**
     * First domain mapper.
     */
    private RefreshDataSourceMapper<Object, Object> secondRefreshMapper = context.mock(RefreshDataSourceMapper.class,
            "secondRM");

    /**
     * First domain mapper.
     */
    private RefreshDataSourceMapper<Object, Object> thirdRefreshMapper = context.mock(RefreshDataSourceMapper.class, "thirdRM");

    /**
     * Test execute with 3 mappers and no refresh mappers, and no results.
     */
    @Test
    public void testExecuteWithNoRefreshMappersAndNoResults()
    {
        List<ChainedDomainMapperDataSource<Object, Object>>
        // line break
        dataSources = new ArrayList<ChainedDomainMapperDataSource<Object, Object>>();
        dataSources.add(new ChainedDomainMapperDataSource<Object, Object>(firstDomainMapper));
        dataSources.add(new ChainedDomainMapperDataSource<Object, Object>(secondDomainMapper));
        dataSources.add(new ChainedDomainMapperDataSource<Object, Object>(thirdDomainMapper));

        final Object request = new Object();

        final Sequence sequence = context.sequence("sequence-name");

        context.checking(new Expectations()
        {
            {
                // first mapper doesn't have it
                oneOf(firstDomainMapper).execute(request);
                inSequence(sequence);
                will(returnValue(null));

                // second mapper doesn't have it
                oneOf(secondDomainMapper).execute(request);
                inSequence(sequence);
                will(returnValue(null));

                // third mapper doesn't have it
                oneOf(thirdDomainMapper).execute(request);
                inSequence(sequence);
                will(returnValue(null));
            }
        });

        ChainedDomainMapper<Object, Object> sut = new ChainedDomainMapper<Object, Object>(dataSources);

        assertNull(sut.execute(request));

        context.assertIsSatisfied();
    }

    /**
     * Test execute with 3 mappers and the second one has results, but no refresh mappers.
     */
    @Test
    public void testExecuteWithNoRefreshMappersAndResultInSecond()
    {
        List<ChainedDomainMapperDataSource<Object, Object>>
        // line break
        dataSources = new ArrayList<ChainedDomainMapperDataSource<Object, Object>>();
        dataSources.add(new ChainedDomainMapperDataSource<Object, Object>(firstDomainMapper));
        dataSources.add(new ChainedDomainMapperDataSource<Object, Object>(secondDomainMapper));
        dataSources.add(new ChainedDomainMapperDataSource<Object, Object>(thirdDomainMapper));

        final Object request = new Object();
        final Object response = new Object();

        final Sequence sequence = context.sequence("sequence-name");

        context.checking(new Expectations()
        {
            {
                // first mapper doesn't have it
                oneOf(firstDomainMapper).execute(request);
                inSequence(sequence);
                will(returnValue(null));

                // second mapper doesn't have it
                oneOf(secondDomainMapper).execute(request);
                inSequence(sequence);
                will(returnValue(response));
            }
        });

        ChainedDomainMapper<Object, Object> sut = new ChainedDomainMapper<Object, Object>(dataSources);

        assertSame(response, sut.execute(request));

        context.assertIsSatisfied();
    }

    /**
     * Test execute with 3 mappers and the first one has results, but no refresh mappers.
     */
    @Test
    public void testExecuteWithNoRefreshMappersAndResultInFirst()
    {
        List<ChainedDomainMapperDataSource<Object, Object>>
        // line break
        dataSources = new ArrayList<ChainedDomainMapperDataSource<Object, Object>>();
        dataSources.add(new ChainedDomainMapperDataSource<Object, Object>(firstDomainMapper));
        dataSources.add(new ChainedDomainMapperDataSource<Object, Object>(secondDomainMapper));
        dataSources.add(new ChainedDomainMapperDataSource<Object, Object>(thirdDomainMapper));

        final Object request = new Object();
        final Object response = new Object();

        final Sequence sequence = context.sequence("sequence-name");

        context.checking(new Expectations()
        {
            {
                // first mapper doesn't have it
                oneOf(firstDomainMapper).execute(request);
                inSequence(sequence);
                will(returnValue(response));
            }
        });

        ChainedDomainMapper<Object, Object> sut = new ChainedDomainMapper<Object, Object>(dataSources);

        assertSame(response, sut.execute(request));

        context.assertIsSatisfied();
    }

    /**
     * Test execute with 3 mappers and 2 refresh mappers that need to be called, result in last.
     */
    @Test
    public void testExecuteWithRefreshMappersAndResults()
    {
        List<ChainedDomainMapperDataSource<Object, Object>>
        // line break
        dataSources = new ArrayList<ChainedDomainMapperDataSource<Object, Object>>();
        dataSources.add(new ChainedDomainMapperDataSource<Object, Object>(firstDomainMapper, firstRefreshMapper));
        dataSources.add(new ChainedDomainMapperDataSource<Object, Object>(secondDomainMapper, secondRefreshMapper));
        dataSources.add(new ChainedDomainMapperDataSource<Object, Object>(thirdDomainMapper, thirdRefreshMapper));

        final Object request = new Object();
        final Object response = new Object();

        final Sequence sequence = context.sequence("sequence-name");

        context.checking(new Expectations()
        {
            {
                // first mapper doesn't have it
                oneOf(firstDomainMapper).execute(request);
                inSequence(sequence);
                will(returnValue(null));

                // second mapper doesn't have it
                oneOf(secondDomainMapper).execute(request);
                inSequence(sequence);
                will(returnValue(null));

                // third mapper doesn't have it
                oneOf(thirdDomainMapper).execute(request);
                inSequence(sequence);
                will(returnValue(response));

                oneOf(secondRefreshMapper).refresh(request, response);

                oneOf(firstRefreshMapper).refresh(request, response);
            }
        });

        ChainedDomainMapper<Object, Object> sut = new ChainedDomainMapper<Object, Object>(dataSources);

        assertSame(response, sut.execute(request));

        context.assertIsSatisfied();
    }

    /**
     * Test execute with 3 mappers and 1 refresh mappers that need to be called, result in last.
     */
    @Test
    public void testExecuteWith1RefreshMappersAndResults()
    {
        List<ChainedDomainMapperDataSource<Object, Object>>
        // line break
        dataSources = new ArrayList<ChainedDomainMapperDataSource<Object, Object>>();
        dataSources.add(new ChainedDomainMapperDataSource<Object, Object>(firstDomainMapper));
        dataSources.add(new ChainedDomainMapperDataSource<Object, Object>(secondDomainMapper, secondRefreshMapper));
        dataSources.add(new ChainedDomainMapperDataSource<Object, Object>(thirdDomainMapper, thirdRefreshMapper));

        final Object request = new Object();
        final Object response = new Object();

        final Sequence sequence = context.sequence("sequence-name");

        context.checking(new Expectations()
        {
            {
                // first mapper doesn't have it
                oneOf(firstDomainMapper).execute(request);
                inSequence(sequence);
                will(returnValue(null));

                // second mapper doesn't have it
                oneOf(secondDomainMapper).execute(request);
                inSequence(sequence);
                will(returnValue(null));

                // third mapper doesn't have it
                oneOf(thirdDomainMapper).execute(request);
                inSequence(sequence);
                will(returnValue(response));

                oneOf(secondRefreshMapper).refresh(request, response);
            }
        });

        ChainedDomainMapper<Object, Object> sut = new ChainedDomainMapper<Object, Object>(dataSources);

        assertSame(response, sut.execute(request));

        context.assertIsSatisfied();
    }
}
