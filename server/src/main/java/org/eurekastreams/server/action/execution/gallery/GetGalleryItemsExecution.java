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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.gallery.GetGalleryItemsRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.persistence.GalleryItemMapper;

/**
 * This class provides the action to retrieve a person from the database by their id.
 *
 * @param <T>
 *            the type of domain entity this class returns
 */
public class GetGalleryItemsExecution<T> implements ExecutionStrategy<ServiceActionContext>
{
    /**
     * Instance of logger.
     */
    private Log log = LogFactory.make();

    /**
     * Recent sort criteria.
     */
    private static final String RECENT_SORT_CRITERIA = "recent";

    /**
     * Popularity sort criteria.
     */
    private static final String POPULARITY_SORT_CRITERIA = "popularity";

    /**
     * GalleryItemMapper used to retrieve person from the db.
     */
    private final GalleryItemMapper<T> mapper;

    /**
     * Constructor.
     *
     * @param inMapper
     *            instance of the {@link GalleryItemMapper} mapper.
     */
    public GetGalleryItemsExecution(final GalleryItemMapper<T> inMapper)
    {
        mapper = inMapper;
    }

    /**
     * {@inheritDoc}.
     *
     * Retrieves the list of gallery items specified by the parameters and action.
     */
    @Override
    public PagedSet<T> execute(final ServiceActionContext inActionContext) throws ExecutionException
    {
        PagedSet<T> galleryItems = null;

        GetGalleryItemsRequest currentRequest = (GetGalleryItemsRequest) inActionContext.getParams();

        if (currentRequest.getSortCriteria().equals(POPULARITY_SORT_CRITERIA))
        {
            if (!currentRequest.getCategory().isEmpty())
            {
                galleryItems = mapper.findForCategorySortedByPopularity(currentRequest.getCategory(), currentRequest
                        .getStartIndex(), currentRequest.getEndIndex());
            }
            else
            {
                galleryItems = mapper.findSortedByPopularity(currentRequest.getStartIndex(), currentRequest
                        .getEndIndex());

            }
        }
        else if (currentRequest.getSortCriteria().equals(RECENT_SORT_CRITERIA))
        {
            if (!currentRequest.getCategory().isEmpty())
            {
                galleryItems = mapper.findForCategorySortedByRecent(currentRequest.getCategory(), currentRequest
                        .getStartIndex(), currentRequest.getEndIndex());
            }
            else
            {
                galleryItems = mapper.findSortedByRecent(currentRequest.getStartIndex(), currentRequest.getEndIndex());

            }
        }

        // the calls and concatenations could slow things down
        // so just log debug messages if you're debugging
        if (log.isDebugEnabled())
        {
            log.debug("Retrieved " + galleryItems.getPagedSet().size() + " gallery items");
        }

        return galleryItems;
    }

}
