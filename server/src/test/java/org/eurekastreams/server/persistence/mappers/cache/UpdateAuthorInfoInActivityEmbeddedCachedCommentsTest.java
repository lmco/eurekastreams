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
package org.eurekastreams.server.persistence.mappers.cache;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for UpdateAuthorInfoInActivityEmbeddedCachedComments.
 */
public class UpdateAuthorInfoInActivityEmbeddedCachedCommentsTest
{
    /**
     * mock context.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Activity.
     */
    private ActivityDTO activity;

    /**
     * Person.
     */
    private Person person;

    /**
     * Updaters.
     */
    private List<UpdateCommentDTOFromPerson> updaters;

    /**
     * Updater #1.
     */
    private UpdateCommentDTOFromPerson updater1;

    /**
     * Updater #2.
     */
    private UpdateCommentDTOFromPerson updater2;

    /**
     * Person id.
     */
    private final Long personId = 29827L;

    /**
     * System under test.
     */
    private UpdateAuthorInfoInActivityEmbeddedCachedComments sut;

    /**
     * First comment.
     */
    private CommentDTO firstComment;

    /**
     * Last comment.
     */
    private CommentDTO lastComment;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        activity = context.mock(ActivityDTO.class);
        person = context.mock(Person.class);
        updaters = new ArrayList<UpdateCommentDTOFromPerson>();
        firstComment = context.mock(CommentDTO.class, "firstComment");
        lastComment = context.mock(CommentDTO.class, "lastComment");
        updater1 = context.mock(UpdateCommentDTOFromPerson.class, "updater1");
        updater2 = context.mock(UpdateCommentDTOFromPerson.class, "updater2");

        context.checking(new Expectations()
        {
            {
                allowing(person).getId();
                will(returnValue(personId));
            }
        });

        sut = new UpdateAuthorInfoInActivityEmbeddedCachedComments(3, updaters);
    }

    /**
     * Test updateCachedEntity() - with no comments.
     */
    @Test
    public void testUpdateCachedEntityWithNoComments()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(activity).getFirstComment();
                will(returnValue(null));

                oneOf(activity).getLastComment();
                will(returnValue(null));
            }
        });

        boolean returnValue = sut.updateCachedEntity(activity, person);
        assertFalse(returnValue);

        context.assertIsSatisfied();
    }

    /**
     * Test updateCachedEntity() - when the first and last comments are authored by the person, and there are no
     * changes.
     */
    @Test
    public void testUpdateCachedEntityWhenNotAuthoredFirstAndLastCommentsAndNoChange()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(firstComment).getAuthorId();
                will(returnValue(personId + 1));

                oneOf(lastComment).getAuthorId();
                will(returnValue(personId + 1));

                oneOf(activity).getFirstComment();
                will(returnValue(firstComment));

                oneOf(activity).getLastComment();
                will(returnValue(lastComment));
            }
        });

        boolean returnValue = sut.updateCachedEntity(activity, person);
        assertFalse(returnValue);

        context.assertIsSatisfied();
    }

    /**
     * Test updateCachedEntity() - when the first and last comments are authored by the person, and there are no
     * changes.
     */
    @Test
    public void testUpdateCachedEntityWhenAuthoredFirstAndLastCommentsAndNoChange()
    {
        setPersonAsAuthorOfFirstAndLastComments();

        context.checking(new Expectations()
        {
            {
                oneOf(updater1).execute(firstComment, person);
                will(returnValue(false));

                oneOf(updater2).execute(firstComment, person);
                will(returnValue(false));

                oneOf(updater1).execute(lastComment, person);
                will(returnValue(false));

                oneOf(updater2).execute(lastComment, person);
                will(returnValue(false));
            }
        });

        updaters.add(updater1);
        updaters.add(updater2);

        boolean returnValue = sut.updateCachedEntity(activity, person);
        assertFalse(returnValue);

        context.assertIsSatisfied();
    }

    /**
     * Test updateCachedEntity().
     */
    @Test
    public void testUpdateCachedEntityWhenAuthoredFirstAndLastCommentsAndOneChange()
    {
        setPersonAsAuthorOfFirstAndLastComments();

        context.checking(new Expectations()
        {
            {
                oneOf(updater1).execute(firstComment, person);
                will(returnValue(false));

                oneOf(updater2).execute(firstComment, person);
                will(returnValue(true));

                oneOf(updater1).execute(lastComment, person);
                will(returnValue(false));

                oneOf(updater2).execute(lastComment, person);
                will(returnValue(false));
            }
        });

        updaters.add(updater1);
        updaters.add(updater2);

        boolean returnValue = sut.updateCachedEntity(activity, person);
        assertTrue(returnValue);

        context.assertIsSatisfied();
    }

    /**
     * Test updateCachedEntity().
     */
    @Test
    public void testUpdateCachedEntityWhenAuthoredFirstAndLastCommentsAndManyChanges()
    {
        setPersonAsAuthorOfFirstAndLastComments();

        context.checking(new Expectations()
        {
            {
                oneOf(updater1).execute(firstComment, person);
                will(returnValue(true));

                oneOf(updater2).execute(firstComment, person);
                will(returnValue(false));

                oneOf(updater1).execute(lastComment, person);
                will(returnValue(true));

                oneOf(updater2).execute(lastComment, person);
                will(returnValue(false));
            }
        });

        updaters.add(updater1);
        updaters.add(updater2);

        boolean returnValue = sut.updateCachedEntity(activity, person);
        assertTrue(returnValue);

        context.assertIsSatisfied();
    }

    /**
     * Set the user as the author of the first and last comment.
     */
    private void setPersonAsAuthorOfFirstAndLastComments()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(firstComment).getAuthorId();
                will(returnValue(personId));

                oneOf(lastComment).getAuthorId();
                will(returnValue(personId));

                oneOf(activity).getFirstComment();
                will(returnValue(firstComment));

                oneOf(activity).getLastComment();
                will(returnValue(lastComment));
            }
        });
    }
}
