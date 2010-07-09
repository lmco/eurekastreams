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
import org.eurekastreams.server.action.request.start.AddGadgetRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.TabMapper;

/**
 *
 */
public class AddGadgetAuthorization implements AuthorizationStrategy<PrincipalActionContext>
{
    /**
     * The mapper we'll used to retrieve and delete the gadget.
     */
    private final TabMapper tabMapper;

    /**
     * TabPermisson utility.
     */
    private TabPermission tabPermission;

    /**
     * Person mapper.
     */
    private PersonMapper personMapper;

    /**
     * Constructor.
     *
     * @param inTabMapper
     *            mapper used to retrieve and delete the gadget.
     * @param inTabPermission
     *            check permissions for the tab operation
     * @param inPersonMapper
     *            used to load the person.
     */
    public AddGadgetAuthorization(final TabMapper inTabMapper, final TabPermission inTabPermission,
            final PersonMapper inPersonMapper)
    {
        tabMapper = inTabMapper;
        tabPermission = inTabPermission;
        personMapper = inPersonMapper;
    }

    /**
     * Perform security check on this action to verify user can execute. Throw appropriate AuthorizationException in
     * needed.
     *
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     * @throws AuthorizationException
     *             thrown if the user does not have proper access
     */
    @Override
    public void authorize(final PrincipalActionContext inActionContext) throws AuthorizationException
    {
        AddGadgetRequest request = (AddGadgetRequest) inActionContext.getParams();
        Long tabId = request.getTabId();

        // get the Tab we're inserting the Gadget into
        if (tabId == null)
        {
            // if client passes null for id, find the first tab
            Person person = personMapper.findByAccountId(inActionContext.getPrincipal().getAccountId());
            tabId = person.getStartTabGroup().getTabs().get(0).getId();
        }

        // This will throw AuthorizationException if user doesn't have permissions.
        if (!tabPermission.canModifyGadgets(inActionContext.getPrincipal().getAccountId(), tabId, true))
        {
            throw new AuthorizationException("Failed to authorize adding of the supplied gadget.");
        }
    }
}
