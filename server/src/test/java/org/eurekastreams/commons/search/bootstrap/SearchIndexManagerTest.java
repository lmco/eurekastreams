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
package org.eurekastreams.commons.search.bootstrap;

import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.SearchFactory;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for SearchIndexManager.
 */
public class SearchIndexManagerTest
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
     * FullTextSession mock.
     */
    FullTextSession fullTextSessionMock;

    /**
     * Query mock.
     */
    Query queryMock;

    /**
     * ScrollableResults mock.
     */
    ScrollableResults scrollMock;

    /**
     * SearchFactory mock.
     */
    SearchFactory searchFactoryMock;

    /**
     * Setup method for tests.
     */
    @Before
    public void setup()
    {
        fullTextSessionMock = context.mock(FullTextSession.class);
        queryMock = context.mock(Query.class);
        scrollMock = context.mock(ScrollableResults.class);
        searchFactoryMock = context.mock(SearchFactory.class);
    }

    /**
     * Test reindexing models using the class overload. The batch size is set to 10, with 11 records. Make sure the
     * flushToIndexes is called
     * 
     * Look, this is ridiculous, I know. This test is nothing more than a useless whitebox test to get past clover
     * tests.
     */
    @Test
    public void testReindexModelsFromClass()
    {
        final int fetchSize = 938;
        final int flushSize = 2;
        context.checking(new Expectations()
        {
            {
                // purge, flush, optimize, flush first
                one(fullTextSessionMock).purgeAll(SearchIndexManagerTest.class);
                one(fullTextSessionMock).flushToIndexes();
                one(fullTextSessionMock).getSearchFactory();
                will(returnValue(searchFactoryMock));
                one(searchFactoryMock).optimize(SearchIndexManagerTest.class);
                one(fullTextSessionMock).flushToIndexes();

                one(fullTextSessionMock).createQuery("FROM SearchIndexManagerTest");
                will(returnValue(queryMock));

                one(queryMock).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
                will(returnValue(queryMock));

                one(queryMock).setCacheMode(CacheMode.IGNORE);
                will(returnValue(queryMock));

                one(queryMock).setFetchSize(fetchSize);
                will(returnValue(queryMock));

                one(queryMock).scroll(ScrollMode.FORWARD_ONLY);
                will(returnValue(scrollMock));

                // 3 results, batch size of 2
                Object entity1 = new Object();
                Object entity2 = new Object();
                Object entity3 = new Object();

                one(scrollMock).next();
                will(returnValue(true));
                one(scrollMock).get(0);
                will(returnValue(entity1));
                one(fullTextSessionMock).index(entity1);

                one(scrollMock).next();
                will(returnValue(true));
                one(scrollMock).get(0);
                will(returnValue(entity2));
                one(fullTextSessionMock).index(entity2);

                // end of batch - flush
                one(fullTextSessionMock).flushToIndexes();
                one(fullTextSessionMock).clear();

                // last one
                one(scrollMock).next();
                will(returnValue(true));
                one(scrollMock).get(0);
                will(returnValue(entity3));
                one(fullTextSessionMock).index(entity3);

                // no more
                one(scrollMock).next();
                will(returnValue(false));

                // flush, optimize, flush, clear remaining
                one(fullTextSessionMock).flushToIndexes();
                one(fullTextSessionMock).clear();
                one(fullTextSessionMock).getSearchFactory();
                will(returnValue(searchFactoryMock));
                one(searchFactoryMock).optimize(SearchIndexManagerTest.class);
            }
        });

        // call the system under test
        SearchIndexManager indexer = new SearchIndexManager(fetchSize, flushSize);
        indexer.reindexEntities(SearchIndexManagerTest.class, fullTextSessionMock);

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Test reindexing models using the class, entityName overload.
     * 
     * Look, this is ridiculous, I know. This test is nothing more than a useless whitebox test to get past clover
     * tests.
     */
    @Test
    public void testReindexModelsFromClassAndEntityName()
    {
        final int fetchSize = 8;
        final int flushSize = 2;
        context.checking(new Expectations()
        {
            {
                // purge, flush, optimize, flush first
                one(fullTextSessionMock).purgeAll(SearchIndexManagerTest.class);
                one(fullTextSessionMock).flushToIndexes();
                one(fullTextSessionMock).getSearchFactory();
                will(returnValue(searchFactoryMock));
                one(searchFactoryMock).optimize(SearchIndexManagerTest.class);
                one(fullTextSessionMock).flushToIndexes();

                one(fullTextSessionMock).createQuery("FROM HeyNow");
                will(returnValue(queryMock));

                one(queryMock).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
                will(returnValue(queryMock));

                one(queryMock).setCacheMode(CacheMode.IGNORE);
                will(returnValue(queryMock));

                one(queryMock).setFetchSize(fetchSize);
                will(returnValue(queryMock));

                one(queryMock).scroll(ScrollMode.FORWARD_ONLY);
                will(returnValue(scrollMock));

                // 3 results, batch size of 2
                Object entity1 = new Object();
                Object entity2 = new Object();
                Object entity3 = new Object();

                one(scrollMock).next();
                will(returnValue(true));
                one(scrollMock).get(0);
                will(returnValue(entity1));
                one(fullTextSessionMock).index(entity1);

                one(scrollMock).next();
                will(returnValue(true));
                one(scrollMock).get(0);
                will(returnValue(entity2));
                one(fullTextSessionMock).index(entity2);

                // end of batch - flush
                one(fullTextSessionMock).flushToIndexes();
                one(fullTextSessionMock).clear();

                // last one
                one(scrollMock).next();
                will(returnValue(true));
                one(scrollMock).get(0);
                will(returnValue(entity3));
                one(fullTextSessionMock).index(entity3);

                // no more
                one(scrollMock).next();
                will(returnValue(false));

                // flush, optimize, flush, clear batch
                one(fullTextSessionMock).clear();
                one(fullTextSessionMock).flushToIndexes();
                one(fullTextSessionMock).getSearchFactory();
                will(returnValue(searchFactoryMock));
                one(searchFactoryMock).optimize(SearchIndexManagerTest.class);
            }
        });

        // call the system under test
        SearchIndexManager indexer = new SearchIndexManager(fetchSize, flushSize);
        indexer.reindexEntities(SearchIndexManagerTest.class, "HeyNow", fullTextSessionMock);

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Test purging search index.
     */
    @Test
    public void testPurgeSearchIndex()
    {
        final int fetchSize = 938;
        final int flushSize = 888;
        context.checking(new Expectations()
        {
            {
                // purge
                one(fullTextSessionMock).purgeAll(SearchIndexManagerTest.class);

                // flush
                one(fullTextSessionMock).flushToIndexes();

                // get SearchFactory
                one(fullTextSessionMock).getSearchFactory();
                will(returnValue(searchFactoryMock));

                // optimize
                one(searchFactoryMock).optimize(SearchIndexManagerTest.class);

                // flush
                one(fullTextSessionMock).flushToIndexes();
            }
        });

        // call the system under test
        SearchIndexManager indexer = new SearchIndexManager(fetchSize, flushSize);
        indexer.purgeSearchIndex(SearchIndexManagerTest.class, fullTextSessionMock);

        // all expectations met?
        context.assertIsSatisfied();
    }
}
