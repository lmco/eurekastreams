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
package org.eurekastreams.server.action.execution.directory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.search.ProjectionSearchRequestBuilder;
import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.action.request.directory.GetDirectorySearchResultsRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.service.actions.strategies.directory.DirectorySearchLuceneQueryBuilder;
import org.eurekastreams.server.service.actions.strategies.directory.SearchResultAdditionalPropertyPopulator;
import org.hibernate.search.jpa.FullTextQuery;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link GetDirectorySearchResultsExecution} class.
 *
 */
public class GetDirectorySearchResultsExecutionTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private GetDirectorySearchResultsExecution sut;

    /**
     * The mocked {@link Principal}.
     */
    private final Principal principalMock = context.mock(Principal.class);

    /**
     * The search term to use.
     */
    private static final String SEARCH_TEXT = "heynow";

    /**
     * The starting index to use.
     */
    private static final int FROM = 0;

    /**
     * The ending index to use.
     */
    private static final int TO = 9;

    /**
     * The org short name.
     */
    private static final String SHORT_NAME = "orgshortname";

    /**
     * The search request builder to pass into the SUT.
     */
    private ProjectionSearchRequestBuilder searchRequestBuilder;

    /**
     * Mocked SearchResultAdditionalPropertyPopulator.
     */
    private SearchResultAdditionalPropertyPopulator additionalPropertyPopulator = context
            .mock(SearchResultAdditionalPropertyPopulator.class);

    /**
     * Strategy to build a Lucene search query for directory searching.
     */
    private DirectorySearchLuceneQueryBuilder queryBuilder = context.mock(DirectorySearchLuceneQueryBuilder.class);

    /**
     * Setup the context.
     */
    @Before
    public void setup()
    {
        searchRequestBuilder = context.mock(ProjectionSearchRequestBuilder.class);
        sut = new GetDirectorySearchResultsExecution(queryBuilder, searchRequestBuilder, additionalPropertyPopulator);
    }

    /**
     * Test performAction() with a user that's logged in.
     *
     * @throws Exception
     *             on error
     */
    @Test
    public void testPerformActionForLoggedInUser() throws Exception
    {
        final String escapedSearchText = "heyNowEscaped";
        final long personId = 58583L;
        final String nativeLuceneQuery = "abcdefgh";
        final FullTextQuery query = context.mock(FullTextQuery.class);
        final List<ModelView> results = new ArrayList<ModelView>();
        final int resultSize = 88281;

        context.checking(new Expectations()
        {
            {
                one(searchRequestBuilder).escapeAllButWildcardCharacters(SEARCH_TEXT);
                will(returnValue(escapedSearchText));

                one(principalMock).getId();
                will(returnValue(personId));

                one(queryBuilder).buildNativeQuery(escapedSearchText, "background", SHORT_NAME, personId);
                will(returnValue(nativeLuceneQuery));

                one(searchRequestBuilder).buildQueryFromNativeSearchString(nativeLuceneQuery);
                will(returnValue(query));

                one(searchRequestBuilder).setPaging(query, FROM, TO);

                one(query).getResultList();
                will(returnValue(results));

                one(additionalPropertyPopulator).populateTransientProperties(results, personId, escapedSearchText);

                one(query).getResultSize();
                will(returnValue(resultSize));
            }
        });

        // invoke
        GetDirectorySearchResultsRequest currentRequest = new GetDirectorySearchResultsRequest(SEARCH_TEXT, SHORT_NAME,
                "background", FROM, TO);

        ServiceActionContext currentActionContext = new ServiceActionContext(currentRequest, principalMock);

        PagedSet<ModelView> pagedSet = sut.execute(currentActionContext);

        // assert
        assertTrue(pagedSet.getElapsedTime().length() > 0);
        assertEquals(FROM, pagedSet.getFromIndex());
        assertEquals(TO, pagedSet.getToIndex());
        assertEquals(resultSize, pagedSet.getTotal());
        assertSame(results, pagedSet.getPagedSet());

        context.assertIsSatisfied();
    }

    /**
     * Test performAction() with a user that's not logged in.
     *
     * @throws Exception
     *             on error
     */
    @Test
    public void testPerformActionForLoggedOutUser() throws Exception
    {
        final String escapedSearchText = "heyNowEscaped";
        final long personId = 0L;
        final String nativeLuceneQuery = "abcdefgh";
        final FullTextQuery query = context.mock(FullTextQuery.class);
        final List<ModelView> results = new ArrayList<ModelView>();
        final int resultSize = 88281;

        context.checking(new Expectations()
        {
            {
                one(searchRequestBuilder).escapeAllButWildcardCharacters(SEARCH_TEXT);
                will(returnValue(escapedSearchText));

                one(principalMock).getId();
                will(returnValue(personId));

                one(queryBuilder).buildNativeQuery(escapedSearchText, "background", SHORT_NAME, personId);
                will(returnValue(nativeLuceneQuery));

                one(searchRequestBuilder).buildQueryFromNativeSearchString(nativeLuceneQuery);
                will(returnValue(query));

                one(searchRequestBuilder).setPaging(query, FROM, TO);

                one(query).getResultList();
                will(returnValue(results));

                one(additionalPropertyPopulator).populateTransientProperties(results, personId, escapedSearchText);

                one(query).getResultSize();
                will(returnValue(resultSize));
            }
        });

        // invoke
        GetDirectorySearchResultsRequest currentRequest = new GetDirectorySearchResultsRequest(SEARCH_TEXT, SHORT_NAME,
                "background", FROM, TO);

        ServiceActionContext currentActionContext = new ServiceActionContext(currentRequest, principalMock);

        PagedSet<ModelView> pagedSet = sut.execute(currentActionContext);

        // assert
        assertTrue(pagedSet.getElapsedTime().length() > 0);
        assertEquals(FROM, pagedSet.getFromIndex());
        assertEquals(TO, pagedSet.getToIndex());
        assertEquals(resultSize, pagedSet.getTotal());
        assertSame(results, pagedSet.getPagedSet());

        context.assertIsSatisfied();
    }

    /**
     * Test formatElapasedTime().
     */
    @Test
    public void testFormatElapasedTime()
    {
        final long start = 1392882;
        final long time1 = 138;
        final long time2 = 1848;
        final long time3 = 50;
        final long time4 = 8;
        assertEquals("0.14 seconds", sut.formatElapasedTime(start, start + time1));
        assertEquals("1.85 seconds", sut.formatElapasedTime(start, start + time2));
        assertEquals("0.05 seconds", sut.formatElapasedTime(start, start + time3));
        assertEquals("0.01 seconds", sut.formatElapasedTime(start, start + time4));
    }
}
