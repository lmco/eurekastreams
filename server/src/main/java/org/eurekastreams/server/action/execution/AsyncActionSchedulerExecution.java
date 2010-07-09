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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;

/**
 * Execution strategy to pass an action through to a configured TaskHandler.
 */
public class AsyncActionSchedulerExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * Logger.
     */
    Log log = LogFactory.make();

    /**
     * Name of the action to execute.
     */
    private String actionName;

    /**
     * Constructor.
     *
     * @param inActionName
     *            the name of the action to execute in the job queue
     */
    public AsyncActionSchedulerExecution(final String inActionName)
    {
        actionName = inActionName;
    }

    /**
     * Action executer to schedule up a named action on the job queue.
     *
     * @param inActionContext
     *            the action context.
     * @return an empty string
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        if (log.isInfoEnabled())
        {
            String params = "(null)";
            if (inActionContext.getActionContext().getParams() != null)
            {
                params = inActionContext.getActionContext().getParams().toString();
            }
            log.info("Scheduling action named '" + actionName + "' with params: " + params
                    + " for async processing.");
        }
        inActionContext.getUserActionRequests().add(
                new UserActionRequest(actionName, null, inActionContext.getActionContext().getParams()));

        return null;
    }
}
