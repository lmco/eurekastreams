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

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.InsertActivityCommentRequest;
import org.eurekastreams.server.persistence.mappers.stream.InsertActivityComment;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for PostActivityCommentExecution class.
 */
public class PostActivityCommentExecutionTest
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
     * The system under test.
     */
    private PostActivityCommentExecution sut;

    /**
     * InsertActivityComment mock.
     */
    private InsertActivityComment insertActivityCommentMock = context.mock(InsertActivityComment.class);

    /**
     * CommentDTO mock.
     */
    private CommentDTO commentDTOMock = context.mock(CommentDTO.class);

    /**
     * Stream entity DTO mock.
     */
    private StreamEntityDTO destinationStreamMock = context.mock(StreamEntityDTO.class);

    /**
     * ActivityDTO mock.
     */
    private ActivityDTO activityDTOMock = context.mock(ActivityDTO.class);

    /**
     * Mock activities mapper.
     */
    private DomainMapper<List<Long>, List<ActivityDTO>>  activitiesMapperMock = context.mock(DomainMapper.class);

    /**
     * {@link PrincipalActionContext}.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Mocked instance of {@link TaskHandlerActionContext}.
     */
    private TaskHandlerActionContext taskHandlerActionContext = context.mock(TaskHandlerActionContext.class);

    /**
     * {@link Principal}.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * Setup sut before each test.
     */
    @Before
    public void setUp()
    {
        sut = new PostActivityCommentExecution(insertActivityCommentMock, activitiesMapperMock);
    }

    /**
     * Test performAction method.
     *
     * @throws Exception
     *             not expected.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testPerformActionPersonalStream() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(actionContext).getParams();
                will(returnValue(commentDTOMock));

                oneOf(principal).getId();

                oneOf(commentDTOMock).getActivityId();

                oneOf(commentDTOMock).getBody();

                oneOf(insertActivityCommentMock).execute(with(any(InsertActivityCommentRequest.class)));

                oneOf(activitiesMapperMock).execute(with(any(List.class)));
                will(returnValue(Arrays.asList(activityDTOMock)));

                oneOf(activityDTOMock).getDestinationStream();
                will(returnValue(destinationStreamMock));

                oneOf(destinationStreamMock).getDestinationEntityId();
                will(returnValue(1L));

                oneOf(destinationStreamMock).getType();
                will(returnValue(EntityType.PERSON));

                allowing(taskHandlerActionContext).getUserActionRequests();
            }
        });

        assertNotNull(sut.execute(taskHandlerActionContext));
        context.assertIsSatisfied();
    }

    /**
     * Test performAction method.
     *
     * @throws Exception
     *             not expected.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testPerformActionGroupStream() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(actionContext).getParams();
                will(returnValue(commentDTOMock));

                oneOf(principal).getId();

                allowing(commentDTOMock).getActivityId();

                allowing(commentDTOMock).getBody();

                oneOf(insertActivityCommentMock).execute(with(any(InsertActivityCommentRequest.class)));

                oneOf(activitiesMapperMock).execute(with(any(List.class)));
                will(returnValue(Arrays.asList(activityDTOMock)));

                allowing(activityDTOMock).getDestinationStream();
                will(returnValue(destinationStreamMock));

                allowing(destinationStreamMock).getDestinationEntityId();
                will(returnValue(1L));

                allowing(destinationStreamMock).getType();
                will(returnValue(EntityType.GROUP));

                allowing(taskHandlerActionContext).getUserActionRequests();
            }
        });

        assertNotNull(sut.execute(taskHandlerActionContext));
        context.assertIsSatisfied();
    }

}
