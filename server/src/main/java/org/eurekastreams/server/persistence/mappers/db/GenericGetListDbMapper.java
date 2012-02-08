/*
 * Copyright (c) 2011-2012 Lockheed Martin Corporation
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

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Get a list via an ID.
 */
public class GenericGetListDbMapper extends BaseArgDomainMapper<Serializable, List<Serializable>>
{
    /** Query. */
    private final String query;

    /**
     * Constructor.
     *
     * @param inQuery
     *            The query.
     */
    public GenericGetListDbMapper(final String inQuery)
    {
        query = inQuery;
    }

    /**
     * Get list of items matching the given ID.
     *
     * @param id
     *            The lookup id.
     * @return List of results.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Serializable> execute(final Serializable id)
    {
        return getEntityManager().createQuery(query).setParameter("id", id).getResultList();
    }
}
