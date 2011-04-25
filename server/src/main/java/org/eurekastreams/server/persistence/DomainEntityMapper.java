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
package org.eurekastreams.server.persistence;

import java.util.ArrayList;
import java.util.HashMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.PagedSet;
import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

/**
 * Parent class for domain entity mappers.
 * 
 * @param <T>
 *            the type of domain entity this class maps
 */
public abstract class DomainEntityMapper<T>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * The QueryOptimizer to use for specialized functions.
     */
    private QueryOptimizer queryOptimizer;

    /**
     * Constructor.
     * 
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for specialized functions.
     */
    public DomainEntityMapper(final QueryOptimizer inQueryOptimizer)
    {
        queryOptimizer = inQueryOptimizer;
    }

    /**
     * Get the QueryOptimizer to use for specialized functions.
     * 
     * @return the QueryOptimizer to use for specialized functions.
     */
    protected QueryOptimizer getQueryOptimizer()
    {
        return queryOptimizer;
    }

    /**
     * EntityManager to use for all ORM operations.
     */
    private EntityManager entityManager;

    /**
     * Getter for entityManager.
     * 
     * @return The entityManager.
     */
    protected EntityManager getEntityManager()
    {
        return entityManager;
    }

    /**
     * Set the entity manager to use for all ORM operations.
     * 
     * @param inEntityManager
     *            the EntityManager to use for all ORM operations.
     */
    @PersistenceContext
    public void setEntityManager(final EntityManager inEntityManager)
    {
        this.entityManager = inEntityManager;
    }

    /**
     * Get the domain entity name for the generic query operations.
     * 
     * @return the domain entity name for the generic query operations.
     */
    protected abstract String getDomainEntityName();

    /**
     * Insert the domain entity.
     * 
     * @param domainEntity
     *            The domainEntity to operate on.
     */
    public void insert(final T domainEntity)
    {
        entityManager.persist(domainEntity);
    }

    /**
     * Update all entities that have changed since they were loaded within the same context.
     */
    public void flush()
    {
        entityManager.flush();
    }

    /**
     * Clear the entity manager. Only use this if you know what you're doing. A good example is when you want to add an
     * item to an indexed collection, and get its proper indexColumn value back.
     */
    public void clear()
    {
        entityManager.clear();
    }

    /**
     * Refresh the domain entity.
     * 
     * @param domainEntity
     *            the domain entity.
     */
    public void refresh(final T domainEntity)
    {
        entityManager.refresh(domainEntity);
    }

    /**
     * Find the domain entity by id.
     * 
     * @param domainEntityId
     *            ID of the entity to look up
     * 
     * @return the entity with the input
     */
    @SuppressWarnings("unchecked")
    public T findById(final Long domainEntityId)
    {
        Query q = entityManager.createQuery("from " + getDomainEntityName() + " where id = :domainEntityId")
                .setParameter("domainEntityId", domainEntityId);

        return (T) q.getSingleResult();
    }

    /**
     * Find the domain entity by id.
     * 
     * @param domainEntityId
     *            ID of the entity to look up
     * @return the entity with the input
     */
    public T findById(final Integer domainEntityId)
    {
        return findById(domainEntityId.longValue());
    }

    /**
     * Get a PagedSet of type V, built from the input query string, with the count determined by the input
     * countQueryString (more efficient than the version of this query without it).
     * 
     * @param <V>
     *            the type of objects to return
     * @param from
     *            the starting index
     * @param to
     *            the ending index
     * @param queryString
     *            the query string
     * @param countQueryString
     *            the query string to use to determine the count - must return an integer
     * @param parameters
     *            the parameters to inject into the query string
     * @return a paged set of objects of type V as built from the input query string
     */
    @SuppressWarnings("unchecked")
    public <V> PagedSet<V> getTypedPagedResults(final int from, final int to, final String queryString,
            final String countQueryString, final HashMap<String, Object> parameters)
    {
        PagedSet<V> results = new PagedSet<V>();

        if (!results.isRangeValid(from, to))
        {
            throw new IllegalArgumentException("from/to are invalid");
        }

        Query count = entityManager.createQuery(countQueryString);
        for (String key : parameters.keySet())
        {
            count.setParameter(key, parameters.get(key));
        }

        long total;
        Object totalValue = count.getSingleResult();
        if (totalValue instanceof Long)
        {
            total = (Long) totalValue;
        }
        else
        {
            total = (Integer) totalValue;
        }

        // if no results, return empty set
        if (total == 0)
        {
            return results;
        }

        // return valid range even if you requested out of range
        int validTo = to;
        if (to >= total)
        {
            validTo = (int) total - 1;
        }

        // query to get the actual results
        Query select = entityManager.createQuery(queryString);
        for (String key : parameters.keySet())
        {
            select.setParameter(key, parameters.get(key));
        }

        select.setFirstResult(from);
        select.setMaxResults(validTo - from + 1);

        ArrayList<V> resultList = (ArrayList<V>) select.getResultList();

        results.setFromIndex(from);
        results.setToIndex(validTo);
        results.setTotal((int) total);
        results.setPagedSet(resultList);

        return results;
    }

    /**
     * Get a PagedSet of type V, built from the input query string.
     * 
     * @param <V>
     *            the type of objects to return
     * @param from
     *            the starting index
     * @param to
     *            the ending index
     * @param queryString
     *            the query string
     * @param parameters
     *            the parameters to inject into the query string
     * @return a paged set of objects of type V as built from the input query string
     */
    @SuppressWarnings("unchecked")
    public <V> PagedSet<V> getTypedPagedResults(final int from, final int to, final String queryString,
            final HashMap<String, Object> parameters)
    {
        PagedSet<V> results = new PagedSet<V>();

        log.debug("from: " + from + ", to: " + to);

        if (!results.isRangeValid(from, to))
        {
            throw new IllegalArgumentException("from/to are invalid");
        }

        // TODO construct count query to avoid returning results of an entire
        // query just to get the count
        // like getPagedResults() does.
        // these didn't work:
        // "select count(q) from (select c.ideas from Campaign c where c.id=" +
        // containerId + ") q";
        // "select count(c.ideas) from Campaign c where c.id=id"

        Query count = entityManager.createQuery(queryString);
        for (String key : parameters.keySet())
        {
            count.setParameter(key, parameters.get(key));
        }

        int total = count.getResultList().size();

        // if no results, return empty set
        if (total == 0)
        {
            return results;
        }

        // return valid range even if you requested out of range
        int validTo = to;
        if (to >= total)
        {
            log.debug("to>=total - to: " + to + ", total: " + total);
            validTo = total - 1;
        }
        log.debug("Query: " + queryString + ", total: " + total + ", validTo: " + validTo);

        // query to get the actual results
        Query select = entityManager.createQuery(queryString);
        for (String key : parameters.keySet())
        {
            select.setParameter(key, parameters.get(key));
        }

        select.setFirstResult(from);
        int maxResults = validTo - from + 1;
        if (maxResults <= 0)
        {
            log.debug("Maxresults would have been negative or zero - "
                    + "most likely an error on the client - returning no records");
            return results;
        }
        select.setMaxResults(maxResults);

        ArrayList<V> resultList = (ArrayList<V>) select.getResultList();

        results.setFromIndex(from);
        results.setToIndex(validTo);
        results.setTotal(total);
        results.setPagedSet(resultList);

        return results;
    }

    /**
     * get paged result set of type T.
     * 
     * Subclasses are expected to construct an appropriate parameterized query string.
     * 
     * suppression of type check warnings is because the JPA call doesn't support generics.
     * 
     * @param from
     *            the from index to return, inclusive. This is not an index like the PK index, this is more like an
     *            array index, the array being the set of returned results from the query.
     * @param to
     *            the to index to select to, inclusive. This is not an index like the PK index, this is more like an
     *            array index, the array being the set of returned results from the query.
     * @param queryString
     *            to turn into a query
     * @param parameters
     *            to use for parameterizing the query.
     * @return paged results for a given query, index is inclusive.
     */
    public PagedSet<T> getPagedResults(final int from, final int to, final String queryString,
            final HashMap<String, Object> parameters)
    {
        return getTypedPagedResults(from, to, queryString, parameters);
    }

    /**
     * get paged result set of type T.
     * 
     * Subclasses are expected to construct an appropriate parameterized query string.
     * 
     * suppression of type check warnings is because the JPA call doesn't support generics.
     * 
     * @param from
     *            the from index to return, inclusive. This is not an index like the PK index, this is more like an
     *            array index, the array being the set of returned results from the query.
     * @param to
     *            the to index to select to, inclusive. This is not an index like the PK index, this is more like an
     *            array index, the array being the set of returned results from the query.
     * @param countQueryString
     *            the query string to use to determine the count - mut return an integer, and will be injected with the
     *            parameters
     * @param queryString
     *            to turn into a query
     * @param parameters
     *            to use for parameterizing the query.
     * @return paged results for a given query, index is inclusive.
     */
    public PagedSet<T> getPagedResults(final int from, final int to, final String countQueryString,
            final String queryString, final HashMap<String, Object> parameters)
    {
        return getTypedPagedResults(from, to, countQueryString, queryString, parameters);
    }

    /**
     * Get the FullTextSession for reindexing entities in the search index.
     * 
     * @return the FullTextSession to use for reindexing in the search index
     */
    protected FullTextSession getFullTextSession()
    {
        return Search.getFullTextSession((Session) entityManager.getDelegate());
    }
}
