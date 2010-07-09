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
import org.eurekastreams.server.persistence.mappers.GetRecursiveOrgCoordinators;

/**
 * Authorization to determine if current user is a org coordinator recursively.
 *
 */
// TODO: This and CurrentUserGroupCoordinatorRecursivelyAuthorization are VERY similar, if the permission checkers
// were refactored to have a common interface with "hasCoordinatorAccessRecursively(personId, entityId)" method, they
// could be collapsed into the same authorization strategy, and permission checker could be passed in and handled
// generically just like RequestTransformer is now.
public class CurrentUserOrgCoordinatorRecursivelyAuthorization implements AuthorizationStrategy<PrincipalActionContext>
{
    /**
     * Transform org id from request.
     */
    private RequestTransformer orgIdtransformer;

    /**
     * Mapper to get all the coordinators of an org, traversing up the tree.
     */
    private GetRecursiveOrgCoordinators orgPermissionsChecker;

    /**
     * Constructor.
     *
     * @param inOrgIdtransformer
     *            Transform org id from request.
     * @param inOrgPermissionsChecker
     *            Mapper to get all the coordinators of an org, traversing up the tree.
     */
    public CurrentUserOrgCoordinatorRecursivelyAuthorization(final RequestTransformer inOrgIdtransformer,
            final GetRecursiveOrgCoordinators inOrgPermissionsChecker)
    {
        orgIdtransformer = inOrgIdtransformer;
        orgPermissionsChecker = inOrgPermissionsChecker;
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
        Long orgId = Long.valueOf((String) orgIdtransformer.transform(inActionContext));
        Long personId = inActionContext.getPrincipal().getId();

        if (!orgPermissionsChecker.isOrgCoordinatorRecursively(personId, orgId))
        {
            // doesn't have permissions
            throw new AuthorizationException("Insufficient permissions.");
        }
    }

}
