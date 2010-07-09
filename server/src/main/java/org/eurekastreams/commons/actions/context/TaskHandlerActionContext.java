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

import java.util.List;

import org.eurekastreams.commons.server.UserActionRequest;

/**
 * This class represents a specialized context for TaskHandler actions. This context carries along the original context,
 * as well as the list of {@link UserActionRequest} objects that are build up during execution and submitted afterwards.
 *
 * @param <T>
 *            - Type of ActionContext that is contained within this specialized context.
 */
public class TaskHandlerActionContext<T extends ActionContext>
{
    /**
     * Local instance of the ActionContext that is being wrapped in this context.
     */
    private T actionContext;

    /**
     * Instance of the List of {@link UserActionRequest} objects for this context.
     */
    private List<UserActionRequest> userActionRequests;

    /**
     * Constructor.
     *
     * @param inActionContext
     *            - instance of the ActionContext that is wrapped and passed on to a TaskHandlerAction.
     * @param inUserActionRequests
     *            - List of {@link UserActionRequest} objects for the TaskHandlerAction to add to.
     */
    public TaskHandlerActionContext(final T inActionContext, final List<UserActionRequest> inUserActionRequests)
    {
        actionContext = inActionContext;
        userActionRequests = inUserActionRequests;
    }

    /**
     * Retrieve the current {@link ActionContext} contained within this context.
     *
     * @return current {@link ActionContext} contained within this context.
     */
    public T getActionContext()
    {
        return actionContext;
    }

    /**
     * Retrieve the current List of {@link UserActionRequest} objects.
     *
     * @return current List of {@link UserActionRequest} objects.
     */
    public List<UserActionRequest> getUserActionRequests()
    {
        return userActionRequests;
    }
}
