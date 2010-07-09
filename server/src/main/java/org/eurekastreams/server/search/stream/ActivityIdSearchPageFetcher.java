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
package org.eurekastreams.server.search.stream;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.Sort;
import org.eurekastreams.commons.search.ProjectionSearchRequestBuilder;
import org.hibernate.search.jpa.FullTextQuery;

/**
 * PageFetcher to return a list of Activity IDs for a Lucene search query.
 */
public class ActivityIdSearchPageFetcher implements PageFetcher<Long>
{
    /**
     * The logger.
     */
    private Log log = LogFactory.getLog(ActivityIdSearchPageFetcher.class);

    /**
     * A multiplier of page size to use when the user is fetching a subsequent page of data - ask for more results than
     * we need so we minimize calls to search.
     */
    private static Long pageSizeMultiplierForSubsequentPages;

    /**
     * The Lucene search query.
     */
    private String searchQuery;

    /**
     * The search request builder.
     */
    private ProjectionSearchRequestBuilder searchRequestBuilder;

    /**
     * The last Activity ID that the user saw - fetch results after that point.
     */
    private Long lastSeenActivityId;

    /**
     * Constructor - taking the Lucene search query and the ProjectionSearchRequestBuilder.
     * 
     * @param inSearchQuery
     *            the native Lucene search query
     * @param inSearchRequestBuilder
     *            the ProjectionSearchRequestBuilder to use to execute the query
     * @param inLastSeenActivityId
     *            the last activity id that the user saw - results should have ids less than this
     * @param inPageSizeMultiplierForSubsequentPages
     *            the multiplier to use against the requested page size when the user is looking for search results past
     *            page 1.
     */
    public ActivityIdSearchPageFetcher(final String inSearchQuery,
            final ProjectionSearchRequestBuilder inSearchRequestBuilder, final Long inLastSeenActivityId,
            final Long inPageSizeMultiplierForSubsequentPages)
    {
        searchQuery = inSearchQuery;
        searchRequestBuilder = inSearchRequestBuilder;
        lastSeenActivityId = inLastSeenActivityId;
        pageSizeMultiplierForSubsequentPages = inPageSizeMultiplierForSubsequentPages;

        if (lastSeenActivityId == null || lastSeenActivityId <= 0)
        {
            lastSeenActivityId = Long.MAX_VALUE;
        }
    }

    /**
     * Fetch a page of activity IDs.
     * 
     * @param inStartIndex
     *            the starting index of the page to fetch - not used
     * @param inPageSize
     *            the page size
     * @return a list of Activity IDs
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Long> fetchPage(final int inStartIndex, final int inPageSize)
    {
        int pageSize = inPageSize;
        if (lastSeenActivityId != Long.MAX_VALUE)
        {
            // If no new activities were added since the user saw the previous
            // page, we'd want to fetch [startIndex, startIndex+inPageSize).
            // We'll fetch a little bit more in case there were extra activities
            // thrown on top.
            pageSize *= pageSizeMultiplierForSubsequentPages;
        }

        FullTextQuery query = searchRequestBuilder.buildQueryFromNativeSearchString(searchQuery);
        query.setSort(new Sort("id", true));
        List<Long> results = new ArrayList<Long>();
        int totalResults = 0;
        int batchSize = 0;
        do
        {
            // get the page size results - don't trust the previous offset in
            // case new activities were added on top
            if (log.isTraceEnabled())
            {
                log.trace("Preparing a query - search: '" + searchQuery + "'; paging:[0," + (pageSize - 1) + "]");
            }

            searchRequestBuilder.setPaging(query, 0, pageSize - 1);

            List<Long> queryResults = query.getResultList();
            if (totalResults == 0)
            {
                // only need to fetch this once - it describes the total results
                // regardless of paging
                totalResults = query.getResultSize();
            }
            batchSize = queryResults.size();

            if (log.isTraceEnabled())
            {
                log.trace("Found: " + batchSize + " of " + totalResults + " results.");
            }

            for (Long activityId : queryResults)
            {
                if (results.size() >= inPageSize)
                {
                    // all done
                    break;
                }
                if (activityId < lastSeenActivityId)
                {
                    if (log.isTraceEnabled())
                    {
                        log.trace("Found activity id: " + activityId + ", which is lower than " + lastSeenActivityId
                                + ", adding to the result list");
                    }
                    // remember this id in case we have to get a second page of
                    // results, to avoid adding it twice
                    lastSeenActivityId = activityId;
                    results.add(activityId);
                }
            }

            // get another page next round
            pageSize += pageSize;
        }
        while (
        // we haven't filled the page yet
        results.size() < inPageSize
        // we got back less results than we asked for - must not be any
                // more
                && batchSize < totalResults);

        // the search request builder should be configured to only fetch ids, so
        // we'll have to populate the rest from
        // the database
        return results;
    }
}
