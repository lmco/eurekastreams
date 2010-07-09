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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.GalleryItem;
import org.eurekastreams.server.persistence.GalleryItemMapper;

/**
 * Creates and returns a galley item.
 * 
 * @param <T>
 *            the type of gallery item.
 */
public class GalleryItemInserter<T extends GalleryItem> implements GalleryItemSaver<T>
{

    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(GalleryItemInserter.class);

    /**
     * Mapper used to get the galleryItem.
     */
    private GalleryItemMapper<T> galleryItemMapper = null;

    /**
     * Constructor.
     * 
     * @param inGalleryItemMapper
     *            injecting the inGalleryItemMapper
     */
    public GalleryItemInserter(
            final GalleryItemMapper<T> inGalleryItemMapper)
    {
        galleryItemMapper = inGalleryItemMapper;
    }

     /** 
     * Inserts a gallery item.
     * 
     * @param inGalleryItem 
     *          the gallery item to insert.
     */
    public void save(final T inGalleryItem)
    {
        galleryItemMapper.insert(inGalleryItem);
    }
}
