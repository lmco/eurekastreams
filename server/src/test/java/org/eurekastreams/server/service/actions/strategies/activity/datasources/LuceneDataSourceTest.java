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
package org.eurekastreams.server.service.actions.strategies.activity.datasources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.lucene.search.Sort;
import org.eurekastreams.commons.search.ProjectionSearchRequestBuilder;
import org.eurekastreams.server.domain.stream.Activity;
import org.hibernate.search.jpa.FullTextQuery;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for LuceneDataSource.
 */
public class LuceneDataSourceTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private LuceneDataSource sut;

    /**
     * Search builder.
     */
    private final ProjectionSearchRequestBuilder builder = context.mock(ProjectionSearchRequestBuilder.class,
            "builder");

    /**
     * Unstemmed search builder.
     */
    private final ProjectionSearchRequestBuilder unstemmedBuilder = context.mock(ProjectionSearchRequestBuilder.class,
            "unstemmed builder");

    /**
     * Max results.
     */
    private static final int MAX_RESULTS = 10;

    /**
     * Setup test fixtures.
     */
    @Before
    public void setup()
    {
        final Map<String, String> searchMap = new HashMap<String, String>();
        searchMap.put("keywords", "content");

        final Map<String, PersistenceDataSourceRequestTransformer> transformerMap
        // new line
        = new HashMap<String, PersistenceDataSourceRequestTransformer>();

        sut = new LuceneDataSource(builder, unstemmedBuilder, searchMap, transformerMap, MAX_RESULTS);
    }

    /**
     * Test execute method with search keywords.
     */
    @Test
    public void testExecuteWithKeywords()
    {

        final JSONObject request = new JSONObject();
        final JSONObject query = new JSONObject();

        query.put("keywords", "hithere:(foo)");
        request.put("query", query);

        final FullTextQuery ftq = context.mock(FullTextQuery.class);
        final List<Long> results = new ArrayList<Long>();

        context.checking(new Expectations()
        {
            {
                oneOf(builder).buildQueryFromNativeSearchString("+content:(hithere(foo)) ");
                will(returnValue(ftq));

                oneOf(ftq).getResultList();
                will(returnValue(results));

                oneOf(builder).setPaging(ftq, 0, MAX_RESULTS);
            }
        });

        assertSame(results, sut.fetch(request, 0L));
        context.assertIsSatisfied();
    }

    /**
     * Test execute method with a bad search.
     */
    @Test
    public void testExecuteWithBadSearch()
    {

        final JSONObject request = new JSONObject();
        final JSONObject query = new JSONObject();

        query.put("keywords", "hithere:(foo)");
        request.put("query", query);

        context.checking(new Expectations()
        {
            {
                oneOf(builder).buildQueryFromNativeSearchString("+content:(hithere(foo)) ");
                will(throwException(new RuntimeException("OOOPS")));
            }
        });

        assertEquals(0, sut.fetch(request, 0L).size());
        context.assertIsSatisfied();
    }

    /**
     * Test execute method with the user excluding a keyword with !.
     */
    @Test
    public void testExecuteWithBangKeyword()
    {

        final JSONObject request = new JSONObject();
        final JSONObject query = new JSONObject();

        query.put("keywords", "!BACON");
        request.put("query", query);

        final FullTextQuery ftq = context.mock(FullTextQuery.class);
        final List<Long> results = new ArrayList<Long>();

        context.checking(new Expectations()
        {
            {
                oneOf(builder).buildQueryFromNativeSearchString(
                        "+content:(" + Activity.CONSTANT_KEYWORD_IN_EVERY_ACTIVITY_CONTENT + " !BACON) ");
                will(returnValue(ftq));

                oneOf(ftq).getResultList();
                will(returnValue(results));

                oneOf(builder).setPaging(ftq, 0, MAX_RESULTS);
            }
        });

        assertSame(results, sut.fetch(request, 0L));
        context.assertIsSatisfied();
    }

    /**
     * Test execute method with the user excluding a keyword with !.
     */
    @Test
    public void testExecuteWithNOTKeyword()
    {

        final JSONObject request = new JSONObject();
        final JSONObject query = new JSONObject();

        query.put("keywords", "NOT BACON");
        request.put("query", query);

        final FullTextQuery ftq = context.mock(FullTextQuery.class);
        final List<Long> results = new ArrayList<Long>();

        context.checking(new Expectations()
        {
            {
                oneOf(builder).buildQueryFromNativeSearchString(
                        "+content:(" + Activity.CONSTANT_KEYWORD_IN_EVERY_ACTIVITY_CONTENT + " NOT BACON) ");
                will(returnValue(ftq));

                oneOf(ftq).getResultList();
                will(returnValue(results));

                oneOf(builder).setPaging(ftq, 0, MAX_RESULTS);
            }
        });

        assertSame(results, sut.fetch(request, 0L));
        context.assertIsSatisfied();
    }

    /**
     * Test execute method with the user excluding a keyword with !.
     */
    @Test
    public void testExecuteWithMinusKeyword()
    {

        final JSONObject request = new JSONObject();
        final JSONObject query = new JSONObject();

        query.put("keywords", "-BACON");
        request.put("query", query);

        final FullTextQuery ftq = context.mock(FullTextQuery.class);
        final List<Long> results = new ArrayList<Long>();

        context.checking(new Expectations()
        {
            {
                oneOf(builder).buildQueryFromNativeSearchString(
                        "+content:(" + Activity.CONSTANT_KEYWORD_IN_EVERY_ACTIVITY_CONTENT + " -BACON) ");
                will(returnValue(ftq));

                oneOf(ftq).getResultList();
                will(returnValue(results));

                oneOf(builder).setPaging(ftq, 0, MAX_RESULTS);
            }
        });

        assertSame(results, sut.fetch(request, 0L));
        context.assertIsSatisfied();
    }

    /**
     * Test execute method with search keywords, sorting by date.
     */
    @Test
    public void testExecuteWithKeywordsSortByDate()
    {
        final JSONObject request = new JSONObject();
        final JSONObject query = new JSONObject();

        query.put("keywords", "hithere:(foo)");
        query.put("sortBy", "date");
        request.put("query", query);

        final FullTextQuery ftq = context.mock(FullTextQuery.class);
        final List<Long> results = new ArrayList<Long>();

        context.checking(new Expectations()
        {
            {
                oneOf(builder).buildQueryFromNativeSearchString("+content:(hithere(foo)) ");
                will(returnValue(ftq));

                oneOf(ftq).getResultList();
                will(returnValue(results));

                // unfortunately it's difficult to check for the correct type of sort
                oneOf(ftq).setSort(with(any(Sort.class)));

                oneOf(builder).setPaging(ftq, 0, MAX_RESULTS);
            }
        });

        assertSame(results, sut.fetch(request, 0L));
        context.assertIsSatisfied();
    }

    /**
     * Test execute method without search keywords or sort.
     */
    @Test
    public void testExecuteWithoutKeywordsAndWithoutSort()
    {
        assertNull(sut.fetch(new JSONObject(), 0L));
        context.assertIsSatisfied();
    }

    // TODO: try with sort, try with keywords separately
}
