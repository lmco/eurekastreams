/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution;

import java.io.Serializable;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.stream.ChangeGroupActivitySubscriptionExecutionRequest;

/**
 * Execution strategy to set a person's group activity notification for a group.
 */
public class ChangeGroupActivitySubscriptionExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Set the current user's group new activity subscription.
     * 
     * @param inActionContext
     *            the action context with the ChangeGroupActivitySubscriptionExecutionRequest
     * @return true
     * @throws ExecutionException
     *             on error
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        if (!(inActionContext.getParams() instanceof ChangeGroupActivitySubscriptionExecutionRequest))
        {
            return new Boolean(false);
        }

        ChangeGroupActivitySubscriptionExecutionRequest request = //
        (ChangeGroupActivitySubscriptionExecutionRequest) inActionContext.getParams();

        return true;
    }
}
