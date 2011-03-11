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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.List;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Get person ids that are followers of a given group and have group activity notification enabled.
 * 
 */
public class GetFollowerPersonIdsForGroupByIdWithActivityNotificationsDbMapper extends
        BaseArgDomainMapper<Long, List<Long>>
{

    /**
     * Get person ids that are followers of a given group and have group activity notification enabled.
     * 
     * @param inRequest
     *            the group id.
     * @return person ids that are followers of a given group.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Long> execute(final Long inRequest)
    {
        return getEntityManager().createQuery(
                "SELECT gf.pk.followerId FROM GroupFollower gf WHERE gf.pk.followingId = :id"
                        + " AND gf.receiveNewActivityNotifications = :boolean").setParameter("id", inRequest)
                .setParameter("boolean", true).getResultList();
    }

}
