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
import org.eurekastreams.server.action.request.profile.ReviewPendingGroupRequest;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.mappers.GetRecursiveOrgCoordinators;

/**
 * Authorization strategy for reviewing a pending group.
 */
public class ReviewPendingGroupAuthorizationStrategy implements AuthorizationStrategy<PrincipalActionContext>
{
    /**
     * Used to check if user has coordinator access to approve the group.
     */
    private GetRecursiveOrgCoordinators getRecursiveOrgCoordinators;

    /**
     * Have to use a group mapper since it is the only way to look up pending groups.
     */
    private DomainGroupMapper getDomainGroups;

    /**
     * @param inGetRecursiveOrgCoordinators
     *            Mapper that returns recursive org coordinators.
     * @param inGetDomainGroups
     *            Mapper that gets Domain group.
     */
    public ReviewPendingGroupAuthorizationStrategy(final GetRecursiveOrgCoordinators inGetRecursiveOrgCoordinators,
            final DomainGroupMapper inGetDomainGroups)
    {
        getRecursiveOrgCoordinators = inGetRecursiveOrgCoordinators;
        getDomainGroups = inGetDomainGroups;
    }

    /**
     * Authorize the user.
     *
     * @param inActionContext
     *            the action context
     */
    @Override
    public void authorize(final PrincipalActionContext inActionContext)
    {
        ReviewPendingGroupRequest request = (ReviewPendingGroupRequest) inActionContext.getParams();

        DomainGroup group = getDomainGroups.findByShortName(request.getGroupShortName());

        Long pOrgID = group.getParentOrganization().getId();

        if (getRecursiveOrgCoordinators.isOrgCoordinatorRecursively(inActionContext.getPrincipal().getAccountId(),
                pOrgID))
        {
            return;
        }
        throw new AuthorizationException("Only a group coordinator can approve or deny a new group");
    }

}
