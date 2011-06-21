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
package org.eurekastreams.server.persistence.comparators;

import java.util.Comparator;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.dto.StreamDTO;

/**
 * Comparator to compare two StreamDTOs based on date added, descending, sorting groups before people when there's a
 * tie.
 */
public class StreamDTODateAddedDescendingComparator implements Comparator<StreamDTO>
{
    /**
     * Compare the input StreamDTOs, based on follower count, descending, returning groups before people on tie.
     * 
     * @param inA
     *            the first to compare
     * @param inB
     *            the second to compare
     * @return -1 if A < B, 0 if equal, or 1 if B > A
     */
    @Override
    public int compare(final StreamDTO inA, final StreamDTO inB)
    {
        if (inA.getDateAdded().compareTo(inB.getDateAdded()) == 0)
        {
            // sort groups ahead of people
            if (inA.getEntityType() == EntityType.GROUP && inB.getEntityType() == EntityType.PERSON)
            {
                return -1;
            }
            if (inA.getEntityType() == EntityType.PERSON && inB.getEntityType() == EntityType.GROUP)
            {
                return 1;
            }
            return 0;
        }
        return inB.getDateAdded().compareTo(inA.getDateAdded());
    }
}
