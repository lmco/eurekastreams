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
package org.eurekastreams.server.action.validation.gallery;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.gallery.GetGalleryItemsRequest;

/**
 * This class provides the validation for the GetGalleryItems action.
 *
 */
public class GetGalleryItemsValidation implements ValidationStrategy<ServiceActionContext>
{
    /**
     * Recent sort criteria.
     */
    private static final String RECENT_SORT_CRITERIA = "recent";

    /**
     * Popularity sort criteria.
     */
    private static final String POPULARITY_SORT_CRITERIA = "popularity";

    /**
     * {@inheritDoc}.
     *
     * Ensure that the sort criteria matches the two types.
     */
    @Override
    public void validate(final ServiceActionContext inActionContext) throws ValidationException
    {
        GetGalleryItemsRequest currentRequest = (GetGalleryItemsRequest) inActionContext.getParams();

        if (!currentRequest.getSortCriteria().equals(RECENT_SORT_CRITERIA) 
        	&& !currentRequest.getSortCriteria().equals(POPULARITY_SORT_CRITERIA))
        {
            throw new ValidationException("Invalid sort criteria for this action.");
        }
    }
}

