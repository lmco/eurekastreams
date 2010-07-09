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
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Sort;
import org.hibernate.Criteria;
import org.hibernate.search.FullTextFilter;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.transform.ResultTransformer;

/**
 * Wrapper around org.eurekastreams.commons.search.FullTextQueryImpl that calls transformList on the result transformer
 * after transforming the tuples in list(), regardless of whether the loader is a ProjectionLoader.
 */
public class ProjectionFullTextQuery implements FullTextQuery
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(ProjectionFullTextQuery.class);

    /**
     * The result transformer.
     */
    private ResultTransformer resultTransformer;

    /**
     * The wrapped FullTextQuery.
     */
    private FullTextQuery wrappedFullTextQuery;

    /**
     * Constructor.
     *
     * @param inWrappedFullTextQuery
     *            the FullTextQuery being wrapped
     */
    public ProjectionFullTextQuery(final FullTextQuery inWrappedFullTextQuery)
    {
        wrappedFullTextQuery = inWrappedFullTextQuery;
    }

    /**
     * Set the result transformer to use to transform the results.
     *
     * @param inTransformer
     *            the transformer to use
     * @return this
     */
    @Override
    public FullTextQuery setResultTransformer(final ResultTransformer inTransformer)
    {
        // todo: make this TRACE
        log.info("Receiving the ResultTransformer.");
        wrappedFullTextQuery.setResultTransformer(inTransformer);
        this.resultTransformer = inTransformer;
        return this;
    }

    /**
     * Get the result list, calling transformTuple on the individual rows, and transformList when all done.
     *
     * @return the result list
     */
    @Override
    @SuppressWarnings("unchecked")
    public List getResultList()
    {
        log.info("Getting the result list from Lucene");

        long start = System.currentTimeMillis();
        List resultList = wrappedFullTextQuery.getResultList();
        long duration = System.currentTimeMillis() - start;

        if (log.isInfoEnabled())
        {
            log.info("Found " + resultList.size() + " results from Lucene in " + duration + " ms.");
        }

        log.info("Transforming the list with resultTransformer.transformList.");
        List transformedList = resultTransformer.transformList(resultList);

        return transformedList;
    }

    /**
     * Wrapped disableFullTextFilter.
     *
     * @param name
     *            the name
     */
    @Override
    public void disableFullTextFilter(final String name)
    {
        wrappedFullTextQuery.disableFullTextFilter(name);
    }

    /**
     * Wrapped enableFullTextFilter.
     *
     * @param name
     *            the name
     * @return the return value of enableFullTextFilter
     */
    @Override
    public FullTextFilter enableFullTextFilter(final String name)
    {
        return wrappedFullTextQuery.enableFullTextFilter(name);
    }

    /**
     * Wrapped explain.
     *
     * @param documentId
     *            the document id
     * @return the value returned by explain
     */
    @Override
    public Explanation explain(final int documentId)
    {
        return wrappedFullTextQuery.explain(documentId);
    }

    /**
     * Wrapped getResultSize.
     *
     * @return the value returned by getResultSize
     */
    @Override
    public int getResultSize()
    {
        return wrappedFullTextQuery.getResultSize();
    }

    /**
     * Wrapped setCriteriaQuery.
     *
     * @param criteria
     *            the criteria
     * @return the value returned by setCriteria
     */
    @Override
    public FullTextQuery setCriteriaQuery(final Criteria criteria)
    {
        return wrappedFullTextQuery.setCriteriaQuery(criteria);
    }

    /**
     * Wrapped setFilter.
     *
     * @param filter
     *            the filter
     * @return value returned by setFilter
     */
    @Override
    public FullTextQuery setFilter(final Filter filter)
    {
        return wrappedFullTextQuery.setFilter(filter);
    }

    /**
     * Wrapped setProjection.
     *
     * @param fields
     *            the fields
     * @return the value returned by setProjection
     */
    @Override
    public FullTextQuery setProjection(final String... fields)
    {
        return wrappedFullTextQuery.setProjection(fields);
    }

    /**
     * Wrapped setSort.
     *
     * @param sort
     *            the sort
     * @return value returned by setSort
     */
    @Override
    public FullTextQuery setSort(final Sort sort)
    {
        return wrappedFullTextQuery.setSort(sort);
    }

    /**
     * Wrapped executeUpdate.
     *
     * @return value returned by executeUpdate
     */
    @Override
    public int executeUpdate()
    {
        return wrappedFullTextQuery.executeUpdate();
    }

    /**
     * Wrapped getSingleResult.
     *
     * @return the value returned by getSingleResult
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object getSingleResult()
    {
        log.info("Getting the single result, wrapping it with resultTransformer.transformList.");
        List list = new ArrayList();
        list.add(wrappedFullTextQuery.getSingleResult());
        list = resultTransformer.transformList(list);
        return list.get(0);
    }

    /**
     * Wrapped setFirstResult.
     *
     * @param arg0
     *            the arg
     * @return the value returned by setFirstResult.
     */
    @Override
    public Query setFirstResult(final int arg0)
    {
        return wrappedFullTextQuery.setFirstResult(arg0);
    }

    /**
     * Wrapped setFlushMode.
     *
     * @param arg0
     *            the arg
     * @return value returned by setFlushMode
     */
    @Override
    public Query setFlushMode(final FlushModeType arg0)
    {
        return wrappedFullTextQuery.setFlushMode(arg0);
    }

    /**
     * Wrapped setHint.
     *
     * @param arg0
     *            first arg
     * @param arg1
     *            second arg
     * @return the value returned by setHint
     */
    @Override
    public Query setHint(final String arg0, final Object arg1)
    {
        return wrappedFullTextQuery.setHint(arg0, arg1);
    }

    /**
     * Wrapped setMaxResults.
     *
     * @param arg0
     *            the arg
     * @return value returned by setMaxResults
     */
    @Override
    public Query setMaxResults(final int arg0)
    {
        return wrappedFullTextQuery.setMaxResults(arg0);
    }

    /**
     * Wrapped setParameter.
     *
     * @param arg0
     *            the first arg
     * @param arg1
     *            the second arg
     * @return value returned by setParameter
     */
    @Override
    public Query setParameter(final String arg0, final Object arg1)
    {
        return wrappedFullTextQuery.setParameter(arg0, arg1);
    }

    /**
     * Wrapped setParameter.
     *
     * @param arg0
     *            the first arg
     * @param arg1
     *            the second arg
     * @return value returned by setParameter
     */
    @Override
    public Query setParameter(final int arg0, final Object arg1)
    {
        return wrappedFullTextQuery.setParameter(arg0, arg1);
    }

    /**
     * Wrapped setParameter.
     *
     * @param arg0
     *            the first arg
     * @param arg1
     *            the second arg
     * @param arg2
     *            the third arg
     * @return value returned by setParameter
     */
    @Override
    public Query setParameter(final String arg0, final Date arg1, final TemporalType arg2)
    {
        return wrappedFullTextQuery.setParameter(arg0, arg1, arg2);
    }

    /**
     * Wrapped setParameter.
     *
     * @param arg0
     *            the first arg
     * @param arg1
     *            the second arg
     * @param arg2
     *            the third arg
     * @return value returned by setParameter
     */
    @Override
    public Query setParameter(final String arg0, final Calendar arg1, final TemporalType arg2)
    {
        return wrappedFullTextQuery.setParameter(arg0, arg1, arg2);
    }

    /**
     * Wrapped setParameter.
     *
     * @param arg0
     *            the first arg
     * @param arg1
     *            the second arg
     * @param arg2
     *            the third arg
     * @return value returned by setParameter
     */
    @Override
    public Query setParameter(final int arg0, final Date arg1, final TemporalType arg2)
    {
        return wrappedFullTextQuery.setParameter(arg0, arg1, arg2);
    }

    /**
     * Wrapped setParameter.
     *
     * @param arg0
     *            the first arg
     * @param arg1
     *            the second arg
     * @param arg2
     *            the third arg
     * @return value returned by setParameter
     */
    @Override
    public Query setParameter(final int arg0, final Calendar arg1, final TemporalType arg2)
    {
        return wrappedFullTextQuery.setParameter(arg0, arg1, arg2);
    }

}
