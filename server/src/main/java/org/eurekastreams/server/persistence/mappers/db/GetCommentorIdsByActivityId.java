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

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;

/**
 * This mapper retrieves a unique list of person ids representing all commentors for a given activity.
 */
public class GetCommentorIdsByActivityId extends BaseDomainMapper
{
    /**
     * Gets the list of commentor ids for the input activity id.
     * 
     * @param activityId
     *            The activity id to find commentors for.
     * @return the unique list of person ids that commented on this activity.
     */
    @SuppressWarnings("unchecked")
    public List<Long> execute(final long activityId)
    {
        Query q2 = getEntityManager().createQuery("select distinct author.id from Comment where target.id = :id")
                .setParameter("id", activityId);
        return q2.getResultList();
    }
}
