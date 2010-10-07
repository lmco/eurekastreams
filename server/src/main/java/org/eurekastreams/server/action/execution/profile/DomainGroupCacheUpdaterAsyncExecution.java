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
package org.eurekastreams.server.action.execution.profile;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.profile.DomainGroupCacheUpdaterRequest;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.mappers.cache.AddPrivateGroupIdToCachedCoordinatorAccessList;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.SaveDomainGroupCoordinatorsListToCache;
import org.eurekastreams.server.persistence.mappers.db.GetActivityIdsAuthoredByOrOriginallyAuthoredByEntity;

/**
 * Action to rebuild any cache related to an update of a Domain Group, designed for async processing.
 */
public class DomainGroupCacheUpdaterAsyncExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Instance of the logger.
     */
    private Log log = LogFactory.getLog(DomainGroupCacheUpdaterAsyncExecution.class);

    /**
     * Mapper to save coordinators to cache.
     */
    private final SaveDomainGroupCoordinatorsListToCache groupCoordinatorCacheManager;

    /**
     * Domain Group Mapper.
     */
    private final DomainGroupMapper domainGroupMapper;

    /**
     * Mapper to add a private group id to the list of private group ids a user has access to view activities for
     * through a group/org coordinator role.
     */
    private final AddPrivateGroupIdToCachedCoordinatorAccessList addPrivateGroupIdToCachedListMapper;

    /**
     * DB Mapper to get the activity ids authored by the current user.
     */
    private GetActivityIdsAuthoredByOrOriginallyAuthoredByEntity getActivityIdsAuthordedByEntityDbMapper;

    /**
     * Local cache client instance.
     */
    private final Cache cache;

    /**
     * Constructor.
     * 
     * @param inGetActivityIdsAuthordedByEntityDbMapper
     *            DB mapper to get all activity ids authored by a group
     * @param inGroupCoordinatorCacheManager
     *            {@link SaveDomainGroupCoordinatorsListToCache} mapper to save coordinators to cache.
     * @param inDomainGroupMapper
     *            {@link DomainGroupMapper}.
     * @param inAddPrivateGroupIdToCachedListMapper
     *            {@link AddPrivateGroupIdToCachedCoordinatorAccessList} mapper to add the target private group id to
     *            the list of group ids for each of the coordinators for the target group.
     * @param inCache
     *            - local cache instance.
     */
    public DomainGroupCacheUpdaterAsyncExecution(
            final GetActivityIdsAuthoredByOrOriginallyAuthoredByEntity inGetActivityIdsAuthordedByEntityDbMapper,
            final SaveDomainGroupCoordinatorsListToCache inGroupCoordinatorCacheManager,
            final DomainGroupMapper inDomainGroupMapper,
            final AddPrivateGroupIdToCachedCoordinatorAccessList inAddPrivateGroupIdToCachedListMapper,
            final Cache inCache)
    {
        getActivityIdsAuthordedByEntityDbMapper = inGetActivityIdsAuthordedByEntityDbMapper;
        groupCoordinatorCacheManager = inGroupCoordinatorCacheManager;
        domainGroupMapper = inDomainGroupMapper;
        addPrivateGroupIdToCachedListMapper = inAddPrivateGroupIdToCachedListMapper;
        cache = inCache;
    }

    /**
     * Perform the action, updating the coordinator cache list for a Domain Group and rebuilding the security-scoped
     * activity search strings for all coordinators of a Domain Group.
     * 
     * @param inActionContext
     *            PrincipalActionContext.
     * @return null
     * @throws ExecutionException
     *             on error
     */
    public final Serializable execute(final ActionContext inActionContext) throws ExecutionException
    {
        // TODO examine logging. log other than info.
        if (log.isDebugEnabled())
        {
            log.debug("Performing action");
        }

        DomainGroupCacheUpdaterRequest request = (DomainGroupCacheUpdaterRequest) inActionContext.getParams();

        Long domainGroupId = request.getDomainGroupId();

        log.info("Loading domain group by id #" + domainGroupId);
        DomainGroup domainGroup = domainGroupMapper.findById(domainGroupId);

        log.info("Updating the cached list of coordinators for group #" + domainGroupId + " from the database");

        List<Long> peopleIds = groupCoordinatorCacheManager.execute(domainGroup);

        // if this is a private group, this change will affect the coordinators'
        // and followers' activity search
        if (!domainGroup.isPublicGroup())
        {
            if (request.getIsUpdate())
            {
                log.info("Looping across the coordinators to update"
                        + " their private group access list to include the group #" + domainGroupId);
                for (Long coordinatorPersonId : peopleIds)
                {
                    cache.addToSet(CacheKeys.PRIVATE_GROUP_IDS_VIEWABLE_BY_PERSON_AS_COORDINATOR + coordinatorPersonId,
                            domainGroupId);
                }
            }
            // skip if it is pending. Will be added on approval.
            else if (!domainGroup.isPending())
            {
                // This will search for all users who have coordinator access to this group
                // through either group or org coordinator roles and add the id to their cached
                // private group access list.
                addPrivateGroupIdToCachedListMapper.execute(domainGroupId);
            }
        }

        // find all the activity ids to update
        List<Long> activityIds = getActivityIdsAuthordedByEntityDbMapper.execute(domainGroup.getShortName(),
                EntityType.GROUP);

        if (log.isInfoEnabled())
        {
            log.info("Found info for '" + domainGroup.getShortName() + "' - applying it to " + activityIds.size()
                    + " activities.");
        }

        for (Long activityId : activityIds)
        {
            cache.delete(CacheKeys.ACTIVITY_BY_ID + activityId);
        }

        if (log.isDebugEnabled())
        {
            log.debug("Action complete");
        }
        return null;
    }

}
