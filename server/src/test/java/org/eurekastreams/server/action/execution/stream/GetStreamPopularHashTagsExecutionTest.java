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
package org.eurekastreams.server.action.execution.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.stream.StreamPopularHashTagsRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.StreamPopularHashTagsReport;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for GetStreamPopularHashTagsExecution.
 */
public class GetStreamPopularHashTagsExecutionTest
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
     * Test execute with no tags.
     */
    @Test
    public final void executeWithNoTags()
    {
        final DomainMapper<StreamPopularHashTagsRequest, StreamPopularHashTagsReport> popularHashTagsMapper = context
                .mock(DomainMapper.class);

        final StreamPopularHashTagsRequest request = new StreamPopularHashTagsRequest();
        final StreamPopularHashTagsReport response = new StreamPopularHashTagsReport();
        final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

        GetStreamPopularHashTagsExecution sut = new GetStreamPopularHashTagsExecution(popularHashTagsMapper);

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(request));

                oneOf(popularHashTagsMapper).execute(with(request));
                will(returnValue(response));
            }
        });

        assertEquals(0, ((ArrayList<String>) sut.execute(actionContext)).size());

        context.assertIsSatisfied();
    }

    /**
     * Test execute with tags.
     */
    @Test
    public final void executeWithTags()
    {
        final DomainMapper<StreamPopularHashTagsRequest, StreamPopularHashTagsReport> popularHashTagsMapper = context
                .mock(DomainMapper.class);

        final StreamPopularHashTagsRequest request = new StreamPopularHashTagsRequest();
        final StreamPopularHashTagsReport response = new StreamPopularHashTagsReport();
        final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);
        final ArrayList<String> hashTags = new ArrayList<String>();
        hashTags.add("#foo");
        hashTags.add("#bar");
        response.setPopularHashTags(hashTags);

        GetStreamPopularHashTagsExecution sut = new GetStreamPopularHashTagsExecution(popularHashTagsMapper);

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(request));

                oneOf(popularHashTagsMapper).execute(with(request));
                will(returnValue(response));
            }
        });

        ArrayList<String> results = (ArrayList<String>) sut.execute(actionContext);
        assertEquals(2, results.size());
        assertTrue(results.contains("#foo"));
        assertTrue(results.contains("#bar"));

        context.assertIsSatisfied();
    }
}
