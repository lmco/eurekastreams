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
package org.eurekastreams.server.action.execution.stream;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.action.request.stream.DeleteActivityCacheUpdateRequest;
import org.eurekastreams.server.persistence.mappers.stream.DeleteActivityCacheUpdate;

/**
 * Update cache after activity deletion.
 * 
 */
public class DeleteActivityCacheUpdateExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * DAO for updating cache after activity deletion.
     */
    private DeleteActivityCacheUpdate deleteActivityCacheUpdateDAO;

    /**
     * Constructor.
     * 
     * @param inDeleteActivityCacheUpdateDAO
     *            DAO for updating cache after activity deletion.
     */
    public DeleteActivityCacheUpdateExecution(final DeleteActivityCacheUpdate inDeleteActivityCacheUpdateDAO)
    {
        deleteActivityCacheUpdateDAO = inDeleteActivityCacheUpdateDAO;
    }

    /**
     * Update cache after activity deletion.
     * 
     * @param inActionContext
     *            {@link ActionContext}.
     * @return true if successful, false otherwise.
     */
    @Override
    public Boolean execute(final ActionContext inActionContext)
    {
        return deleteActivityCacheUpdateDAO.execute((DeleteActivityCacheUpdateRequest) inActionContext.getParams());
    }

}
