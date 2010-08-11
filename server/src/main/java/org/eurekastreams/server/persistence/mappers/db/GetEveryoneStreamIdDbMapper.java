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

import javax.persistence.Query;

import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Get the everyone stream view id.
 *
 */
public class GetEveryoneStreamIdDbMapper extends BaseDomainMapper implements DomainMapper<Object, Long>
{

    /**
     * Get the everyone stream view id.
     *
     * @param inRequest
     *            nothing.
     * @return the stream view id.
     */
    @Override
    public Long execute(final Object inRequest)
    {
        String everyoneQueryString = "SELECT id from StreamView where type = :type";
        Query everyoneQuery = getEntityManager().createQuery(everyoneQueryString).setParameter("type",
                StreamView.Type.EVERYONE);
        return (Long) everyoneQuery.getSingleResult();
    }

}
