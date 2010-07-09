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
package org.eurekastreams.server.action.execution.profile;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.persistence.mappers.db.DeleteAllFeedSubscriberByEntityTypeAndId;
import org.eurekastreams.server.persistence.mappers.db.DeleteGroup;
import org.eurekastreams.server.persistence.mappers.db.DeleteGroupActivity;
import org.eurekastreams.server.persistence.mappers.db.RemoveGroupFollowers;
import org.eurekastreams.server.persistence.mappers.requests.DeleteAllFeedSubscriberByEntityTypeAndIdRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for DeleteGroupFromDBExecution class.
 * 
 */
public class DeleteGroupFromDBExecutionTest
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
     * {@link DeleteGroupActivity}.
     */
    private DeleteGroupActivity deleteGroupActivityDAO = context.mock(DeleteGroupActivity.class);

    /**
     * {@link RemoveGroupFollowers}.
     */
    private RemoveGroupFollowers removeGroupFollowersDAO = context.mock(RemoveGroupFollowers.class);

    /**
     * {@link DeleteGroup}.
     */
    private DeleteGroup deleteGroupDAO = context.mock(DeleteGroup.class);

    /**
     * {@link DeleteAllFeedSubscriberByEntityTypeAndId}.
     */
    private DeleteAllFeedSubscriberByEntityTypeAndId deleteGroupSubscriptionsDAO = context
            .mock(DeleteAllFeedSubscriberByEntityTypeAndId.class);

    /**
     * {@link TaskHandlerActionContext}.
     */
    @SuppressWarnings("unchecked")
    private TaskHandlerActionContext taskHandlerConext = context.mock(TaskHandlerActionContext.class);

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * Group id.
     */
    private final Long groupId = 1L;

    /**
     * System under test.
     */
    private DeleteGroupFromDBExecution sut;

    /**
     * Test.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute()
    {
        final List<UserActionRequest> requests = new ArrayList<UserActionRequest>();
        final int maxCacheListSize = 10000;

        sut = new DeleteGroupFromDBExecution(deleteGroupActivityDAO, removeGroupFollowersDAO, deleteGroupDAO,
                deleteGroupSubscriptionsDAO, maxCacheListSize);

        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerConext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getParams();
                will(returnValue(groupId));

                allowing(deleteGroupActivityDAO).execute(groupId);

                allowing(removeGroupFollowersDAO).execute(groupId);

                allowing(deleteGroupSubscriptionsDAO).execute(
                        with(any(DeleteAllFeedSubscriberByEntityTypeAndIdRequest.class)));

                allowing(deleteGroupDAO).execute(groupId);

                allowing(taskHandlerConext).getUserActionRequests();
                will(returnValue(requests));
            }
        });

        sut.execute(taskHandlerConext);
        context.assertIsSatisfied();
    }
}
