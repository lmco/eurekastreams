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

import org.eurekastreams.server.persistence.mappers.ReadMapper;

/**
 * DB Mapper to fetch the IDs of all activities where the input person authored
 * either the first or last comment.
 */
public class GetActivityIdsWithFirstOrLastCommentsAuthoredByPersonId extends
        ReadMapper<Long, List<Long>>
{
    /**
     * Fetch the IDs of all activities where the input person authored either
     * the first or last comment.
     * 
     * @param inPersonId
     *            the id of the person to fetch activity ids for
     * @return a list of IDs of all activities where the input person authored
     *         either the first or last comment.
     */
    @SuppressWarnings("unchecked")
    public List<Long> execute(final Long inPersonId)
    {
        String query = "SELECT DISTINCT target.id FROM Comment WHERE author.id=:authorId "
                + "AND (id IN (SELECT MIN(id) FROM Comment Group BY target.id) "
                + "OR id IN (SELECT MAX(id) FROM Comment Group BY target.id))";
        return getEntityManager().createQuery(query).setParameter("authorId",
                inPersonId).getResultList();
    }
}
