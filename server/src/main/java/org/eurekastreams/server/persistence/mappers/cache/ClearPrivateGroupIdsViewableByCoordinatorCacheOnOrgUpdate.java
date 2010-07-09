/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.db.GetOrgCoordinatorIds;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Clear the cache of private group ids visible (via org or group coord status)
 * by people that are coordinators of an organization.
 */
public class ClearPrivateGroupIdsViewableByCoordinatorCacheOnOrgUpdate
        extends CachedDomainMapper
{
    /**
     * Logger.
     */
    private Log log = LogFactory
            .getLog(ClearPrivateGroupIdsViewableByCoordinatorCacheOnOrgUpdate.class);

    /**
     * Mapper to get organization coordinator ids straight from the database.
     */
    private GetOrgCoordinatorIds getOrgCoordinatorIdsFromDbMapper;

    /**
     * Constructor.
     * 
     * @param inGetOrgCoordinatorIdsFromDbMapper
     *            the db mapper to get the org coordinator ids for an org
     */
    public ClearPrivateGroupIdsViewableByCoordinatorCacheOnOrgUpdate(
            final GetOrgCoordinatorIds inGetOrgCoordinatorIdsFromDbMapper)
    {
        getOrgCoordinatorIdsFromDbMapper = inGetOrgCoordinatorIdsFromDbMapper;
    }

    /**
     * Clear the activity stream search string for user cache for all
     * coordinators of an organization.
     * 
     * @param inOrganizationId
     *            the id of the organization that's being updated
     */
    public void execute(final Long inOrganizationId)
    {
        log
                .info("Clearing the cached security-scoped activity search strings for coordinators of organization #"
                        + inOrganizationId);

        Set<Long> coordinatorPeopleIds = getOrgCoordinatorIdsFromDbMapper
                .execute(inOrganizationId);

        for (Long id : coordinatorPeopleIds)
        {
            log
                    .info("Clearing the cached security-scoped activity search string for user with person id: "
                            + id);
            getCache()
                    .delete(CacheKeys.PRIVATE_GROUP_IDS_VIEWABLE_BY_PERSON_AS_COORDINATOR + id);
        }
    }
}
