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
package org.eurekastreams.server.action.execution.profile;

import java.io.Serializable;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;

/**
 * Action for wrapping existing persistResourceExecution functionality. This allows for control over return value to
 * client.
 * 
 * @param <originalReturnType>
 *            Return type of wrapped persistResourceExecution.
 * @param <newReturnType>
 *            New type to be returned.
 */
public class PersistResourceWrapperExecution<originalReturnType, newReturnType extends Serializable> implements
        TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /**
     * Persist resource action.
     */
    private TaskHandlerExecutionStrategy<PrincipalActionContext> persistResourceExecution;

    /**
     * Transformer to convert persistResource result to result sent to client.
     */
    private Transformer<originalReturnType, newReturnType> transformer;

    /**
     * Constructor.
     * 
     * @param inPersistResourceExecution
     *            PersistResourceExecution for creating group.
     * @param inTransformer
     *            Transformer to convert persistResource result to result sent to client.
     */
    public PersistResourceWrapperExecution(
            final TaskHandlerExecutionStrategy<PrincipalActionContext> inPersistResourceExecution,
            final Transformer<originalReturnType, newReturnType> inTransformer)
    {
        persistResourceExecution = inPersistResourceExecution;
        transformer = inTransformer;
    }

    /**
     * Create a group. This is a pass through to old PersistResourceExecution so we can retain functionality, but not
     * return DomainEntities to client.
     * 
     * @param inActionContext
     *            the action context.
     * @return New return type of original persistResourceExecution.
     */
    @Override
    public newReturnType execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
    {
        return transformer.transform((originalReturnType) persistResourceExecution.execute(inActionContext));
    }

}
