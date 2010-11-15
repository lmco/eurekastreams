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
package org.eurekastreams.server.action.execution;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for QueueKeyBasedTasksExecution.
 * 
 */
public class QueueKeyBasedTasksExecutionTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mapper to get list of keys of entities to warm.
     */
    private DomainMapper<Serializable, List<Serializable>> objectKeyMapper = context.mock(DomainMapper.class);

    /**
     * {@link TaskHandlerActionContext}.
     */
    private TaskHandlerActionContext actionContext = context.mock(TaskHandlerActionContext.class);

    /**
     * List of longs.
     */
    private List<Long> objectKeyResults = new ArrayList<Long>(Arrays.asList(5L));

    /**
     * Test.
     */
    @Test
    public void test()
    {
        QueueKeyBasedTasksExecution sut = new QueueKeyBasedTasksExecution(new ArrayList<String>(Arrays
                .asList("actionkey")), objectKeyMapper);

        final List<UserActionRequest> list = new ArrayList<UserActionRequest>();

        context.checking(new Expectations()
        {
            {
                allowing(objectKeyMapper).execute(null);
                will(returnValue(objectKeyResults));

                allowing(actionContext).getUserActionRequests();
                will(returnValue(list));
            }
        });

        sut.execute(actionContext);

        // assert correct number or UserActionRequests.
        assertEquals(1, list.size());

        // assert it was built correctly.
        assertEquals(5L, (list.get(0)).getParams());
        assertEquals("actionkey", (list.get(0)).getActionKey());

        context.assertIsSatisfied();
    }
}
