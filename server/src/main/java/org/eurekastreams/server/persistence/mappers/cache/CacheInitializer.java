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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.task.TaskHandler;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.EntityCacheUpdater;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;

/**
 * Cache initializer - loads up hibernate entity updaters and kick off async job to warm/initialize cache.
 */
public class CacheInitializer
{
    /**
     * Logger.
     */
    private Log logger = LogFactory.make();

    /**
     * Wires entity cache updaters to their domain entities.
     * 
     * @param inDomainGroupEntityCacheUpdater
     *            the domain group EntityCacheUpdater
     * @param inOrgEntityCacheUpdater
     *            the organization EntityCacheUpdater
     * @param inPersonEntityCacheUpdater
     *            the person EntityCacheUpdater
     */
    @SuppressWarnings("unchecked")
    public void wireCacheUpdaters(final EntityCacheUpdater inDomainGroupEntityCacheUpdater,
            final EntityCacheUpdater inOrgEntityCacheUpdater, final EntityCacheUpdater inPersonEntityCacheUpdater)
    {
        // set the updaters
        if (logger.isInfoEnabled())
        {
            logger.info("Wiring up cache updaters for Person, DomainGroup, Organization.");
        }

        DomainGroup.setEntityCacheUpdater(inDomainGroupEntityCacheUpdater);
        Organization.setEntityCacheUpdater(inOrgEntityCacheUpdater);
        Person.setEntityCacheUpdater(inPersonEntityCacheUpdater);
    }

    /**
     * Submits async job to warm cache.
     * 
     * @param inActionSubmitter
     *            the async action submitter
     */
    public void initializeCache(final TaskHandler inActionSubmitter)
    {
        if (logger.isInfoEnabled())
        {
            logger.info("initializeCache.");
        }

        // put the cache warming action on the queue.
        try
        {
            inActionSubmitter.handleTask(new UserActionRequest("initializeCache", null, null));
        }
        catch (Exception ex)
        {
            logger.error("Error occurred initializing cache", ex);
        }
    }
}
