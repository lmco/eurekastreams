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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.InlineExecutionStrategyWrappingExecutor;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.test.EasyMatcher;
import org.eurekastreams.server.action.request.UpdateStickyActivityRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.DeleteActivityRequest;
import org.eurekastreams.server.persistence.mappers.stream.DeleteActivity;
import org.eurekastreams.server.persistence.mappers.stream.GetPersonIdsWithStarredActivity;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.testing.TestContextCreator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for DeleteActivityExecution class.
 *
 */
public class DeleteActivityExecutionTest
{
    /** Test data. */
    private static final Long GROUP_ID = 500L;

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
     * Delete activity DAO.
     */
    private final DeleteActivity deleteActivityDAO = context.mock(DeleteActivity.class);

    /**
     * DAO for finding comment ids.
     */
    private final DomainMapper<Long, List<Long>> commentIdsByActivityIdDAO = context.mock(DomainMapper.class);

    /**
     * DAO for getting person Ids for users that have deleted activity starred.
     */
    private final GetPersonIdsWithStarredActivity getPersonIdsWithStarredActivityDAO = context
            .mock(GetPersonIdsWithStarredActivity.class);

    /**
     * Activity id used in tests.
     */
    private final Long activityId = 5L;

    /**
     * Current user id for tests.
     */
    private final Long currentUserId = 1L;

    /**
     * List of comment ids for an activity.
     */
    private final List<Long> commentIds = new ArrayList<Long>();

    /**
     * List of user ids that have activity as starred item.
     */
    private final List<Long> personIdsWithActivityStarred = new ArrayList<Long>();

    /**
     * Activity mock.
     */
    private final ActivityDTO activity = context.mock(ActivityDTO.class);

    /** Fixture: activity's stream. */
    private final StreamEntityDTO stream = context.mock(StreamEntityDTO.class);

    /** For getting the group for clearing sticky activities. */
    private final DomainMapper<Long, DomainGroupModelView> groupMapper = context.mock(DomainMapper.class,
            "groupMapper");

    /** Group. */
    private final DomainGroupModelView group = context.mock(DomainGroupModelView.class, "group");

    /** For clearing a group's sticky activity. */
    private final InlineExecutionStrategyWrappingExecutor clearGroupStickyActivityExecutor = context.mock(
            InlineExecutionStrategyWrappingExecutor.class, "clearGroupStickyActivityExecutor");

    /**
     * System under test.
     */
    private final DeleteActivityExecution sut = new DeleteActivityExecution(deleteActivityDAO,
            commentIdsByActivityIdDAO, getPersonIdsWithStarredActivityDAO, groupMapper,
            clearGroupStickyActivityExecutor);

    /**
     * Test.
     */
    @Test
    public void testExecuteNullActivity()
    {
        TaskHandlerActionContext<PrincipalActionContext> actionContext = TestContextCreator
                .createTaskHandlerContextWithPrincipal(activityId, null, currentUserId);

        context.checking(new Expectations()
        {
            {
                allowing(commentIdsByActivityIdDAO).execute(activityId);
                will(returnValue(commentIds));

                allowing(getPersonIdsWithStarredActivityDAO).execute(activityId);
                will(returnValue(personIdsWithActivityStarred));

                allowing(deleteActivityDAO).execute(with(any(DeleteActivityRequest.class)));
                will(returnValue(null));
            }
        });

        assertTrue(sut.execute(actionContext));
        context.assertIsSatisfied();
        assertTrue(actionContext.getUserActionRequests().isEmpty());
    }

    /**
     * Shared expectations.
     */
    private void commonExpectations()
    {
        context.checking(new Expectations()
        {
            {
                allowing(commentIdsByActivityIdDAO).execute(activityId);
                will(returnValue(commentIds));

                allowing(getPersonIdsWithStarredActivityDAO).execute(activityId);
                will(returnValue(personIdsWithActivityStarred));

                allowing(deleteActivityDAO).execute(with(any(DeleteActivityRequest.class)));
                will(returnValue(activity));

                allowing(activity).getDestinationStream();
                will(returnValue(stream));
            }
        });
    }

    /**
     * Test.
     */
    @Test
    public void testExecuteStreamNotGroup()
    {
        TaskHandlerActionContext<PrincipalActionContext> actionContext = TestContextCreator
                .createTaskHandlerContextWithPrincipal(activityId, null, currentUserId);

        commonExpectations();
        context.checking(new Expectations()
        {
            {
                allowing(stream).getEntityType();
                will(returnValue(EntityType.PERSON));
            }
        });

        assertTrue(sut.execute(actionContext));
        context.assertIsSatisfied();
        assertEquals(2, actionContext.getUserActionRequests().size());
    }

    /**
     * Test.
     */
    @Test
    public void testExecuteStreamGroupNotFound()
    {
        TaskHandlerActionContext<PrincipalActionContext> actionContext = TestContextCreator
                .createTaskHandlerContextWithPrincipal(activityId, null, currentUserId);

        commonExpectations();
        context.checking(new Expectations()
        {
            {
                allowing(stream).getEntityType();
                will(returnValue(EntityType.GROUP));

                allowing(stream).getDestinationEntityId();
                will(returnValue(GROUP_ID));

                oneOf(groupMapper).execute(GROUP_ID);
                will(returnValue(null));
            }
        });

        assertTrue(sut.execute(actionContext));
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testExecuteStreamNotSticky()
    {
        TaskHandlerActionContext<PrincipalActionContext> actionContext = TestContextCreator
                .createTaskHandlerContextWithPrincipal(activityId, null, currentUserId);

        commonExpectations();
        context.checking(new Expectations()
        {
            {
                allowing(stream).getEntityType();
                will(returnValue(EntityType.GROUP));

                allowing(stream).getDestinationEntityId();
                will(returnValue(GROUP_ID));

                oneOf(groupMapper).execute(GROUP_ID);
                will(returnValue(group));

                allowing(group).getStickyActivityId();
                will(returnValue(activityId + 1));
            }
        });

        assertTrue(sut.execute(actionContext));
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testExecuteStreamSticky()
    {
        final TaskHandlerActionContext<PrincipalActionContext> actionContext = TestContextCreator
                .createTaskHandlerContextWithPrincipal(activityId, null, currentUserId);

        commonExpectations();
        context.checking(new Expectations()
        {
            {
                allowing(stream).getEntityType();
                will(returnValue(EntityType.GROUP));

                allowing(stream).getDestinationEntityId();
                will(returnValue(GROUP_ID));

                oneOf(groupMapper).execute(GROUP_ID);
                will(returnValue(group));

                allowing(group).getStickyActivityId();
                will(returnValue(activityId));

                allowing(group).getId();
                will(returnValue(GROUP_ID));

                oneOf(clearGroupStickyActivityExecutor).execute(with(same(actionContext)),
                        with(new EasyMatcher<UpdateStickyActivityRequest>()
                        {
                            @Override
                            protected boolean isMatch(final UpdateStickyActivityRequest rqst)
                            {
                                return rqst.getActivityId() == null && rqst.getGroupId() == GROUP_ID;
                            }
                        }));
            }
        });

        assertTrue(sut.execute(actionContext));
        context.assertIsSatisfied();
    }
}
