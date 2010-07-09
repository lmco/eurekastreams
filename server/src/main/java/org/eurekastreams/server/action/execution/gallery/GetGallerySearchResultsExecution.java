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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.gallery.GetGallerySearchResultsRequest;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.GalleryItem;
import org.eurekastreams.server.domain.GalleryItemType;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.persistence.mappers.LuceneSearchMapper;
import org.eurekastreams.server.persistence.mappers.requests.LuceneSearchRequest;

/**
 * Execution strategy for retrieving search results on a gallery search.
 *
 */
public class GetGallerySearchResultsExecution implements ExecutionStrategy<ServiceActionContext>
{

    /**
     * Logger instance of this strategy.
     */
    private final Log logger = LogFactory.make();

    /**
     * The search mapper.
     */
    private final LuceneSearchMapper<GalleryItem> searchMapper;

    /**
     * Constructor.
     *
     * @param inSearchMapper
     *            - instance of the search mapper to use for this execution strategy.
     */
    public GetGallerySearchResultsExecution(final LuceneSearchMapper<GalleryItem> inSearchMapper)
    {
        searchMapper = inSearchMapper;
    }

    /**
     * {@inheritDoc}.
     *
     * Retrieve the search results from the gallery search based on the provided params.
     */
    @Override
    public PagedSet<GalleryItem> execute(final ServiceActionContext inActionContext) throws ExecutionException
    {
        long startTime = System.currentTimeMillis();

        GetGallerySearchResultsRequest actionRequest = (GetGallerySearchResultsRequest) inActionContext.getParams();

        LuceneSearchRequest request = new LuceneSearchRequest();

        request.setMaxResults(actionRequest.getMaxResultsPerPage());
        request.setFirstResult(actionRequest.getStartingIndex());

        Class< ? > objectType;

        if (actionRequest.getType() == GalleryItemType.THEME)
        {
            objectType = Theme.class;
        }
        else
        {
            objectType = GadgetDefinition.class;
        }

        // TODO pull into Spring.
        Map<String, Float> fields = new HashMap<String, Float>();
        fields.put("name", 2.0F);
        fields.put("title", 2.0F);
        fields.put("description", 1.0F);
        fields.put("author", 1.0F);
        request.setFields(fields);

        List<String> sortFields = new ArrayList<String>();
        sortFields.add(actionRequest.getSort());
        request.setSortFields(sortFields);
        request.setObjectType(objectType);
        request.setSearchString(actionRequest.getSearchText());

        PagedSet<GalleryItem> results = searchMapper.execute(request);

        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieved " + results.getTotal() + " from the search, returning "
                    + results.getPagedSet().size() + " items from the searchText: " + actionRequest.getSearchText()
                    + " .");
        }

        // set the elapsed time
        String elapsedTime = formatElapasedTime(startTime, System.currentTimeMillis());
        results.setElapsedTime(elapsedTime);

        return results;
    }

    /**
     * Determine and format the elapsed time for a server request.
     *
     * @param startTime
     *            the starting milliseconds
     * @param endTime
     *            the ending milliseconds
     * @return a formatted elapsed time in the format "0.23 seconds"
     */
    private String formatElapasedTime(final long startTime, final long endTime)
    {
        final int millisecondsPerSecond = 1000;
        String elapsedTime = String.format("%1$.2f seconds", (endTime - startTime) / (float) millisecondsPerSecond);
        if (elapsedTime == "0.00 seconds")
        {
            elapsedTime = "0.01 seconds";
        }
        return elapsedTime;
    }

}
