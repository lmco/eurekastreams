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
package org.eurekastreams.server.action.execution;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabGroupType;
import org.eurekastreams.server.persistence.PersonMapper;

/**
 * Strategy to retrieve a person from the database by their id.
 * 
 */
public class GetPersonExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Instance of the logger.
     */
    private final Log log = LogFactory.make();

    /**
     * PersonMapper used to retrieve person from the db.
     */
    private PersonMapper mapper = null;

    /**
     * Constructor that sets up the mapper.
     * 
     * @param inMapper
     *            - instance of PersonMapper
     */
    public GetPersonExecution(final PersonMapper inMapper)
    {
        mapper = inMapper;
    }

    /**
     * Retrieve a person from the database by their id, or current user if id is null.
     * 
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     * @return Person from the database by their id, or current user if id is null.
     */
    @Override
    public Person execute(final PrincipalActionContext inActionContext)
    {
        Person result = null;
        String identifierParam = (String) inActionContext.getParams();

        // Null parameter indicates request for person from start page so load up current
        // user with tabs/gadgets/tasks. Non-null requests load specified Person and skip
        // tabs/gadgets/tasks loading
        if (identifierParam == null)
        {
            result = mapper.findByAccountId(inActionContext.getPrincipal().getAccountId());

            if (result != null)
            {
                // Trigger loading of the tabs and gadget tasks.
                for (Tab tab : result.getTabs(TabGroupType.START))
                {
                    for (Gadget gadget : tab.getGadgets())
                    {
                        gadget.getGadgetDefinition();
                    }
                }
            }
        }
        else
        {
            result = mapper.findByAccountId(identifierParam);

            if (result != null && result.getBackground() != null)
            {
                result.getBackground().getBackgroundItems(BackgroundItemType.SKILL).size();
            }
        }
        return result;
    }

}
