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
import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests populating activity liked data.
 */
public class PopulateActivityDTOLikeDateTest
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
     * Liked activity by user mapper.
     */
    private static DomainMapper<List<Long>, List<List<Long>>> getLikedActivityByUser = CONTEXT.mock(DomainMapper.class,
            "getLikedActivityByUser");

    /**
     * People who liked activity mapper.
     */
    private static DomainMapper<List<Long>, List<List<Long>>> getPeopleWhoLikedActivity = CONTEXT.mock(
            DomainMapper.class, "getPeopleWhoLikedActivity");

    /**
     * People mapper.
     */
    private static DomainMapper<List<Long>, List<PersonModelView>> peopleMapper = CONTEXT.mock(DomainMapper.class);

    /**
     * System under test.
     */
    private static PopulateActivityDTOLikeData sut;

    /**
     * Liker limit.
     */
    private static final int LIKER_LIMIT = 10;

    /**
     * Setup fixtures.
     */
    @BeforeClass
    public static void setup()
    {
        sut = new PopulateActivityDTOLikeData(getLikedActivityByUser, getPeopleWhoLikedActivity, peopleMapper,
                LIKER_LIMIT);
    }

    /**
     * Tests getting liked activity for single DTO that the user has liked.
     */
    @Test
    public void testSingleDTO()
    {
        final Long activityId = 2L;
        final Long personId = 1L;

        final ActivityDTO activity = new ActivityDTO();
        activity.setId(activityId);

        final PersonModelView user = new PersonModelView();
        user.setEntityId(personId);

        final List<ActivityDTO> activities = Arrays.asList(activity);

        final List<Long> likedActivityForUser = new LinkedList<Long>();
        likedActivityForUser.add(activityId);
        likedActivityForUser.add(3L);
        likedActivityForUser.add(4L);

        final List<Long> usersWhoLikedActivity = new LinkedList<Long>();
        usersWhoLikedActivity.add(personId);
        usersWhoLikedActivity.add(7L);
        usersWhoLikedActivity.add(6L);

        final List<PersonModelView> usersDTOs = new LinkedList<PersonModelView>();
        usersDTOs.add(new PersonModelView());
        usersDTOs.add(new PersonModelView());
        usersDTOs.add(new PersonModelView());

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(getLikedActivityByUser).execute(Arrays.asList(personId));
                will(returnValue(Arrays.asList(likedActivityForUser)));

                oneOf(getPeopleWhoLikedActivity).execute(Arrays.asList(activityId));
                will(returnValue(Arrays.asList(usersWhoLikedActivity)));

                oneOf(peopleMapper).execute(usersWhoLikedActivity);
                will(returnValue(usersDTOs));
            }
        });

        sut.filter(activities, user);

        Assert.assertTrue(activity.isLiked());
        Assert.assertEquals(usersDTOs.size(), activity.getLikers().size());

        CONTEXT.assertIsSatisfied();
    }

    /**
     * Tests getting liked activity for single DTO that the user has not liked.
     */
    @Test
    public void testSingleDTONotLiked()
    {
        final Long activityId = 2L;
        final Long personId = 1L;

        final ActivityDTO activity = new ActivityDTO();
        activity.setId(activityId);

        final PersonModelView user = new PersonModelView();
        user.setEntityId(personId);

        final List<ActivityDTO> activities = Arrays.asList(activity);

        final List<Long> likedActivityForUser = new LinkedList<Long>();
        likedActivityForUser.add(1L);
        likedActivityForUser.add(3L);
        likedActivityForUser.add(4L);

        final List<Long> usersWhoLikedActivity = new LinkedList<Long>();
        usersWhoLikedActivity.add(5L);
        usersWhoLikedActivity.add(7L);
        usersWhoLikedActivity.add(6L);

        final List<PersonModelView> usersDTOs = new LinkedList<PersonModelView>();
        usersDTOs.add(new PersonModelView());
        usersDTOs.add(new PersonModelView());
        usersDTOs.add(new PersonModelView());

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(getLikedActivityByUser).execute(Arrays.asList(personId));
                will(returnValue(Arrays.asList(likedActivityForUser)));

                oneOf(getPeopleWhoLikedActivity).execute(Arrays.asList(activityId));
                will(returnValue(Arrays.asList(usersWhoLikedActivity)));

                oneOf(peopleMapper).execute(usersWhoLikedActivity);
                will(returnValue(usersDTOs));
            }
        });

        sut.filter(activities, user);

        Assert.assertFalse(activity.isLiked());
        Assert.assertEquals(usersDTOs.size(), activity.getLikers().size());

        CONTEXT.assertIsSatisfied();
    }

    /**
     * Tests getting liked activity for single DTO that the user has not liked. There are more likes than will be
     * displayed.
     */
    @Test
    public void testSingleDTONotLikedManyLikes()
    {
        final Long activityId = 2L;
        final Long personId = 1L;

        final ActivityDTO activity = new ActivityDTO();
        activity.setId(activityId);

        final PersonModelView user = new PersonModelView();
        user.setEntityId(personId);

        final List<ActivityDTO> activities = Arrays.asList(activity);

        final List<Long> likedActivityForUser = new LinkedList<Long>();
        likedActivityForUser.add(1L);
        likedActivityForUser.add(3L);
        likedActivityForUser.add(4L);

        final int numberOfLikes = 100;

        final List<Long> usersWhoLikedActivity = new LinkedList<Long>();
        for (int i = 0; i < numberOfLikes; i++)
        {
            usersWhoLikedActivity.add(new Long(i));
        }

        final List<PersonModelView> usersDTOs = new LinkedList<PersonModelView>();
        usersDTOs.add(new PersonModelView());
        usersDTOs.add(new PersonModelView());
        usersDTOs.add(new PersonModelView());
        usersDTOs.add(new PersonModelView());
        usersDTOs.add(new PersonModelView());
        usersDTOs.add(new PersonModelView());
        usersDTOs.add(new PersonModelView());
        usersDTOs.add(new PersonModelView());
        usersDTOs.add(new PersonModelView());
        usersDTOs.add(new PersonModelView());

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(getLikedActivityByUser).execute(Arrays.asList(personId));
                will(returnValue(Arrays.asList(likedActivityForUser)));

                oneOf(getPeopleWhoLikedActivity).execute(Arrays.asList(activityId));
                will(returnValue(Arrays.asList(usersWhoLikedActivity)));

                oneOf(peopleMapper).execute(usersWhoLikedActivity.subList(0, LIKER_LIMIT));
                will(returnValue(usersDTOs));
            }
        });

        sut.filter(activities, user);

        Assert.assertFalse(activity.isLiked());
        Assert.assertEquals(usersDTOs.size(), activity.getLikers().size());

        CONTEXT.assertIsSatisfied();
    }
}
