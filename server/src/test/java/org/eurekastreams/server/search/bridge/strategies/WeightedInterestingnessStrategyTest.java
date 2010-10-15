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
package org.eurekastreams.server.search.bridge.strategies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.db.GetCommentorIdsByActivityId;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the weighted interestingness strategy for activities.
 */
public class WeightedInterestingnessStrategyTest
{
    /**
     * Context for building mock objects.
     */
    private static final Mockery CONTEXT = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private static WeightedInterestingnessStrategy sut;

    /**
     * Get ordered comment IDs DAO mock.
     */
    private static DomainMapper<Long, List<Long>> commentIdsByActivityIdDAOMock = CONTEXT.mock(DomainMapper.class,
            "commentIdsByActivityIdDAOMock");

    /**
     * Get unique commentors IDs DAO mock.
     */
    private static GetCommentorIdsByActivityId commentorIdsByActivityDAOMock = CONTEXT
            .mock(GetCommentorIdsByActivityId.class);

    /**
     * DAO for finding likes.
     */
    private static DomainMapper<Collection<Long>, Collection<Collection<Long>>> getPeopleWhoLikedMapper = CONTEXT
            .mock(DomainMapper.class);

    /**
     * Activity mock.
     */
    private static Activity activity = CONTEXT.mock(Activity.class);

    /**
     * Comment weight.
     */
    private static final int COMMENT_WEIGHT = 2;

    /**
     * Unique commentor weight.
     */
    private static final int COMMENTOR_WEIGHT = 1;

    /**
     * Like weight.
     */
    private static final int LIKE_WEIGHT = 1;

    /**
     * Time weight.
     */
    private static final int TIME_WEIGHT = 15;

    /**
     * Setup fixtures.
     */
    @BeforeClass
    public static final void setUp()
    {
        sut = new WeightedInterestingnessStrategy(commentIdsByActivityIdDAOMock, commentorIdsByActivityDAOMock,
                getPeopleWhoLikedMapper, COMMENT_WEIGHT, COMMENTOR_WEIGHT, LIKE_WEIGHT, TIME_WEIGHT);
    }

    /**
     * Test with uninteresting content that returns null for comment information.
     */
    @Test
    public void uninterestingNullTest()
    {
        final Long activityID = 1L;

        CONTEXT.checking(new Expectations()
        {
            {
                allowing(activity).getId();
                will(returnValue(activityID));

                oneOf(commentIdsByActivityIdDAOMock).execute(activityID);
                will(returnValue(null));

                oneOf(commentorIdsByActivityDAOMock).execute(activityID);
                will(returnValue(null));

                oneOf(getPeopleWhoLikedMapper).execute(Arrays.asList(activityID));
                will(returnValue(null));
            }
        });

        Assert.assertEquals(new Long(0L), sut.computeInterestingness(activity));
    }

    /**
     * Test with uninteresting content that returns zero comment information.
     */
    @Test
    public void uninterestingZeroTest()
    {
        final Long activityID = 1L;
        final List<Long> commentIds = new ArrayList<Long>();
        final List<Long> commentorIds = new ArrayList<Long>();
        final Collection<Collection<Long>> likers = new ArrayList<Collection<Long>>();

        CONTEXT.checking(new Expectations()
        {
            {
                allowing(activity).getId();
                will(returnValue(activityID));

                oneOf(commentIdsByActivityIdDAOMock).execute(activityID);
                will(returnValue(commentIds));

                oneOf(commentorIdsByActivityDAOMock).execute(activityID);
                will(returnValue(commentorIds));

                oneOf(getPeopleWhoLikedMapper).execute(Arrays.asList(activityID));
                will(returnValue(likers));
            }
        });

        Assert.assertEquals(new Long(0L), sut.computeInterestingness(activity));
    }

    /**
     * Test with interesting.
     */
    @Test
    public void interestingTest()
    {
        final Long activityID = 1L;
        final List<Long> commentIds = new ArrayList<Long>();
        commentIds.add(1L);
        commentIds.add(2L);

        final List<Long> commentorIds = new ArrayList<Long>();
        commentorIds.add(1L);
        commentorIds.add(2L);

        final List<List<Long>> likers = Arrays.asList(Arrays.asList(1L, 2L));

        final Date postedTime = CONTEXT.mock(Date.class);

        final Long postedTimeLong = 1265068800L;

        CONTEXT.checking(new Expectations()
        {
            {
                allowing(activity).getId();
                will(returnValue(activityID));

                oneOf(activity).getPostedTime();
                will(returnValue(postedTime));

                oneOf(postedTime).getTime();
                will(returnValue(postedTimeLong));

                oneOf(commentIdsByActivityIdDAOMock).execute(activityID);
                will(returnValue(commentIds));

                oneOf(commentorIdsByActivityDAOMock).execute(activityID);
                will(returnValue(commentorIds));

                oneOf(getPeopleWhoLikedMapper).execute(Arrays.asList(activityID));
                will(returnValue(likers));
            }
        });

        Long interestingness = postedTimeLong / WeightedInterestingnessStrategy.MILLISECONDS_IN_DAY * TIME_WEIGHT;
        interestingness += commentIds.size() * COMMENT_WEIGHT;
        interestingness += commentorIds.size() * COMMENTOR_WEIGHT;
        interestingness += likers.iterator().next().size() * LIKE_WEIGHT;

        Assert.assertEquals(interestingness, sut.computeInterestingness(activity));
    }
}
