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

import java.io.Serializable;

import org.eurekastreams.commons.actions.InlineActionExecutor;
import org.eurekastreams.commons.actions.TaskHandlerAction;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.service.utility.TextSplitter;

/**
 * Execution strategy for posting multiple comments to an activity. Splits the input string into comment-sized pieces.
 */
public class PostSplitActivityCommentsExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /** For splitting the input comment into allowed size pieces. */
    private final TextSplitter textSplitter;

    /** For executing the child actions. */
    private final InlineActionExecutor executor;

    /** Action to post a comment. */
    private final TaskHandlerAction postCommentAction;

    /**
     * Constructor.
     *
     * @param inTextSplitter
     *            For splitting the input comment into allowed size pieces.
     * @param inExecutor
     *            For executing the child actions.
     * @param inPostCommentAction
     *            Action to post a comment.
     */
    public PostSplitActivityCommentsExecution(final TextSplitter inTextSplitter,
            final InlineActionExecutor inExecutor, final TaskHandlerAction inPostCommentAction)
    {
        textSplitter = inTextSplitter;
        executor = inExecutor;
        postCommentAction = inPostCommentAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
    {
        // use a CommentDTO as input so this action can use the same authorizer as postActivityCommentAction
        CommentDTO inputComment = (CommentDTO) inActionContext.getActionContext().getParams();
        String inputText = inputComment.getBody();

        for (String piece : textSplitter.split(inputText))
        {
            CommentDTO thisComment = new CommentDTO();
            thisComment.setBody(piece);
            thisComment.setActivityId(inputComment.getActivityId());
            executor.execute(postCommentAction, inActionContext, thisComment);
        }
        return null;
    }
}
