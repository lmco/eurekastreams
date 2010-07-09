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

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.action.request.stream.SetActivityStarRequest;
import org.eurekastreams.server.action.request.stream.SetActivityStarRequest.StarActionType;
import org.eurekastreams.server.domain.stream.StarredActivity;
import org.eurekastreams.server.persistence.mappers.DeleteStarredActivity;
import org.eurekastreams.server.persistence.mappers.InsertStarredActivity;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link SetActivityStarExecution} class.
 *
 */
public class SetActivityStarExecutionTest
{
    /**
     * System under test.
     */
    private SetActivityStarExecution sut;

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
     * DeleteStarredActivity mapper mock.
     */
    private InsertStarredActivity starMapperMock = context.mock(InsertStarredActivity.class);

    /**
     * DeleteStarredActivity mapper mock.
     */
    private DeleteStarredActivity unstarMapperMock = context.mock(DeleteStarredActivity.class);

    /**
     * Mocked instance of the principal object.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Prepare the sut.
     */
    @Before
    public void setup()
    {
        sut = new SetActivityStarExecution(starMapperMock, unstarMapperMock);
    }

    /**
     * Test adding a star.
     */
    @Test
    public void testAddStar()
    {
        SetActivityStarRequest currentRequest = new SetActivityStarRequest(1L, StarActionType.ADD_STAR);
        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getId();
                will(returnValue(5L));

                oneOf(starMapperMock).execute(with(any(StarredActivity.class)));
            }
        });
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);
        sut.execute(currentContext);
        context.assertIsSatisfied();
    }

    /**
     * Test removing a star.
     */
    public void testRemoveStar()
    {
        SetActivityStarRequest currentRequest = new SetActivityStarRequest(1L, StarActionType.REMOVE_STAR);
        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getId();
                will(returnValue(5L));

                oneOf(unstarMapperMock).execute(with(any(StarredActivity.class)));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);
        sut.execute(currentContext);
        context.assertIsSatisfied();

    }
}
