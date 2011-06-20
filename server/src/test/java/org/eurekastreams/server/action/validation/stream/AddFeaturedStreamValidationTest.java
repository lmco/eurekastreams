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
package org.eurekastreams.server.action.validation.stream;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.dto.FeaturedStreamDTO;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for AddFeaturedStreamValidation.
 * 
 */
@SuppressWarnings("unchecked")
public class AddFeaturedStreamValidationTest
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
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * Find by id mapper.
     */
    private DomainMapper<FindByIdRequest, StreamScope> streamScopeMapper = context.mock(DomainMapper.class,
            "streamScopeMapper");

    /**
     * {@link FeaturedStreamDTO}.
     */
    private FeaturedStreamDTO fsdto = context.mock(FeaturedStreamDTO.class);

    /**
     * {@link StreamScope}.
     */
    private StreamScope streamScope = context.mock(StreamScope.class);

    /**
     * System under test.
     */
    private AddFeaturedStreamValidation sut = new AddFeaturedStreamValidation(9, streamScopeMapper);

    /**
     * Stream id.
     */
    private long streamId = 5L;

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testEmptyDescription()
    {
        final String description = null;

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(fsdto));

                allowing(fsdto).getDescription();
                will(returnValue(description));

            }
        });

        sut.validate(actionContext);

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testLongDescription()
    {
        final String description = "0123456789";

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(fsdto));

                allowing(fsdto).getDescription();
                will(returnValue(description));
            }
        });

        sut.validate(actionContext);

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testStreamNotFound()
    {
        final String description = "012345678";

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(fsdto));

                allowing(fsdto).getDescription();
                will(returnValue(description));

                allowing(fsdto).getStreamId();
                will(returnValue(streamId));

                allowing(streamScopeMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(null));
            }
        });

        sut.validate(actionContext);

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testWrongScopeType()
    {
        final String description = "012345678";
        final HashMap<String, Object> state = new HashMap<String, Object>();

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(fsdto));

                allowing(fsdto).getDescription();
                will(returnValue(description));

                allowing(streamScopeMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(streamScope));

                allowing(fsdto).getStreamId();
                will(returnValue(streamId));

                allowing(streamScope).getScopeType();
                will(returnValue(ScopeType.RESOURCE));

                allowing(actionContext).getState();
                will(returnValue(state));
            }
        });

        sut.validate(actionContext);

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testPassPerson()
    {
        final String description = "012345678";
        final HashMap<String, Object> state = new HashMap<String, Object>();

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(fsdto));

                allowing(fsdto).getDescription();
                will(returnValue(description));

                allowing(streamScopeMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(streamScope));

                allowing(fsdto).getStreamId();
                will(returnValue(streamId));

                allowing(streamScope).getScopeType();
                will(returnValue(ScopeType.PERSON));

                allowing(actionContext).getState();
                will(returnValue(state));
            }
        });

        sut.validate(actionContext);

        assertEquals(streamScope, state.get("streamScope"));

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testPassGroup()
    {
        final String description = "012345678";
        final HashMap<String, Object> state = new HashMap<String, Object>();

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(fsdto));

                allowing(fsdto).getDescription();
                will(returnValue(description));

                allowing(streamScopeMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(streamScope));

                allowing(fsdto).getStreamId();
                will(returnValue(streamId));

                allowing(streamScope).getScopeType();
                will(returnValue(ScopeType.GROUP));

                allowing(actionContext).getState();
                will(returnValue(state));
            }
        });

        sut.validate(actionContext);

        assertEquals(streamScope, state.get("streamScope"));

        context.assertIsSatisfied();
    }

}
