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
 * Interface to update a CommentDTO with information from the input person.
 */
public interface UpdateCommentDTOFromPerson
{
    /**
     * Update the input comment dto with the input person.
     *
     * @param inCommentDTO
     *            the comment dto to update author information from
     * @param inPerson
     *            the person with to update the comment from
     * @return true if the comment dto was updated
     */
    boolean execute(final CommentDTO inCommentDTO, final Person inPerson);
}
