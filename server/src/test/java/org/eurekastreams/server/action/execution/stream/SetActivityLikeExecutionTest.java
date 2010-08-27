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
import org.eurekastreams.server.action.request.stream.SetActivityLikeRequest;
import org.eurekastreams.server.action.request.stream.SetActivityLikeRequest.LikeActionType;
import org.eurekastreams.server.domain.stream.LikedActivity;
import org.eurekastreams.server.persistence.mappers.DeleteLikedActivity;
import org.eurekastreams.server.persistence.mappers.InsertLikedActivity;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link SetActivityLikeExecution} class.
 *
 */
public class SetActivityLikeExecutionTest
{
    /**
     * System under test.
     */
    private SetActivityLikeExecution sut;

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
     * DeleteLikedActivity mapper mock.
     */
    private InsertLikedActivity likeMapperMock = context.mock(InsertLikedActivity.class);

    /**
     * DeleteLikedActivity mapper mock.
     */
    private DeleteLikedActivity unlikeMapperMock = context.mock(DeleteLikedActivity.class);

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
        sut = new SetActivityLikeExecution(likeMapperMock, unlikeMapperMock);
    }

    /**
     * Test adding a like.
     */
    @Test
    public void testAddLike()
    {
        SetActivityLikeRequest currentRequest = new SetActivityLikeRequest(1L, LikeActionType.ADD_LIKE);
        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getId();
                will(returnValue(5L));

                oneOf(likeMapperMock).execute(with(any(LikedActivity.class)));
            }
        });
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);
        sut.execute(currentContext);
        context.assertIsSatisfied();
    }

    /**
     * Test removing a like.
     */
    public void testRemoveLike()
    {
        SetActivityLikeRequest currentRequest = new SetActivityLikeRequest(1L, LikeActionType.REMOVE_LIKE);
        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getId();
                will(returnValue(5L));

                oneOf(unlikeMapperMock).execute(with(any(LikedActivity.class)));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);
        sut.execute(currentContext);
        context.assertIsSatisfied();

    }
}
