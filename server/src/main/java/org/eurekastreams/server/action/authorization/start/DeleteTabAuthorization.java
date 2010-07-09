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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.commons.logging.LogFactory;

/**
 * Strategy to Authorize the request of deleting a tab.
 *
 */
public class DeleteTabAuthorization implements AuthorizationStrategy<PrincipalActionContext>
{

    /**
     * Local logger instance.
     */
    private final Log logger = LogFactory.make();

    /**
     * TabPermisson utility.
     */
    private final TabPermission tabPermission;

    /**
     * Constructor for authorization to Delete Tab action.
     *
     * @param inTabPermission
     *            - instance of the {@link TabPermission} class for this authorization strategy.
     */
    public DeleteTabAuthorization(final TabPermission inTabPermission)
    {
        tabPermission = inTabPermission;
    }

    @Override
    public void authorize(final PrincipalActionContext inActionContext) throws AuthorizationException
    {
        // This will throw AuthorizationException if user doesn't have permissions.
        tabPermission.canDeleteStartPageTab(inActionContext.getPrincipal().getAccountId(), (Long) inActionContext
                .getParams(), true);
    }

}
