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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;

/**
 * Get activityIds authored by a person.
 */
public class GetActivityIdsAuthoredByOrOriginallyAuthoredByEntity extends
        BaseDomainMapper
{
    /**
     * Get all activity ids authored by a person.
     * 
     * @param inAuthorId
     *            the unique string representing the author of the activities
     * @param inAuthorEntityType
     *            the type of entity that authored the activities
     * @return a list of ids of all activities authored by the actor with the
     *         input id
     */
    @SuppressWarnings("unchecked")
    public List<Long> execute(final String inAuthorId,
            final EntityType inAuthorEntityType)
    {
        return (List<Long>) getEntityManager()
                .createQuery(
                        "SELECT id FROM Activity WHERE (actorId=:actorId AND actorType=:actorType) "
                                + "OR (originalActorId=:actorId AND originalActorType=:actorType)")
                .setParameter("actorId", inAuthorId).setParameter("actorType",
                        inAuthorEntityType).getResultList();
    }
}
