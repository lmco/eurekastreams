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
 * Get list of followed group ids for provided person id.
 * 
 */
public class GetFollowedGroupIdsForPersonByIdDbMapper extends BaseArgDomainMapper<Long, List<Long>>
{

    /**
     * Get list of followed group ids for provided person id.
     * 
     * @param inRequest
     *            person id to find followed ids for.
     * @return list of followed group ids for provided person id.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Long> execute(final Long inRequest)
    {
        return getEntityManager().createQuery(
                "select gf.pk.followingId from GroupFollower gf where gf.pk.followerId = :id").setParameter("id",
                inRequest).getResultList();
    }

}
