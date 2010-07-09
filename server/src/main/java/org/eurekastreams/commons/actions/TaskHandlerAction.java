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
package org.eurekastreams.commons.actions;

import org.eurekastreams.commons.task.TaskHandler;

/**
 * This interface describes the contract needed for an action to be able to submit
 * UserActionRequest objects to the {@link TaskHandler}.
 *
 */
public interface TaskHandlerAction
{
    /**
     * This flag indicates whether or not the Action implementation is read-only.  This
     * determines how the action controller will load the appropriate transaction.  There
     * are significant savings in running a read-only transaction over a write-enabled
     * transaction.
     * @return true if transaction is read-only.
     */
    boolean isReadOnly();

    /**
     * An Action's validation strategy is the first checkpoint in Action exection.  This
     * strategy allows an action to ensure that the input values are valid with respect
     * to the action being called.
     * @return instance of a {@link ValidationStrategy}.
     */
    ValidationStrategy getValidationStrategy();

    /**
     * The Authorization strategy provides the logic to determine if the current context
     * of the action is authorized to execute.  This is different from validation in that
     * it assumes that the inputs are valid and executes the business logic that controls
     * the execution of the action.
     * @return instance of an {@link AuthorizationStrategy}.
     */
    AuthorizationStrategy getAuthorizationStrategy();

    /**
     * The Execution Strategy that will assemble UserActionRequest objects to be
     * submitted to the {@link TaskHandler} instance within the implementing class.
     * @return instance of a {@link TaskHandlerExecutionStrategy}.
     */
    TaskHandlerExecutionStrategy getExecutionStrategy();

    /**
     * Retrieve the {@link TaskHandler}.
     * @return - instance of the {@link TaskHandler}.
     */
    TaskHandler getTaskHandler();

}
