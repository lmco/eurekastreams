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

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.domain.GalleryItem;
import org.eurekastreams.server.persistence.GalleryItemMapper;

/**
 * Action to delete a specified gallery item.
 * 
 * @param <T>
 *            Type of gallery item.
 */
public class DeleteGalleryItemExecution<T extends GalleryItem> implements ExecutionStrategy<ActionContext>
{
    /**
     * Mapper used to delete the theme.
     */
    private GalleryItemMapper<T> galleryItemMapper;

    /**
     * Constructor.
     * 
     * @param inGalleryItemMapper
     *            the mapper used to delete the theme
     */
    public DeleteGalleryItemExecution(final GalleryItemMapper<T> inGalleryItemMapper)
    {
        galleryItemMapper = inGalleryItemMapper;
    }

    /**
     * Deletes the specified gallery item.
     * 
     * @param inActionContext
     *            The {@link ActionContext}.
     * @return True if successful.
     */
    @Override
    public Boolean execute(final ActionContext inActionContext)
    {
        // TODO: This action should be configured with validator that ensures parameter is non-null
        Long galleryItemId = (Long) inActionContext.getParams();

        T galleryItem = galleryItemMapper.findById(galleryItemId);

        // delete the gallery item. Will throw NoResultException if no theme by that ID
        galleryItemMapper.delete(galleryItem);

        return true;
    }

}
