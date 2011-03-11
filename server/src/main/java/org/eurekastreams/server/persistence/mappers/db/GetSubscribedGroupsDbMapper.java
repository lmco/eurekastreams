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

import java.util.ArrayList;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Get a list of the short names of groups subscribed to by a person.
 */
public class GetSubscribedGroupsDbMapper extends BaseArgDomainMapper<Long, ArrayList<String>>
{
    /**
     * Get a list of the short names of groups subscribed to by a person.
     * 
     * @param inPersonId
     *            the person id to check notifications for
     * @return the short names of all groups subscribed to by the input person
     */
    @Override
    public ArrayList<String> execute(final Long inPersonId)
    {
        String q = "SELECT shortName FROM DomainGroup WHERE id IN "
                + "(SELECT pk.followingId FROM GroupFollower WHERE pk.followerId = :personId "
                + "AND receiveNewActivityNotifications = :isSubscribed)";
        Query query = getEntityManager().createQuery(q).setParameter("personId", inPersonId).setParameter(
                "isSubscribed", true);
        ArrayList<String> results = new ArrayList<String>(query.getResultList());
        return results;
    }
}
