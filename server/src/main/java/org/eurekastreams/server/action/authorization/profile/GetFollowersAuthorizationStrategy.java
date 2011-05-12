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
package org.eurekastreams.server.action.authorization.profile;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.request.profile.GetFollowersFollowingRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;

/**
 * Authorization strategy for GetPendingGroups.
 */
public class GetFollowersAuthorizationStrategy implements AuthorizationStrategy<PrincipalActionContext>
{
    /**
     * Mapper to get coordinators for a group.
     */
    private DomainMapper<Long, List<Long>> coordMapper;

    /**
     * Mapper to get group followers.
     */
    private DomainMapper<Long, List<Long>> groupFollowerIdsMapper;

    /**
     * Mapper to get group info by short name.
     */
    private GetDomainGroupsByShortNames groupMapper;

    /**
     * Mapper to get a list of admin ids.
     */
    private DomainMapper<Serializable, List<Long>> adminIdsMapper;

    /**
     * Constructor.
     * 
     * @param inCoordMapper
     *            the group coordinator mapper.
     * @param inGroupFollowerIdsMapper
     *            the group follower mapper.
     * @param inGroupMapper
     *            the group mapper.
     * @param inAdminIdsMapper
     *            mapper to get a list of admin ids
     */
    public GetFollowersAuthorizationStrategy(final DomainMapper<Long, List<Long>> inCoordMapper,
            final DomainMapper<Long, List<Long>> inGroupFollowerIdsMapper,
            final GetDomainGroupsByShortNames inGroupMapper,
            final DomainMapper<Serializable, List<Long>> inAdminIdsMapper)
    {
        coordMapper = inCoordMapper;
        groupFollowerIdsMapper = inGroupFollowerIdsMapper;
        groupMapper = inGroupMapper;
        adminIdsMapper = inAdminIdsMapper;
    }

    /**
     * Authorization strategy for GetPendingGroups.
     * 
     * @param inActionContext
     *            the action context
     */
    @Override
    public void authorize(final PrincipalActionContext inActionContext)
    {
        GetFollowersFollowingRequest actionRequest = (GetFollowersFollowingRequest) inActionContext.getParams();

        EntityType targetType = actionRequest.getEntityType();

        if (targetType == EntityType.PERSON)
        {
            return;
        }

        if (targetType == EntityType.GROUP)
        {
            long userId = inActionContext.getPrincipal().getId();
            String groupShortName = actionRequest.getEntityId();
            List<DomainGroupModelView> groups = groupMapper.execute(Collections.singletonList(groupShortName));

            if (groups.size() > 0)
            {
                DomainGroupModelView group = groups.get(0);

                if (group.isPublic() || isUserCoordForGroup(userId, group.getId())
                        || isUserFollowingGroup(userId, group.getId())
                        || adminIdsMapper.execute(null).contains(inActionContext.getPrincipal().getId()))
                {
                    return;
                }
            }

            throw new AuthorizationException("Only a coordinator or follower can get the list of followers "
                    + "of a private group");
        }
    }

    /**
     * Checks to see if user is coordinator for a group.
     * 
     * @param userId
     *            the user id being checked.
     * @param groupId
     *            the group being checked.
     * @return true if user is a coordinator, false otherwise.
     */
    private boolean isUserCoordForGroup(final long userId, final long groupId)
    {
        List<Long> ids = coordMapper.execute(groupId);
        if (ids.contains(userId))
        {
            return true;
        }
        return false;
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
