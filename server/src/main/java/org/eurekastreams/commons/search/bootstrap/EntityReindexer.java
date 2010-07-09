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

import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.search.FullTextSession;

/**
 * Reindex domain entities from a list of class types and a SearchIndexManager. This can be created in Spring, along
 * with the SearchIndexManager. Since the SearchIndexManager can have its EntityManager automatically injected by Spring
 * if created by Spring, this works nicely. This doesn't do much, but keeps the logic out of the servlet and makes it
 * more reusable.
 */
public class EntityReindexer
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * EntityManager, injected through setter by user or Spring.
     */
    private EntityManager entityManager;

    /**
     * Default types of Entities to return - can be overridden per search.
     */
    @SuppressWarnings("unchecked")
    private Class[] entitiesToReindex;

    /**
     * SearchIndexManager to use for reindexing.
     */
    private SearchIndexManager searchIndexManager;

    /**
     * Set the domain entities to reindex.
     *
     * @param theEntitiesToReindex
     *            the domain entities to reindex
     */
    @SuppressWarnings("unchecked")
    public void setEntitiesToReindex(final Class[] theEntitiesToReindex)
    {
        log.info("setEntitiesToReindex(" + Arrays.toString(theEntitiesToReindex) + ")");
        entitiesToReindex = theEntitiesToReindex;
    }

    /**
     * Reindex the entities set with the entitiesToReindex setter using the given entityManager.
     */
    @SuppressWarnings("unchecked")
    public void reindex()
    {
        // can't keep the full text session object across connections, so get it from the entity manager and pass it in
        FullTextSession search = getFullTextSession();
        log.info("reindex()");
        for (Class entityClass : entitiesToReindex)
        {
            log.info("Reindexing entity of type " + entityClass.toString());
            searchIndexManager.reindexEntities(entityClass, search);
            log.info("Done reindexing entity of type " + entityClass.toString());
        }
    }

    /**
     * Set the SearchIndexManager.
     *
     * @param theSearchIndexManager
     *            the searchIndexManager to set
     */
    public void setSearchIndexManager(final SearchIndexManager theSearchIndexManager)
    {
        log.info("setSearchIndexManager(theSearchIndexManager)");
        searchIndexManager = theSearchIndexManager;
    }

    /**
     * Set the entity manager, injectable by Spring on object creation.
     *
     * @param theEntityManager
     *            the entity manager.
     */
    @PersistenceContext
    public void setEntityManager(final EntityManager theEntityManager)
    {
        log.info("setEntityManager(theEntityManager)");
        entityManager = theEntityManager;
    }

    /**
     * Get the FullTextSession, converting the entityManager if necessary.
     *
     * Protected to override in a child class just for unit testing - this wraps the unavoidable singleton call to
     * Search.getFullTextSession.
     *
     * It's not safe to keep a FullTextSession around in a singleton object across requests - the connection will be
     * closed.
     *
     * @return the FullTextSession
     */
    protected FullTextSession getFullTextSession()
    {
        return org.hibernate.search.Search.getFullTextSession((Session) entityManager.getDelegate());
    }
}
