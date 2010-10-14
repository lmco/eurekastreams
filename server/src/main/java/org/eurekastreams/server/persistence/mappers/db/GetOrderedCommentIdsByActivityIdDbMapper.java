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

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Db Mapper to return a list of comment ids for a given activity id.
 * 
 */
public class GetOrderedCommentIdsByActivityIdDbMapper extends BaseArgDomainMapper<Long, List<Long>>
{

    /**
     * Returns a list of comment ids for a given activity id.
     * 
     * @param inActivityId
     *            The activity id.
     * @return A list of comment ids for a given activity id.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Long> execute(final Long inActivityId)
    {
        return getEntityManager().createQuery(
                "SELECT c.id FROM Comment c WHERE c.target.id = :activityId " + "ORDER BY c.id ASC").setParameter(
                "activityId", inActivityId).getResultList();
    }
}
