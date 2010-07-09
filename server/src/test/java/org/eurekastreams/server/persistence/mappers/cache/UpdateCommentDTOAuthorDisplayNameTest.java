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

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for UpdateCommentDTOAuthorDisplayName.
 */
public class UpdateCommentDTOAuthorDisplayNameTest
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
     * System under test.
     */
    private UpdateCommentDTOAuthorDisplayName sut = new UpdateCommentDTOAuthorDisplayName();

    /**
     * test execute with a new display name.
     */
    @Test
    public void testExecuteWithNewDisplayName()
    {
        final String oldDisplayName = "sdlfjsdfkdfjsdf";
        final String newDisplayName = "dksdkdkdkdk";

        final CommentDTO comment = context.mock(CommentDTO.class);
        final Person person = context.mock(Person.class);

        context.checking(new Expectations()
        {
            {
                oneOf(comment).getAuthorDisplayName();
                will(returnValue(oldDisplayName));

                oneOf(person).getDisplayName();
                will(returnValue(newDisplayName));

                oneOf(comment).setAuthorDisplayName(newDisplayName);
            }
        });

        sut.execute(comment, person);

        context.assertIsSatisfied();
    }

    /**
     * test execute with a same display name.
     */
    @Test
    public void testExecuteWithSameDisplayName()
    {
        final String oldDisplayName = "sdlfjsdfkdfjsdf";
        final String newDisplayName = oldDisplayName;

        final CommentDTO comment = context.mock(CommentDTO.class);
        final Person person = context.mock(Person.class);

        context.checking(new Expectations()
        {
            {
                oneOf(comment).getAuthorDisplayName();
                will(returnValue(oldDisplayName));

                oneOf(person).getDisplayName();
                will(returnValue(newDisplayName));
            }
        });

        sut.execute(comment, person);

        context.assertIsSatisfied();
    }
}
