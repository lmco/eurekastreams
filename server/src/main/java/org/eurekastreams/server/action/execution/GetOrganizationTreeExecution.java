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

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.domain.OrganizationTreeDTO;
import org.eurekastreams.server.persistence.mappers.GetOrganizationTreeDTO;

/**
 * Gets the entire organization tree.
 */
public class GetOrganizationTreeExecution implements ExecutionStrategy<ActionContext>
{
    /**
     *OrganizationTree DAO.
     */
    private GetOrganizationTreeDTO organizationTreeDAO;

    /**
     * Constructor.
     * 
     * @param inOrganizationTreeDAO
     *            OrganizationTree DAO.
     */
    public GetOrganizationTreeExecution(final GetOrganizationTreeDTO inOrganizationTreeDAO)
    {
        organizationTreeDAO = inOrganizationTreeDAO;
    }

    /**
     * Gets the entire organization tree.
     * 
     * @param inActionContext
     *            {@link ActionContext}.
     * @return {@link OrganizationTreeDTO} representing entire organization tree.
     */
    @Override
    public OrganizationTreeDTO execute(final ActionContext inActionContext)
    {
        return organizationTreeDAO.execute();
    }

}
