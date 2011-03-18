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

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Gets a list of Liked activity ids for a given user.
 */
public class GetLikedActivityIdsByUserIdsDbMapper extends BaseArgDomainMapper<List<Long>, List<List<Long>>>
{
    /**
     * Gets the list of liked activity ids for each user.
     * 
     * @param userIds
     *            the user ids to find likes for.
     * @return the list of liked activity ids for each user.
     */
    @SuppressWarnings("unchecked")
    public List<List<Long>> execute(final List<Long> userIds)
    {
        List<List<Long>> results = new ArrayList<List<Long>>();

        for (long userId : userIds)
        {
            Query q = getEntityManager().createQuery(
                    "SELECT la.pk.activityId FROM LikedActivity la WHERE la.pk.personId = :id "
                            + "ORDER BY la.pk.activityId DESC").setParameter("id", userId);

            results.add(q.getResultList());
        }

        return results;
    }
}