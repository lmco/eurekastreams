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
package org.eurekastreams.server.action.execution;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.cache.DomainGroupCacheLoader;
import org.eurekastreams.server.persistence.mappers.cache.OrganizationHierarchyCacheLoader;
import org.eurekastreams.server.persistence.mappers.cache.PersonCacheLoader;

/**
 * This action initialized/warms the cache by running a series of cache loaders. This action is meant to be run
 * asynchronously at application startup.
 */
public class InitializeCacheExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Local log instance.
     */
    private final Log logger = LogFactory.make();

    /**
     * Loader for domain groups.
     */
    private final DomainGroupCacheLoader domainGroupCacheLoader;

    /**
     * Loader for organizations.
     */
    private final OrganizationHierarchyCacheLoader organizationCacheLoader;

    /**
     * Loader for people.
     */
    private final PersonCacheLoader personCacheLoader;

    /**
     * Constructor.
     *
     * @param inDomainGroupCacheLoader
     *            the group loader.
     * @param inOrganizationCacheLoader
     *            the org loader.
     * @param inPersonCacheLoader
     *            the person loader.
     */
    public InitializeCacheExecution(final DomainGroupCacheLoader inDomainGroupCacheLoader,
            final OrganizationHierarchyCacheLoader inOrganizationCacheLoader,
            final PersonCacheLoader inPersonCacheLoader)
    {
        domainGroupCacheLoader = inDomainGroupCacheLoader;
        organizationCacheLoader = inOrganizationCacheLoader;
        personCacheLoader = inPersonCacheLoader;
    }

    /**
     * {@inheritDoc}.
     *
     * Performs the cache initialization by invoking each of the cache loaders.
     */
    @Override
    public Serializable execute(final ActionContext inActionContext) throws ExecutionException
    {
        Date start = new Date();
        try
        {
            // grabs the cache from one of the loaders and clears all existing data.
            domainGroupCacheLoader.getCache().clear();

            // initializes each loader.
            domainGroupCacheLoader.initialize();
            organizationCacheLoader.initialize();
            personCacheLoader.initialize();
        }
        catch (Exception ex)
        {
            logger.error("Error occurred initializing cache", ex);
        }
        Date end = new Date();

        logger.info("Cache Initialization: elapsed time: "
                + DurationFormatUtils.formatDurationHMS(end.getTime() - start.getTime()));
        return null;
    }

}
