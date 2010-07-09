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
package org.eurekastreams.server.service.actions.strategies.galleryitem;

import org.eurekastreams.server.domain.GalleryItem;

/**
 * Creates and returns a gallery item.
 *
 * @param <T>
 *            the type of gallery item.
 */
public interface GalleryItemPopulator<T extends GalleryItem>
{
    /**
     * Creates and returns a gallery item.
     *
     * @param inGalleryItem
     *            the gallery item to populate
     * @param inGalleryItemUrl
     *            the gallery item url.
     */
    void populate(final T inGalleryItem, final String inGalleryItemUrl);
}
