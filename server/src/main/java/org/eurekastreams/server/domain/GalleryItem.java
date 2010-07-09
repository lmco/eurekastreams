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
package org.eurekastreams.server.domain;

import java.io.Serializable;

/**
 * Any domain entity that can be included in a gallery.
 *
 */
public interface GalleryItem extends Serializable
{

    /**
     * set the url.
     *
     * @param inUrl
     *            The URL of the gallery item.
     */
    void setUrl(final String inUrl);

    /**
     * @return the URL.
     */
    String getUrl();

    /**
     * @return the UUID.
     */
    String getUUID();

    /**
     * @return the owner
     */
    Person getOwner();

    /**
     * @return the category.
     */
    GalleryItemCategory getCategory();

    /**
     * @param inOwner
     *            the owner to set
     */
    void setOwner(final Person inOwner);

    /**
     * Needed for serialization.
     *
     * @param inCategory
     *            Category to use.
     */
    void setCategory(final GalleryItemCategory inCategory);

    /**
     * Needed for serialization.
     *
     * @param inUUID
     *            UUID to use.
     */
    void setUUID(final String inUUID);

    /**
     * Show in gallery?
     * @return the value.
     */
    Boolean getShowInGallery();

    /**
     * Set show in gallery.
     * @param inShowInGallery show in gallery.
     */
    void setShowInGallery(final Boolean inShowInGallery);
}
