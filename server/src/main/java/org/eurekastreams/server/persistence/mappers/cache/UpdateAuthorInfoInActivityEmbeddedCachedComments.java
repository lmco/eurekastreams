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

import java.util.List;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.search.modelview.CommentDTO;

/**
 * Paged bulk updater of an author's cached comments embedded in ActivityDTOs as either the first or last comment.
 */
public class UpdateAuthorInfoInActivityEmbeddedCachedComments extends UpdateCachedItemsByIds<ActivityDTO, Person>
{
    /**
     * List of updaters for the comments.
     */
    private List<UpdateCommentDTOFromPerson> commentUpdaters;

    /**
     * Constructor.
     *
     * @param inBatchSize
     *            the batch size for grabbing ActivityDTOs
     * @param inCommentUpdaters
     *            the comment updaters
     */
    public UpdateAuthorInfoInActivityEmbeddedCachedComments(final Integer inBatchSize,
            final List<UpdateCommentDTOFromPerson> inCommentUpdaters)
    {
        super(inBatchSize);
        commentUpdaters = inCommentUpdaters;
    }

    /**
     * Get the cache key prefix to fetch ActivityDTOs by id.
     *
     * @return the cache key prefix to fetch ActivityDTOs by id.
     */
    @Override
    protected String getCacheKeyPrefix()
    {
        return CacheKeys.ACTIVITY_BY_ID;
    }

    /**
     * Check if the input person is the author of either the first or last comment in the input ActivityDTO - if so,
     * update the comment's author's avatar id with the current value. The permissions need to be checked in case
     * comments were deleted or added, and because we need to handle the case of the first and/or last comment being
     * authored by the person.
     *
     * @param inActivityDTO
     *            the ActivityDTO to check the embedded comments' author for
     * @param inPerson
     *            the person to update avatar info for
     * @return whether any changes were made
     */
    @Override
    protected Boolean updateCachedEntity(final ActivityDTO inActivityDTO, final Person inPerson)
    {
        boolean isUpdated = false;

        CommentDTO firstComment = inActivityDTO.getFirstComment();
        CommentDTO lastComment = inActivityDTO.getLastComment();

        // check the first comment embedded in the activity dto
        if (isAuthoredByPerson(firstComment, inPerson))
        {
            for (UpdateCommentDTOFromPerson updater : commentUpdaters)
            {
                isUpdated |= updater.execute(firstComment, inPerson);
            }
        }

        // check the first comment embedded in the activity dto
        if (isAuthoredByPerson(lastComment, inPerson))
        {
            for (UpdateCommentDTOFromPerson updater : commentUpdaters)
            {
                isUpdated |= updater.execute(lastComment, inPerson);
            }
        }

        return isUpdated;
    }

    /**
     * Return whether the input comment was authored by the input person.
     *
     * @param inComment
     *            the comment to check authorship of
     * @param inPerson
     *            the person to check authorship from
     * @return whether the input comment was authored by the input person
     */
    private boolean isAuthoredByPerson(final CommentDTO inComment, final Person inPerson)
    {
        return inComment != null && inComment.getAuthorId() == inPerson.getId();
    }
}
