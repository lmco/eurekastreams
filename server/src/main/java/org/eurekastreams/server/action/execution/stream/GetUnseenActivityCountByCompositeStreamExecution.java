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
package org.eurekastreams.server.action.execution.stream;

import java.io.Serializable;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;

/**
 * Gets the unseen activity given an exector.
 *
 */
public class GetUnseenActivityCountByCompositeStreamExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * The get activity executor.
     */
    private ExecutionStrategy getActivitiesByCompositeStreamExecutor;

    /**
     * Default constructor.
     *
     * @param inExecutionStrategy
     *            the executor.
     */
    public GetUnseenActivityCountByCompositeStreamExecution(final ExecutionStrategy inExecutionStrategy)
    {
        getActivitiesByCompositeStreamExecutor = inExecutionStrategy;
    }

    /**
     * Execute the executor and return it's size.
     *
     * @param inActionContext
     *            the action context.
     * @throws ExecutionException
     *             the exception.
     * @return the size.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        PagedSet<ActivityDTO> activities = (PagedSet<ActivityDTO>) getActivitiesByCompositeStreamExecutor
                .execute(inActionContext);
        return Integer.valueOf(activities.getPagedSet().size());
    }

}
