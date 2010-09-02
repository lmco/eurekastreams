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

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.stream.SetActivityLikeRequest;
import org.eurekastreams.server.action.request.stream.SetActivityLikeRequest.LikeActionType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.LikedActivity;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DeleteLikedActivity;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.InsertLikedActivity;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Collections;

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
    private PrincipalActionContext principalActionContextMock = context.mock(PrincipalActionContext.class);

    /**
     * Principal mock.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Action Context mock.
     */
    private TaskHandlerActionContext<PrincipalActionContext> contextMock = context.mock(TaskHandlerActionContext.class);

    /**
     * Mapper mock.
     */
    private DomainMapper<List<Long>, List<ActivityDTO>> activityMapper = context.mock(DomainMapper.class);

    /**
     * Prepare the sut.
     */
    @Before
    public void setup()
    {
        sut = new SetActivityLikeExecution(likeMapperMock, unlikeMapperMock, activityMapper);
    }

    /**
     * Test adding a like.
     */
    @Test
    public void testAddLike()
    {
        final ActivityDTO activity = context.mock(ActivityDTO.class);
        final StreamEntityDTO actor = context.mock(StreamEntityDTO.class);
        final List<ActivityDTO> activities = Collections.singletonList(activity);
        final List<UserActionRequest> requests = new ArrayList<UserActionRequest>();
        final SetActivityLikeRequest currentRequest = new SetActivityLikeRequest(1L, LikeActionType.ADD_LIKE);
        context.checking(new Expectations()
        {
            {
                oneOf(activityMapper).execute(with(any(List.class)));
                will(returnValue(activities));

                allowing(activity).getId();
                will(returnValue(1L));

                allowing(activity).getActor();
                will(returnValue(actor));

                allowing(actor).getId();
                will(returnValue(1L));


                oneOf(contextMock).getUserActionRequests();
                will(returnValue(requests));


                allowing(contextMock).getActionContext();
                will(returnValue(principalActionContextMock));

                allowing(principalActionContextMock).getPrincipal();
                will(returnValue(principalMock));

                allowing(principalActionContextMock).getParams();
                will(returnValue(currentRequest));

                allowing(principalMock).getId();
                will(returnValue(5L));

                oneOf(likeMapperMock).execute(with(any(LikedActivity.class)));
            }
        });

        sut.execute(contextMock);
        context.assertIsSatisfied();
    }

    /**
     * Test removing a like.
     */
    @Test
    public void testRemoveLike()
    {
        final SetActivityLikeRequest currentRequest = new SetActivityLikeRequest(1L, LikeActionType.REMOVE_LIKE);
        context.checking(new Expectations()
        {
            {
                allowing(contextMock).getActionContext();
                will(returnValue(principalActionContextMock));

                oneOf(principalActionContextMock).getPrincipal();
                will(returnValue(principalMock));

                oneOf(principalActionContextMock).getParams();
                will(returnValue(currentRequest));

                oneOf(principalMock).getId();
                will(returnValue(5L));

                oneOf(unlikeMapperMock).execute(with(any(LikedActivity.class)));
            }
        });


        sut.execute(contextMock);
        context.assertIsSatisfied();

    }
}
