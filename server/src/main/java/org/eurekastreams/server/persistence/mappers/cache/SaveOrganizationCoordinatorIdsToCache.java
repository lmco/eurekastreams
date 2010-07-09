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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.profile.OrganizationCacheUpdaterRequest;
import org.eurekastreams.server.persistence.mappers.stream.BaseArgCachedDomainMapper;

/**
 * Save the list of coordinators for an organization in cache.
 */
public class SaveOrganizationCoordinatorIdsToCache extends
        BaseArgCachedDomainMapper<OrganizationCacheUpdaterRequest, Boolean>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Save a set of coordinator person ids for an organization in cache.
     * 
     * @param inRequest
     *            the SaveOrganizationCoordinatorIdsToCacheRequest
     * @return True if successful.
     */
    @Override
    public Boolean execute(final OrganizationCacheUpdaterRequest inRequest)
    {
        Long orgId = inRequest.getOrganizationId();
        Set<Long> coordIds = inRequest.getCoordinatorIds();
        Set<Long> origCoordIds = inRequest.getOriginalCoordinatorIds() == null ? new HashSet<Long>(0) : inRequest
                .getOriginalCoordinatorIds();

        log.info("Saving org #" + orgId + "'s cordinators to cache - coordinator ids: " + coordIds.toString());
        getCache().set(CacheKeys.ORGANIZATION_COORDINATORS_BY_ORG_ID + inRequest.getOrganizationId(),
                inRequest.getCoordinatorIds());

        // update orgs directly coordinated by people cache keys.
        // do additions
        for (Long newId : coordIds)
        {
            if (!origCoordIds.contains(newId))
            {
                getCache().addToSet(CacheKeys.ORG_IDS_DIRECTLY_COORD_BY_PERSON + newId, orgId);
            }
        }

        // do removes
        for (Long origId : origCoordIds)
        {
            if (!coordIds.contains(origId))
            {
                getCache().removeFromSet(CacheKeys.ORG_IDS_DIRECTLY_COORD_BY_PERSON + origId, orgId);
            }
        }

        return true;
    }
}
