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
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.EntityCacheUpdater;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * EntityCacheUpdater for DomainGroups.
 * 
 */
public class DomainGroupEntityCacheUpdater extends CachedDomainMapper implements EntityCacheUpdater<DomainGroup>
{
    /**
     * Logger instance.
     */
    private Log log = LogFactory.make();

    /**
     * Domain Group updater implementation - fired when an existing domain group entity is updated.
     * 
     * @param inUpdatedDomainGroup
     *            the domain group just updated
     */
    @Override
    public void onPostUpdate(final DomainGroup inUpdatedDomainGroup)
    {
        if (log.isInfoEnabled())
        {
            log.info("DomainGroup.onPostUpdate - removing group #" + inUpdatedDomainGroup.getId() + " from cache");
        }
        getCache().delete(CacheKeys.GROUP_BY_ID + inUpdatedDomainGroup.getId());
    }

    /**
     * Domain Group persist implementation - fired when a new domain group entity is persisted.
     * 
     * @param inDomainGroup
     *            the domainGroup just persisted
     */
    @Override
    public void onPostPersist(final DomainGroup inDomainGroup)
    {
        if (log.isInfoEnabled())
        {
            log.info("DomainGroup.onPostPersist - group with shortName " + inDomainGroup.getShortName()
                    + " - doing nothing.");
        }
        // no-op - cache will be loaded when someone requests this domain group
    }

}
