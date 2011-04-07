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
import org.junit.Test;

/**
 * Test for HideResourceActivityExecution.
 * 
 */
public class HideResourceActivityExecutionTest
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
    @SuppressWarnings("unchecked")
    private TaskHandlerActionContext taskActionContext = context.mock(TaskHandlerActionContext.class);

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * Activity id used in tests.
     */
    private Long activityId = 5L;

    /**
     * Mapper to hide resource activity.
     */
    private DomainMapper<Long, Void> hideResourceActivityMapper = context.mock(DomainMapper.class,
            "hideResourceActivityMapper");

    /**
     * System under test.
     */
    private HideResourceActivityExecution sut = new HideResourceActivityExecution(hideResourceActivityMapper);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        final List<UserActionRequest> uars = new ArrayList<UserActionRequest>();
        context.checking(new Expectations()
        {
            {
                oneOf(taskActionContext).getActionContext();
                will(returnValue(actionContext));

                oneOf(actionContext).getParams();
                will(returnValue(activityId));

                oneOf(hideResourceActivityMapper).execute(activityId);

                oneOf(taskActionContext).getUserActionRequests();
                will(returnValue(uars));
            }
        });

        sut.execute(taskActionContext);
        assertEquals(1, uars.size());
        context.assertIsSatisfied();
    }
}
