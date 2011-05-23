/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.DeleteStreamRequest;

/**
 * Mapper to delete a person's person_stream entry and collapse index of remaining entries.
 * 
 */
public class DeleteStreamDbMapper extends BaseArgDomainMapper<DeleteStreamRequest, Boolean>
{
    /**
     * Delete the stream id for the user.
     * 
     * @param inRequest
     *            the request containing the person id and stream id
     * @return true
     */
    @Override
    public Boolean execute(final DeleteStreamRequest inRequest)
    {
        Long userId = inRequest.getPersonId();
        Long streamId = inRequest.getStreamId();

        // Delete Entry
        getEntityManager().createQuery(
                "DELETE FROM PersonStream WHERE pk.personId = :personId AND pk.streamId = :streamId").setParameter(
                "personId", userId).setParameter("streamId", streamId).executeUpdate();

        return true;
    }
}
