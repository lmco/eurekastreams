/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Return DomainGroupModelView for provided group shortName.
 */
public class GetDomainGroupModelViewByShortNameExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();
	
    /** Mapper used to look up the group. */
    private final GetDomainGroupsByShortNames groupByShortNameMapper;

    /** Mapper to get all person ids that have group coordinator access for a given group. */
    private final GetAllPersonIdsWhoHaveGroupCoordinatorAccess groupCoordinatorIdsDAO;

    /** Mapper to get followers for a group. */
    private final DomainMapper<Long, List<Long>> groupFollowerIdsMapper;

    /** Get ids for direct group coordinators. */
    private final DomainMapper<Long, List<Long>> groupCoordinatorIdsByGroupIdMapper;

    /** Get PersonModelViews by id. */
    private final DomainMapper<List<Long>, List<PersonModelView>> personModelViewsByIdMapper;

    /** Mapper for getting group entity. */
    private final DomainMapper<FindByIdRequest, DomainGroup> groupEntityMapper;

    /** Mapper to get an activity. */
    private final DomainMapper<Long, ActivityDTO> activityMapper;
    
    /**
     * Constructor.
     * 
     * @param inGroupByShortNameMapper
     *            injecting the mapper.
     * @param inGroupCoordinatorIdsDAO
     *            Mapper to get all person ids that have group coordinator access for a given group.
     * @param inGroupFollowerIdsMapper
     *            Instance of the {@link GetGroupFollowerIds}.
     * @param inGroupCoordinatorIdsByGroupIdMapper
     *            Get ids for direct group coordinators.
     * @param inPersonModelViewsByIdMapper
     *            Get PersonModelViews by id.
     * @param inGroupEntityMapper
     *            Mapper for getting group entity.
     * @param inActivityMapper
     *            Mapper to get an activity.
     */
    public GetDomainGroupModelViewByShortNameExecution(final GetDomainGroupsByShortNames inGroupByShortNameMapper,
            final GetAllPersonIdsWhoHaveGroupCoordinatorAccess inGroupCoordinatorIdsDAO,
            final DomainMapper<Long, List<Long>> inGroupFollowerIdsMapper,
            final DomainMapper<Long, List<Long>> inGroupCoordinatorIdsByGroupIdMapper,
            final DomainMapper<List<Long>, List<PersonModelView>> inPersonModelViewsByIdMapper,
            final DomainMapper<FindByIdRequest, DomainGroup> inGroupEntityMapper,
            final DomainMapper<Long, ActivityDTO> inActivityMapper)
    {
        groupByShortNameMapper = inGroupByShortNameMapper;
        groupCoordinatorIdsDAO = inGroupCoordinatorIdsDAO;
        groupFollowerIdsMapper = inGroupFollowerIdsMapper;
        groupCoordinatorIdsByGroupIdMapper = inGroupCoordinatorIdsByGroupIdMapper;
        personModelViewsByIdMapper = inPersonModelViewsByIdMapper;
        groupEntityMapper = inGroupEntityMapper;
        activityMapper = inActivityMapper;
    }

    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        String shortName = (String) inActionContext.getParams();
        log.debug("shortName = " + shortName);
        DomainGroupModelView result = groupByShortNameMapper.fetchUniqueResult(shortName);
        log.debug("result = " + result);

        if (result != null)
        {
            // set banner for group.
            result.setBannerEntityId(result.getId());

            // short circuit here if restricted for user.
            if (!isAccessPermitted(inActionContext.getPrincipal(), result))
            {
                // convert to new limited model view to prevent data leakage as model view grows.
                DomainGroupModelView restricted = new DomainGroupModelView();
                restricted.setRestricted(true);
                restricted.setEntityId(result.getId());
                restricted.setBannerId(result.getBannerId());
                restricted.setName(result.getName());
                restricted.setShortName(result.getShortName());
                restricted.setAvatarId(result.getAvatarId());
                return restricted;
            }
            else
            {
                result.setRestricted(false);

                // return the sticky activity with the group
                if (result.getStickyActivityId() != null)
                {
                    final ActivityDTO activity = activityMapper.execute(result.getStickyActivityId());
                    if (activity != null)
                    {
                        result.setStickyActivity(activity);
                        activity.setShareable(result.isPublic());
                    }
                }
            }

            result.setCoordinators(personModelViewsByIdMapper.execute(groupCoordinatorIdsByGroupIdMapper.execute(result
                    .getId())));

            result.setCapabilities(getCapabilities(result.getId()));
        }

        return result;
    }

    /**
     * Get group capabilities.
     * 
     * @param groupId
     *            id
     * @return list of capabilities (strings).
     */
    // TODO: for now this comes from entity as it did before. Should create new mapper and set up cache for group
    // capabilities to improve performance.
    private List<String> getCapabilities(final Long groupId)
    {
        List<String> results = new ArrayList<String>();
        DomainGroup g = groupEntityMapper.execute(new FindByIdRequest("DomainGroup", groupId));
        List<BackgroundItem> caps = g.getCapabilities();
        for (BackgroundItem bgi : caps)
        {
            results.add(bgi.getName());
        }

        return results;
    }

    /**
     * Check whether this group has restricted access and whether the current user is allowed access.
     * 
     * @param inPrincipal
     *            user principal.
     * @param inGroup
     *            the group the user wants to view
     * @return true if this person is allowed to see this group, false otherwise
     */
    private boolean isAccessPermitted(final Principal inPrincipal, final DomainGroupModelView inGroup)
    {
        // if group is public or user is coordinator recursively or follower, return true, otherwise false.
        return (inGroup.isPublic() || groupCoordinatorIdsDAO.execute(inGroup.getId()).contains(inPrincipal.getId()) //
                || isUserFollowingGroup(inPrincipal.getId(), inGroup.getId()));

    }

    /**
     * Checks to see if user is following a group.
     * 
     * @param userId
     *            the user id being checked.
     * @param groupId
     *            the group being checked.
     * @return true if user is a follower, false otherwise.
     */
    private boolean isUserFollowingGroup(final long userId, final long groupId)
    {
        List<Long> ids = groupFollowerIdsMapper.execute(groupId);
        if (ids.contains(userId))
        {
            return true;
        }
        return false;
    }
}
