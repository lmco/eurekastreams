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

/**
 * Request object for updated the cached org/group banner id.
 *
 */
public class UpdateCachedBannerMapperRequest
{
    /**
     * Local instance of the banner id.
     */
    private final String bannerId;

    /**
     * Local instance of the Entity id for this request.
     */
    private final Long entityId;

    /**
     * Constructor.
     * @param inBannerId - value of the Banner id to be updated.
     * @param inEntityId - id for the entity's banner to be updated.
     */
    public UpdateCachedBannerMapperRequest(final String inBannerId, final Long inEntityId)
    {
        bannerId = inBannerId;
        entityId = inEntityId;
    }

    /**
     * Retrieve the banner id value to be updated.
     * @return - banner id.
     */
    public String getBannerId()
    {
        return bannerId;
    }

    /**
     * Retrieve the EntityId of the entity to update the banner for.
     * @return - entity id of the entity to update.
     */
    public Long getEntityId()
    {
        return entityId;
    }
}
