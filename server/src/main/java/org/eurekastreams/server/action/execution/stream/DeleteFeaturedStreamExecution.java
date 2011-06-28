/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import java.io.Serializable;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Action to delete a featured stream.
 */
public class DeleteFeaturedStreamExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * Delete mapper.
     */
    DomainMapper<Long, Void> deleteMapper;

    /**
     * Constructor.
     * 
     * @param inDeleteMapper
     *            Delete mapper.
     */
    public DeleteFeaturedStreamExecution(final DomainMapper<Long, Void> inDeleteMapper)
    {
        deleteMapper = inDeleteMapper;
    }

    /**
     * Delete the featured stream, and kick off a task to rebuild the discovery page.
     * 
     * @param inActionContext
     *            the action context
     * @return truth
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        // delete featured stream.
        deleteMapper.execute((Long) inActionContext.getActionContext().getParams());

        // kick off the action to rebuild the Discover Page cache - but don't delete the key now, because it takes
        // seconds to rebuild
        inActionContext.getUserActionRequests().add(
                new UserActionRequest("regenerateStreamDiscoverListsJob", null, null));

        return true;
    }
}
