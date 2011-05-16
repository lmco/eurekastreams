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
package org.eurekastreams.server.persistence.mappers.db;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.ReadMapper;

/**
 * Get a list of all private group ids under a collection of organizations.
 */
public class GetPrivateGroupIds extends ReadMapper<Serializable, Set<Long>>
{
    /**
     * Get all private group ids.
     * 
     * @param ignored
     *            ignored
     * @return a set of group ids
     */
    @Override
    @SuppressWarnings("unchecked")
    public Set<Long> execute(final Serializable ignored)
    {
        HashSet<Long> groupIds = new HashSet<Long>();
        Query q = getEntityManager().createQuery("SELECT id FROM DomainGroup WHERE publicGroup = false");
        groupIds.addAll(q.getResultList());
        return groupIds;
    }

}
