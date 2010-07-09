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

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.UpdateNotificationsOnNameChangeRequest;

/**
 * Mapper to update the denormailzed actor/destination names in the DB.
 *
 */
public class UpdateNotificationsOnNameChangeMapper extends
        BaseArgDomainMapper<UpdateNotificationsOnNameChangeRequest, Boolean>
{

/**
     * Update the denormailzed actor/destination names in the DB.
     *
     * @param inRequest
     *            {@link UpdateNotificationsOnNameChangeRequest).
     *            @return True on success.
     */
    @Override
    public Boolean execute(final UpdateNotificationsOnNameChangeRequest inRequest)
    {
        String name = inRequest.getName();
        String key = inRequest.getUniqueKey();
        EntityType type = inRequest.getType();

        // Actors are restricted to Person types, so only execute this update if type is person.
        if (type == EntityType.PERSON)
        {
            // update actor names.
            getEntityManager().createQuery(
                    "UPDATE VERSIONED ApplicationAlertNotification a "
                            + "SET a.actorName = :name WHERE a.actorAccountId = :key")
                    .setParameter("name", name).setParameter("key", key).executeUpdate();
        }

        // update destination
        getEntityManager().createQuery(
                "UPDATE VERSIONED ApplicationAlertNotification a SET a.destinationName = :name "
                        + "WHERE a.destinationUniqueId = :key  AND a.destinationType = :type ").setParameter("name",
                name).setParameter("key", key).setParameter("type", type).executeUpdate();

        // update auxiliary
        getEntityManager().createQuery(
                "UPDATE VERSIONED ApplicationAlertNotification a SET a.auxiliaryName = :name "
                        + "WHERE a.auxiliaryUniqueId = :key  AND a.auxiliaryType = :type ").setParameter("name", name)
                .setParameter("key", key).setParameter("type", type).executeUpdate();

        return Boolean.TRUE;
    }
}
