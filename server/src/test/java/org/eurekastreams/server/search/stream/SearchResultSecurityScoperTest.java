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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Test fixture for SortedListIntersector.
 */
public class SearchResultSecurityScoperTest
{
    /**
     * I (heart) checkstyle.
     */
    private static final long TWENTY = 20L;

    /**
     * I (heart) checkstyle.
     */
    private static final long EIGHTEEN = 18L;

    /**
     * I (heart) checkstyle.
     */
    private static final long SEVENTEEN = 17L;

    /**
     * I (heart) checkstyle.
     */
    private static final long FIFTEEN = 15L;

    /**
     * I (heart) checkstyle.
     */
    private static final long ELEVEN = 11L;

    /**
     * I (heart) checkstyle.
     */
    private static final int TEN = 10;

    /**
     * Test fetching a page of data when a full page does exist, but requires
     * multiple calls to the first list.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testFetchPageWhenFullPageExistsAndMultipleRequestsToListA()
    {
        ArrayList<Long> listA = new ArrayList<Long>();
        ArrayList<Long> listB = new ArrayList<Long>();

        listA.addAll(Arrays.asList(new Long[] { TWENTY, EIGHTEEN, FIFTEEN,
                ELEVEN, 9L, 8L }));
        listB.addAll(Arrays.asList(new Long[] { TWENTY, SEVENTEEN, FIFTEEN,
                ELEVEN, 9L, 8L, 5L }));

        ListOfLongsFetcher searchResultsPageFetcher = new ListOfLongsFetcher(
                listA);
        ListOfLongsFetcher availableIDsListFetcher = new ListOfLongsFetcher(
                listB);

        SearchResultListScoper sut = buildSearchResultSecurityScoper(
                searchResultsPageFetcher, availableIDsListFetcher, 3);

        // perform SUT
        List<Long> results = sut.fetchPage(0, 4);

        // make sure we got the right values
        assertArrayEquals(new Long[] { TWENTY, FIFTEEN, ELEVEN, 9L }, results
                .toArray(new Long[results.size()]));

        // make sure it took the expected number of page hits to list A
        assertEquals(2, searchResultsPageFetcher.getRequestedPageCount());
    }

    /**
     * Test fetching a page of data when a full page does exist, and only
     * requires one hit to the first list.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testFetchPageWhenFullPageExistsAndSingleRequestsToListA()
    {
        ArrayList<Long> listA = new ArrayList<Long>();
        ArrayList<Long> listB = new ArrayList<Long>();

        listA.addAll(Arrays.asList(new Long[] { TWENTY, EIGHTEEN, FIFTEEN,
                ELEVEN, 9L, 8L }));
        listB.addAll(Arrays.asList(new Long[] { TWENTY, SEVENTEEN, FIFTEEN,
                ELEVEN, 9L, 8L, 5L }));

        ListOfLongsFetcher searchResultsPageFetcher = new ListOfLongsFetcher(
                listA);
        ListOfLongsFetcher availableIDsListFetcher = new ListOfLongsFetcher(
                listB);

        SearchResultListScoper sut = buildSearchResultSecurityScoper(
                searchResultsPageFetcher, availableIDsListFetcher, TEN * 2);

        // perform SUT
        List<Long> results = sut.fetchPage(0, 4);

        // make sure we got the right values
        assertArrayEquals(new Long[] { TWENTY, FIFTEEN, ELEVEN, 9L }, results
                .toArray(new Long[results.size()]));

        // make sure it took the expected number of page hits to list A
        assertEquals(1, searchResultsPageFetcher.getRequestedPageCount());
    }

    /**
     * Test fetching a page of data when there's not enough matches, requiring
     * only one hit to the first list.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testFetchPageWhenNotEnoughResultsAndSingleRequestsToListA()
    {
        ArrayList<Long> listA = new ArrayList<Long>();
        ArrayList<Long> listB = new ArrayList<Long>();

        listA.addAll(Arrays.asList(new Long[] { TWENTY, EIGHTEEN, FIFTEEN,
                ELEVEN, 9L, 8L }));
        listB.addAll(Arrays.asList(new Long[] { TWENTY, SEVENTEEN, FIFTEEN,
                ELEVEN, 9L, 8L, 5L }));

        ListOfLongsFetcher searchResultsPageFetcher = new ListOfLongsFetcher(
                listA);
        ListOfLongsFetcher availableIDsListFetcher = new ListOfLongsFetcher(
                listB);

        SearchResultListScoper sut = buildSearchResultSecurityScoper(
                searchResultsPageFetcher, availableIDsListFetcher, TEN * 2);

        // perform SUT
        List<Long> results = sut.fetchPage(0, TEN);

        // make sure we got the right values
        assertArrayEquals(new Long[] { TWENTY, FIFTEEN, ELEVEN, 9L, 8L },
                results.toArray(new Long[results.size()]));

        // make sure it took the expected number of page hits to list A
        assertEquals(1, searchResultsPageFetcher.getRequestedPageCount());
    }

    /**
     * Test fetching a page of data when there's not enough in the first list,
     * requiring only one hit to the first list.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testFetchPageWhenNotEnoughResultsAndSingleRequestsToListA2()
    {
        ArrayList<Long> listA = new ArrayList<Long>();
        ArrayList<Long> listB = new ArrayList<Long>();

        listA.addAll(Arrays.asList(new Long[] { TWENTY }));
        listB.addAll(Arrays.asList(new Long[] { TWENTY, SEVENTEEN, FIFTEEN,
                ELEVEN, 9L, 8L, 5L }));

        ListOfLongsFetcher searchResultsPageFetcher = new ListOfLongsFetcher(
                listA);
        ListOfLongsFetcher availableIDsListFetcher = new ListOfLongsFetcher(
                listB);

        SearchResultListScoper sut = buildSearchResultSecurityScoper(
                searchResultsPageFetcher, availableIDsListFetcher, TEN * 2);

        // perform SUT
        List<Long> results = sut.fetchPage(0, TEN);

        // make sure we got the right values
        assertArrayEquals(new Long[] { TWENTY }, results
                .toArray(new Long[results.size()]));

        // make sure it took the expected number of page hits to list A
        assertEquals(1, searchResultsPageFetcher.getRequestedPageCount());
    }

    /**
     * Test fetching a page when there are no results, and we know this without
     * looping to the bottom of the first list.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testFetchPageWhenNoResultsAndOptimizedEndOfLooping()
    {
        ArrayList<Long> listA = new ArrayList<Long>();
        ArrayList<Long> listB = new ArrayList<Long>();

        listA.addAll(Arrays.asList(new Long[] { TWENTY, EIGHTEEN, FIFTEEN,
                ELEVEN, 9L, 8L }));
        listB.addAll(Arrays.asList(new Long[] { TWENTY * 2 }));

        ListOfLongsFetcher searchResultsPageFetcher = new ListOfLongsFetcher(
                listA);
        ListOfLongsFetcher availableIDsListFetcher = new ListOfLongsFetcher(
                listB);

        SearchResultListScoper sut = buildSearchResultSecurityScoper(
                searchResultsPageFetcher, availableIDsListFetcher, 1);

        // perform SUT
        List<Long> results = sut.fetchPage(0, TEN);

        // make sure we got the right values
        assertEquals(0, results.size());

        // make sure it took the expected number of page hits to list A - this
        // is an optimization - the code should
        // realize that there's no point in continuing
        assertEquals(1, searchResultsPageFetcher.getRequestedPageCount());
    }

    /**
     * Test fetching a page of data when there is a full page, but we have to
     * loop through the first list until we find it.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testFetchPageWhenFullPageExistsAtEndOfSourceListMultipleSourcePages()
    {
        ArrayList<Long> listA = new ArrayList<Long>();
        ArrayList<Long> listB = new ArrayList<Long>();

        listA.addAll(Arrays.asList(new Long[] { TWENTY, EIGHTEEN, FIFTEEN,
                ELEVEN, 9L, 8L, 7L, 3L, 1L }));
        listB.addAll(Arrays.asList(new Long[] { 3L, 2L, 1L }));

        ListOfLongsFetcher searchResultsPageFetcher = new ListOfLongsFetcher(
                listA);
        ListOfLongsFetcher availableIDsListFetcher = new ListOfLongsFetcher(
                listB);

        SearchResultListScoper sut = buildSearchResultSecurityScoper(
                searchResultsPageFetcher, availableIDsListFetcher, 1);

        // perform SUT
        List<Long> results = sut.fetchPage(0, 2);

        // make sure we got the right values
        assertArrayEquals(new Long[] { 3L, 1L }, results
                .toArray(new Long[results.size()]));

        // this should have taken 9 requests to the source list
        assertEquals(9, searchResultsPageFetcher.getRequestedPageCount());
    }

    /**
     * Test fetching a page when there is a full page, but only at the end of
     * the two lists, and we make one big paged request.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testFetchPageWhenFullPageExistsAtEndOfSourceListSingleSourcePage()
    {
        ArrayList<Long> listA = new ArrayList<Long>();
        ArrayList<Long> listB = new ArrayList<Long>();

        listA.addAll(Arrays.asList(new Long[] { TWENTY, EIGHTEEN, FIFTEEN,
                ELEVEN, 9L, 8L, 7L, 3L, 1L }));
        listB.addAll(Arrays.asList(new Long[] { 3L, 2L, 1L }));

        ListOfLongsFetcher searchResultsPageFetcher = new ListOfLongsFetcher(
                listA);
        ListOfLongsFetcher availableIDsListFetcher = new ListOfLongsFetcher(
                listB);

        SearchResultListScoper sut = buildSearchResultSecurityScoper(
                searchResultsPageFetcher, availableIDsListFetcher, TEN * 2);

        // perform SUT
        List<Long> results = sut.fetchPage(0, 2);

        // make sure we got the right values
        assertArrayEquals(new Long[] { 3L, 1L }, results
                .toArray(new Long[results.size()]));

        // this should have taken 1 request to the source list
        assertEquals(1, searchResultsPageFetcher.getRequestedPageCount());
    }

    /**
     * Build a SearchResultsSecurityScoper using the factory.
     *
     * @param inSearchResultsFetcher
     *            the page fetcher for getting the search results
     * @param inAvailableIdsFetcher
     *            the page fetcher for getting the available ids
     * @param searchResultsPageSize
     *            the page size to use for search results
     * @return a SearchResultSecurityScoper, using the factory
     */
    private SearchResultListScoper buildSearchResultSecurityScoper(
            final PageFetcher<Long> inSearchResultsFetcher,
            final PageFetcher<Long> inAvailableIdsFetcher,
            final int searchResultsPageSize)
    {
        SearchResultListScoperFactory factory = new SearchResultListScoperFactory(
                searchResultsPageSize);
        return factory.buildSearchResultSecurityScoper(inSearchResultsFetcher,
                inAvailableIdsFetcher, Long.MAX_VALUE);
    }

