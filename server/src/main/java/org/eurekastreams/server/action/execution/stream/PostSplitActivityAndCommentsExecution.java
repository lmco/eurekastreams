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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.actions.InlineActionExecutor;
import org.eurekastreams.commons.actions.TaskHandlerAction;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.action.request.stream.PostSplitActivityAndCommentsRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.ActivityVerb;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.service.utility.TextSplitter;

/**
 * Execution strategy for posting an activity and multiple comments to it. Splits the input string into
 * appropriate-sized pieces.
 */
public class PostSplitActivityAndCommentsExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /** For splitting the input comment into allowed size pieces. */
    private final TextSplitter textSplitter;

    /** For executing the child actions. */
    private final InlineActionExecutor executor;

    /** Actions to post an activity by stream type. */
    private final Map<EntityType, TaskHandlerAction> postActivityActions;

    /** Action to post a comment. */
    private final TaskHandlerAction postCommentAction;

    /** DAOs to fetch unique IDs for stream entities. */
    private final Map<EntityType, DomainMapper<Long, String>> uniqueIdDAOs;

    /**
     * Constructor.
     *
     * @param inTextSplitter
     *            For splitting the input comment into allowed size pieces.
     * @param inExecutor
     *            For executing the child actions.
     * @param inPostActivityActions
     *            Actions to post an activity by stream type.
     * @param inPostCommentAction
     *            Action to post a comment.
     * @param inEntityDAOs
     *            DAOs to fetch unique IDs for stream entities.
     */
    public PostSplitActivityAndCommentsExecution(final TextSplitter inTextSplitter,
            final InlineActionExecutor inExecutor, final Map<EntityType, TaskHandlerAction> inPostActivityActions,
            final TaskHandlerAction inPostCommentAction, final Map<EntityType, DomainMapper<Long, String>> inEntityDAOs)
    {
        textSplitter = inTextSplitter;
        executor = inExecutor;
        postActivityActions = inPostActivityActions;
        postCommentAction = inPostCommentAction;
        uniqueIdDAOs = inEntityDAOs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
    {
        PostSplitActivityAndCommentsRequest request = (PostSplitActivityAndCommentsRequest) inActionContext
                .getActionContext().getParams();

        // get the entity whose stream to post to
        DomainMapper<Long, String> uniqueIdDAO = uniqueIdDAOs.get(request.getEntityType());
        if (uniqueIdDAO == null)
        {
            throw new ValidationException("Request to post to unsupported stream type " + request.getEntityType());
        }
        String uniqueId = uniqueIdDAO.execute(request.getEntityId());

        // split the text
        List<String> pieces = textSplitter.split(request.getText());
        if (pieces.isEmpty())
        {
            throw new ValidationException("Text to post must not be empty.");
        }

        // post the activity
        ActivityDTO activityToPost = new ActivityDTO();
        activityToPost.setBaseObjectType(BaseObjectType.NOTE);
        activityToPost.setVerb(ActivityVerb.POST);
        activityToPost.setDestinationStream(new StreamEntityDTO(request.getEntityType(), uniqueId));
        activityToPost.setBaseObjectProperties(new HashMap<String, String>(Collections.singletonMap("content",
                pieces.get(0))));
        TaskHandlerAction postActivityAction = postActivityActions.get(request.getEntityType());
        ActivityDTO postedActivity = (ActivityDTO) executor.execute(postActivityAction, inActionContext,
                new PostActivityRequest(activityToPost));

        // post the comments
        for (String piece : pieces.subList(1, pieces.size()))
        {
            CommentDTO thisComment = new CommentDTO();
            thisComment.setBody(piece);
            thisComment.setActivityId(postedActivity.getId());
            executor.execute(postCommentAction, inActionContext, thisComment);
        }

        return postedActivity.getId();
    }
}
