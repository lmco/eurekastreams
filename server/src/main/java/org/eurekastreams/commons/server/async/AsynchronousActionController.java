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
package org.eurekastreams.commons.server.async;

import java.io.Serializable;

import org.eurekastreams.commons.actions.TaskHandlerAction;
import org.eurekastreams.commons.actions.async.AsyncAction;
import org.eurekastreams.commons.actions.context.async.AsyncActionContext;

/**
 * Interface for executing an AsyncAction.
 */
public interface AsynchronousActionController
{

    /**
     * Execute the supplied {@link AsyncAction} with the given {@link AsyncActionContext}.
     * 
     * @param inAsyncActionContext
     *            - instance of the {@link AsyncActionContext} with which to execution the {@link AsyncAction}.
     * @param inAsyncAction
     *            - instance of the {@link AsyncAction} to execute.
     * @return - results from the execution of the AsyncAction.
     * 
     *         - GeneralException - when an unexpected error occurs. - ValidationException - when a
     *         {@link ValidationException} occurs. - ExecutionException - when an {@link ExecutionException} occurs.
     */
    Serializable execute(final AsyncActionContext inAsyncActionContext, final AsyncAction inAsyncAction);

    /**
     * This method executes a {@link TaskHandlerAction} with the supplied {@link AsyncActionContext}.
     * 
     * @param inAsyncActionContext
     *            - instance of the {@link AsyncActionContext} associated with this request.
     * @param inTaskHandlerAction
     *            - instance of the {@link TaskHandlerAction}.
     * @return - results of the execution.
     * 
     *         - GeneralException - when an unexpected error occurs. - ValidationException - when a
     *         {@link ValidationException} occurs. - ExecutionException - when an {@link ExecutionException} occurs.
     */
    Serializable execute(final AsyncActionContext inAsyncActionContext, final TaskHandlerAction inTaskHandlerAction);

}
