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

/**
 * Update the input CommentDTO with the avatar name of the input person.
 */
public class UpdateCommentDTOAuthorAvatarId implements UpdateCommentDTOFromPerson
{
    /**
     * Update the input CommentDTO with the avatar id of the input person.
     *
     * @param inCommentDTO
     *            the comment to update the avatar id for
     * @param inPerson
     *            the person to get the author avatar id from
     * @return true if the comment was altered
     */
    @Override
    public boolean execute(final CommentDTO inCommentDTO, final Person inPerson)
    {
        String oldAvatarId = inCommentDTO.getAuthorAvatarId();
        String newAvatarId = inPerson.getAvatarId();

        if (oldAvatarId == null || !oldAvatarId.equals(newAvatarId))
        {
            inCommentDTO.setAuthorAvatarId(newAvatarId);
            return true;
        }
        return false;
    }

}
