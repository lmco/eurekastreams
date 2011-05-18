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

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Get streams for a user.
 */
public class GetUserStreamsDbMapper extends BaseArgDomainMapper<Long, List<StreamFilter>> implements
        DomainMapper<Long, List<StreamFilter>>
{
    /**
     * Get the streams for a user.
     * 
     * @param inUserEntityId
     *            the user id.
     * @return the streams.
     */
    public List<StreamFilter> execute(final Long inUserEntityId)
    {
        List<Stream> streams = getEntityManager().createQuery(
                "SELECT streams from Person p where p.id = :userId order by name").setParameter("userId",
                inUserEntityId).getResultList();

        List<StreamFilter> filters = new ArrayList<StreamFilter>();

        for (Stream stream : streams)
        {
            filters.add(stream);
        }

        return filters;
    }

}
