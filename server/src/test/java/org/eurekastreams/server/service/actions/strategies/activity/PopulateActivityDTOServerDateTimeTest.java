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
import java.util.Date;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests populating DTOs with server time.
 */
public class PopulateActivityDTOServerDateTimeTest
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
     * System under test.
     */
    private PopulateActivityDTOServerDateTime sut = new PopulateActivityDTOServerDateTime();

    /**
     * Test filtering without comments.
     */
    @Test
    public void testFilterNoComment()
    {
        final ActivityDTO activity = context.mock(ActivityDTO.class);
        PersonModelView user = new PersonModelView();

        context.checking(new Expectations()
        {
            {
                oneOf(activity).setServerDateTime(with(any(Date.class)));
                
                oneOf(activity).getFirstComment();
                will(returnValue(null));

                oneOf(activity).getLastComment();
                will(returnValue(null));
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
        final ActivityDTO activity = context.mock(ActivityDTO.class);
        final CommentDTO firstComment = context.mock(CommentDTO.class, "firstComment");
        final CommentDTO lastComment = context.mock(CommentDTO.class, "lastComment");
        
        PersonModelView user = new PersonModelView();

        context.checking(new Expectations()
        {
            {
                oneOf(activity).setServerDateTime(with(any(Date.class)));
                
                allowing(activity).getFirstComment();
                will(returnValue(firstComment));

                allowing(activity).getLastComment();
                will(returnValue(lastComment));

                oneOf(firstComment).setServerDateTime(with(any(Date.class)));
                oneOf(lastComment).setServerDateTime(with(any(Date.class)));
            }
        });

        sut.filter(Arrays.asList(activity), user);

        context.assertIsSatisfied();
    }
}
