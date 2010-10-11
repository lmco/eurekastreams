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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.EntityCacheUpdater;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * EntityCacheUpdater for Organizations.
 * 
 */
public class OrganizationEntityCacheUpdater extends CachedDomainMapper implements EntityCacheUpdater<Organization>
{
    /**
     * Logger instance.
     */
    private Log log = LogFactory.make();

    /**
     * Organization updater implementation - fired when an organization entity is updated. Nothing that we currently
     * store in cache can change, so this method does nothing right now.
     * 
     * @param inUpdatedOrganization
     *            the organization just updated
     */
    @Override
    public void onPostUpdate(final Organization inUpdatedOrganization)
    {
        // clear the cache
        log.info("Removing org #" + inUpdatedOrganization.getId() + " from cache onUpdate.");
        getCache().delete(CacheKeys.ORGANIZATION_BY_ID + inUpdatedOrganization.getId());
        log.info("Removing leader ids for org #" + inUpdatedOrganization.getId() + " from cache onUpdate.");
        getCache().delete(CacheKeys.ORGANIZATION_LEADERS_BY_ORG_ID + inUpdatedOrganization.getId());

    }

    /**
     * Organization persist implementation - fired when an organization entity is persisted.
     * 
     * @param inNewOrganization
     *            the organization just created
     */
    @Override
    public void onPostPersist(final Organization inNewOrganization)
    {
        if (log.isInfoEnabled())
        {
            log.info("Adding new organization to cache - Organization: " + inNewOrganization.toString());
        }
        long orgId = inNewOrganization.getId();
        if (orgId <= 0)
        {
            throw new RuntimeException("Can't add this organization to cache until "
                    + "it's been inserted in the database.");
        }

        long parentOrgId = inNewOrganization.getParentOrganization().getId();

        log.info("Cleaning Org Tree from cache onPostPersist.");
        getCache().delete(CacheKeys.ORGANIZATION_TREE_DTO);

        log.info("Deleting direct children org id cache of parent org to " + orgId + ": " + parentOrgId);
    }

}
