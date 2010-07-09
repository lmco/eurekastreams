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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.profile.GetCurrentUserFollowingStatusRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.FollowMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByOpenSocialIds;

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
     * PersonMapper used to retrieve person from the db.
     */
    private PersonMapper personMapper = null;

    /**
     * GroupMapper used to retrieve a group if that's the target type.
     */
    private DomainGroupMapper groupMapper = null;

    /**
     * Mapper that looks-to/loads cache with people modelviews by open social id.
     */
    private GetPeopleByOpenSocialIds getPeopleByOpenSocialIdsMapper;

    /**
     * A Regex pattern to match OpenSocial ids used by the local container.
     */
    private String openSocialPattern;

    /**
     * Constructor that sets up the mapper.
     * 
     * @param inPersonMapper
     *            - instance of PersonMapper
     * @param inGroupMapper
     *            - instance of GroupMapper
     * 
     * @param inGetPeopleByOpenSocialIdsMapper
     *            - instance of GetPeopleByOpenSocialIdsMapper
     * @param inPattern
     *            the pattern for matching open social ids.
     */
    public GetCurrentUserFollowingStatusExecution(final PersonMapper inPersonMapper,
            final DomainGroupMapper inGroupMapper, final GetPeopleByOpenSocialIds inGetPeopleByOpenSocialIdsMapper,
            final String inPattern)
    {
        personMapper = inPersonMapper;
        groupMapper = inGroupMapper;
        getPeopleByOpenSocialIdsMapper = inGetPeopleByOpenSocialIdsMapper;
        openSocialPattern = inPattern;
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
        final String accountId = inActionContext.getPrincipal().getAccountId();

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

        FollowMapper followMapper = pickFollowMapper(entityType);

        Follower.FollowerStatus status = Follower.FollowerStatus.DISABLED;

        if (null != followMapper)
        {
            status = followMapper.isFollowing(accountId, followedEntityId) ? Follower.FollowerStatus.FOLLOWING
                    : Follower.FollowerStatus.NOTFOLLOWING;
        }

        return status;
    }

    /**
     * @param inGetPeopleByOpenSocialIdsMapper
     *            the getPeopleByOpenSocialIdsMapper to set
     */
    // public void setGetPeopleByOpenSocialIdsMapper(final GetPeopleByOpenSocialIds inGetPeopleByOpenSocialIdsMapper)
    // {
    // this.getPeopleByOpenSocialIdsMapper = inGetPeopleByOpenSocialIdsMapper;
    // }
    /**
     * Pick one of the mappers based on the entity type we're looking at.
     * 
     * @param type
     *            the type of the target entity
     * @return a mapper or null
     */
    private FollowMapper pickFollowMapper(final EntityType type)
    {
        if (EntityType.PERSON == type)
        {
            return personMapper;
        }
        else if (EntityType.GROUP == type)
        {
            return groupMapper;
        }
        return null;
    }
}
