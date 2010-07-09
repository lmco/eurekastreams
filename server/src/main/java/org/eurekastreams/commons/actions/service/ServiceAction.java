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

import org.eurekastreams.commons.actions.Action;
import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.ValidationStrategy;

/**
 * Service based implementation of the Action interface.
 * Service actions are intended to be called through the GWT RPC servlet
 * and populated with an appropriate ServiceActionContext.
 *
 */
public class ServiceAction implements Action
{
    /**
     * Instance of the {@link ValidationStrategy} for this action.
     */
    private final ValidationStrategy validationStrategy;
    
    /**
     * Instance of the {@link AuthorizationStrategy} for this action.
     */
    private final AuthorizationStrategy authorizationStrategy;
    
    /**
     * Instance of the {@link ExecutionStrategy} for this action.
     */
    private final ExecutionStrategy executionStrategy;
    
    /**
     * State of the readOnly flag for this action.
     */
    private final boolean readOnly;

    /**
     * Constructor for the ServiceAction class.
     * @param inValidationStrategy - instance of the {@link ValidationStrategy} for this action.
     * @param inAuthorizationStrategy - instance of the {@link AuthorizationStrategy} for this action.
     * @param inExecutionStrategy - instance of the {@link ExecutionStrategy} for this action.
     * @param inReadOnly - read-only state for this action implementation.
     */
    ServiceAction(final ValidationStrategy inValidationStrategy, final AuthorizationStrategy inAuthorizationStrategy,
            final ExecutionStrategy inExecutionStrategy, final boolean inReadOnly)
    {
        validationStrategy = inValidationStrategy;
        authorizationStrategy = inAuthorizationStrategy;
        executionStrategy = inExecutionStrategy;
        readOnly = inReadOnly;
    }

    /**
     * {@inheritDoc}
     */
    public AuthorizationStrategy getAuthorizationStrategy()
    {
        return authorizationStrategy;
    }

    /**
     * {@inheritDoc}
     */
    public ExecutionStrategy getExecutionStrategy()
    {
        return executionStrategy;
    }

    /**
     * {@inheritDoc}
     */
    public ValidationStrategy getValidationStrategy()
    {
        return validationStrategy;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isReadOnly()
    {
        return readOnly;
    }

}
