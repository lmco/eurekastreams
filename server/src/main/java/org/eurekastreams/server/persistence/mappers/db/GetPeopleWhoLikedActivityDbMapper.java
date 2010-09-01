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
import java.util.Collection;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Get the people who liked an activity.
 * 
 */
public class GetPeopleWhoLikedActivityDbMapper extends
        BaseArgDomainMapper<Collection<Long>, Collection<Collection<Long>>>
{

    /**
     * Execute.
     * 
     * @param inRequest
     *            the request.
     * @return the list of ids.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Collection<Collection<Long>> execute(final Collection<Long> inRequest)
    {
        Collection<Collection<Long>> values = new ArrayList<Collection<Long>>();

        for (Long activityId : inRequest)
        {
            values.add(getEntityManager().createQuery(
                    "SELECT pk.personId FROM LikedActivity WHERE activityId = :activityId").setParameter("activityId",
                    activityId).getResultList());
        }

        return values;

    }
}