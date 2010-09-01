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
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests populating the information about comments and activities deletability.
 */
public class PopulateActivityDTODeletabilityDataTest
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
    private static PopulateActivityDTODeletabilityData sut;

    /**
     * Comment delete strategy.
     */
    private static CommentDeletePropertyStrategy commentDeleteStrategy = CONTEXT
            .mock(CommentDeletePropertyStrategy.class);

    /**
     * Property delete strategy.
     */
    private static ActivityDeletePropertyStrategy activityDeleteStrategy = CONTEXT
            .mock(ActivityDeletePropertyStrategy.class);

    /**
     * Activity.
     */
    private static ActivityDTO activity = CONTEXT.mock(ActivityDTO.class);

    /**
     * First comment.
     */
    private static CommentDTO firstComment = CONTEXT.mock(CommentDTO.class, "firstComment");

    /**
     * Last comment.
     */
    private static CommentDTO lastComment = CONTEXT.mock(CommentDTO.class, "lastComment");

    /**
     * Setup fixtures.
     */
    @BeforeClass
    public static void setup()
    {
        sut = new PopulateActivityDTODeletabilityData(commentDeleteStrategy, activityDeleteStrategy);
    }

    /**
     * Test filtering without comments.
     */
    @Test
    public void testFilterNoComment()
    {
        final PersonModelView user = new PersonModelView();
        user.setAccountId("accountid");

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(activityDeleteStrategy).execute(user.getAccountId(), activity);

                oneOf(activity).getFirstComment();
                will(returnValue(null));
            }
        });

        sut.filter(Arrays.asList(activity), user);

        CONTEXT.assertIsSatisfied();
    }


    /**
     * Test filtering with one comment.
     */
    @Test
    public void testFilterOneComment()
    {

        final PersonModelView user = new PersonModelView();
        user.setAccountId("accountid");

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(activityDeleteStrategy).execute(user.getAccountId(), activity);

                allowing(activity).getFirstComment();
                will(returnValue(firstComment));

                allowing(activity).getLastComment();
                will(returnValue(null));

                oneOf(commentDeleteStrategy).execute(with(user.getAccountId()), with(activity), with(any(List.class)));
            }
        });

        sut.filter(Arrays.asList(activity), user);

        CONTEXT.assertIsSatisfied();
    }
    
    /**
     * Test filtering with comments.
     */
    @Test
    public void testFilterComments()
    {

        final PersonModelView user = new PersonModelView();
        user.setAccountId("accountid");

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(activityDeleteStrategy).execute(user.getAccountId(), activity);

                allowing(activity).getFirstComment();
                will(returnValue(firstComment));

                allowing(activity).getLastComment();
                will(returnValue(lastComment));

                oneOf(commentDeleteStrategy).execute(with(user.getAccountId()), with(activity), with(any(List.class)));
            }
        });

        sut.filter(Arrays.asList(activity), user);

        CONTEXT.assertIsSatisfied();
    }
}
