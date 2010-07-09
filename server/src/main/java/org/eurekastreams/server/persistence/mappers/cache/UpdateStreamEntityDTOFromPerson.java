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
import org.eurekastreams.server.domain.stream.StreamEntityDTO;

/**
 * Interface to update a StreamEntityDTO with information from the input person.
 */
public interface UpdateStreamEntityDTOFromPerson
{
    /**
     * Update the input stream entity dto with the avatar from the input person.
     *
     * @param inStreamEntityDTO
     *            the stream entity dto to update author information from
     * @param inActivityAuthor
     *            the person with the avatar to set in the stream entity dto
     * @return true if the stream entity was updated
     */
    boolean execute(final StreamEntityDTO inStreamEntityDTO, final Person inActivityAuthor);
}
