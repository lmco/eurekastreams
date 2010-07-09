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
package org.eurekastreams.commons.hibernate;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Query;
import org.hibernate.classic.Session;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for QueryOptimizer class.
 */
public class QueryOptimizerTest
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
     * The result count.
     */
    private static final long RESULT_COUNT = 323L;

    /**
     * Test determining a collection's size without first setting the
     * EntityManager.
     */
    @Test(expected = NullPointerException.class)
    public void testDetermineCollectionSizeThrowsExceptionWithoutEntityManager()
    {
        QueryOptimizer optimizer = new QueryOptimizer();
        optimizer.determineCollectionSize(new ArrayList<String>());
    }

    /**
     * Test setting the entity manager.
     */
    @Test
    public void testEntityManagerSetter()
    {
        EntityManager entityManager = context.mock(EntityManager.class);

        QueryOptimizer optimizer = new QueryOptimizer();
        optimizer.setEntityManager(entityManager);
    }

    /**
     * Test determineCollectionSize.
     */
    @Test
    public void testDetermineCollectionSize()
    {
        final EntityManager entityManager = context.mock(EntityManager.class);
        final Session hibernateSession = context.mock(Session.class);
        final Query hibernateQuery = context.mock(Query.class);
        final Collection<Object> collectionToSize = new ArrayList<Object>();

        final List<Long> resultList = new ArrayList<Long>();
        resultList.add(new Long(RESULT_COUNT));

        QueryOptimizer optimizer = new QueryOptimizer();
        optimizer.setEntityManager(entityManager);

        // setup the expectations.
        context.checking(new Expectations()
        {
            {
                one(entityManager).getDelegate();
                will(returnValue(hibernateSession));

                one(hibernateSession).createFilter(collectionToSize, "select count(*)");
                will(returnValue(hibernateQuery));

                one(hibernateQuery).list();
                will(returnValue(resultList));
            }
        });

        // call the SUT
        assertEquals(RESULT_COUNT, optimizer.determineCollectionSize(collectionToSize));

        // all expectations met?
        context.assertIsSatisfied();
    }
}
