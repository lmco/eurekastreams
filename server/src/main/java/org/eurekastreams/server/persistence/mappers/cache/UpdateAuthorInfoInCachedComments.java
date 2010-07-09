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
import org.eurekastreams.server.search.modelview.CommentDTO;

/**
 * Paged bulk updater of activity comment author's avatar.
 */
public class UpdateAuthorInfoInCachedComments extends UpdateCachedItemsByIds<CommentDTO, Person>
{
    /**
     * List of CommentDTO author updaters.
     */
    private List<UpdateCommentDTOFromPerson> authorCommentDTOUpdaters;

    /**
     * Constructor.
     *
     * @param inBatchSize
     *            the size of the batch to use to fetch comments from cache
     * @param inAuthorCommentDTOUpdaters
     *            the list of stream entity dto updaters
     */
    public UpdateAuthorInfoInCachedComments(final Integer inBatchSize,
            final List<UpdateCommentDTOFromPerson> inAuthorCommentDTOUpdaters)
    {
        super(inBatchSize);
        authorCommentDTOUpdaters = inAuthorCommentDTOUpdaters;
    }

    /**
     * Return the cache key prefix for activity comment by id.
     *
     * @return the cache key prefix for activity comment by id.
     */
    @Override
    protected String getCacheKeyPrefix()
    {
        return CacheKeys.COMMENT_BY_ID;
    }

    /**
     * Update the input cached activity comment DTO by setting its author's avatar to the input avatar.
     *
     * @param inCachedActivityCommentDTO
     *            the cached activity comment dto to update
     * @param inPerson
     *            the person to update the comment from
     * @return whether the input cached item was changed
     */
    @Override
    protected Boolean updateCachedEntity(final CommentDTO inCachedActivityCommentDTO, final Person inPerson)
    {
        boolean isUpdated = false;

        for (UpdateCommentDTOFromPerson updater : authorCommentDTOUpdaters)
        {
            isUpdated |= updater.execute(inCachedActivityCommentDTO, inPerson);
        }

        return isUpdated;
    }

}
