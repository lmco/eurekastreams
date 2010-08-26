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
package org.eurekastreams.commons.server.service;

import java.io.Serializable;

import org.eurekastreams.commons.actions.TaskHandlerAction;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;

/**
 * Interface for executing a ServiceAction.
 */
public interface ActionController
{

    /**
     * Execute the supplied {@link ServiceAction} with the given {@link ServiceActionContext}.
     * 
     * @param inServiceActionContext
     *            - instance of the {@link ServiceActionContext} with which to execution the {@link ServiceAction}.
     * @param inServiceAction
     *            - instance of the {@link ServiceAction} to execute.
     * @return - results from the execution of the ServiceAction.
     * 
     *         - GeneralException - when an unexpected error occurs. - ValidationException - when a
     *         {@link ValidationException} occurs. - AuthorizationException - when an {@link AuthorizationException}
     *         occurs. - ExecutionException - when an {@link ExecutionException} occurs.
     */
    Serializable execute(final ServiceActionContext inServiceActionContext, final ServiceAction inServiceAction);

    /**
     * This method executes a {@link TaskHandlerAction} with the supplied {@link ServiceActionContext}.
     * 
     * @param inServiceActionContext
     *            - instance of the {@link ServiceActionContext} associated with this request.
     * @param inTaskHandlerAction
     *            - instance of the {@link TaskHandlerAction}.
     * @return - results of the execution.
     * 
     *         - GeneralException - when an unexpected error occurs. - ValidationException - when a
     *         {@link ValidationException} occurs. - AuthorizationException - when an {@link AuthorizationException}
     *         occurs. - ExecutionException - when an {@link ExecutionException} occurs.
     */
    Serializable execute(final ServiceActionContext inServiceActionContext, // \n
            final TaskHandlerAction inTaskHandlerAction);

}