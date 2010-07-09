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
package org.eurekastreams.commons.actions.service;

import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.TaskHandlerAction;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.task.TaskHandler;

/**
 * This class represents the {@link ServiceAction} based implementation of the {@link TaskHandlerAction} interface.
 *
 * TaskHandlerServiceActions are similar to ServiceActions but contain the mechanisms to allow a
 * TaskHandlerExecutionStrategy to build up UserActionRequest objects and a configured {@link TaskHandler} for
 * the controller to use to submit the requests.
 *
 */
public class TaskHandlerServiceAction implements TaskHandlerAction
{
    /**
     * Instance of the {@link TaskHandler} for this class.
     */
    private final TaskHandler taskHandler;

    /**
     * Instance of the {@link ValidationStrategy} for this class.
     */
    private final ValidationStrategy validationStrategy;

    /**
     * Instance of the {@link AuthorizationStrategy} for this class.
     */
    private final AuthorizationStrategy authorizationStrategy;

    /**
     * Instance of the {@link TaskHandlerExecutionStrategy} for this class.
     */
    private final TaskHandlerExecutionStrategy taskHandlerExecutionStrategy;

    /**
     * Flag indicating whether or this this action is read only.
     */
    private final boolean readOnly;

    /**
     * Constructor for this class.
     *
     * @param inValidationStrategy
     *            - instance of the {@link ValidationStrategy} for this class.
     * @param inAuthorizationStrategy
     *            - instance of the {@link AuthorizationStrategy} for this class.
     * @param inTaskHandlerExecutionStrategy
     *            - instance of the {@link TaskHandlerExecutionStrategy} for this class.
     * @param inAsyncActionSubmitter
     *            - instance of the {@link TaskHandler} to submit UserActionRequest objects to.
     * @param inReadOnly
     *            - flag indicating whether or not this action is read only.
     */
    public TaskHandlerServiceAction(final ValidationStrategy inValidationStrategy,
            final AuthorizationStrategy inAuthorizationStrategy,
            final TaskHandlerExecutionStrategy inTaskHandlerExecutionStrategy,
            final TaskHandler inAsyncActionSubmitter, final boolean inReadOnly)
    {
        validationStrategy = inValidationStrategy;
        authorizationStrategy = inAuthorizationStrategy;
        taskHandlerExecutionStrategy = inTaskHandlerExecutionStrategy;
        taskHandler = inAsyncActionSubmitter;
        readOnly = inReadOnly;
    }

    /**
     * {@inheritDoc}
     */
    public TaskHandler getTaskHandler()
    {
        return taskHandler;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AuthorizationStrategy getAuthorizationStrategy()
    {
        return authorizationStrategy;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public TaskHandlerExecutionStrategy getExecutionStrategy()
    {
        return taskHandlerExecutionStrategy;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ValidationStrategy getValidationStrategy()
    {
        return validationStrategy;
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
