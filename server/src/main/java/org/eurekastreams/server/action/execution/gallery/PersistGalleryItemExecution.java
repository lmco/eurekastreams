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
package org.eurekastreams.server.action.execution.gallery;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.GalleryItem;
import org.eurekastreams.server.domain.GalleryItemCategory;
import org.eurekastreams.server.domain.GalleryItemType;
import org.eurekastreams.server.persistence.GalleryItemCategoryMapper;
import org.eurekastreams.server.service.actions.strategies.galleryitem.GalleryItemPopulator;
import org.eurekastreams.server.service.actions.strategies.galleryitem.GalleryItemProvider;
import org.eurekastreams.server.service.actions.strategies.galleryitem.GalleryItemSaver;

/**
 * Assigns a GalleryItem to a Person based on a UUID or a URL.
 *
 * @param <T>
 *            the type of gallery item.
 */
public class PersistGalleryItemExecution<T extends GalleryItem> implements ExecutionStrategy<ServiceActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * url key.
     */
    private static final String URL_KEY = "url";

    /**
     * url key.
     */
    private static final String CATEGORY_KEY = "category";

    /**
     * Used to obtain the galleryItem to be persisted.
     */
    private GalleryItemProvider<T> galleryItemProvider = null;

    /**
     * GalleryItem Populator.
     */
    private GalleryItemPopulator<T> galleryItemPopulator = null;

    /**
     * Mapper used to save the galleryItem.
     */
    private GalleryItemSaver<T> galleryItemSaver = null;

    /**
     * Mapper used to look up the theme category.
     */
    private GalleryItemCategoryMapper galleryItemCategoryMapper = null;

    /**
     * Gallery Item Type.
     */
    private GalleryItemType galleryItemType = null;

    /**
     * Constructor.
     *
     * @param inGalleryItemProvider
     *            injecting the GalleryItemProvider 2
     * @param inGalleryItemPopulator
     *            injecting the GalleryItemPopulator 3
     * @param inGalleryItemSaver
     *            injecting the GalleryItemSaver 4
     * @param inGalleryItemCategoryMapper
     *            injecting the GalleryItemCategoryMapper 5
     * @param inGalleryItemType
     *            injecting a inGalleryItemType 7
     */
    public PersistGalleryItemExecution(final GalleryItemProvider<T> inGalleryItemProvider,
            final GalleryItemPopulator<T> inGalleryItemPopulator, final GalleryItemSaver<T> inGalleryItemSaver,
            final GalleryItemCategoryMapper inGalleryItemCategoryMapper, final GalleryItemType inGalleryItemType)
    {
        galleryItemProvider = inGalleryItemProvider;
        galleryItemPopulator = inGalleryItemPopulator;
        galleryItemSaver = inGalleryItemSaver;
        galleryItemCategoryMapper = inGalleryItemCategoryMapper;
        galleryItemType = inGalleryItemType;
    }

    /**
     * Persist the gallery item.
     *
     * @param inActionContext
     *            the principal action context
     * @return the gallery item
     */
    @SuppressWarnings("unchecked")
    @Override
    public Serializable execute(final ServiceActionContext inActionContext)
    {
        // convert the params to a map
        Map<String, Serializable> fields = (Map<String, Serializable>) inActionContext.getParams();

        String galleryItemUrl = (String) fields.get(URL_KEY);
        String category = (String) fields.get(CATEGORY_KEY);

        // get the gallery item to persist
        T galleryItem = galleryItemProvider.provide(inActionContext, fields);

        // populate the gallery item
        galleryItemPopulator.populate(galleryItem, galleryItemUrl);

        // set the url
        galleryItem.setUrl(galleryItemUrl);

        // get the category
        GalleryItemCategory galleryItemCategory = galleryItemCategoryMapper.findByName(galleryItemType, category);

        log.debug("Search for gallery item using galleryItemType: " + galleryItemType + "and category: " + category
                + " returned: " + galleryItemCategory);

        // set the category
        galleryItem.setCategory(galleryItemCategory);

        // persist the gallery item
        galleryItemSaver.save(galleryItem);

        return galleryItem;
    }

}
