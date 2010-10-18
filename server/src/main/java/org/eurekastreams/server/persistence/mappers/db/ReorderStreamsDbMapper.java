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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.List;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.PersonStream;
import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;

/**
 * Change the order of the list of streams.
 */
public class ReorderStreamsDbMapper extends BaseDomainMapper
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
     * @param newHiddenLineIndex
     *            the new hidden line index.
     */
    public void execute(final long userId, final List<PersonStream> streams, final int newHiddenLineIndex)
    {
        // there's a constraint on the indexed column, so temporarily make all indexes available by going negative
        getEntityManager().createQuery(
                "UPDATE PersonStream SET streamIndex = -1 - streamIndex WHERE pk.personId = :personId").setParameter(
                "personId", userId).executeUpdate();

        // set the new order
        for (int streamIndex = 0; streamIndex < streams.size(); streamIndex++)
        {
            PersonStream ps = streams.get(streamIndex);
            log.info("Setting stream index to " + streamIndex + " for stream id #" + ps.getStreamId() + " for person #"
                    + userId);

            // have to update in real time, rather than wait for flush - the constraints make this tough
            getEntityManager().createQuery(
                    "UPDATE PersonStream SET streamIndex = :streamIndex "
                            + "WHERE pk.personId = :personId AND streamId = :streamId").setParameter("streamIndex",
                    streamIndex).setParameter("personId", ps.getPersonId()).setParameter("streamId", ps.getStreamId())
                    .executeUpdate();
        }

        // Update hidden line index
        String queryString = "update versioned Person set streamViewHiddenLineIndex = :newIndex where id = :id";

        log.debug("New hidden line index: " + newHiddenLineIndex);
        Query q = getEntityManager().createQuery(queryString).setParameter("newIndex", newHiddenLineIndex)
                .setParameter("id", userId);
        q.executeUpdate();
    }
}
