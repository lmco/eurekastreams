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
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.GalleryItemCategory;
import org.eurekastreams.server.domain.GalleryItemType;
import org.eurekastreams.server.persistence.GalleryItemCategoryMapper;

/**
 * This class provides the action to retrieve a person from the database by their id.
 *
 */
public class GetGalleryItemCategoriesExecution implements ExecutionStrategy<ServiceActionContext>
{

    /**
     * Instance of the logger.
     */
    private Log log = LogFactory.make();

    /**
     * GalleryItemCategoryMapper used to retrieve person from the db.
     */
    private final GalleryItemCategoryMapper galleryItemMapper;

    /**
     * Gallery item type used to filter records from the database.
     */
    private final GalleryItemType galleryItemType;

    /**
     * Constructor for execution strategy.
     *
     * @param inGalleryItemMapper
     *            - instance of {@link GalleryItemCategoryMapper} for this execution strategy.
     * @param inGalleryItemType
     *            - instance of {@link GalleryItemType} for this execution strategy.
     */
    public GetGalleryItemCategoriesExecution(final GalleryItemCategoryMapper inGalleryItemMapper,
            final GalleryItemType inGalleryItemType)
    {
        galleryItemMapper = inGalleryItemMapper;
        galleryItemType = inGalleryItemType;
    }

    /**
     * {@inheritDoc}.
     *
     * This method retrieves the list of gallery item categories based on the provided mapper.
     */
    @Override
    public Serializable execute(final ServiceActionContext inActionContext) throws ExecutionException
    {
        List<GalleryItemCategory> galleryItemCategories = galleryItemMapper.findGalleryItemCategories(galleryItemType);

        LinkedList outList = new LinkedList();
        for (GalleryItemCategory galleryItemCategory : galleryItemCategories)
        {
            outList.add(galleryItemCategory.getName());
        }

        // the calls and concatenations could slow things down
        // so just log debug messages if you're debugging
        if (log.isDebugEnabled())
        {
            log.debug("Retrieved " + galleryItemCategories.size() + " galleryItem Categories");
        }

        return outList;
    }

}
