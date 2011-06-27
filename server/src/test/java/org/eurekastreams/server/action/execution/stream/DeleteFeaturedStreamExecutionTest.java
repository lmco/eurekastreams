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
package org.eurekastreams.server.action.execution.stream;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for DeleteFeaturedStreamExecution.
 * 
 */
@SuppressWarnings("unchecked")
public class DeleteFeaturedStreamExecutionTest
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
     * {@link TaskHandlerActionContext}.
     */
    private TaskHandlerActionContext taskHandlerConext = context.mock(TaskHandlerActionContext.class);

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * Delete mapper.
     */
    private DomainMapper<Long, Void> deleteMapper = context.mock(DomainMapper.class, "deleteMapper");

    /**
     * FeaturedStream id.
     */
    private Long featuredStreamId = 5L;

    /**
     * System under test.
     */
    private DeleteFeaturedStreamExecution sut = new DeleteFeaturedStreamExecution(deleteMapper);

    /**
     * Test.
     */
    @Test
    public void testExecute()
    {
        final List<UserActionRequest> requests = new ArrayList<UserActionRequest>();

        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerConext).getActionContext();
                will(returnValue(actionContext));

                allowing(actionContext).getParams();
                will(returnValue(featuredStreamId));

                allowing(deleteMapper).execute(featuredStreamId);

                allowing(taskHandlerConext).getUserActionRequests();
                will(returnValue(requests));
            }
        });

        sut.execute(taskHandlerConext);

        // make sure the discover lists cache is rebuilt
        Assert.assertEquals(1, requests.size());
        Assert.assertEquals("regenerateStreamDiscoverListsJob", requests.get(0).getActionKey());

        context.assertIsSatisfied();
    }
}
