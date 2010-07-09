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

import javax.persistence.Query;

import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.requests.FindUserStreamFilterByIdRequest;

/**
 * DAO for finding a user's {@link StreamView} by id.
 *
 */
public class FindUserStreamViewById extends ReadMapper<FindUserStreamFilterByIdRequest, StreamView>
{

    /**
     * Find a user's {@link StreamView} by id.
     * @param inRequest the request params.
     * @return The {@link StreamView}.
     */
    @Override
    public StreamView execute(final FindUserStreamFilterByIdRequest inRequest)
    {
        Query q = getEntityManager().createQuery(
                "SELECT sv FROM Person p "
                + "JOIN p.streamViews sv "
                + "WHERE sv.id = :streamViewId AND p.id = :personId");

        q.setParameter("streamViewId", inRequest.getStreamFilterId());
        q.setParameter("personId", inRequest.getPersonId());

        return (StreamView) q.getSingleResult();
    }

}
