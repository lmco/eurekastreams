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
package org.eurekastreams.server.persistence.mappers.requests;

import java.io.Serializable;
import java.util.Set;

/**
 * Response object for MoveOrganizationPeopleDBMapper.
 * 
 */
public class MoveOrganizationPeopleResponse implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = -6935582257514859571L;

    /**
     * Ids of people that have been assigned new parent org.
     */
    private Set<Long> movedPersonIds;

    /**
     * Ids of activities that have been assigned new recipient parent org.
     */
    private Set<Long> movedActivityIds;

    /**
     * Constructor.
     * 
     * @param inMovedPersonIds
     *            Ids of people that have been assigned new parent org.
     * @param inMovedActivityIds
     *            Ids of activities that have been assigned new recipient parent org.
     */
    public MoveOrganizationPeopleResponse(final Set<Long> inMovedPersonIds, final Set<Long> inMovedActivityIds)
    {
        movedPersonIds = inMovedPersonIds;
        movedActivityIds = inMovedActivityIds;
    }

    /**
     * @return the movedPersonIds
     */
    public Set<Long> getMovedPersonIds()
    {
        return movedPersonIds;
    }

    /**
     * @return the movedActivityIds
     */
    public Set<Long> getMovedActivityIds()
    {
        return movedActivityIds;
    }

}
