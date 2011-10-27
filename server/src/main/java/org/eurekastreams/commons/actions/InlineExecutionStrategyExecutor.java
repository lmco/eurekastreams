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
package org.eurekastreams.commons.actions;

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContextImpl;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.server.UserActionRequest;

/**
 * Class which allows execution strategies to easily be invoked inline by other execution strategies. Same fundamental
 * purpose as InlineExecutionStrategyWrappingExecutor but takes the action as a call parameter instead of a constructor
 * argument.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class InlineExecutionStrategyExecutor
{
    /**
     * Executes the given execution strategy.
     *
     * @param execution
     *            The execution strategy.
     * @param params
     *            Parameters for the execution strategy to be invoked.
     * @param principal
     *            Principal to provide to the execution strategy.
     * @return Return value from the execution strategy.
     */
    public Serializable execute(final ExecutionStrategy execution, final Serializable params, final Principal principal)
    {
        PrincipalActionContext context = new ServiceActionContext(params, principal);
        return execution.execute(context);
    }

    /**
     * Executes the given execution strategy.
     *
     * @param execution
     *            The execution strategy.
     * @param params
     *            Parameters for the execution strategy to be invoked.
     * @param parentContext
     *            Caller's action context.
     * @return Return value from the execution strategy.
     */
    public Serializable execute(final ExecutionStrategy execution, final Serializable params,
            final ActionContext parentContext)
    {
        Principal principal = null;
        if (parentContext instanceof PrincipalActionContext)
        {
            principal = ((PrincipalActionContext) parentContext).getPrincipal();
        }
        return execute(execution, params, principal);
    }

    /**
     * Executes the given execution strategy.
     *
     * @param execution
     *            The execution strategy.
     * @param params
     *            Parameters for the execution strategy to be invoked.
     * @param principal
     *            Principal to provide to the execution strategy.
     * @param requests
     *            List to receive any queued action requests from the execution strategy.
     * @return Return value from the execution strategy.
     */
    public Serializable execute(final TaskHandlerExecutionStrategy execution, final Serializable params,
            final Principal principal, final List<UserActionRequest> requests)
    {
        PrincipalActionContext innerContext = new ServiceActionContext(params, principal);
        TaskHandlerActionContext<PrincipalActionContext> context = new TaskHandlerActionContextImpl(innerContext,
                requests);
        return execution.execute(context);
    }

    /**
     * Executes the given execution strategy.
     *
     * @param execution
     *            The execution strategy.
     * @param params
     *            Parameters for the execution strategy to be invoked.
     * @param parentContext
     *            Caller's action context.
     * @return Return value from the execution strategy.
     */
    public Serializable execute(final TaskHandlerExecutionStrategy execution, final Serializable params,
            final TaskHandlerActionContext< ? extends ActionContext> parentContext)
    {
        Principal principal = null;
        ActionContext parentInnerContext = parentContext.getActionContext();
        if (parentInnerContext instanceof PrincipalActionContext)
        {
            principal = ((PrincipalActionContext) parentInnerContext).getPrincipal();
        }
        return execute(execution, params, principal, parentContext.getUserActionRequests());
    }
}
