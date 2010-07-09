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
package org.eurekastreams.server.action.validation.stream;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.stream.BulkActivitiesMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for DeleteUserActivityValidation class.
 * 
 */
public class DeleteUserActivityValidationTest
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
     * {@link BulkActivitiesMapper}.
     */
    private BulkActivitiesMapper activityMapper = context.mock(BulkActivitiesMapper.class);

    /**
     * {@link PrincipalActionContext}.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * {@link Principal}.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * Principal id used for test.
     */
    private Long principalId = 9L;

    /**
     * {@link ActivityDTO}.
     */
    private ActivityDTO activity1 = context.mock(ActivityDTO.class, "activity1");

    /**
     * {@link StreamEntityDTO}.
     */
    private StreamEntityDTO actor1 = context.mock(StreamEntityDTO.class, "actor1");

    /**
     * {@link ActivityDTO}.
     */
    private ActivityDTO activity2 = context.mock(ActivityDTO.class, "activity2");

    /**
     * {@link StreamEntityDTO}.
     */
    private StreamEntityDTO actor2 = context.mock(StreamEntityDTO.class, "actor2");

    /**
     * System under test.
     */
    private DeleteUserActivityValidation sut = new DeleteUserActivityValidation(activityMapper);

    /**
     * Test.
     */
    @Test
    public void testAllPass()
    {
        final ArrayList<ActivityDTO> activityDTOs = new ArrayList<ActivityDTO>(Arrays.asList(activity1, activity2));

        final ArrayList<Long> activityIds = new ArrayList<Long>(Arrays.asList(4L, 5L));

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(activityIds));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getId();
                will(returnValue(principalId));

                allowing(activityMapper).execute(activityIds, null);
                will(returnValue(activityDTOs));

                allowing(activity1).getActor();
                will(returnValue(actor1));

                allowing(activity2).getActor();
                will(returnValue(actor2));

                allowing(activity1).getId();
                will(returnValue(4L));

                allowing(activity2).getId();
                will(returnValue(5L));

                allowing(actor1).getId();
                will(returnValue(principalId));

                allowing(actor2).getId();
                will(returnValue(principalId));

            }
        });

        sut.validate(actionContext);

        // assert that the list still has all activities
        assertEquals(2, activityIds.size());

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testNotAllPass()
    {
        final ArrayList<ActivityDTO> activityDTOs = new ArrayList<ActivityDTO>(Arrays.asList(activity1, activity2));

        final ArrayList<Long> activityIds = new ArrayList<Long>(Arrays.asList(4L, 5L));

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(activityIds));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getId();
                will(returnValue(principalId));

                allowing(activityMapper).execute(activityIds, null);
                will(returnValue(activityDTOs));

                allowing(activity1).getActor();
                will(returnValue(actor1));

                allowing(activity2).getActor();
                will(returnValue(actor2));

                allowing(activity1).getId();
                will(returnValue(4L));

                allowing(activity2).getId();
                will(returnValue(5L));

                allowing(actor1).getId();
                will(returnValue(principalId));

                // This one will allow the id to get pulled.
                allowing(actor2).getId();
                will(returnValue(principalId + 1));

            }
        });

        sut.validate(actionContext);

        // assert that the id was pulled from list
        assertEquals(1, activityIds.size());

        context.assertIsSatisfied();
    }
}
