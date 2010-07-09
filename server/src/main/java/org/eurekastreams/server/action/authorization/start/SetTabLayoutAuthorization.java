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
import org.eurekastreams.server.action.request.start.SetTabLayoutRequest;

/**
 * Authorization Strategy to determine if the requester has the authorization to change the tab layout.
 *
 */
public class SetTabLayoutAuthorization implements AuthorizationStrategy<PrincipalActionContext>
{
    /**
     * Instance of the Logger for this class.
     */
    private final Log logger = LogFactory.make();

    /**
     * Instance of the {@link TabPermission} class.
     */
    private final TabPermission tabPermission;

    /**
     * Base constructor for this Authorization Strategy.
     *
     * @param inTabPermission
     *            - instance of the {@link TabPermission} strategy for determining if the current request
     *            has the proper
     *            authorization to change the tab layout.
     */
    public SetTabLayoutAuthorization(final TabPermission inTabPermission)
    {
        tabPermission = inTabPermission;
    }

    /**
     * {@inheritDoc}. Determine if the user making the request has the appropriate permissions to modifiy the supplied
     * tab id.
     */
    @Override
    public void authorize(final PrincipalActionContext inActionContext) throws AuthorizationException
    {
        SetTabLayoutRequest currentRequest = (SetTabLayoutRequest) inActionContext.getParams();
        try
        {
            if (!tabPermission.canChangeTabLayout(inActionContext.getPrincipal().getAccountId(), currentRequest
                    .getTabId(), true))
            {
                throw new Exception();
            }
        }
        catch (Exception ex)
        {
            logger.info("Authorization to change tab layout failed for this request");
            throw new AuthorizationException("Authorization to change tab layout failed for this request", ex);
        }
    }

}
