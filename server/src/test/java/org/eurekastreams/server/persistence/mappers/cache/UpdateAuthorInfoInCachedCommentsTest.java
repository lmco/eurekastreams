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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for UpdateAuthorInfoInCachedComments.
 */
public class UpdateAuthorInfoInCachedCommentsTest
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
     * CommentDTO updater #1.
     */
    private UpdateCommentDTOFromPerson updater1;

    /**
     * CommentDTO updater #1.
     */
    private UpdateCommentDTOFromPerson updater2;

    /**
     * Comment to mock.
     */
    private CommentDTO comment;

    /**
     * Person to mock.
     */
    private Person person;

    /**
     * list of updaters to pass into SUT.
     */
    private List<UpdateCommentDTOFromPerson> authorCommentDTOUpdaters;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        updater1 = context.mock(UpdateCommentDTOFromPerson.class, "updater1");
        updater2 = context.mock(UpdateCommentDTOFromPerson.class, "updater2");
        comment = context.mock(CommentDTO.class);
        person = context.mock(Person.class);
        authorCommentDTOUpdaters = new ArrayList<UpdateCommentDTOFromPerson>();
    }

    /**
     * Test getCacheKeyPrefix.
     */
    @Test
    public void testGetCacheKeyPrefix()
    {
        UpdateAuthorInfoInCachedComments sut = new UpdateAuthorInfoInCachedComments(9,
                new ArrayList<UpdateCommentDTOFromPerson>());
        assertEquals(CacheKeys.COMMENT_BY_ID, sut.getCacheKeyPrefix());
    }

    /**
     * Test updateCachedEntity with two updaters, no change.
     */
    @Test
    public void testUpdateCachedEntityNoChanges()
    {
        authorCommentDTOUpdaters.add(updater1);
        authorCommentDTOUpdaters.add(updater2);

        boolean result = setupTest(new Expectations()
        {
            {
                oneOf(updater1).execute(comment, person);
                will(returnValue(false));

                oneOf(updater2).execute(comment, person);
                will(returnValue(false));
            }
        });

        assertFalse(result);
    }

    /**
     * Test updateCachedEntity with two updaters, one change.
     */
    @Test
    public void testUpdateCachedEntityOneOfTwoUpdaterChanges()
    {
        authorCommentDTOUpdaters.add(updater1);
        authorCommentDTOUpdaters.add(updater2);

        boolean result = setupTest(new Expectations()
        {
            {
                oneOf(updater1).execute(comment, person);
                will(returnValue(false));

                oneOf(updater2).execute(comment, person);
                will(returnValue(true));
            }
        });

        assertTrue(result);
    }

    /**
     * Test updateCachedEntity with two updaters, two changes.
     */
    @Test
    public void testUpdateCachedEntityTwoOfTwoUpdaterChanges()
    {
        authorCommentDTOUpdaters.add(updater1);
        authorCommentDTOUpdaters.add(updater2);

        boolean result = setupTest(new Expectations()
        {
            {
                oneOf(updater1).execute(comment, person);
                will(returnValue(true));

                oneOf(updater2).execute(comment, person);
                will(returnValue(true));
            }
        });

        assertTrue(result);
    }

    /**
     * Test updateCachedEntity with no updaters, no change.
     */
    @Test
    public void testUpdateCachedEntityNoUpdaters()
    {
        boolean result = setupTest(new Expectations()
        {
            {
                int noOp = 0;
                noOp++;
            }
        });

        assertFalse(result);
    }

    /**
     * Helper to setup the updateCachedEntity tests.
     *
     * @param expectations
     *            the expectations on the test.
     * @return the result of updateCachedEntity
     */
    private boolean setupTest(final Expectations expectations)
    {
        UpdateAuthorInfoInCachedComments sut = new UpdateAuthorInfoInCachedComments(9, authorCommentDTOUpdaters);

        context.checking(expectations);

        // invoke sut
        boolean isChanged = sut.updateCachedEntity(comment, person);

        context.assertIsSatisfied();

        return isChanged;
    }
}
