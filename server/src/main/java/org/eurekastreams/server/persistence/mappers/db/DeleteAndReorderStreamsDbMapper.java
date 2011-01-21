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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.PersonStream;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.DeleteAndReorderStreamsRequest;

/**
 * Mapper to delete a person's person_stream entry and collapse index of remaining entries.
 * 
 */
public class DeleteAndReorderStreamsDbMapper extends BaseArgDomainMapper<DeleteAndReorderStreamsRequest, Boolean>
{
    /**
     * Local logger instance.
     */
    private final Log log = LogFactory.make();

    @SuppressWarnings("unchecked")
    @Override
    public Boolean execute(final DeleteAndReorderStreamsRequest inRequest)
    {
        Long userId = inRequest.getPersonId();
        Long streamId = inRequest.getStreamId();

        // Delete Entry
        getEntityManager().createQuery(
                "DELETE FROM PersonStream WHERE pk.personId = :personId AND pk.streamId = :streamId").setParameter(
                "personId", userId).setParameter("streamId", streamId).executeUpdate();

        // Get remaining person streams for user
        List<PersonStream> streams = getEntityManager().createQuery(
                "FROM PersonStream WHERE pk.personId = :personId ORDER BY streamIndex")
                .setParameter("personId", userId).getResultList();

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

        return true;
    }

}
