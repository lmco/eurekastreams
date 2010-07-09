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
package org.eurekastreams.server.persistence.mappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.search.QueryParserBuilder;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.persistence.mappers.requests.LuceneSearchRequest;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;

/**
 * Lucene search mapper.
 * 
 * @param <T>
 *            mapper type.
 */
public class LuceneSearchMapper<T> extends ReadMapper<LuceneSearchRequest, PagedSet<T>>
{
    /**
     * Log.
     */
    Log log = LogFactory.make();

    /**
     * The query builder.
     */
    private QueryParserBuilder queryBuilder;

    /**
     * Full text entity manager.
     */
    private FullTextEntityManager fullTextEntityManager;

    /**
     * @param inFullTextEntityManager
     *            the fullTextEntityManager to set.
     */
    public final void setFullTextEntityManager(final FullTextEntityManager inFullTextEntityManager)
    {
        this.fullTextEntityManager = inFullTextEntityManager;
    }

    /**
     * @return the queryBuilder.
     */
    public final QueryParserBuilder getQueryBuilder()
    {
        return queryBuilder;
    }

    /**
     * @param inQueryBuilder
     *            the queryBuilder to set.
     */
    public final void setQueryBuilder(final QueryParserBuilder inQueryBuilder)
    {
        this.queryBuilder = inQueryBuilder;
    }

    /**
     * Execute the mapper.
     * 
     * @param inRequest
     *            the request.
     * @return the items.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PagedSet<T> execute(final LuceneSearchRequest inRequest)
    {
        /**
         * Convoluted like this to make testing possible.
         */
        FullTextEntityManager sessionEntityManager = fullTextEntityManager;

        if (sessionEntityManager == null)
        {
            sessionEntityManager = Search.getFullTextEntityManager(getEntityManager());
        }

        QueryParser parser = queryBuilder.buildQueryParser();

        List<T> results = null;

        PagedSet<T> pagedSet = null;

        try
        {
            StringBuilder query = new StringBuilder();

            // TODO Escape!!
            for (Entry<String, Float> entry : inRequest.getFields().entrySet())
            {
                query.append(entry.getKey());
                query.append(":");
                query.append("(%1$s)^");
                query.append(Float.toString(entry.getValue()));
                query.append(" ");
            }

            String luceneQueryString = String.format(query.toString(), inRequest.getSearchString());
            org.apache.lucene.search.Query luceneQuery = parser.parse(luceneQueryString);

            FullTextQuery fullTextQuery = sessionEntityManager.createFullTextQuery(luceneQuery, inRequest
                    .getObjectType());

            fullTextQuery.setFirstResult(inRequest.getFirstResult());
            fullTextQuery.setMaxResults(inRequest.getMaxResults());

            SortField[] fields = new SortField[inRequest.getSortFields().size()];

            for (int i = 0; i < inRequest.getSortFields().size(); i++)
            {
                fields[i] = new SortField(inRequest.getSortFields().get(i), true);
            }

            Sort sort = new Sort(fields);

            fullTextQuery.setSort(sort);

            results = fullTextQuery.getResultList();

            /**
             * GWT can't serialize EMPTY_LIST.
             */
            if (results.equals(Collections.EMPTY_LIST))
            {
                results = new ArrayList<T>();
            }

            pagedSet = new PagedSet<T>(inRequest.getFirstResult(), inRequest.getFirstResult() + results.size() - 1,
                    fullTextQuery.getResultSize(), results);
        }
        catch (ParseException e)
        {
            log.error(e);
        }

        return pagedSet;
    }

}
