/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.commons.actions.async;

import org.eurekastreams.commons.actions.Action;
import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.async.AsyncActionContext;

/**
 * Async based implementation of the Action interface.
 * Async actions are intended to be pulled off of the queue by the TaskQueueProcessor
 * and populated with an appropriate AsyncActionContext.
 *
 */
public class AsyncAction implements Action
{

    /**
     * Local instance of the {@link ValidationStrategy} for this async action.
     */
    private final ValidationStrategy<AsyncActionContext> valStrat;

    /**
     * Local instance of the {@link ExecutionStrategy} for this async action.
     */
    private final ExecutionStrategy<AsyncActionContext> execStrat;

    /**
     * Flag indicating whether this action needs a readonly transaction.
     */
    private final boolean readOnly;

    /**
     * Constructor for the AsyncAction.
     * @param inValidationStrategy - instance of the {@link ValidationStrategy} to use with this action.
     * @param inExecutionStrategy - instance of the {@link ExecutionStrategy} to use with this action.
     * @param inReadOnly - flag indicating whether this action need a readonly transaction.
     */
    public AsyncAction(final ValidationStrategy<AsyncActionContext> inValidationStrategy,
            final ExecutionStrategy<AsyncActionContext> inExecutionStrategy, final boolean inReadOnly)
    {
        valStrat = inValidationStrategy;
        execStrat = inExecutionStrategy;
        readOnly = inReadOnly;
    }

    /**
     * This method is not intended to be called within the Async side of the Action Framework.
     * 
     * {@inheritDoc}.
     */
    @Override
    public AuthorizationStrategy<PrincipalActionContext> getAuthorizationStrategy()
    {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ExecutionStrategy<AsyncActionContext> getExecutionStrategy()
    {
        return execStrat;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ValidationStrategy<AsyncActionContext> getValidationStrategy()
    {
        return valStrat;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isReadOnly()
    {
        return readOnly;
    }

}
