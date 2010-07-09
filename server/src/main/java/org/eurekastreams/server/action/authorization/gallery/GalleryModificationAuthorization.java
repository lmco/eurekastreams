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
package org.eurekastreams.server.action.authorization.gallery;

import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.authorization.IsOrganizationCoordinatorForAnyOrg;

/**
 * Authorization strategy for modifying gallery items.
 * 
 */
public class GalleryModificationAuthorization implements AuthorizationStrategy<PrincipalActionContext>
{
    /**
     * {@link IsOrganizationCoordinatorForAnyOrg}.
     */
    private IsOrganizationCoordinatorForAnyOrg isAnyOrgCoordinator;

    /**
     * Constructor.
     * 
     * @param inStrategy
     *            {@link IsOrganizationCoordinatorForAnyOrg}.
     */
    public GalleryModificationAuthorization(final IsOrganizationCoordinatorForAnyOrg inStrategy)
    {
        isAnyOrgCoordinator = inStrategy;
    }

    /**
     * Authorize.
     * 
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     */
    @Override
    public void authorize(final PrincipalActionContext inActionContext)
    {
        if (!isAnyOrgCoordinator.execute(inActionContext.getPrincipal().getId()))
        {
            throw new AuthorizationException("Current user does not have permissions to modify the gallery");
        }

    }

}
