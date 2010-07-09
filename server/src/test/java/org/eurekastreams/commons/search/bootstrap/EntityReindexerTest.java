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

import javax.persistence.EntityManager;

import org.hibernate.search.FullTextSession;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for EntityReindexer. 
 */
public class EntityReindexerTest
{
    /**
     * Helper class to mock out singleton-wrapping method.
     */
    private class EntityReindexerTester extends EntityReindexer
    {
        /**
         * Mocked out fullTextSession.
         */
        private FullTextSession fullTextSession;

        /**
         * Set the FullTextSession mock.
         * 
         * @param theFullTextSession
         *            a mocked FullTextSession to inject into the parent class with the overriden getFullTextSession()
         *            method.
         */
        public void setFullTextSession(final FullTextSession theFullTextSession)
        {
            fullTextSession = theFullTextSession;
        }

        /**
         * Overridden method to avoid necessary Hibernate singleton call.
         * 
         * @return the mocked fullTextSession
         */
        @Override
        protected FullTextSession getFullTextSession()
        {
            return fullTextSession;
        }
    }

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
     * Test class for reindexing.
     */
    private class TestClass1
    {
    }

    /**
     * Test class for reindexing.
     */
    private class TestClass2
    {
    }

    /**
     * FullTextSession mock.
     */
    FullTextSession fullTextSessionMock;

    /**
     * Setup method for tests.
     */
    @Before
    public void setup()
    {
        fullTextSessionMock = context.mock(FullTextSession.class);
    }

    /**
     * Test the reindex method, making sure it uses the input SearchIndexManager to reindex.
     */
    @Test
    public void testReindex()
    {
        final SearchIndexManager searchIndexManagerMock = context.mock(SearchIndexManager.class);
        final EntityManager emMock = context.mock(EntityManager.class);
        
        context.checking(new Expectations()
        {
            {
                one(searchIndexManagerMock).reindexEntities(TestClass1.class, fullTextSessionMock);
                one(searchIndexManagerMock).reindexEntities(TestClass2.class, fullTextSessionMock);
            }
        });

        EntityReindexerTester reindexer = new EntityReindexerTester();
        reindexer.setFullTextSession(fullTextSessionMock);
        reindexer.setSearchIndexManager(searchIndexManagerMock);
        reindexer.setEntityManager(emMock);
        reindexer.setEntitiesToReindex(new Class[] { TestClass1.class, TestClass2.class });

        // invoke SUT
        reindexer.reindex();

        // all expectations met?
        context.assertIsSatisfied();
    }

    /**
     * Test that setting the entity manager passes through to the underlying SearchIndexManager.
     */
    @Test
    public void testSetEntityManager()
    {
        EntityReindexer reindexer = new EntityReindexer();

        // invoke SUT
        reindexer.setEntityManager(context.mock(EntityManager.class));
    }
}
