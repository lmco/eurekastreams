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
package org.eurekastreams.server.action.authorization;

import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.request.transformer.RequestTransformer;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;

/**
 * Authorization strategy for determining if current user has group coordinator permissions anywhere up the tree from a
 * group (inclusive).
 * 
 */
// TODO: This and CurrenUserOrgCooridinatorRecursivelyAuthorization are VERY similar, if the permission checkers
// were refactored to have a common interface with "hasCoordinatorAccessRecursively(personId, entityId)" method, they
// could be collapsed into the same authorization strategy, and permission checker could be passed in and handled
// generically just like RequestTransformer is now.
public class CurrentUserGroupCoordinatorRecursivelyAuthorization implements
        AuthorizationStrategy<PrincipalActionContext>
{
    /**
     * Transform org id from request.
     */
    private RequestTransformer groupIdtransformer;

    /**
     * Mapper to determine if a user has access to update a group.
     */
    private GetAllPersonIdsWhoHaveGroupCoordinatorAccess groupPermissionsChecker;

    /**
     * Constructor.
     * 
     * @param inOrgIdtransformer
     *            Transform org id from request.
     * @param inGroupPermissionsChecker
     *            Mapper to determine if a user has access to update a group.
     */
    public CurrentUserGroupCoordinatorRecursivelyAuthorization(final RequestTransformer inOrgIdtransformer,
            final GetAllPersonIdsWhoHaveGroupCoordinatorAccess inGroupPermissionsChecker)
    {
        groupIdtransformer = inOrgIdtransformer;
        groupPermissionsChecker = inGroupPermissionsChecker;
    }

    /**
     * Determine if current user is a org coordinator recursively.
     * 
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     */
    @Override
    public void authorize(final PrincipalActionContext inActionContext)
    {
        Long groupId = Long.valueOf((String) groupIdtransformer.transform(inActionContext));
        Long personId = inActionContext.getPrincipal().getId();

        if (!groupPermissionsChecker.hasGroupCoordinatorAccessRecursively(personId, groupId))
        {
            // doesn't have permissions
            throw new AuthorizationException("Insufficient permissions to update group.");
        }
    }

}
