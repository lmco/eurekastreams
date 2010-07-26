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

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;

/**
 * Submit an async action to the queue.
 * 
 */
public class QueueAsynchActionExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * Action key.
     */
    private String actionKey = null;

    /**
     * Constructor.
     * 
     * @param inActionKey
     *            Action key.
     */
    public QueueAsynchActionExecution(final String inActionKey)
    {
        actionKey = inActionKey;
    }

    /**
     * Submit the action to the queue.
     * 
     * @param inActionContext
     *            {@link TaskHandlerActionContext}.
     * @return null.
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        if (actionKey != null && !actionKey.isEmpty())
        {
            inActionContext.getUserActionRequests().add(new UserActionRequest(actionKey, null, null));
        }
        return null;
    }
}
