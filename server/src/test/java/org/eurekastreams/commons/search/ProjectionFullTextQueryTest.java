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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.TemporalType;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Sort;
import org.hibernate.Criteria;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.transform.ResultTransformer;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for ProjectionFullTextQuery.
 */
public class ProjectionFullTextQueryTest
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
     * Mocked wrapped FullTextQuery.
     */
    private FullTextQuery ftqMock = context.mock(FullTextQuery.class);

    /**
     * System under test.
     */
    private ProjectionFullTextQuery sut = new ProjectionFullTextQuery(ftqMock);

    /**
     * The result transformer.
     */
    private ResultTransformer resultTransformer = context
            .mock(ResultTransformer.class);

    /**
     * Test that getResultList passes the resulting list through the
     * transformList method of the ResultTransformer.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testGetResultList()
    {
        final List list = context.mock(List.class);
        context.checking(new Expectations()
        {
            {
                oneOf(ftqMock).setResultTransformer(resultTransformer);

                oneOf(ftqMock).getResultList();
                will(returnValue(list));

                oneOf(resultTransformer).transformList(list);

                allowing(list).size();
                will(returnValue(1));
            }
        });

        sut.setResultTransformer(resultTransformer);
        sut.getResultList();
        context.assertIsSatisfied();
    }

    /**
     * Test that getSingleResult passes the resulting object through the
     * transformList method of the ResultTransformer in a wrapping List.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testGetSingleResult()
    {
        final Object singleResult = context.mock(Object.class);
        final List wrappingList = new ArrayList();
        wrappingList.add(singleResult);
        context.checking(new Expectations()
        {
            {
                oneOf(ftqMock).setResultTransformer(resultTransformer);

                oneOf(ftqMock).getSingleResult();
                will(returnValue(singleResult));

                oneOf(resultTransformer).transformList(wrappingList);
            }
        });

        sut.setResultTransformer(resultTransformer);
        sut.getSingleResult();
        context.assertIsSatisfied();
    }

    /**
     * Test that the pass-through is correct.
     */
    @Test
    public void testDisableFullTextFilter()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(ftqMock).disableFullTextFilter("foo");
            }
        });
        sut.disableFullTextFilter("foo");
        context.assertIsSatisfied();
    }

    /**
     * Test that the pass-through is correct.
     */
    @Test
    public void testEnableFullTextFilter()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(ftqMock).enableFullTextFilter("foo");
            }
        });
        sut.enableFullTextFilter("foo");
        context.assertIsSatisfied();
    }

    /**
     * Test that the pass-through is correct.
     */
    @Test
    public void testExplain()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(ftqMock).explain(3);
            }
        });
        sut.explain(3);
        context.assertIsSatisfied();
    }

    /**
     * Test that the pass-through is correct.
     */
    @Test
    public void testGetResultSize()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(ftqMock).getResultSize();
            }
        });
        sut.getResultSize();
        context.assertIsSatisfied();
    }

    /**
     * Test that the pass-through is correct.
     */
    @Test
    public void testSetCriteriaQuery()
    {
        final Criteria crit = context.mock(Criteria.class);
        context.checking(new Expectations()
        {
            {
                oneOf(ftqMock).setCriteriaQuery(crit);
            }
        });
        sut.setCriteriaQuery(crit);
        context.assertIsSatisfied();
    }

    /**
     * Test that the pass-through is correct.
     */
    @Test
    public void testSetFilter()
    {
        final Filter f = context.mock(Filter.class);
        context.checking(new Expectations()
        {
            {
                oneOf(ftqMock).setFilter(f);
            }
        });
        sut.setFilter(f);
        context.assertIsSatisfied();
    }

    /**
     * Test that the pass-through is correct.
     */
    @Test
    public void testSetProjection()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(ftqMock).setProjection("foo", "bar");
            }
        });
        sut.setProjection("foo", "bar");
        context.assertIsSatisfied();
    }

    /**
     * Test that the pass-through is correct.
     */
    @Test
    public void testSetSort()
    {
        final Sort sort = context.mock(Sort.class);
        context.checking(new Expectations()
        {
            {
                oneOf(ftqMock).setSort(sort);
            }
        });
        sut.setSort(sort);
        context.assertIsSatisfied();
    }

    /**
     * Test that the pass-through is correct.
     */
    @Test
    public void testExecuteUpdate()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(ftqMock).executeUpdate();
            }
        });
        sut.executeUpdate();
        context.assertIsSatisfied();
    }

    /**
     * Test that the pass-through is correct.
     */
    @Test
    public void testSetFirstResult()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(ftqMock).setFirstResult(3);
            }
        });
        sut.setFirstResult(3);
        context.assertIsSatisfied();
    }

    /**
     * Test that the pass-through is correct.
     */
    @Test
    public void testSetFlushMode()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(ftqMock).setFlushMode(FlushModeType.AUTO);
            }
        });
        sut.setFlushMode(FlushModeType.AUTO);
        context.assertIsSatisfied();
    }

    /**
     * Test that the pass-through is correct.
     */
    @Test
    public void testSetHint()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(ftqMock).setHint("foo", "bar");
            }
        });
        sut.setHint("foo", "bar");
        context.assertIsSatisfied();
    }

    /**
     * Test that the pass-through is correct.
     */
    @Test
    public void testSetMaxResults()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(ftqMock).setMaxResults(9);
            }
        });
        sut.setMaxResults(9);
        context.assertIsSatisfied();
    }


    /**
     * Test that the pass-through is correct.
     */
    @Test
    public void testSetParameter1()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(ftqMock).setParameter(2, "foo");
            }
        });
        sut.setParameter(2, "foo");
        context.assertIsSatisfied();
    }

    /**
     * Test that the pass-through is correct.
     */
    @Test
    public void testSetParameter2()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(ftqMock).setParameter("foo", "bar");
            }
        });
        sut.setParameter("foo", "bar");
        context.assertIsSatisfied();
    }

    /**
     * Test that the pass-through is correct.
     */
    @Test
    public void testSetParameter3()
    {
        final Calendar cal = context.mock(Calendar.class);
        context.checking(new Expectations()
        {
            {
                oneOf(ftqMock).setParameter(5, cal, TemporalType.DATE);
            }
        });
        sut.setParameter(5, cal, TemporalType.DATE);
        context.assertIsSatisfied();
    }

    /**
     * Test that the pass-through is correct.
     */
    @Test
    public void testSetParameter4()
    {
        final Date date = context.mock(Date.class);
        context.checking(new Expectations()
        {
            {
                oneOf(ftqMock).setParameter(3, date, TemporalType.TIMESTAMP);
            }
        });
        sut.setParameter(3, date, TemporalType.TIMESTAMP);
        context.assertIsSatisfied();
    }

    /**
     * Test that the pass-through is correct.
     */
    @Test
    public void testSetParameter5()
    {
        final Calendar cal = context.mock(Calendar.class);
        context.checking(new Expectations()
        {
            {
                oneOf(ftqMock).setParameter("foo", cal, TemporalType.DATE);
            }
        });
        sut.setParameter("foo", cal, TemporalType.DATE);
        context.assertIsSatisfied();
    }

    /**
     * Test that the pass-through is correct.
     */
    @Test
    public void testSetParameter6()
    {
        final Date date = context.mock(Date.class);
        context.checking(new Expectations()
        {
            {
                oneOf(ftqMock)
                        .setParameter("foo", date, TemporalType.TIMESTAMP);
            }
        });
        sut.setParameter("foo", date, TemporalType.TIMESTAMP);
        context.assertIsSatisfied();
    }
}
