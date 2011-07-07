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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Get person ids that are followers of a given group and have group activity notification enabled.
 *
 */
public class GetSubscribedPersonIdsForStreamDbMapper extends BaseArgDomainMapper<Long, List<Long>>
{
    /** Name of entity to use in query. */
    private String entityName;

    /**
     * Constructor.
     *
     * @param entityType
     *            Entity type of streams to operate on.
     */
    public GetSubscribedPersonIdsForStreamDbMapper(final EntityType entityType)
    {
        switch (entityType)
        {
        case PERSON:
            entityName = "Follower";
            break;
        case GROUP:
            entityName = "GroupFollower";
            break;
        default:
            throw new IllegalArgumentException("Entity type " + entityType + " not allowed.  Only PERSON and GROUP.");
        }
    }

    /**
     * Get person ids that are followers of a given group and have group activity notification enabled.
     *
     * @param inRequest
     *            the group id.
     * @return person ids that are followers of a given group.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Long> execute(final Long inRequest)
    {
        return getEntityManager()
                .createQuery(
                        "SELECT gf.pk.followerId FROM " + entityName + " gf WHERE gf.pk.followingId = :id"
                                + " AND gf.receiveNewActivityNotifications = true").setParameter("id", inRequest)
                .getResultList();
    }
}