    /**
     * Helper PageFetcher that keeps track of how many pages of data are
     * requested.
     */
    private class ListOfLongsFetcher implements PageFetcher<Long>
    {
        /**
         * Keeps track of the number of page requests.
         */
        private int requestedPageCount = 0;

        /**
         * The list of Longs.
         */
        private List<Long> data;

        /**
         * Constructor - takes the list of longs.
         *
         * @param inData
         *            the list of longs
         */
        public ListOfLongsFetcher(final List<Long> inData)
        {
            data = inData;
        }

        /**
         * Fetch a page of data.
         *
         * @param inStartIndex
         *            the starting index
         * @param inPageSize
         *            the page size
         * @return the page of data
         */
        @Override
        public List<Long> fetchPage(final int inStartIndex, final int inPageSize)
        {
            requestedPageCount++;
            List<Long> results;
            if (data.size() < inStartIndex)
            {
                results = new ArrayList<Long>();
            }
            else if (data.size() < inStartIndex + inPageSize)
            {
                results = data
                        .subList(inStartIndex, data.size() - inStartIndex);
            }
            else
            {
                results = data.subList(inStartIndex, inStartIndex + inPageSize);
            }
            return results;
        }

        /**
         * @return the requestedPageCount
         */
        public int getRequestedPageCount()
        {
            return requestedPageCount;
        }

    }
}
