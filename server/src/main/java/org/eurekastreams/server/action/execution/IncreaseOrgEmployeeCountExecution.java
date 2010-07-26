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
import org.eurekastreams.server.action.request.IncreaseOrgEmployeeCountRequest;
import org.eurekastreams.server.persistence.mappers.db.IncreaseOrgEmployeeCount;

/**
 * Execution strategy for incrementing organization employee count.
 */
public class IncreaseOrgEmployeeCountExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Mapper for increasing the employee count.
     */
    private IncreaseOrgEmployeeCount employeeCountDAO;

    /**
     * Constructor.
     * 
     * @param inEmployeeCountDAO
     *            {@link IncreaseOrgEmployeeCount}.
     */
    public IncreaseOrgEmployeeCountExecution(final IncreaseOrgEmployeeCount inEmployeeCountDAO)
    {
        employeeCountDAO = inEmployeeCountDAO;
    }

    /**
     * Increase an organizations employee count by a given amount.
     * 
     * @param inActionContext
     *            {@link ActionContext}.
     * @return True if successful.
     */
    @Override
    public Boolean execute(final ActionContext inActionContext)
    {
        IncreaseOrgEmployeeCountRequest request = (IncreaseOrgEmployeeCountRequest) inActionContext.getParams();
        employeeCountDAO.execute(request);
        return Boolean.TRUE;
    }
}
