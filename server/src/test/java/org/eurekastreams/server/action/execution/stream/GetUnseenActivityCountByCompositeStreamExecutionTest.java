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

import junit.framework.Assert;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests the counting action.
 *
 */
public class GetUnseenActivityCountByCompositeStreamExecutionTest
{
    /**
     * System under test.
     */
    private GetUnseenActivityCountByCompositeStreamExecution sut;

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
     * Exe mock.
     */
    private ExecutionStrategy<PrincipalActionContext> exeMock = context.mock(ExecutionStrategy.class);

    /**
     * Test. Put in 3 things, get a count of 3.
     */
    @Test
    public void execute()
    {
        final PagedSet<ActivityDTO> activities = new PagedSet<ActivityDTO>();
        List<ActivityDTO> activityList = new ArrayList<ActivityDTO>();
        activityList.add(new ActivityDTO());
        activityList.add(new ActivityDTO());
        activityList.add(new ActivityDTO());

        activities.setPagedSet(activityList);

        sut = new GetUnseenActivityCountByCompositeStreamExecution(exeMock);

        context.checking(new Expectations()
        {
            {
                oneOf(exeMock).execute(null);
                will(returnValue(activities));
            }
        });

        Integer result = (Integer) sut.execute(null);
        Assert.assertEquals(new Integer(3), result);
    }
}
