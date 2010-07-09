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

import java.io.Serializable;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;

/**
 * This class contains the business logic for the Action.
 *
 * @param <T> - type of ActionContext that will be wrapped by the TaskHandlerActionContext.
 */
public interface TaskHandlerExecutionStrategy<T extends ActionContext>
{
    /**
     * Executes the business logic for the Action and assembles the UserActionRequest objects
     * that need to be submitted to the TaskHandler.
     * @param inActionContext - instance of TaskHandlerActionContext that contains the {@link ActionContext} for
     * this execution strategy as well as a List container for UserActionRequest objects assembled during
     * execution.
     * @return {@link Serializable} results from the execution.
     */
    Serializable execute(TaskHandlerActionContext<T> inActionContext);
}
