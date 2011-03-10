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

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.ChangeGroupActivitySubscriptionMapperRequest;

/**
 * Mapper to update a user's notification preference for new activities for a specific group.
 */
public class ChangeGroupActivitySubscriptionDbMapper extends
        BaseArgDomainMapper<ChangeGroupActivitySubscriptionMapperRequest, Boolean>
{
    /**
     * Update the person's new activity notifications for a group that they are a member of.
     * 
     * @param inRequest
     *            the ChangeGroupActivitySubscriptionMapperRequest containing the person id, group id, and notification
     *            setting
     * @return true
     */
    @Override
    public Boolean execute(final ChangeGroupActivitySubscriptionMapperRequest inRequest)
    {
        String q = "UPDATE GroupFollower SET receiveNewActivityNotifications = :receiveNewActivityNotifications "
                + "WHERE pk.followerId = :personId AND pk.followingId = :groupId";
        Query query = getEntityManager().createQuery(q).setParameter("receiveNewActivityNotifications",
                inRequest.getReceiveNewActivityNotifications()).setParameter("personId", inRequest.getPersonId())
                .setParameter("groupId", inRequest.getGroupId());
        query.executeUpdate();

        return new Boolean(true);
    }
}
