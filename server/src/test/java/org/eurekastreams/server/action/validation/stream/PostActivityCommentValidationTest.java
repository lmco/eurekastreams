/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.testing.TestContextCreator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for PostActivityCommentValidation.
 */
public class PostActivityCommentValidationTest
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
     * Comment DTO.
     */
    private final CommentDTO comment = context.mock(CommentDTO.class);

    /**
     * System under test.
     */
    private final ValidationStrategy<ActionContext> sut = new PostActivityCommentValidation();

    /**
     * Core of all tests that have a comment.
     *
     * @param commentBody
     *            Text for comment body.
     */
    private void coreTest(final String commentBody)
    {
        context.checking(new Expectations()
        {
            {
                oneOf(comment).getBody();
                will(returnValue(commentBody));
            }
        });

        sut.validate(TestContextCreator.createPrincipalActionContext(comment, null));
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testSuccess()
    {
        coreTest("commentBody");
    }


    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testNullCommentBody()
    {
        coreTest(null);
    }

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testEmptyCommentBody()
    {
        coreTest("");
    }

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testCommentBodyJustSpaces()
    {
        coreTest(" \t  \t ");
    }

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testNullContextParams()
    {
        sut.validate(TestContextCreator.createPrincipalActionContext(null, null));
        context.assertIsSatisfied();
    }

}
