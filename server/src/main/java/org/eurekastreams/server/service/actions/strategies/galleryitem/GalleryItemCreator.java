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
import java.util.UUID;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.GalleryItem;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.GalleryItemMapper;
import org.eurekastreams.server.persistence.PersonMapper;

/**
 * Creates and returns a galleryItem.
 *
 * @param <T>
 *            the type of gallery item.
 */
public class GalleryItemCreator<T extends GalleryItem> implements GalleryItemProvider<T>
{
    /**
     * url key.
     */
    private static final String URL_KEY = "url";

    /**
     * GalleryItem Mapper.
     */
    private GalleryItemMapper<T> galleryItemMapper = null;

    /**
     * GalleryItem Mapper.
     */
    private GalleryItemFactory<T> galleryItemFactory = null;

    /**
     * Used to look up the current user so we can get the name.
     */
    private PersonMapper personMapper = null;

    /**
     * Constructor.
     *
     * @param inGalleryItemMapper
     *            injecting the GalleryItemMapper
     * @param inGalleryItemFactory
     *            injecting a GalleryItemFactory
     * @param inPersonMapper
     *            injecting the PersonMapper
     */
    public GalleryItemCreator(final GalleryItemMapper<T> inGalleryItemMapper,
            final GalleryItemFactory<T> inGalleryItemFactory, final PersonMapper inPersonMapper)
    {
        galleryItemMapper = inGalleryItemMapper;
        galleryItemFactory = inGalleryItemFactory;
        personMapper = inPersonMapper;
    }

    /**
     * Creates and returns a gallery item.
     *
     * @param inContext
     *            the context
     * @param inParams
     *            the parameters that were passed into the action
     * @return the gallery item
     */
    public T provide(final PrincipalActionContext inContext, final Map<String, Serializable> inParams)
    {
        String galleryItemUrl = (String) inParams.get(URL_KEY);
        T outGalleryItem = null;

        // GalleryItem is a URL, find or create.
        T inUseGalleryItem = galleryItemMapper.findByUrl(galleryItemUrl);
        if (inUseGalleryItem != null && inUseGalleryItem.getShowInGallery())
        {
            ValidationException ve = new ValidationException();
            ve.addError(URL_KEY, "Url has already been uploaded to Eureka");
            throw ve;
        }
        else if (inUseGalleryItem != null && !inUseGalleryItem.getShowInGallery())
        {
            outGalleryItem = inUseGalleryItem;
            outGalleryItem.setShowInGallery(true);
        }
        else
        {
            outGalleryItem = galleryItemFactory.create();
            Person owner = personMapper.findByAccountId(inContext.getPrincipal().getAccountId());
            outGalleryItem.setOwner(owner);
            outGalleryItem.setUUID(UUID.randomUUID().toString());
        }

        return outGalleryItem;
    }
}
