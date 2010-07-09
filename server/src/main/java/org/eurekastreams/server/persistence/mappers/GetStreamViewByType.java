/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
import org.eurekastreams.server.persistence.mappers.requests.StreamViewTypeRequest;

/**
 * Gets the stream view by type.
 *
 */
public class GetStreamViewByType extends ReadMapper<StreamViewTypeRequest, StreamView>
{

    /**
     * 
     * TODO: Figure out caching problem.
     * @param inRequest the request.
     * @return the list.
     */
    @Override
    public StreamView execute(final StreamViewTypeRequest inRequest)
    {
        Query q = getEntityManager().createQuery("from "
                + "StreamView where type = :type").setParameter("type",
                inRequest.getType());

        return (StreamView) q.getResultList().get(0);
    }
}
