/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.transform;

import org.eurekastreams.server.domain.Identifiable;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * DAO decorator which returns the unique ID for an object given the ID.
 */
public class UniqueIdFromIdMapper implements DomainMapper<Long, String>
{
    /** The DAO to use to get the object in question. */
    private final DomainMapper<Long, Identifiable> dao;

    /**
     * Constructor.
     *
     * @param inDao
     *            The DAO to use to get the object in question.
     */
    public UniqueIdFromIdMapper(final DomainMapper<Long, Identifiable> inDao)
    {
        dao = inDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String execute(final Long inRequest)
    {
        Identifiable item = dao.execute(inRequest);
        return (item != null) ? item.getUniqueId() : null;
    }
}
