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
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.DeleteActivityRequest;
import org.eurekastreams.server.persistence.mappers.stream.DeleteActivity;
import org.eurekastreams.server.persistence.mappers.stream.GetPersonIdsWithStarredActivity;
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
    private DeleteActivity deleteActivityDAO = context.mock(DeleteActivity.class);

    /**
     * DAO for finding comment ids.
     */
    private DomainMapper<Long, List<Long>> commentIdsByActivityIdDAO = context.mock(DomainMapper.class);

    /**
     * DAO for getting person Ids for users that have deleted activity starred.
     */
    private GetPersonIdsWithStarredActivity getPersonIdsWithStarredActivityDAO = context
            .mock(GetPersonIdsWithStarredActivity.class);

    /**
     * {@link TaskHandlerActionContext}.
     */
    @SuppressWarnings("unchecked")
    private TaskHandlerActionContext taskActionContext = context.mock(TaskHandlerActionContext.class);

    /**
     * {@link PrincipalActionContext}.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Activity id used in tests.
     */
    private Long activityId = 5L;

    /**
     * Current user id for tests.
     */
    private Long currentUserId = 1L;

    /**
     * List of comment ids for an activity.
     */
    private List<Long> commentIds = new ArrayList<Long>();

    /**
     * List of user ids that have activity as starred item.
     */
    private List<Long> personIdsWithActivityStarred = new ArrayList<Long>();

    /**
     * Activity mock.
     */
    private ActivityDTO activity = context.mock(ActivityDTO.class);

    /**
     * {@link Principal}.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * System under test.
     */
    private DeleteActivityExecution sut = new DeleteActivityExecution(deleteActivityDAO, commentIdsByActivityIdDAO,
            getPersonIdsWithStarredActivityDAO);

    /**
     * Test.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute()
    {
        context.checking(new Expectations()
        {
            {
                allowing(taskActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(actionContext).getParams();
                will(returnValue(activityId));

                oneOf(principal).getId();
                will(returnValue(currentUserId));

                allowing(commentIdsByActivityIdDAO).execute(activityId);
                will(returnValue(commentIds));

                allowing(getPersonIdsWithStarredActivityDAO).execute(activityId);
                will(returnValue(personIdsWithActivityStarred));

                allowing(deleteActivityDAO).execute(with(any(DeleteActivityRequest.class)));
                will(returnValue(activity));

                allowing(taskActionContext).getUserActionRequests();
                will(returnValue(new ArrayList<UserActionRequest>()));

            }
        });

        assertTrue(sut.execute(taskActionContext));
        assertEquals(2, taskActionContext.getUserActionRequests().size());
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteNullActivity()
    {
        context.checking(new Expectations()
        {
            {
                allowing(taskActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(actionContext).getParams();
                will(returnValue(activityId));

                oneOf(principal).getId();
                will(returnValue(currentUserId));

                allowing(commentIdsByActivityIdDAO).execute(activityId);
                will(returnValue(commentIds));

                allowing(getPersonIdsWithStarredActivityDAO).execute(activityId);
                will(returnValue(personIdsWithActivityStarred));

                allowing(deleteActivityDAO).execute(with(any(DeleteActivityRequest.class)));
                will(returnValue(null));
            }
        });

        assertTrue(sut.execute(taskActionContext));
        context.assertIsSatisfied();
    }
}
