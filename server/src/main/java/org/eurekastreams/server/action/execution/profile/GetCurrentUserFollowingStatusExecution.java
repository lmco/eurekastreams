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
package org.eurekastreams.server.action.execution.profile;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.profile.GetCurrentUserFollowingStatusRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetFollowerIds;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByOpenSocialIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Action to determine if current user has Follower relationship with another user.
 * 
 */
public class GetCurrentUserFollowingStatusExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Mapper that looks-to/loads cache with people modelviews by open social id.
     */
    private GetPeopleByOpenSocialIds getPeopleByOpenSocialIdsMapper;

    /**
     * Mapper to get followers of a group.
     */
    private DomainMapper<Long, List<Long>> groupFollowerIdsMapper;

    /**
     * Mapper to get followers of a person.
     */
    private GetFollowerIds followerIdsMapper;

    /**
     * Mapper to get a group by shortname.
     */
    private GetDomainGroupsByShortNames groupsByNameMapper;

    /**
     * Mapper to get person by account id.
     */
    private GetPeopleByAccountIds peopleByAccountMapper;

    /**
     * A Regex pattern to match OpenSocial ids used by the local container.
     */
    private String openSocialPattern;

    /**
     * Constructor that sets up the mapper.
     * 
     * @param inGetPeopleByOpenSocialIdsMapper
     *            instance of GetPeopleByOpenSocialIdsMapper
     * @param inPattern
     *            the pattern for matching open social ids.
     * @param inGroupFollowerIdsMapper
     *            instance of GetGroupFollowerIds.
     * @param inGollowerIdsMapper
     *            instance of GetFollowerIds.
     * @param inGroupsByNameMapper
     *            instance of GetDomainGroupsByShortNames.
     * @param inPeopleByAccountMapper
     *            instance of GetPeopleByAccountIds.
     */
    public GetCurrentUserFollowingStatusExecution(final GetPeopleByOpenSocialIds inGetPeopleByOpenSocialIdsMapper,
            final String inPattern, final DomainMapper<Long, List<Long>> inGroupFollowerIdsMapper,
            final GetFollowerIds inGollowerIdsMapper, final GetDomainGroupsByShortNames inGroupsByNameMapper,
            final GetPeopleByAccountIds inPeopleByAccountMapper)
    {
        getPeopleByOpenSocialIdsMapper = inGetPeopleByOpenSocialIdsMapper;
        openSocialPattern = inPattern;
        groupFollowerIdsMapper = inGroupFollowerIdsMapper;
        followerIdsMapper = inGollowerIdsMapper;
        groupsByNameMapper = inGroupsByNameMapper;
        peopleByAccountMapper = inPeopleByAccountMapper;
    }

    /**
     * Returns true or false if the group exists and the current user is a coordinator.
     * 
     * @param inActionContext
     *            The action context.
     * @return true if the group exists and the user is authorized, false otherwise
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext)
    {
        // get the request.
        GetCurrentUserFollowingStatusRequest inRequest = (GetCurrentUserFollowingStatusRequest) inActionContext
                .getParams();

        // get the user's account id.
        final Principal principal = inActionContext.getPrincipal();
        final String accountId = principal.getAccountId();
        final Long userId = principal.getId();

        // the followed entity's account id.
        String followedEntityId = inRequest.getFollowedEntityId();

        // the followed entity's entity type.
        final EntityType entityType = inRequest.getEntityType();

        if (log.isTraceEnabled())
        {
            log.trace("Checking the following status for the user " + accountId + " following " + followedEntityId
                    + " of entity type " + entityType.toString());
        }

        // if open social id was passed for person, convert to acct. id. else entityId is an accountId
        if (followedEntityId.matches(openSocialPattern) && entityType == EntityType.PERSON)
        {
            followedEntityId = getPeopleByOpenSocialIdsMapper.fetchUniqueResult(followedEntityId).getAccountId();
        }

        // if not logged in or trying to follow yourself, disable.
        if (accountId == null || (accountId.equalsIgnoreCase(followedEntityId) && entityType == EntityType.PERSON))
        {
            return Follower.FollowerStatus.DISABLED;
        }

        Follower.FollowerStatus status = Follower.FollowerStatus.DISABLED;

        if (EntityType.PERSON == entityType)
        {
            status = isUserFollowingUser(userId, followedEntityId);
        }
        else if (EntityType.GROUP == entityType)
        {
            status = isUserFollowingGroup(userId, followedEntityId);
        }

        return status;
    }

    /**
     * Checks to see if a user is following a group.
     * 
     * @param userId
     *            id of the user that is being checked as a follower.
     * @param groupShortName
     *            id of the group being checked for followers.
     * @return FollowerStatus of the user.
     */
    private Follower.FollowerStatus isUserFollowingGroup(final long userId, final String groupShortName)
    {
        List<DomainGroupModelView> groups = groupsByNameMapper.execute(Collections.singletonList(groupShortName));
        if (groups.size() > 0)
        {
            long groupId = groups.get(0).getEntityId();
            List<Long> ids = groupFollowerIdsMapper.execute(groupId);
            if (ids.contains(userId))
            {
                return Follower.FollowerStatus.FOLLOWING;
            }
        }
        return Follower.FollowerStatus.NOTFOLLOWING;
    }

    /**
     * Checks to see if a user is following another user.
     * 
     * @param userId
     *            id of the user that is being checked as a follower.
     * @param personAccountId
     *            id of the user being checked for followers
     * @return FollowerStatus of the user.
     */
    private Follower.FollowerStatus isUserFollowingUser(final long userId, final String personAccountId)
    {
        List<PersonModelView> people = peopleByAccountMapper.execute(Collections.singletonList(personAccountId));
        if (people.size() > 0)
        {
            long followingUserId = people.get(0).getEntityId();
            List<Long> ids = followerIdsMapper.execute(followingUserId);
            if (ids.contains(userId))
            {
                return Follower.FollowerStatus.FOLLOWING;
            }
        }
        return Follower.FollowerStatus.NOTFOLLOWING;
    }
}
