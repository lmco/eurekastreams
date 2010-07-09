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
package org.eurekastreams.server.action.authorization.start;

import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.request.start.RenameTabRequest;

/**
 * Throws AuthorizationException if user doesn't have permissions to rename the specified tab.
 * 
 */
public class RenameTabAuthorization implements AuthorizationStrategy<PrincipalActionContext>
{
    /**
     * {@link TabPermission}.
     */
    private TabPermission tabPermission;

    /**
     * Constructor.
     * 
     * @param inTabPermission
     *            {@link TabPermission}.
     */
    public RenameTabAuthorization(final TabPermission inTabPermission)
    {
        tabPermission = inTabPermission;
    }

    /**
     * Throws AuthorizationException if user doesn't have permissions to rename the specified tab.
     * 
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     */
    @Override
    public void authorize(final PrincipalActionContext inActionContext)
    {
        if (!tabPermission.canRenameTab(inActionContext.getPrincipal().getAccountId(),
                ((RenameTabRequest) inActionContext.getParams()).getTabId(), false))
        {
            throw new AuthorizationException("Insufficient permissions to rename tab.");
        }
    }
}
