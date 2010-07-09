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
package org.eurekastreams.server.action.execution.stream;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.persistence.mappers.stream.DeleteActivityComment;

/**
 * Execution strategy for deleting a comment.
 *
 */
public class DeleteActivityCommentExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * Delete Comment DAO.
     */
    private DeleteActivityComment deleteCommentDAO;

    /**
     * Constructor.
     *
     * @param inDeleteActivityComment
     *            Id of the comment to delete.
     */
    public DeleteActivityCommentExecution(final DeleteActivityComment inDeleteActivityComment)
    {
        deleteCommentDAO = inDeleteActivityComment;
    }

    /**
     * Deletes the Comment.
     *
     * @param inActionContext
     *            {@link ActionContext}.
     * @return true if successful.
     */
    @Override
    public Boolean execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        return deleteCommentDAO.execute((Long) inActionContext.getActionContext().getParams());
    }

}
