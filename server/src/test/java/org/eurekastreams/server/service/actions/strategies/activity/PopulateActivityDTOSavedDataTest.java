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
package org.eurekastreams.server.service.actions.strategies.activity;

import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.stream.GetStarredActivityIds;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests populating the DTOs with personalized saved data.
 */
public class PopulateActivityDTOSavedDataTest
{
    /**
     * Mocking context.
     */
    private static final JUnit4Mockery CONTEXT = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private static PopulateActivityDTOSavedData sut;

    /**
     * Starred activity mapper.
     */
    private static GetStarredActivityIds starredMapper = CONTEXT.mock(GetStarredActivityIds.class);

    /**
     * Setup fixtures.
     */
    @BeforeClass
    public static void setup()
    {
        sut = new PopulateActivityDTOSavedData(starredMapper);
    }

    /**
     * Test when activity is not saved by the user.
     */
    @Test
    public void testNotSaved()
    {
        final Long activityId = 2L;
        final Long personId = 1L;

        final ActivityDTO activity = new ActivityDTO();
        activity.setId(activityId);

        final PersonModelView user = new PersonModelView();
        user.setEntityId(personId);

        final List<ActivityDTO> activities = Arrays.asList(activity);
        
        final List<Long> starredActivities = Arrays.asList(3L);

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(starredMapper).execute(personId);
                will(returnValue(starredActivities));
            }
        });

        sut.filter(activities, user);

        Assert.assertFalse(activity.isStarred());

        CONTEXT.assertIsSatisfied();
    }

    /**
     * Test when activity is saved by the user.
     */
    @Test
    public void testSaved()
    {
        final Long activityId = 2L;
        final Long personId = 1L;

        final ActivityDTO activity = new ActivityDTO();
        activity.setId(activityId);

        final PersonModelView user = new PersonModelView();
        user.setEntityId(personId);

        final List<ActivityDTO> activities = Arrays.asList(activity);
        
        final List<Long> starredActivities = Arrays.asList(activityId);

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(starredMapper).execute(personId);
                will(returnValue(starredActivities));
            }
        });

        sut.filter(activities, user);

        Assert.assertTrue(activity.isStarred());

        CONTEXT.assertIsSatisfied();        
    }
}
