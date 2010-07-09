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

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.GalleryItem;
import org.eurekastreams.server.persistence.GalleryItemMapper;

/**
 * Creates and returns a galleryItem.
 *
 * @param <T>
 *            the type of gallery item.
 */
public class GalleryItemFinder<T extends GalleryItem> implements GalleryItemProvider<T>
{

    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(GalleryItemFinder.class);

    /**
     * url key.
     */
    private static final String URL_KEY = "url";

    /**
     * url key.
     */
    private static final String ID_KEY = "id";

    /**
     * GalleryItem Mapper.
     */
    private GalleryItemMapper<T> galleryItemMapper = null;

    /**
     * Constructor.
     *
     * @param inGalleryItemMapper
     *            injecting the GalleryItemMapper
     */
    public GalleryItemFinder(final GalleryItemMapper<T> inGalleryItemMapper)
    {
        galleryItemMapper = inGalleryItemMapper;
    }

    /**
     * Creates and returns a gallery item.
     *
     * @param inContext
     *            the principal action context
     * @param inParams
     *            the parameters that were passed into the action
     * @return the gallery item
     */
    public T provide(final PrincipalActionContext inContext, final Map<String, Serializable> inParams)
    {
        Long galleryItemId = Long.valueOf((String) inParams.get(ID_KEY));

        log.debug("Finding gallery item with id: " + galleryItemId);

        // GalleryItem is a URL, find or create.
        T outGalleryItem = galleryItemMapper.findById(galleryItemId);

        return outGalleryItem;
    }
}
