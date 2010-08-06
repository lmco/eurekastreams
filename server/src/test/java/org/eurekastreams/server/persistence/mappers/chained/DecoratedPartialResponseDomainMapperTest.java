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
package org.eurekastreams.server.persistence.mappers.chained;

import static org.junit.Assert.assertSame;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for DecoratedPartialResponseDomainMapper.
 */
public class DecoratedPartialResponseDomainMapperTest
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
     * partial response mapper to get the response and new request.
     */
    private DomainMapper<Object, PartialMapperResponse<Object, Object>> partialMapper = context.mock(
            DomainMapper.class, "partialMapper");

    /**
     * the decorated mapper.
     */
    private DomainMapper<Object, Object> decoratedMapper = context.mock(DomainMapper.class, "decoratedMapper");

    /**
     * results combiner.
     */
    private ResultsCombinerStrategy<Object> resultsCombiner = context.mock(ResultsCombinerStrategy.class);

    /**
     * refresher.
     */
    private RefreshStrategy<Object, Object> refreshStrategy = context.mock(RefreshStrategy.class);

    /**
     * Test execute when the main mapper has all of the data.
     */
    @Test
    public void testExecuteWithCompleteResponseFromMapper()
    {
        DecoratedPartialResponseDomainMapper<Object, Object> sut =
        // line break
        new DecoratedPartialResponseDomainMapper<Object, Object>(partialMapper, decoratedMapper, resultsCombiner,
                refreshStrategy);

        final Object request = new Object();
        final Object response = new Object();
        final PartialMapperResponse<Object, Object> partialResponse = new PartialMapperResponse<Object, Object>(
                response, null);

        context.checking(new Expectations()
        {
            {
                oneOf(partialMapper).execute(request);
                will(returnValue(partialResponse));
            }
        });

        assertSame(response, sut.execute(request));

        context.assertIsSatisfied();
    }

    /**
     * Test execute when the main mapper only returns partial data and there's no decorated mapper.
     */
    @Test
    public void testExecuteWithParitialResponseFromMapperAndNoDecoratedMapper()
    {
        DecoratedPartialResponseDomainMapper<Object, Object> sut =
        // line break
        new DecoratedPartialResponseDomainMapper<Object, Object>(partialMapper, null, resultsCombiner, refreshStrategy);

        final Object request = new Object();
        final Object response = new Object();
        final PartialMapperResponse<Object, Object> partialResponse = new PartialMapperResponse<Object, Object>(
                response, new Object());
        final Object combinedResponse = new Object();

        context.checking(new Expectations()
        {
            {
                oneOf(partialMapper).execute(request);
                will(returnValue(partialResponse));

                oneOf(resultsCombiner).combine(response, null);
                will(returnValue(combinedResponse));
            }
        });

        assertSame(combinedResponse, sut.execute(request));

        context.assertIsSatisfied();
    }

    /**
     * Test execute when the main mapper returns partial data, and the decorated mapper is called.
     */
    @Test
    public void testExecuteWithPartialResponseFromMapperAndResponseFromDecoratedMapper()
    {
        DecoratedPartialResponseDomainMapper<Object, Object> sut =
        // line break
        new DecoratedPartialResponseDomainMapper<Object, Object>(partialMapper, decoratedMapper, resultsCombiner,
                refreshStrategy);

        final Object request = new Object();
        final Object response = new Object();

        final Object newRequest = new Object();
        final Object newResponse = new Object();

        final PartialMapperResponse<Object, Object> partialResponse = new PartialMapperResponse<Object, Object>(
                response, newRequest);
        final Object combinedResponse = new Object();

        context.checking(new Expectations()
        {
            {
                oneOf(partialMapper).execute(request);
                will(returnValue(partialResponse));

                oneOf(decoratedMapper).execute(newRequest);
                will(returnValue(newResponse));

                oneOf(refreshStrategy).refresh(newRequest, newResponse);

                oneOf(resultsCombiner).combine(response, newResponse);
                will(returnValue(combinedResponse));
            }
        });

        assertSame(combinedResponse, sut.execute(request));

        context.assertIsSatisfied();
    }
}
