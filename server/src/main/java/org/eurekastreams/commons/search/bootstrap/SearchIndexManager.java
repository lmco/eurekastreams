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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.search.FullTextSession;

/**
 * Index everything in the database into Hibernate-Search.
 *
 * Note: This might move into src/test eventually.
 */
public class SearchIndexManager
{
    /**
     * Batch size.
     */
    private Integer flushBatchSize;

    /**
     * Fetch size.
     */
    private Integer fetchBatchSize;

    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Constructor.
     *
     * @param inFetchBatchSize
     *            the number of records to fetch at a time
     * @param inFlushBatchSize
     *            the number of records to flush to the index at a time
     */
    public SearchIndexManager(final Integer inFetchBatchSize, final Integer inFlushBatchSize)
    {
        fetchBatchSize = inFetchBatchSize;
        flushBatchSize = inFlushBatchSize;
    }

    /**
     * Purge & index all entities with the input type, assuming the entity name is the same as the class name.
     *
     * @param entityClass
     *            the class of the entity to reindex into the search index
     * @param search
     *            the FullTextSession to use
     */
    @SuppressWarnings("unchecked")
    public void reindexEntities(final Class entityClass, final FullTextSession search)
    {
        log.info("reindexEntities(" + entityClass.toString() + ")");
        String entityName = entityClass.toString().substring(entityClass.toString().lastIndexOf('.') + 1);
        reindexEntities(entityClass, entityName, search);
    }

    /**
     * Purge & index all entities with the input class and name.
     *
     * @param entityClass
     *            the type of entities to reindex into search index.
     *
     * @param entityName
     *            the name of the entity to reindex
     *
     * @param search
     *            the FullTextSession to use
     */
    @SuppressWarnings("unchecked")
    public void reindexEntities(final Class entityClass, final String entityName, final FullTextSession search)
    {
        log.info("reindexEntities(" + entityClass.toString() + ", " + entityName + ")");

        // purge first
        purgeSearchIndex(entityClass, search);

        log.info("Creating query to find batches of " + entityName);
        Query q = search.createQuery("FROM " + entityName)
        // set the result transformer
                .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
                // minimize cache
                .setCacheMode(CacheMode.IGNORE)
                // limit fetch size
                .setFetchSize(fetchBatchSize);
        log.info("setting scroll mode to FORWARD_ONLY for " + entityName);
        ScrollableResults scroll = q.scroll(ScrollMode.FORWARD_ONLY);

        int batch = 0;
        while (scroll.next())
        {
            batch++;
            search.index(scroll.get(0));
            if (batch % flushBatchSize == 0)
            {
                if (log.isInfoEnabled())
                {
                    log.info("Flushing " + entityName + " - " + batch);
                }

                // no need to call s.flush()
                // we don't change anything
                search.flushToIndexes();
                search.clear();
            }
        }

        log.info("Flushing " + entityName + " - " + batch + " (final)");
        search.flushToIndexes();
        search.clear();

        log.info("Optimizing index for " + entityName);
        search.getSearchFactory().optimize(entityClass);
    }

    /**
     * purge the search index of all the entities of the input type.
     *
     * @param entityClass
     *            the type of entities to purge
     * @param search
     *            the FullTextSession to use
     */
    @SuppressWarnings("unchecked")
    public void purgeSearchIndex(final Class entityClass, final FullTextSession search)
    {
        log.info("purgeSearchIndex(" + entityClass.toString() + ")");

        String entityName = entityClass.toString().substring(entityClass.toString().lastIndexOf('.') + 1);

        log.info("Purging indexed data for " + entityName);
        search.purgeAll(entityClass);
        search.flushToIndexes();

        log.info("Optimizing index for " + entityName);
        search.getSearchFactory().optimize(entityClass);
        search.flushToIndexes();
    }
}
