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

/**
 * Authorization to determine if current user is a root org coordinator.
 * 
 */
public class CurrentUserRootOrganizationCoordinatorAuthorization implements
        AuthorizationStrategy<PrincipalActionContext>
{
    /**
     *{@link IsRootOrganizationCoordinator}.
     */
    private IsRootOrganizationCoordinator rootOrgCoordinatorStrategy;

    /**
     * Constructor.
     * 
     * @param inRootOrgCoordinatorStrategy
     *            {@link IsRootOrganizationCoordinator}
     */
    public CurrentUserRootOrganizationCoordinatorAuthorization(
            final IsRootOrganizationCoordinator inRootOrgCoordinatorStrategy)
    {
        rootOrgCoordinatorStrategy = inRootOrgCoordinatorStrategy;
    }

    /**
     * Determine if current user is a root org coordinator.
     * 
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     */
    @Override
    public void authorize(final PrincipalActionContext inActionContext)
    {
        // if the user is not an org coordinator
        if (!rootOrgCoordinatorStrategy.isRootOrganizationCoordinator(inActionContext.getPrincipal().getId()))
        {
            throw new AuthorizationException("Insufficient permissions.");
        }

    }

}
