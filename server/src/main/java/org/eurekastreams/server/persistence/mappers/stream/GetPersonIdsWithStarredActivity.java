/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.List;

import org.eurekastreams.server.persistence.mappers.ReadMapper;

/**
 * Returns ids for all users that have starred a given activity id. This 
 * info is NOT cached.
 */
public class GetPersonIdsWithStarredActivity extends ReadMapper<Long, List<Long>>
{
    /**
     * Returns ids for all users that have starred a given activity id.
     * @param inActivityId the activity id.
     * @return list of user ids.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Long> execute(final Long inActivityId)
    {
        return getEntityManager().createQuery(
                "SELECT sa.pk.personId from StarredActivity sa WHERE sa.pk.activityId = :activityId")
                .setParameter("activityId", inActivityId)
                .getResultList();
    }
}
