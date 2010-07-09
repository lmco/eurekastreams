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
package org.eurekastreams.web.client.events;

import org.eurekastreams.server.domain.Bannerable;
import org.eurekastreams.server.domain.EntityType;

/**
 * Clear an uploaded image.
 *
 */
public class ClearUploadedImageEvent
{
    /**
     * The image type.
     *
     */
    public enum ImageType
    {
        /**
         * Banners.
         */
        BANNER,
        /**
         * Avatars.
         */
        AVATAR
    }

    /**
     * Image type.
     */
    private ImageType imageType;
    /**
     * Entity type.
     */
    private EntityType entityType;

    /**
     * Entity.
     */
    private Bannerable entity;

    /**
     * Constructor.
     *
     * @param inEntityType
     *            the entity type.
     * @param inImageType
     *            the image type.
     * @param inEntity
     *            the bannerable entity for this event.
     */
    public ClearUploadedImageEvent(final EntityType inEntityType, final ImageType inImageType,
            final Bannerable inEntity)
    {
        imageType = inImageType;
        entityType = inEntityType;
        entity = inEntity;
    }

    /**
     * Constructor.
     *
     * @param inEntityType
     *            the entity type.
     * @param inImageType
     *            the image type.
     */
    public ClearUploadedImageEvent(final EntityType inEntityType, final ImageType inImageType)
    {
        imageType = inImageType;
        entityType = inEntityType;
        entity = null;
    }

    /**
     * Image type.
     *
     * @return the image type.
     */
    public ImageType getImageType()
    {
        return imageType;
    }

    /**
     * Gets the entity type.
     *
     * @return the entity type.
     */
    public EntityType getEntityType()
    {
        return entityType;
    }

    /**
     * Gets the Bannerable Entity.
     *
     * @return the entity.
     */
    public Bannerable getEntity()
    {
        return entity;
    }
}
