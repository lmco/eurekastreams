/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.profile;

import java.io.Serializable;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.profile.OrganizationCacheUpdaterRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.SaveOrganizationCoordinatorIdsToCache;

/**
 * Execution to perform async tasks after an organization has been updated. This currently includes:
 * 
 * 1. updating the activity search string for all people that are coordinators of the organization 2. save updated list
 * of coordinators to cache. 3. Conditionally clear the Recursive Org Children ID cache for every Org up the tree from
 * input org (only done on create).
 */
public class OrganizationCacheUpdaterExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Instance of the logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Mapper to get the private group ids a user has the ability to view activities for through an org/group
     * coordinator role.
     */
    private final DomainMapper<Long, Set<Long>> privateGroupIdsCacheRefreshMapper;

    /**
     * Cache mapper for Organization coordinators.
     */
    private final SaveOrganizationCoordinatorIdsToCache saveOrgCoordinatorIdsToCacheDAO;

    /**
     * Constructor.
     * 
     * @param inPrivateGroupIdsCacheRefreshMapper
     *            mapper to retrieve the private group ids that a user has access to view activities through a org/group
     *            coord role.
     * @param inOrgCoordinatorCacheManager
     *            {@link SaveOrganizationCoordinatorIdsToCache}.
     */
    public OrganizationCacheUpdaterExecution(final DomainMapper<Long, Set<Long>> inPrivateGroupIdsCacheRefreshMapper,
            final SaveOrganizationCoordinatorIdsToCache inOrgCoordinatorCacheManager)
    {
        privateGroupIdsCacheRefreshMapper = inPrivateGroupIdsCacheRefreshMapper;
        saveOrgCoordinatorIdsToCacheDAO = inOrgCoordinatorCacheManager;
    }

    /**
     * Perform the action, updating the coordinator cache list for a Domain Group and rebuilding the security-scoped
     * activity search strings for all coordinators of a Domain Group.
     * 
     * @param inActionContext
     *            the action context with the org id to update cache for
     * @return null
     */
    @Override
    public Serializable execute(final ActionContext inActionContext)
    {
        log.info("Performing action");

        OrganizationCacheUpdaterRequest request = (OrganizationCacheUpdaterRequest) inActionContext.getParams();

        Set<Long> coordinatorIds = request.getCoordinatorIds();

        for (Long coordinatorPersonId : coordinatorIds)
        {
            // Rebuild (force reload) the cache list of the private group ids that the current user
            // has the ability to view activities for through org/group coordinator access.
            privateGroupIdsCacheRefreshMapper.execute(coordinatorPersonId);
        }

        saveOrgCoordinatorIdsToCacheDAO.execute(request);

        log.info("Action complete");
        return null;
    }
}
