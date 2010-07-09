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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.Sort;
import org.eurekastreams.commons.search.ProjectionSearchRequestBuilder;
import org.hibernate.search.jpa.FullTextQuery;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for ActivityIdSearchPageFetcher.
 */
public class ActivityIdSearchPageFetcherTest
{
    /**
     * System under test.
     */
    private ActivityIdSearchPageFetcher sut;

    /**
     * mock context.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mock for ProjectionSearchRequestBuilder.
     */
    private ProjectionSearchRequestBuilder projectionSearchRequestBuilderMock = context
            .mock(ProjectionSearchRequestBuilder.class);

    /**
     * Mocked FullTextQuery.
     */
    private FullTextQuery fullTextQueryMock = context.mock(FullTextQuery.class);

    /**
     * Test fetchPage.
     */
    @Test
    public void testFetchPage()
    {
        final String searchQuery = "hey now";
        final int totalNumberOfResults = 0;
        final int pageSize = 10;
        final int expectedLastIndex = 9;
        final List<Long> results = new ArrayList<Long>();

        context.checking(new Expectations()
        {
            {
                oneOf(projectionSearchRequestBuilderMock).buildQueryFromNativeSearchString(searchQuery);
                will(returnValue(fullTextQueryMock));

                // assert that it was sorted - unfortunately can't check the
                // sort parameters
                oneOf(fullTextQueryMock).setSort(with(any(Sort.class)));

                // make sure the paging is handled properly
                oneOf(projectionSearchRequestBuilderMock).setPaging(fullTextQueryMock, 0, expectedLastIndex);

                oneOf(fullTextQueryMock).getResultList();
                will(returnValue(results));

                oneOf(fullTextQueryMock).getResultSize();
                will(returnValue(totalNumberOfResults));
            }
        });

        sut = new ActivityIdSearchPageFetcher(searchQuery, projectionSearchRequestBuilderMock, Long.MAX_VALUE, 5L);
        assertEquals(results, sut.fetchPage(0, pageSize));

        context.assertIsSatisfied();
    }

    /**
     * Test fetchPage.
     */
    @Test
    public void testFetchPageWithResults()
    {
        final String searchQuery = "hey now";
        final int start = 21;
        final int pageSize = 10;
        final int expectedLastIndex = 9;
        final List<Long> results = new ArrayList<Long>();

        // add results
        results.add(9L);
        results.add(8L);
        results.add(7L);
        results.add(6L);
        results.add(5L);
        results.add(4L);
        results.add(3L);
        results.add(2L);
        results.add(1L);

        context.checking(new Expectations()
        {
            {
                oneOf(projectionSearchRequestBuilderMock).buildQueryFromNativeSearchString(searchQuery);
                will(returnValue(fullTextQueryMock));

                // assert that it was sorted - unfortunately can't check the
                // sort parameters
                oneOf(fullTextQueryMock).setSort(with(any(Sort.class)));

                // make sure the paging is handled properly
                oneOf(projectionSearchRequestBuilderMock).setPaging(fullTextQueryMock, 0, expectedLastIndex);

                // only return 9 results - this will cause only one loop
                oneOf(fullTextQueryMock).getResultSize();
                will(returnValue(results.size()));

                oneOf(fullTextQueryMock).getResultList();
                will(returnValue(results));
            }
        });

        sut = new ActivityIdSearchPageFetcher(searchQuery, projectionSearchRequestBuilderMock, Long.MAX_VALUE, 5L);
        assertEquals(results, sut.fetchPage(start, pageSize));

        context.assertIsSatisfied();
    }

    /**
     * Test fetchPage.
     */
    @Test
    public void testFetchPageWithResultsRequiringThreePagesDueToLastSeenId()
    {
        final String searchQuery = "hey now";
        final int start = 21;
        final int pageSize = 4;
        final int expectedPage1ToIndex = 19;
        final int expectedPage2ToIndex = 39;
        final List<Long> results1 = new ArrayList<Long>();
        final List<Long> results2 = new ArrayList<Long>();

        // result page 1 - 25 ->
        final int twentyFive = 25;
        for (long i = twentyFive; i >= 6; i--)
        {
            results1.add(i);
        }

        // result page 2 - 25->1
        for (long i = twentyFive; i >= 1; i--)
        {
            results2.add(i);
        }

        context.checking(new Expectations()
        {
            {
                oneOf(projectionSearchRequestBuilderMock).buildQueryFromNativeSearchString(searchQuery);
                will(returnValue(fullTextQueryMock));

                // assert that it was sorted - unfortunately can't check the
                // sort parameters
                oneOf(fullTextQueryMock).setSort(with(any(Sort.class)));

                // -- PAGE 1

                // make sure the paging is handled properly
                oneOf(projectionSearchRequestBuilderMock).setPaging(fullTextQueryMock, 0, expectedPage1ToIndex);

                oneOf(fullTextQueryMock).getResultList();
                will(returnValue(results1));

                oneOf(fullTextQueryMock).getResultSize();
                will(returnValue(twentyFive));

                // -- PAGE 2

                // make sure the paging is handled properly
                oneOf(projectionSearchRequestBuilderMock).setPaging(fullTextQueryMock, 0, expectedPage2ToIndex);

                // we'll fill the list before checking how big page #2 is

                oneOf(fullTextQueryMock).getResultList();
                will(returnValue(results2));
            }
        });

        sut = new ActivityIdSearchPageFetcher(searchQuery, projectionSearchRequestBuilderMock, 7L, 5L);
        List<Long> expectedResults = new ArrayList<Long>();
        expectedResults.add(6L);
        expectedResults.add(5L);
        expectedResults.add(4L);
        expectedResults.add(3L);

        assertEquals(expectedResults, sut.fetchPage(start, pageSize));

        context.assertIsSatisfied();
    }
}
