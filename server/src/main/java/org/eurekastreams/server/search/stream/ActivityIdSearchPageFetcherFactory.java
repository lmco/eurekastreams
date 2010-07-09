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

import org.eurekastreams.commons.search.ProjectionSearchRequestBuilder;

/**
 * Factory to build ActivityIdSearchPageFetcher.
 */
public class ActivityIdSearchPageFetcherFactory
{
    /**
     * The multiplier to use against the requested page size when the user is looking for search results past page 1.
     */
    private Long pageSizeMultiplierForSubsequentPages;

    /**
     * Constructor.
     * 
     * @param inPageSizeMultiplierForSubsequentPages
     *            the multiplier to use against the requested page size when the user is looking for search results past
     *            page 1.
     */
    public ActivityIdSearchPageFetcherFactory(final Long inPageSizeMultiplierForSubsequentPages)
    {
        pageSizeMultiplierForSubsequentPages = inPageSizeMultiplierForSubsequentPages;
    }

    /**
     * Factory method - build a ActivityIdSearchPageFetcher given the input search query and search request builder.
     * 
     * @param inSearchQuery
     *            the native lucene query to parse
     * @param inSearchRequestBuilder
     *            the search request builder to do the parsing
     * @param lastSeenActivityId
     *            the last ActivityID the user has seen - start returning activities with IDs less than this number
     * @return a ActivityIdSearchPageFetcher to fetch a page from lucene
     */
    public ActivityIdSearchPageFetcher buildActivityIdSearchPageFetcher(final String inSearchQuery,
            final ProjectionSearchRequestBuilder inSearchRequestBuilder, final Long lastSeenActivityId)
    {
        return new ActivityIdSearchPageFetcher(inSearchQuery, inSearchRequestBuilder, lastSeenActivityId,
                pageSizeMultiplierForSubsequentPages);
    }
}
