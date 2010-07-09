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

import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.request.profile.GetFollowersFollowingRequest;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.mappers.GetRecursiveOrgCoordinators;

/**
 * Authorization strategy for GetPendingGroups.
 */
public class GetFollowersAuthorizationStrategy implements AuthorizationStrategy<PrincipalActionContext>
{
    /**
     * The domain group mapper.
     */
    private final DomainGroupMapper groupMapper;

    /**
     * Mapper to get all the coordinators of an org, traversing up the tree.
     */
    private GetRecursiveOrgCoordinators orgPermissionsChecker;

    /**
     * Constructor.
     *
     * @param inGroupMapper
     *            the domain group mapper.
     * @param inOrgPermissionsChecker
     *            Mapper to get all the coordinators of an org, traversing up the tree.
     */
    public GetFollowersAuthorizationStrategy(final DomainGroupMapper inGroupMapper,
            final GetRecursiveOrgCoordinators inOrgPermissionsChecker)
    {
        groupMapper = inGroupMapper;
        orgPermissionsChecker = inOrgPermissionsChecker;
    }

    /**
     * Authorization strategy for GetPendingGroups.
     *
     * @param inActionContext
     *            the action context
     */
    @SuppressWarnings("deprecation")
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
            String accountId = inActionContext.getPrincipal().getAccountId();
            String groupShortName = actionRequest.getEntityId();
            DomainGroup group = groupMapper.findByShortName(groupShortName);

            if (group.isPublicGroup()
                    || group.isCoordinator(accountId)
                    || groupMapper.isFollowing(accountId, groupShortName)
                    || orgPermissionsChecker.isOrgCoordinatorRecursively(inActionContext.getPrincipal().getId(),
                            group.getParentOrgId()))
            {
                return;
            }

            throw new AuthorizationException("Only a coordinator or follower can get the list of followers "
                    + "of a private group");
        }
    }

}
