/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.List;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Clear the cache of private group ids visible (via org or group coord status) by people that are coordinators of a
 * group.
 */
public class ClearPrivateGroupIdsViewableByCoordinatorCacheOnGroupUpdate extends CachedDomainMapper implements
        DomainMapper<Long, Void>
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.getLog(ClearPrivateGroupIdsViewableByCoordinatorCacheOnGroupUpdate.class);

    /**
     * Clear the activity stream search string for user cache for all coordinators and followers of a domain group.
     * 
     * @param inDomainGroupId
     *            the id of the domain group that's being updated
     * @return Nothing.
     */
    @SuppressWarnings("unchecked")
    public Void execute(final Long inDomainGroupId)
    {
        String queryTemplate = "SELECT p.id FROM Person p, DomainGroup g "
                + "WHERE g.id = :groupId AND p MEMBER OF g.coordinators";

        Query query = getEntityManager().createQuery(queryTemplate);
        query.setParameter("groupId", inDomainGroupId);

        List<Long> peopleIds = query.getResultList();

        for (Long id : peopleIds)
        {
            log.info("Clearing the cached security-scoped activity search string for user with person id: " + id);

            getCache().delete(CacheKeys.PRIVATE_GROUP_IDS_VIEWABLE_BY_PERSON_AS_COORDINATOR + id);
        }
        return null;
    }
}
