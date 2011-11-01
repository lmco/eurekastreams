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

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContextImpl;
import org.eurekastreams.commons.actions.context.async.AsyncActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;

/**
 * Class which allows execution strategies to easily be invoked inline by other execution strategies.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class InlineExecutionStrategyWrappingExecutor
{
    /** If the execution strategy needs a principal. */
    private final boolean needsPrincipal;

    /** Execution strategy to execute, if it needs to queue async tasks. */
    private TaskHandlerExecutionStrategy taskHandlerExecution;

    /** Execution strategy to execute, if it does not need to queue async tasks. */
    private ExecutionStrategy plainExecution;

    /**
     * Constructor.
     *
     * @param inNeedsPrincipal
     *            If the execution strategy needs a principal.
     * @param inPlainExecution
     *            Execution strategy to execute which does not need to queue async tasks.
     */
    public InlineExecutionStrategyWrappingExecutor(final boolean inNeedsPrincipal,
            final ExecutionStrategy inPlainExecution)
    {
        needsPrincipal = inNeedsPrincipal;
        plainExecution = inPlainExecution;

        assert plainExecution != null;
    }

    /**
     * Constructor.
     *
     * @param inNeedsPrincipal
     *            If the execution strategy needs a principal.
     * @param inTaskHandlerExecution
     *            Execution strategy to execute which does need to queue async tasks.
     */
    public InlineExecutionStrategyWrappingExecutor(final boolean inNeedsPrincipal,
            final TaskHandlerExecutionStrategy inTaskHandlerExecution)
    {
        needsPrincipal = inNeedsPrincipal;
        taskHandlerExecution = inTaskHandlerExecution;

        assert taskHandlerExecution != null;
    }

    /**
     * Invokes the execution strategy getting needed data from a provided TaskHandlerActionContext.
     *
     * @param inActionContext
     *            Caller's TaskHandlerActionContext.
     * @param params
     *            Parameters for the execution strategy to be invoked.
     * @return Results from the invoked execution strategy.
     * @throws ExecutionException
     *             If execution strategy threw an exception or call did not provide all data needed for the execution
     *             strategy.
     */
    public Serializable execute(final TaskHandlerActionContext< ? extends ActionContext> inActionContext,
            final Serializable params) throws ExecutionException
    {
        ActionContext inInnerContext = inActionContext.getActionContext();
        boolean hasPrincipalContext = inInnerContext instanceof PrincipalActionContext;
        if (needsPrincipal && !hasPrincipalContext)
        {
            throw new ExecutionException("Incorrect action execution invocation.  "
                    + "Execution requires principal but none was provided on call.");
        }
        ActionContext innerContext = hasPrincipalContext ? new ServiceActionContext(params,
                ((PrincipalActionContext) inInnerContext).getPrincipal()) : new AsyncActionContext(params);

        if (taskHandlerExecution != null)
        {
            TaskHandlerActionContext outerContext = new TaskHandlerActionContextImpl<ActionContext>(innerContext,
                    inActionContext.getUserActionRequests());
            return taskHandlerExecution.execute(outerContext);
        }
        return plainExecution.execute(innerContext);
    }

    /**
     * Invokes the execution strategy getting needed data from a provided ActionContext.
     *
     * @param inActionContext
     *            Caller's ActionContext.
     * @param params
     *            Parameters for the execution strategy to be invoked.
     * @return Results from the invoked execution strategy.
     * @throws ExecutionException
     *             If execution strategy threw an exception or call did not provide all data needed for the execution
     *             strategy.
     */
    public Serializable execute(final ActionContext inActionContext, final Serializable params)
            throws ExecutionException
    {
        boolean hasPrincipalContext = inActionContext instanceof PrincipalActionContext;
        if (needsPrincipal && !hasPrincipalContext)
        {
            throw new ExecutionException("Incorrect action execution invocation.  "
                    + "Execution requires principal but none was provided on call.");
        }

        if (taskHandlerExecution != null)
        {
            throw new ExecutionException("Incorrect action execution invocation.  Execution requires user action "
                    + "request list (TaskHandlerActionContext) but none was provided on call.");
        }

        ActionContext context = hasPrincipalContext ? new ServiceActionContext(params,
                ((PrincipalActionContext) inActionContext).getPrincipal()) : new AsyncActionContext(params);

        return plainExecution.execute(context);
    }
}
