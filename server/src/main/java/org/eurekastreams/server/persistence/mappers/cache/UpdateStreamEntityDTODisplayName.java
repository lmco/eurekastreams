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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;

/**
 * Update a stream entity's display name.
 */
public class UpdateStreamEntityDTODisplayName implements UpdateStreamEntityDTOFromPerson
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Update the input stream entity dto with the avatar from the input person.
     *
     * @param inStreamEntityDTO
     *            the stream entity dto to update the avatar of
     * @param inPerson
     *            the person with the avatar to set in the stream entity dto
     * @return true if the stream entity was updated
     */
    @Override
    public boolean execute(final StreamEntityDTO inStreamEntityDTO, final Person inPerson)
    {
        String oldDisplayName = inStreamEntityDTO.getDisplayName();
        String newDisplayName = inPerson.getDisplayName();

        if (oldDisplayName == null || !oldDisplayName.equals(newDisplayName))
        {
            if (log.isTraceEnabled())
            {
                log.trace("actor display name for StreamEntityDTO with id:" + inStreamEntityDTO.getId()
                        + " is being changed from '" + oldDisplayName + "' to '" + newDisplayName + "'");
            }

            inStreamEntityDTO.setDisplayName(newDisplayName);
            return true;
        }
        return false;
    }

}
