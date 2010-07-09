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
package org.eurekastreams.commons.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.transform.ResultTransformer;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for ProjectionSearchRequestBuilder.
 */
public class ProjectionSearchRequestBuilderTest
{
    /**
     * Context for mocking.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Test the escape() method.
     */
    @Test
    public void testEscape()
    {
        ProjectionSearchRequestBuilder sut = new ProjectionSearchRequestBuilder();
        assertEquals("Hey now!", sut.escape("Hey now!"));
        assertEquals("\\\\Hey \\:no\\^w\\|what's \\&up?", sut.escape("\\Hey :no^w|what's &up?"));
    }

    /**
     * Test the paging method, setPaging().
     */
    @Test
    public void testSetPaging()
    {
        ProjectionSearchRequestBuilder sut = new ProjectionSearchRequestBuilder();
        final FullTextQuery query = context.mock(FullTextQuery.class);

        context.checking(new Expectations()
        {
            {
                one(query).setFirstResult(3);
                one(query).setMaxResults(7);
            }
        });

        // invoke sut
        sut.setPaging(query, 3, 9);

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Test the containsAdvancedSearchCharacters() method.
     */
    @Test
    public void testContainsAdvancedSearchCharacters()
    {
        ProjectionSearchRequestBuilder sut = new ProjectionSearchRequestBuilder();
        assertTrue(sut.containsAdvancedSearchCharacters("(some text)"));
        assertTrue(sut.containsAdvancedSearchCharacters("(some text"));
        assertTrue(sut.containsAdvancedSearchCharacters("some text)"));
        assertTrue(sut.containsAdvancedSearchCharacters("+some text"));
        assertTrue(sut.containsAdvancedSearchCharacters("some-text"));
        assertTrue(sut.containsAdvancedSearchCharacters("some +text"));
        assertTrue(sut.containsAdvancedSearchCharacters("some text?"));
        assertTrue(sut.containsAdvancedSearchCharacters("some text*"));
        assertTrue(sut.containsAdvancedSearchCharacters("some \"text\""));
        assertFalse(sut.containsAdvancedSearchCharacters("some text"));
        assertFalse(sut.containsAdvancedSearchCharacters("hey now!"));
        assertFalse(sut.containsAdvancedSearchCharacters("whoa there, buddy!"));
    }

    /**
     * Test the escapeAllButWildcardCharacters() method.
     */
    @Test
    public void testEscapeAllButWildcardCharacters()
    {
        ProjectionSearchRequestBuilder sut = new ProjectionSearchRequestBuilder();
        assertEquals("foo bar", sut.escapeAllButWildcardCharacters("foo bar"));
        assertEquals("\\(foo bar\\)", sut.escapeAllButWildcardCharacters("(foo bar)"));
        assertEquals("\\\\foo bar", sut.escapeAllButWildcardCharacters("\\foo bar"));
        assertEquals("foo\\:bar", sut.escapeAllButWildcardCharacters("foo:bar"));
        assertEquals("foo\\^bar", sut.escapeAllButWildcardCharacters("foo^bar"));
        assertEquals("foo\\|bar", sut.escapeAllButWildcardCharacters("foo|bar"));
        assertEquals("foo\\&bar", sut.escapeAllButWildcardCharacters("foo&bar"));
        assertEquals("\\\"foo bar\\\"", sut.escapeAllButWildcardCharacters("\"foo bar\""));
        assertEquals("foo\\+bar", sut.escapeAllButWildcardCharacters("foo+bar"));
        assertEquals("foo\\-bar", sut.escapeAllButWildcardCharacters("foo-bar"));
        assertEquals("\\!foo bar", sut.escapeAllButWildcardCharacters("!foo bar"));

        // test valid wildcards
        assertEquals("he?lo there", sut.escapeAllButWildcardCharacters("he?lo there"));
        assertEquals("hello* there*", sut.escapeAllButWildcardCharacters("hello* there*"));
        assertEquals("he**o th??e", sut.escapeAllButWildcardCharacters("he**o th??e"));

        // test bare wildcards
        assertEquals("hi*  hi", sut.escapeAllButWildcardCharacters("hi* * hi"));
        assertEquals("hi?*  hi", sut.escapeAllButWildcardCharacters("hi?* ? hi"));
        assertEquals(" asd hi", sut.escapeAllButWildcardCharacters("* asd hi"));

        // test words composed of only wildcards
        assertEquals(" hi  hi", sut.escapeAllButWildcardCharacters("*** hi *** hi"));
        assertEquals("", sut.escapeAllButWildcardCharacters("*"));
        assertEquals("", sut.escapeAllButWildcardCharacters("*?*?*?*"));
        assertEquals("hello there", sut.escapeAllButWildcardCharacters("*hello there"));
        assertEquals("hello there", sut.escapeAllButWildcardCharacters("****hello there"));
        assertEquals("hello there", sut.escapeAllButWildcardCharacters("?hello there"));
        assertEquals(" hello there", sut.escapeAllButWildcardCharacters(" ?hello there"));
    }

    /**
     * Test the buildFieldList() method.
     */
    @Test
    public void testBuildFieldList()
    {
        ProjectionSearchRequestBuilder sut = new ProjectionSearchRequestBuilder();

        List<String> fields = sut.buildFieldList();

        // make sure the default fields are populated, even if you don't specify any others
        assertEquals(2, fields.size());
        assertTrue(fields.contains(FullTextQuery.ID));
        assertTrue(fields.contains(FullTextQuery.OBJECT_CLASS));

        ArrayList<String> entityFields = new ArrayList<String>();
        entityFields.add("foo");
        entityFields.add("bar");

        // test without the search score or managed entity
        sut.setResultFields(entityFields);
        fields = sut.buildFieldList();
        assertEquals(4, fields.size());
        assertTrue(fields.contains("foo"));
        assertTrue(fields.contains("bar"));
        assertTrue(fields.contains(FullTextQuery.ID));
        assertTrue(fields.contains(FullTextQuery.OBJECT_CLASS));
    }

    /**
     * Test the prepareQuery() method.
     *
     * This unit test won't really help anyone - it's pretty much white box.
     *
     * @throws ParseException
     *             on error
     */
    @Test(expected = RuntimeException.class)
    public void testPrepareQueryWithParseError() throws ParseException
    {
        ProjectionSearchRequestBuilderTestHelper sutSubclass = new ProjectionSearchRequestBuilderTestHelper();

        final QueryParser queryParser = context.mock(QueryParser.class, "mockedQueryParser");
        final String searchText = "foo now hey bar";

        context.checking(new Expectations()
        {
            {
                one(queryParser).parse(searchText);
                will(throwException(new ParseException()));
            }
        });

        // invoke sut
        sutSubclass.prepareQuery(queryParser, searchText);

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Test the prepareQuery() method.
     *
     * This unit test won't really help anyone - it's pretty much white box.
     *
     * @throws ParseException
     *             when sad
     */
    @Test
    public void testPrepareQueryWithSuccess() throws ParseException
    {
        ProjectionSearchRequestBuilderTestHelper sutSubclass = new ProjectionSearchRequestBuilderTestHelper();

        final String searchText = "foo now hey bar";
        final Class< ? >[] resultTypes = { Integer.class };
        final FullTextEntityManager ftem = context.mock(FullTextEntityManager.class, "mockedFtem");
        final QueryParser queryParser = context.mock(QueryParser.class, "mockedQueryParser");
        final Query luceneQuery = context.mock(Query.class, "mockedQuery");
        final org.hibernate.search.jpa.FullTextQuery expectedReturnValue = context.mock(
                org.hibernate.search.jpa.FullTextQuery.class, "mockedFullTextQuery");
        final ResultTransformer resultTransformer = context.mock(ResultTransformer.class, "mockedResultTransformer");

        ArrayList<String> entityFields = new ArrayList<String>();
        entityFields.add("foo");
        entityFields.add("bar");

        context.checking(new Expectations()
        {
            {
                one(queryParser).parse(searchText);
                will(returnValue(luceneQuery));

                one(ftem).createFullTextQuery(luceneQuery, resultTypes);
                will(returnValue(expectedReturnValue));

                one(expectedReturnValue).setProjection(with(any(String[].class)));

                one(expectedReturnValue).setResultTransformer(resultTransformer);
            }
        });

        // setup sut
        sutSubclass.setFullTextEntityManager(ftem);
        sutSubclass.setResultTypes(resultTypes);
        sutSubclass.setResultFields(entityFields);
        sutSubclass.setResultTransformer(resultTransformer);

        assertEquals("Integer", sutSubclass.getEntityNames());

        // invoke sut
        org.hibernate.search.jpa.FullTextQuery returnedValue = sutSubclass.prepareQuery(queryParser, searchText);

        // broken
        assertTrue(returnedValue instanceof ProjectionFullTextQuery);

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Test the getEntityNames() method.
     */
    @Test
    public void testGetEntityNames()
    {
        final Class< ? >[] resultTypes = { Integer.class, Long.class };

        ProjectionSearchRequestBuilderTestHelper sutSubclass = new ProjectionSearchRequestBuilderTestHelper();
        sutSubclass.setResultTypes(resultTypes);
        assertEquals("Integer, Long", sutSubclass.getEntityNames());
    }

    /**
     * Test the buildQueryFromSearchText() method.
     */
    @Test
    public void testBuildQueryFromSearchTextWithAdvancedCharacters()
    {
        ProjectionSearchRequestBuilderTestHelper sutSubclass = new ProjectionSearchRequestBuilderTestHelper();
        final org.hibernate.search.jpa.FullTextQuery preparedQuery = context.mock(
                org.hibernate.search.jpa.FullTextQuery.class, "preparedQuery");
        final QueryParserBuilder advancedQueryParserBuilder = context.mock(QueryParserBuilder.class,
                "advanced query parser");
        final QueryParser queryParser = context.mock(QueryParser.class);
        final QueryParserBuilder basicQueryParserBuilder = context.mock(QueryParserBuilder.class, "basic query parser");

        sutSubclass.setAdvancedQueryParserBuilder(advancedQueryParserBuilder);
        sutSubclass.setQueryParserBuilder(basicQueryParserBuilder);
        sutSubclass.setPreparedQuery(preparedQuery);

        // Set up expectations
        context.checking(new Expectations()
        {
            {
                one(advancedQueryParserBuilder).buildQueryParser();
                will(returnValue(queryParser));
            }
        });

        // invoke sut
        org.hibernate.search.jpa.FullTextQuery returnValue = sutSubclass
                .buildQueryFromSearchText("(hi: there) (foo\\ bar)");

        assertSame(preparedQuery, returnValue);

        // make sure the expected params were passed into prepareQuery
        assertSame(queryParser, sutSubclass.getPreparedQueryQueryParser());
        assertEquals("(hi\\: there) (foo\\\\ bar)", sutSubclass.getPrepareQueryNativeSearchString());

        context.assertIsSatisfied();
    }

    /**
     * Test the buildQueryFromSearchText() method.
     */
    @Test
    public void testBuildQueryFromSearchTextWithStandardCharacters()
    {
        ProjectionSearchRequestBuilderTestHelper sutSubclass = new ProjectionSearchRequestBuilderTestHelper();
        final org.hibernate.search.jpa.FullTextQuery preparedQuery = context.mock(
                org.hibernate.search.jpa.FullTextQuery.class, "preparedQuery");
        final QueryParserBuilder advancedQueryParserBuilder = context.mock(QueryParserBuilder.class,
                "advanced query parser");
        final QueryParserBuilder basicQueryParserBuilder = context.mock(QueryParserBuilder.class, "basic query parser");
        final QueryParser queryParser = context.mock(QueryParser.class);

        sutSubclass.setAdvancedQueryParserBuilder(advancedQueryParserBuilder);
        sutSubclass.setQueryParserBuilder(basicQueryParserBuilder);
        sutSubclass.setPreparedQuery(preparedQuery);
        sutSubclass.setSearchStringFormat("field1:(%1$s) or field2:(%1$s)");

        // Set up expectations
        context.checking(new Expectations()
        {
            {
                one(basicQueryParserBuilder).buildQueryParser();
                will(returnValue(queryParser));
            }
        });

        // invoke sut
        org.hibernate.search.jpa.FullTextQuery returnValue = sutSubclass.buildQueryFromSearchText("hi there foo bar");

        assertSame(preparedQuery, returnValue);

        // make sure the expected params were passed into prepareQuery
        assertSame(queryParser, sutSubclass.getPreparedQueryQueryParser());
        assertEquals("field1:(hi there foo bar) or field2:(hi there foo bar)", sutSubclass
                .getPrepareQueryNativeSearchString());

        context.assertIsSatisfied();
    }

    /**
     * Test the buildQueryFromNativeSearchString() method.
     */
    @Test
    public void testBuildQueryFromNativeSearchString()
    {
        ProjectionSearchRequestBuilderTestHelper sutSubclass = new ProjectionSearchRequestBuilderTestHelper();
        final org.hibernate.search.jpa.FullTextQuery preparedQuery = context.mock(
                org.hibernate.search.jpa.FullTextQuery.class, "preparedQuery");
        sutSubclass.setPreparedQuery(preparedQuery);
        final QueryParserBuilder basicQueryParserBuilder = context.mock(QueryParserBuilder.class, "basic query parser");
        sutSubclass.setQueryParserBuilder(basicQueryParserBuilder);
        final QueryParser queryParser = context.mock(QueryParser.class);

        // Set up expectations
        context.checking(new Expectations()
        {
            {
                one(basicQueryParserBuilder).buildQueryParser();
                will(returnValue(queryParser));
            }
        });

        // invoke sut
        sutSubclass.buildQueryFromNativeSearchString("field1:(hi there foo bar) or field2:(hi there foo bar)");

        // make sure the expected params were passed into prepareQuery
        assertSame(queryParser, sutSubclass.getPreparedQueryQueryParser());
        assertEquals("field1:(hi there foo bar) or field2:(hi there foo bar)", sutSubclass
                .getPrepareQueryNativeSearchString());

        context.assertIsSatisfied();
    }

    /**
     * Helper class for ProjectionSearchRequestBuilder - for stubbing out untestable stuff.
     */
    private class ProjectionSearchRequestBuilderTestHelper extends ProjectionSearchRequestBuilder
    {
        /**
         * Mocked out FullTextEntityManager.
         */
        private FullTextEntityManager fullTextEntityManager;

        /**
         * Mocked prepared query.
         */
        private org.hibernate.search.jpa.FullTextQuery preparedQuery;

        /**
         * Setter for preparedQuery.
         *
         * @param inPreparedQuery
         *            the preparedQuery
         */
        public void setPreparedQuery(final org.hibernate.search.jpa.FullTextQuery inPreparedQuery)
        {
            preparedQuery = inPreparedQuery;
        }

        /**
         * Setter for fullTextEntityManager.
         *
         * @param ftem
         *            the FullTextEntityManager
         * @return
         */
        public void setFullTextEntityManager(final FullTextEntityManager ftem)
        {
            fullTextEntityManager = ftem;
        }

        /**
         * Override the parent class's getFullTextEntityManager() method to return a canned one.
         *
         * @return a FullTextEntityManager from the entityManager.
         */
        @Override
        protected FullTextEntityManager getFullTextEntityManager()
        {
            return fullTextEntityManager;
        }

        /**
         * Prepare the input luceneQuery - return the mocked out one if not null, else call super.prepareQuery().
         *
         * @param inQueryParser
         *            the QueryParser to use
         * @param nativeSearchString
         *            the query to prepare
         * @return the fully prepared FullTextQuery
         */
        @Override
        protected org.hibernate.search.jpa.FullTextQuery prepareQuery(final QueryParser inQueryParser,
                final String nativeSearchString)
        {
            if (preparedQuery != null)
            {
                // store these values for assertions
                preparedQueryQueryParser = inQueryParser;
                prepareQueryNativeSearchString = nativeSearchString;

                // return the canned query
                return preparedQuery;
            }
            else
            {
                return super.prepareQuery(inQueryParser, nativeSearchString);
            }
        }

        /**
         * The QueryParser passed into prepareQuery - stored for assertion.
         */
        private QueryParser preparedQueryQueryParser;

        /**
         * The native search string passed into prepareQuery - stored for assertion.
         */
        private String prepareQueryNativeSearchString;

        /**
         * Get the QueryParser passed into prepareQuery.
         *
         * @return the QueryParser passed into prepareQuery
         */
        public QueryParser getPreparedQueryQueryParser()
        {
            return preparedQueryQueryParser;
        }

        /**
         * Get the native search string passed into prepareQuery.
         *
         * @return the prepared query that was passed into the query parser
         */
        public String getPrepareQueryNativeSearchString()
        {
            return prepareQueryNativeSearchString;
        }
    }
}
