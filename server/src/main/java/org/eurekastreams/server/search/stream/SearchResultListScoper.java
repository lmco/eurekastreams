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

/**
 * Intersects two sorted lists of Longs to build a page of data. The source list is requested a page at a time, and the
 * second list is requested in its entirety.
 */
public class SearchResultListScoper implements PageFetcher<Long>
{
    /**
     * The logger.
     */
    private Log log = LogFactory.getLog(SearchResultListScoper.class);

    /**
     * The PageFetcher to pull the first list of Longs.
     */
    private PageFetcher<Long> searchResultsFetcher;

    /**
     * The PageFetcher to pull the second list of Longs.
     */
    private PageFetcher<Long> availableIdsFetcher;

    /**
     * The page size for the search results.
     */
    private int searchResultPageSize;

    /**
     * Constructor.
     *
     * @param inSearchResultsFetcher
     *            the PageFetcher to get the search results
     * @param inAvailableIdsFetcher
     *            the PageFetcher get the list of available ids
     * @param inSearchResultPageSize
     *            the search result page size
     * @param inLastSeenActivityId
     *            the last activity id that the user has seen - return results with ids less than this
     */
    public SearchResultListScoper(final PageFetcher<Long> inSearchResultsFetcher,
            final PageFetcher<Long> inAvailableIdsFetcher, final int inSearchResultPageSize,
            final Long inLastSeenActivityId)
    {
        searchResultsFetcher = inSearchResultsFetcher;
        availableIdsFetcher = inAvailableIdsFetcher;
        searchResultPageSize = inSearchResultPageSize;
    }

    /**
     * Fetch a page of results by intersecting a source list of Longs with a second list of Longs.
     *
     * @param inStartIndex
     *            the starting index of the page to fetch
     * @param inPageSize
     *            the size of the page to build
     * @return an intersection of two lists of Longs
     */
    @Override
    public List<Long> fetchPage(final int inStartIndex, final int inPageSize)
    {
        int searchResultListIndex = 0;

        // we fetch the entire second list of IDs in one request
        if (log.isTraceEnabled())
        {
            log.trace("Loading up all of the available activity IDs for searching");
        }
        List<Long> availableIDs = availableIdsFetcher.fetchPage(0, Integer.MAX_VALUE);
        if (log.isTraceEnabled())
        {
            log.trace("Loaded up all the available activity IDs for searching.");
        }

        // the list of results
        List<Long> results = new ArrayList<Long>();

        if (availableIDs == null || availableIDs.size() == 0)
        {
            log.trace("no available activity IDs - don't bother searching, just return empty list");
            return results;
        }

        if (log.isTraceEnabled())
        {
            log.trace("Begin security scoping to fetch page.");
            if (log.isTraceEnabled())
            {
                log.trace("Available IDs to be scoped: " + availableIDs.toString());
            }
        }

        // the point we last checked the second list
        int nextListBIndex = 0;

        // loop across the source list, finding those that are in the second
        // list
        List<Long> sourceList = null;
        do
        {
            if (log.isTraceEnabled())
            {
                log.trace("Fetching page of search results with index: " + searchResultListIndex + " and page size: "
                        + searchResultPageSize);
            }

            // get a page of results from the source
            sourceList = searchResultsFetcher.fetchPage(searchResultListIndex, searchResultPageSize);

            if (log.isTraceEnabled())
            {
                log.trace("Fetched page of search results for scoping with ids: " + sourceList.toString());
            }

            // loop across each ID in the page of source IDs
            for (Long sourceId : sourceList)
            {
                // search for this source ID in the second list, but stop
                // looking for the source ID if it's newer than
                // the second id
                for (int listBIndex = nextListBIndex;
                // line break
                sourceId <= availableIDs.get(listBIndex); nextListBIndex++, listBIndex++)
                {
                    if (sourceId.equals(availableIDs.get(listBIndex)))
                    {
                        results.add(sourceId);
                        if (results.size() == inPageSize)
                        {
                            // we have a full page of results
                            if (log.isTraceEnabled())
                            {
                                log.trace("Finished security scoping to fetch page - returning results: "
                                        + results.toString());
                            }
                            return results;
                        }
                    }

                    if (listBIndex == availableIDs.size() - 1)
                    {
                        // we've hit the end of list B - we're done
                        if (log.isTraceEnabled())
                        {
                            log.trace("Finished security scoping to fetch page - returning results: "
                                    + results.toString());
                        }
                        return results;
                    }
                }
            }
            searchResultListIndex += searchResultPageSize;
        }
        while (sourceList.size() == searchResultPageSize); // stop looping if
        // our last page
        // request didn't
        // get a full
        // page

        if (log.isTraceEnabled())
        {
            log.trace("Finished security scoping to fetch page - returning results: " + results.toString());
        }
        return results;
    }
}
