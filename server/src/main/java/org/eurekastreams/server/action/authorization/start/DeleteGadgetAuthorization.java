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
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.persistence.TabMapper;

/**
 * Perform security check on this action to verify user can execute. Throw appropriate AuthorizationException in needed.
 *
 */
public class DeleteGadgetAuthorization implements AuthorizationStrategy<PrincipalActionContext>
{
    /**
     * The mapper we'll used to retrieve and delete the gadget.
     */
    private final TabMapper tabMapper;

    /**
     * TabPermisson utility.
     */
    private final TabPermission tabPermission;

    /**
     * Constructor.
     *
     * @param mapper
     *            mapper used to retrieve and delete the gadget.
     * @param inTabPermission
     *            used to check user permissions.
     */
    public DeleteGadgetAuthorization(final TabMapper mapper, final TabPermission inTabPermission)
    {
        tabMapper = mapper;
        tabPermission = inTabPermission;
    }

    /**
     * {@inheritDoc}.
     *
     * Perform security check on this action to verify user can execute. Throw appropriate AuthorizationException in
     * needed.
     */
    @Override
    public void authorize(final PrincipalActionContext inActionContext) throws AuthorizationException
    {
        Long gadgetId = (Long) inActionContext.getParams();

        Tab tab = tabMapper.findTabByGadgetId(gadgetId);

        // This will throw AuthorizationException if user doesn't have permissions.
        if (!tabPermission.canModifyGadgets(inActionContext.getPrincipal().getAccountId(), tab.getId(), true))
        {
            throw new AuthorizationException("Failed to authorize deleting of the supplied gadget.");
        }
    }

}
