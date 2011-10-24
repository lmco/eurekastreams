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

import java.util.Set;

import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;

/**
 * This class enforces the authorization business rules for a user following a group.
 *
 */
public class SetFollowingGroupStatusAuthorization implements AuthorizationStrategy<PrincipalActionContext>
{
    /**
     * Local instance of the GetDomainGroupsByShortNames mapper.
     */
    private final GetDomainGroupsByShortNames groupMapper;

    /**
     * Local instance of the GetAllPersonIdsWhoHaveGroupCoordinatorAccess.
     */
    private final GetAllPersonIdsWhoHaveGroupCoordinatorAccess groupCoordMapper;

    /**
     * Constructor for the FollowingGroupAuthorization Strategy.
     *
     * @param inGroupMapper
     *            - instance of the group mapper to be used for authorization.
     * @param inGroupCoordMapper
     *            - instance of the group coordinator mapper to be used for authorization.
     */
    public SetFollowingGroupStatusAuthorization(final GetDomainGroupsByShortNames inGroupMapper,
            final GetAllPersonIdsWhoHaveGroupCoordinatorAccess inGroupCoordMapper)
    {
        groupMapper = inGroupMapper;
        groupCoordMapper = inGroupCoordMapper;
    }

    /**
     * This method enforces the authorization business rules for when a user follows a group. - Only coordinators can
     * add followers to a private group - Coordinators and a follower can remove that follower/themselves
     * from following
     * a private group. - Only followers can remove themselves from public groups.
     *
     * {@inheritDoc}
     */
    @Override
    public void authorize(final PrincipalActionContext inActionContext) throws AuthorizationException
    {
        SetFollowingStatusRequest request = (SetFollowingStatusRequest) inActionContext.getParams();

        DomainGroupModelView targetResult = groupMapper.fetchUniqueResult(request.getTargetUniqueId());

        if (request.getFollowerStatus().equals(Follower.FollowerStatus.FOLLOWING))
        {
            // If the group is private, only a group coordinator can add a follower to a group.
            if (!targetResult.isPublic())
            {
                Set<Long> groupCoordinators = groupCoordMapper.execute(targetResult.getEntityId());
                if (!groupCoordinators.contains(inActionContext.getPrincipal().getId()))
                {
                    throw new AuthorizationException("Only group coordinators can add members to a private group.");
                }
            }
        }
        else
        {
            // if the group is private, the follower and group coordinators are the only users that can sever the
            // relationship.
            if (!targetResult.isPublic())
            {
                Set<Long> groupCoordinators = groupCoordMapper.execute(targetResult.getEntityId());
                if (!groupCoordinators.contains(inActionContext.getPrincipal().getId())
                        && !request.getFollowerUniqueId().equals(inActionContext.getPrincipal().getAccountId()))
                {
                    throw new AuthorizationException("Coordinators and Followers are the only ones who can remove a "
                            + "follower from a private group.");
                }
            }
            // If the group is public only the own can sever the relationship.
            else if (request.getFollowerUniqueId() != null && request.getFollowerUniqueId() != ""
                    && !request.getFollowerUniqueId().equals(inActionContext.getPrincipal().getAccountId()))
            {
                throw new AuthorizationException("Only the owner of a relationship can remove it.");
            }
        }

    }

}
