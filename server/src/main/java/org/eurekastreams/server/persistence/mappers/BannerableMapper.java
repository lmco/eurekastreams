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
package org.eurekastreams.server.persistence.mappers;

import javax.persistence.Query;

import org.eurekastreams.server.domain.Bannerable;

/**
 * BannerableMapper.
 *
 */
public abstract class BannerableMapper extends BaseDomainMapper
{
    /**
     * @return the name of the Entity. This is expected to be mapped the Domain Object Name and also the name of Entity
     *         specific spring configurations.
     */
    protected abstract String getEntityName();

    /**
     *
     * update the banner Id of an object.
     *
     * @param entityId
     *            The id of the entity to update.
     * @param bannerId
     *            The new id to set.
     * @return whether the operation was successful.
     */
    public boolean updateBannerId(final Long entityId, final String bannerId)
    {

        Query query = getEntityManager().createQuery(
                "UPDATE " + getEntityName() + " set bannerId=:bannerId WHERE id =:entityId");
        query.setParameter("bannerId", bannerId);
        query.setParameter("entityId", entityId);
        return query.executeUpdate() == 1;
    }

    /**
     * Get the Entity banner Id.
     *
     * @param entityId
     *            The id of the entity to get the banner Id for.
     * @return The Banner ID.
     */
    public Bannerable getBannerableDTO(final Long entityId)
    {
        Query query = getEntityManager().createQuery(
                "Select new org.eurekastreams.server.domain.BannerableDTO(bannerId,id) from "
                        + getEntityName() + " WHERE id =:entityId");

        query.setParameter("entityId", entityId);
        return (Bannerable) query.getSingleResult();
    }
}
