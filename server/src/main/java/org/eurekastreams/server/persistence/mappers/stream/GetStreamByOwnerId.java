/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.stream;

import javax.persistence.Query;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;

/**
 * This class provides a mechanism for retrieving the CompositeStream for a user or group. The "Wall". This uses a
 * direct database query to retrieve the StreamFilter object. This is in place of calling the cache,
 * because this mapper may be called for DomainGroups that are in a pending state, and those
 * groups are not in cache due to cache rules.
 *
 */
public class GetStreamByOwnerId extends BaseDomainMapper
{
    /**
     * Local instance of the EntityType for the owner of the stream.
     */
    private final EntityType ownerType;

    /**
     * Constructor for GetStreamByOwnerId mapper.
     *
     * @param inOwnerType
     *            - EntityType of the owner of the stream (wall) being retrieved.
     */
    public GetStreamByOwnerId(final EntityType inOwnerType)
    {
        ownerType = inOwnerType;
    }

    /**
     * Method for retrieving the StreamFilter instance that describes the Composite Stream for a user/groups personal
     * stream (wall).
     *
     * @param ownerId
     *            - id of the owner for whom the person stream is to be retrieved.
     * @return - StreamFilter containing information about the person stream.
     */
    public StreamFilter execute(final Long ownerId)
    {
        if (ownerType.equals(EntityType.PERSON))
        {
            Query q = getEntityManager().createQuery("select entityStreamView from Person p where p.id =:personId")
                    .setParameter("personId", ownerId);
            return (StreamFilter) q.getSingleResult();
        }
        else if (ownerType.equals(EntityType.GROUP))
        {
            Query q = getEntityManager().createQuery(
                    "select entityStreamView from DomainGroup dg where dg.id =:domainGroupId").setParameter(
                    "domainGroupId", ownerId);
            return (StreamFilter) q.getSingleResult();
        }
        else
        {
            throw new IllegalArgumentException("Unsupported Entity Type");
        }
    }
}
