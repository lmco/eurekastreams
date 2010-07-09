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

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;

/**
 * This mapper finds all activities that have passed a given expiration date. Also, any activity that has been "starred"
 * is not included in the returned list.
 */
@SuppressWarnings("unchecked")
public class GetExpiredActivities extends BaseDomainMapper
{
    /**
     * Executes database queries to find expired activities that are not starred.
     *
     * @param expirationDate
     *            the date an activity is considered to expire.
     * @return the list of expired activity ids.
     */
    public List<Long> execute(final Date expirationDate)
    {
        // Gets all expired activities
        Query q = getEntityManager()
                .createQuery("select id from Activity where postedTime < :expirationDate "
                        + "and id not in (select pk.activityId from StarredActivity)")
                .setParameter("expirationDate", expirationDate);
        return q.getResultList();
    }
}
