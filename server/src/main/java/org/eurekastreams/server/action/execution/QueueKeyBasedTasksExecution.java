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

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Queue up async actions for cache initialization.
 * 
 */
public class QueueKeyBasedTasksExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * Action key (name) for task to queue.
     */
    private List<String> actionKeys;

    /**
     * Mapper to get list of keys of entities to warm.
     */
    private DomainMapper<Serializable, List<Serializable>> objectKeyMapper;

    /**
     * Constructor.
     * 
     * @param inActionKeys
     *            Action key (name) for task to queue.
     * @param inOjectKeyMapper
     *            Mapper to get list of keys of entities to warm.
     */
    public QueueKeyBasedTasksExecution(final List<String> inActionKeys,
            final DomainMapper<Serializable, List<Serializable>> inOjectKeyMapper)
    {
        actionKeys = inActionKeys;
        objectKeyMapper = inOjectKeyMapper;
    }

    /**
     * Queue up async actions for cache initialization.
     * 
     * @param inActionContext
     *            {@link TaskHandlerActionContext}.
     * @return null.
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        List<Serializable> objectKeys = objectKeyMapper.execute(null);

        List<UserActionRequest> tasks = inActionContext.getUserActionRequests();

        for (Serializable objectKey : objectKeys)
        {
            for (String actionKey : actionKeys)
            {
                tasks.add(new UserActionRequest(actionKey, null, objectKey));
            }
        }

        return null;
    }
}
