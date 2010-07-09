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


/**
 * This interface describes the fundamental parts of an action.  Actions represent the
 * business logic layer of EurekaStreams.  The Action Framework consists of Authentication,
 * Authorization and Transaction Management at a high level.
 *
 *
 */
public interface Action
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
     * Main business logic for an Action.
     * @return instance of an {@link ExecutionStrategy}.
     */
    ExecutionStrategy getExecutionStrategy();
}
