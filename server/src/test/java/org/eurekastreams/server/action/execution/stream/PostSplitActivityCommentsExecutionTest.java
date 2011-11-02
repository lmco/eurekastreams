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
package org.eurekastreams.server.action.execution.stream;

import java.util.Arrays;

import org.eurekastreams.commons.actions.InlineActionExecutor;
import org.eurekastreams.commons.actions.TaskHandlerAction;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.test.EasyMatcher;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.service.utility.TextSplitter;
import org.eurekastreams.server.testing.TestContextCreator;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests PostSplitActivityCommentsExecution.
 */
public class PostSplitActivityCommentsExecutionTest
{
    /** Test data. */
    private static final long ACTIVITY_ID = 100L;

    /** Used for mocking objects. */
    private final Mockery mockery = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Test.
     */
    @Test
    public void test()
    {
        final TextSplitter textSplitter = mockery.mock(TextSplitter.class, "textSplitter");
        final InlineActionExecutor executor = mockery.mock(InlineActionExecutor.class, "executor");
        final TaskHandlerAction postCommentAction = mockery.mock(TaskHandlerAction.class, "postCommentAction");

        final String inputString = "  This is the text to be split and posted as multiple comments.  ";
        final String piece1 = "This is the text...";
        final String piece2 = "...to be split and posted...";
        final String piece3 = "...as multiple comments.";

        PostSplitActivityCommentsExecution sut = new PostSplitActivityCommentsExecution(textSplitter, executor,
                postCommentAction);

        final CommentDTO params = mockery.mock(CommentDTO.class, "params");

        final TaskHandlerActionContext<PrincipalActionContext> context = TestContextCreator
                .createTaskHandlerContextWithPrincipal(params, "user", 8L);

        final Sequence seq = mockery.sequence("calls");
        mockery.checking(new Expectations()
        {
            {
                allowing(params).getActivityId();
                will(returnValue(ACTIVITY_ID));
                allowing(params).getBody();
                will(returnValue(inputString));

                oneOf(textSplitter).split(inputString);
                will(returnValue(Arrays.asList(piece1, piece2, piece3)));

                oneOf(executor).execute(with(same(postCommentAction)), with(same(context)),
                        with(getCommentDtoMatcher(piece1)));
                inSequence(seq);
                oneOf(executor).execute(with(same(postCommentAction)), with(same(context)),
                        with(getCommentDtoMatcher(piece2)));
                inSequence(seq);
                oneOf(executor).execute(with(same(postCommentAction)), with(same(context)),
                        with(getCommentDtoMatcher(piece3)));
                inSequence(seq);
            }
        });

        sut.execute(context);
        mockery.assertIsSatisfied();
    }

    /**
     * Creates a matcher to check for the specific comment being created.
     *
     * @param text
     *            Text to expect.
     * @return Matcher.
     */
    private Matcher<CommentDTO> getCommentDtoMatcher(final String text)
    {
        return new EasyMatcher<CommentDTO>()
        {
            @Override
            protected boolean isMatch(final CommentDTO inTestObject)
            {
                return inTestObject.getActivityId() == ACTIVITY_ID && text.equals(inTestObject.getBody());
            }
        };
    }
}
