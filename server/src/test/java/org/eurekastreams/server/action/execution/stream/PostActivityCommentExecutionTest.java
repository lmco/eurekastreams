/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
import static org.junit.Assert.assertNotNull;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.InsertActivityCommentRequest;
import org.eurekastreams.server.persistence.mappers.stream.InsertActivityComment;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.testing.TestContextCreator;
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
    /** Test data. */
    private static final long ACTIVITY_ID = 4444L;

    /** Test data. */
    private static final long USER_ID = 200L;

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
    private final InsertActivityComment insertActivityCommentMock = context.mock(InsertActivityComment.class);

    /**
     * CommentDTO mock.
     */
    private final CommentDTO commentDTOMock = context.mock(CommentDTO.class);

    /**
     * Stream entity DTO mock.
     */
    private final StreamEntityDTO destinationStreamMock = context.mock(StreamEntityDTO.class);

    /**
     * ActivityDTO mock.
     */
    private final ActivityDTO activityDTOMock = context.mock(ActivityDTO.class);

    /** Mock activities mapper. */
    private final DomainMapper<Long, ActivityDTO> activityDAO = context.mock(DomainMapper.class, "activityDAO");

    /**
     * Setup sut before each test.
     */
    @Before
    public void setUp()
    {
        sut = new PostActivityCommentExecution(insertActivityCommentMock, activityDAO);
    }

    /**
     * Test performAction method.
     *
     * @throws Exception
     *             not expected.
     */
    @Test
    public void testPerformActionPersonalStream() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(commentDTOMock).getActivityId();
                will(returnValue(ACTIVITY_ID));

                oneOf(commentDTOMock).getBody();

                oneOf(insertActivityCommentMock).execute(with(any(InsertActivityCommentRequest.class)));

                oneOf(activityDAO).execute(with(ACTIVITY_ID));
                will(returnValue(activityDTOMock));

                oneOf(activityDTOMock).getDestinationStream();
                will(returnValue(destinationStreamMock));

                oneOf(destinationStreamMock).getDestinationEntityId();
                will(returnValue(1L));

                oneOf(destinationStreamMock).getType();
                will(returnValue(EntityType.PERSON));
            }
        });

        TaskHandlerActionContext<PrincipalActionContext> actionContext = TestContextCreator
                .createTaskHandlerContextWithPrincipal(commentDTOMock, null, USER_ID);
        assertNotNull(sut.execute(actionContext));

        context.assertIsSatisfied();
        assertEquals(1, actionContext.getUserActionRequests().size());
    }

    /**
     * Test performAction method.
     *
     * @throws Exception
     *             not expected.
     */
    @Test
    public void testPerformActionGroupStream() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(commentDTOMock).getActivityId();
                will(returnValue(ACTIVITY_ID));

                allowing(commentDTOMock).getBody();

                oneOf(insertActivityCommentMock).execute(with(any(InsertActivityCommentRequest.class)));

                oneOf(activityDAO).execute(with(ACTIVITY_ID));
                will(returnValue(activityDTOMock));

                allowing(activityDTOMock).getDestinationStream();
                will(returnValue(destinationStreamMock));

                allowing(destinationStreamMock).getDestinationEntityId();
                will(returnValue(1L));

                allowing(destinationStreamMock).getType();
                will(returnValue(EntityType.GROUP));
            }
        });

        TaskHandlerActionContext<PrincipalActionContext> actionContext = TestContextCreator
                .createTaskHandlerContextWithPrincipal(commentDTOMock, null, USER_ID);
        assertNotNull(sut.execute(actionContext));

        context.assertIsSatisfied();
        assertEquals(1, actionContext.getUserActionRequests().size());
    }
}
