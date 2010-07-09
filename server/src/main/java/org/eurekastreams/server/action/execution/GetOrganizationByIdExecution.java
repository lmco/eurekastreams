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

import java.util.Collections;
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByIds;
import org.eurekastreams.server.search.modelview.OrganizationModelView;

/**
 * Returns an OrganizationModelView corresponding to the org identified by the id parameter.
 */
public class GetOrganizationByIdExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Organization by id DAO.
     */
    private GetOrganizationsByIds organizationByIdDAO;

    /**
     * Constructor.
     * 
     * @param inOrganizationByIdDAO
     *            Organization by id DAO.
     */
    public GetOrganizationByIdExecution(final GetOrganizationsByIds inOrganizationByIdDAO)
    {
        organizationByIdDAO = inOrganizationByIdDAO;
    }

    /**
     * Finds the specified organization.
     * 
     * @param inActionContext
     *            The {@link ActionContext}.
     * 
     * @return OrganizationModelView corresponding to the id parameter.
     * 
     */
    @Override
    public OrganizationModelView execute(final ActionContext inActionContext)
    {
        Long orgId = (Long) inActionContext.getParams();
        List<OrganizationModelView> orgs = organizationByIdDAO.execute(Collections.singletonList(orgId));
        if (orgs.size() == 0)
        {
            return null;
        }
        return orgs.get(0);
    }
}
