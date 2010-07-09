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
package org.eurekastreams.server.action.execution;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.search.bootstrap.EntityReindexer;

/**
 * This class reindexes the database into the Lucene search index.
 */
public class ReindexEntitiesExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Only allow a single reindex at a time with this flag.
     */
    private static Boolean indexInProgress = false;

    /**
     * EntityReindexer to use for indexing.
     */
    private EntityReindexer reindexer;

    /**
     * Constructor.
     * @param inReindexer - instance of the reindexer used to update the lucene index.
     */
    public ReindexEntitiesExecution(final EntityReindexer inReindexer)
    {
        reindexer = inReindexer;
    }

    @Override
    public Serializable execute(final ActionContext inActionContext) throws ExecutionException
    {
        if (indexInProgress)
        {
            // another thread is indexing, leave now
            log.error("User attempted to kick off a reindexing of the search engine while it's already in progress.");
            throw new ExecutionException("Cannot perform a reindexing right now - it's already in progress.");
        }

        // the indexing is not in progress - get a lock then start indexing
        synchronized (indexInProgress)
        {
            // now that we have the lock - make sure it wasn't kicked off by
            // another thread while we waited
            if (indexInProgress)
            {
                log.error("User attempted to kick off a reindexing of the search engine while it's "
                        + "already in progress.  The other thread started while this one waited for the lock");
                throw new ExecutionException("Cannot perform a reindexing right now - "
                        + "it's already in progress, kicked off while we waited for the lock.");
            }
            indexInProgress = true;
        }

        try
        {
            log.info("Search index reindex performed");
            reindexer.reindex();
            return "true";
        }
        finally
        {
            log.info("Indexing stopping...");
            indexInProgress = false;
        }
    }

}
