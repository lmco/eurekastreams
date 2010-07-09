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

/**
 * Configurable factory for SearchResultSecurityScopers.
 */
public class SearchResultListScoperFactory
{
    /**
     * The page size for the search results.
     */
    private int searchResultPageSize;

    /**
     * Constructor.
     * 
     * @param inSearchResultPageSize
     *            the page size for pulling search results
     */
    public SearchResultListScoperFactory(final int inSearchResultPageSize)
    {
        searchResultPageSize = inSearchResultPageSize;
    }

    /**
     * Build a SearchResultSecurityScoper.
     * 
     * @param inSearchResultsFetcher
     *            fetcher for search results
     * @param inAvailableIdsFetcher
     *            fetcher for the available ids
     * @param inLastSeenActivityId
     *            the last activity id that the user has seen - return results
     *            with ids less than this
     * @return a SearchResultSecurityScoper to combine the two lists to fetch
     *         pages of data
     */
    public SearchResultListScoper buildSearchResultSecurityScoper(
            final PageFetcher<Long> inSearchResultsFetcher,
            final PageFetcher<Long> inAvailableIdsFetcher,
            final Long inLastSeenActivityId)
    {
        return new SearchResultListScoper(inSearchResultsFetcher,
                inAvailableIdsFetcher, searchResultPageSize,
                inLastSeenActivityId);
    }
}
