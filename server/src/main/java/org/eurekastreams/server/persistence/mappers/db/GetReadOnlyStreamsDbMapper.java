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

import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.persistence.mappers.BaseNoArgDomainMapper;

/**
 * Database mapper to get all of the readonly Streams.
 */
public class GetReadOnlyStreamsDbMapper extends BaseNoArgDomainMapper<List<Stream>>
{
    /**
     * Get all of the readonly Streams.
     * @return a list of all readonly streams
     */
    @Override
    public List<Stream> execute()
    {
        return getEntityManager().createQuery("FROM Stream WHERE readonly=:readonly").setParameter("readonly", true)
                .getResultList();
    }
}
