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

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;

/**
 * The AuthorizationStrategy represents the logic that restricts execution privileges of an action.
 *
 * @param <T>
 *            action context type.
 */
public interface AuthorizationStrategy<T extends PrincipalActionContext>
{
    /**
     * Perform the authorization business logic with the given context.
     * 
     * @param inActionContext
     *            context of the Action being authorized.
     * @throws AuthorizationException
     *             on error.
     */
    void authorize(T inActionContext) throws AuthorizationException;
}
