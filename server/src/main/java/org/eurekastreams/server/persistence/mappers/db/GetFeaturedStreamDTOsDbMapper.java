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

import java.util.List;

import org.eurekastreams.server.domain.dto.FeaturedStreamDTO;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;

/**
 * DB mapper for getting {@link FeaturedStreamDTO}s. NOTE these are not full {@link FeaturedStreamDTO}s, just the
 * cachable version excluding per-request info such as following status and dynamic info like avatar id.
 *
 */
public class GetFeaturedStreamDTOsDbMapper extends BaseArgDomainMapper<MapperRequest, List<FeaturedStreamDTO>>
{

    /**
     * Return list of {@link FeaturedStreamDTO}s.
     *
     * @param inRequest
     *            ignored.
     * @return {@link FeaturedStreamDTO}s from DB, ordered by date desc.
     */
    @Override
    public List<FeaturedStreamDTO> execute(final MapperRequest inRequest)
    {
        String q = "SELECT NEW org.eurekastreams.server.domain.dto.FeaturedStreamDTO "
                + "(id, description, streamScope.id, streamScope.scopeType, "
                + "streamScope.uniqueKey, streamScope.destinationEntityId) "
                + "FROM FeaturedStream ORDER BY created DESC";

        return getEntityManager().createQuery(q).getResultList();
    }

}
