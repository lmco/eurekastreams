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
package org.eurekastreams.commons.actions.context;

import java.io.Serializable;
import java.util.Map;

/**
 * This is the context of the action. Each execution of an action has a unique context described by this interface.
 *
 */
public interface ActionContext extends Serializable
{
    /**
     * Retrieve the parameters of the current action execution.
     *
     * @return instance of the parameters for the current action execution.
     */
    Serializable getParams();

    /**
     * Retrieve the execution state of the Action. This is an instance maintained by the controller to allow state to be
     * passed between the ValidationStrategy, AuthorizationStrategy and ExecutionStrategy
     * instances.
     *
     * @return instance of the state for the current execution.
     */
    Map<String, Object> getState();
}
