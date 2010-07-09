/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.actions.strategies;

import java.io.Serializable;
import java.util.Map;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;

/**
 * Persists a resource.
 *
 * @param <T>
 *            the resource type.
 */
public interface ResourcePersistenceStrategy<T>
{
    /**
     * Gets a resource.
     *
     * @param inActionContext
     *            the action context.
     * @param inFields
     *            the resources fields.
     * @return the resource.
     */
    T get(final TaskHandlerActionContext<PrincipalActionContext> inActionContext, Map<String, Serializable> inFields);

    /**
     * Persists a resource.
     *
     * @param inActionContext
     *            the action context.
     * @param inFields
     *            form data from the user describing the resource
     * @param inResource
     *            the resource.
     * @throws Exception
     *             Note: can this be made more narrow?
     */
    void persist(TaskHandlerActionContext<PrincipalActionContext> inActionContext, Map<String, Serializable> inFields,
            T inResource) throws Exception;
}
