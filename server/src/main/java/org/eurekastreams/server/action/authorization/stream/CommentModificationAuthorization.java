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
package org.eurekastreams.server.action.authorization.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.strategies.CommentDeletePropertyStrategy;
import org.eurekastreams.server.search.modelview.CommentDTO;

/**
 * Strategy to determine if user has permission to delete a comment. Currently this allows deletion if user is 1)
 * comment author. 2) activity comment is on is posted to a group and user is group coordinator 3) activity comment is
 * on is posted to a person and user is that person.
 * 
 */
public class CommentModificationAuthorization implements AuthorizationStrategy<PrincipalActionContext>
{
    /**
     * logger instance.
     */
    private static Log log = LogFactory.make();

    /**
     * DAO for looking up commentDTO.
     */
    private DomainMapper<List<Long>, List<CommentDTO>> commentDAO;

    /**
     * DAO for looking up activity by id.
     */
    private DomainMapper<List<Long>, List<ActivityDTO>> activityDAO;

    /**
     * Strategy for setting Deletable property on CommentDTOs.
     */
    private CommentDeletePropertyStrategy commentDeletableSetter;

    /**
     * Constructor.
     * 
     * @param inCommentDAO
     *            DAO for looking up commentDTO.
     * @param inActivityDAO
     *            DAO for looking up activity by id.
     * @param inCommentDeletableSetter
     *            Strategy for setting comment deletable property.
     */
    public CommentModificationAuthorization(final DomainMapper<List<Long>, List<CommentDTO>> inCommentDAO,
            final DomainMapper<List<Long>, List<ActivityDTO>> inActivityDAO,
            final CommentDeletePropertyStrategy inCommentDeletableSetter)
    {
        commentDAO = inCommentDAO;
        activityDAO = inActivityDAO;
        commentDeletableSetter = inCommentDeletableSetter;
    }

    /**
     * Determines if user has permission to modify (edit|delete) a comment.
     * 
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     */
    @SuppressWarnings("serial")
    @Override
    public void authorize(final PrincipalActionContext inActionContext)
    {
        final long commentId = (Long) inActionContext.getParams();
        String currentUserAccountId = inActionContext.getPrincipal().getAccountId();

        // grab the comment in take action on.
        final CommentDTO comment = getCommentById(commentId);

        // Use CommentDeletePropertyStrategy to set Deletable flag on
        // comment appropriately.
        commentDeletableSetter.execute(currentUserAccountId, getParentActivity(comment, currentUserAccountId),
                new ArrayList<CommentDTO>()
                {
                    {
                        add(comment);
                    }
                });

        // If unable to delete, throw access exception.
        if (!comment.isDeletable())
        {
            // if you get to this point, "No soup for you!".
            throw new AuthorizationException("Current user does not have permissions to modify comment id: "
                    + commentId);
        }

    }

    /**
     * Get {@link CommentDTO} by Id.
     * 
     * @param inCommentId
     *            The comment id.
     * @return The {@link CommentDTO}.
     */
    @SuppressWarnings("serial")
    private CommentDTO getCommentById(final long inCommentId)
    {
        List<CommentDTO> comments = commentDAO.execute(new ArrayList<Long>()
        {
            {
                add(inCommentId);
            }
        });
        if (comments.size() == 0)
        {
            log.error("Unable to locate comment with id: " + inCommentId + ". User will be denied authorization.");
            throw new AuthorizationException("Current user does not have permissions to modify comment id: "
                    + inCommentId);
        }
        return comments.get(0);
    }

    /**
     * Get {@link ActivityDTO} that comment is associated with.
     * 
     * @param inCommentDTO
     *            The {@link CommentDTO}.
     * @param inCurrentUserAcctId
     *            The current user's accountId
     * @return {@link ActivityDTO}.
     */
    @SuppressWarnings("serial")
    private ActivityDTO getParentActivity(final CommentDTO inCommentDTO, final String inCurrentUserAcctId)
    {
        List<ActivityDTO> activities = activityDAO.execute(Arrays.asList(inCommentDTO.getActivityId()));
        if (activities.size() == 0)
        {
            log.error("Unable to locate activity with id: " + inCommentDTO.getActivityId() + ". User : "
                    + inCurrentUserAcctId + " will be denied authorization.");
            throw new AuthorizationException("Current user does not have permissions to modify comment id: "
                    + inCommentDTO.getId());
        }
        return activities.get(0);
    }

}
