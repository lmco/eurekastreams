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

import org.eurekastreams.server.domain.PersonStream;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Database mapper to get all of the PersonStream entities in order for a person by id.
 */
public class GetOrderedPersonStreamsByPersonIdDbMapper extends BaseArgDomainMapper<Long, List<PersonStream>>
{
    /**
     * Get all PersonStream objects for a person by id, in order.
     * 
     * @param inPersonId
     *            the id of the person to get PersonStreams for
     * @return an ordered list of PersonStreams for the person, ordered by stream index
     */
    @Override
    public List<PersonStream> execute(final Long inPersonId)
    {
        return getEntityManager().createQuery("FROM PersonStream WHERE pk.personId = :personId ORDER BY streamIndex")
                .setParameter("personId", inPersonId).getResultList();
    }

}
