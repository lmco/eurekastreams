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
package org.eurekastreams.server.persistence.mappers;

import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.domain.Person;
import org.hibernate.search.FullTextSession;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for IndexEntity.
 * 
 */
public class IndexEntityTest
{
    /**
     * Helper class to mock out singleton-wrapping method.
     */
    private class IndexEntityTester extends IndexEntity<Person>
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
     * FullTextSession mock.
     */
    private FullTextSession fullTextSessionMock = context.mock(FullTextSession.class);

    /**
     * Person mock.
     */
    private Person person = context.mock(Person.class);

    /**
     * Test.
     */
    @Test
    public void testExecute()
    {
        IndexEntityTester sut = new IndexEntityTester();
        sut.setFullTextSession(fullTextSessionMock);

        context.checking(new Expectations()
        {
            {
                oneOf(fullTextSessionMock).index(person);
            }
        });

        assertTrue(sut.execute(person));
        context.assertIsSatisfied();

    }

}
