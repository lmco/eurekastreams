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
import org.eurekastreams.server.persistence.strategies.ActivityDeletePropertyStrategy;
import org.eurekastreams.server.persistence.strategies.CommentDeletePropertyStrategy;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests populating the information about comments and activities deletability.
 */
public class PopulateActivityDTODeletabilityDataTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Comment delete strategy.
     */
    private CommentDeletePropertyStrategy commentDeleteStrategy = context.mock(CommentDeletePropertyStrategy.class);

    /**
     * Property delete strategy.
     */
    private ActivityDeletePropertyStrategy activityDeleteStrategy = context.mock(ActivityDeletePropertyStrategy.class);

    /**
     * Activity.
     */
    private ActivityDTO activity = context.mock(ActivityDTO.class);

    /**
     * First comment.
     */
    private CommentDTO firstComment = context.mock(CommentDTO.class, "firstComment");

    /**
     * Last comment.
     */
    private CommentDTO lastComment = context.mock(CommentDTO.class, "lastComment");

    /**
     * User's account id.
     */
    private final String userAccountId = "accountid";

    /**
     * User's id.
     */
    private final Long userId = 2342L;

    /**
     * System under test.
     */
    private PopulateActivityDTODeletabilityData sut = new PopulateActivityDTODeletabilityData(commentDeleteStrategy,
            activityDeleteStrategy);

    /**
     * mocked current user.
     */
    private final PersonModelView user = context.mock(PersonModelView.class);

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        context.checking(new Expectations()
        {
            {
                allowing(user).getAccountId();
                will(returnValue(userAccountId));

                allowing(user).getId();
                will(returnValue(userId));
            }
        });
    }

    /**
     * Test filtering without comments.
     */
    @Test
    public void testFilterNoComment()
    {

        context.checking(new Expectations()
        {
            {
                oneOf(activityDeleteStrategy).execute(userAccountId, userId, activity);

                oneOf(activity).getFirstComment();
                will(returnValue(null));
            }
        });

        sut.filter(Arrays.asList(activity), user);

        context.assertIsSatisfied();
    }

    /**
     * Test filtering with one comment.
     */
    @Test
    public void testFilterOneComment()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(activityDeleteStrategy).execute(userAccountId, userId, activity);

                allowing(activity).getFirstComment();
                will(returnValue(firstComment));

                allowing(activity).getLastComment();
                will(returnValue(null));

                oneOf(commentDeleteStrategy).execute(with(user.getAccountId()), with(activity), with(any(List.class)));
            }
        });

        sut.filter(Arrays.asList(activity), user);

        context.assertIsSatisfied();
    }

    /**
     * Test filtering with comments.
     */
    @Test
    public void testFilterComments()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(activityDeleteStrategy).execute(userAccountId, userId, activity);

                allowing(activity).getFirstComment();
                will(returnValue(firstComment));

                allowing(activity).getLastComment();
                will(returnValue(lastComment));

                oneOf(commentDeleteStrategy).execute(with(user.getAccountId()), with(activity), with(any(List.class)));
            }
        });

        sut.filter(Arrays.asList(activity), user);

        context.assertIsSatisfied();
    }
}
