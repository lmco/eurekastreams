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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * Change the order of the list of streams.
 */
public class ReorderStreams extends CachedDomainMapper
{
    /**
     * Local logger instance.
     */
    private final Log log = LogFactory.make();

    /**
     * Update the order of streams in the db.
     * 
     * @param userId
     *            The user id to find followed groups for.
     * @param streams
     *            The newly ordered list of streams.
     * @param newHiddenLineIndex the new hidden line index.
     */
    public void execute(final long userId, final List<Stream> streams, final int newHiddenLineIndex)
    {
        // Set in database
        EntityManager mgr = getEntityManager();
        for (int i = 0; i < streams.size(); i++)
        {
            String query = "update PersonStream set streamindex = :streamindex "
                    + "where personId=:personId and streamId=:streamId";

            Query q = mgr.createQuery(query).setParameter("streamindex", i).setParameter("personId", userId)
                    .setParameter("streamId", streams.get(i).getId());

            q.executeUpdate();
        }
        
        // Update hidden line index
        String queryString = "update versioned Person set streamViewHiddenLineIndex = :newIndex where id = :id";
        
        log.debug("New hidden line index: " + newHiddenLineIndex);
        Query q = mgr.createQuery(queryString)
            .setParameter("newIndex", newHiddenLineIndex).setParameter("id", userId);
        q.executeUpdate();
        
        getCache().delete(CacheKeys.PERSON_BY_ID + userId);
    }
}
