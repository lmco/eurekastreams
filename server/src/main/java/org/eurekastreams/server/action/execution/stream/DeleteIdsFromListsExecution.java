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

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.action.request.stream.DeleteIdsFromListsRequest;
import org.eurekastreams.server.persistence.mappers.cache.RemoveIdsFromLists;

/**
 * This is the execution strategy for the action that will remove a given list of ids from a given list of cache
 * keys that point to list of ids.
 */
public class DeleteIdsFromListsExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Mapper that will remove ids from cached lists.
     */
    private RemoveIdsFromLists deleteMapper;

    /**
     * Constructor.
     * 
     * @param inDeleteMapper
     *            the delete mapper.
     */
    public DeleteIdsFromListsExecution(final RemoveIdsFromLists inDeleteMapper)
    {
        deleteMapper = inDeleteMapper;
    }

    /**
     * Execute the cache mapper to remove the necessary ids from the necessary lists.
     * 
     * @param inActionContext
     *            {@link ActionContext}.
     * @return true.
     */
    @Override
    public Boolean execute(final ActionContext inActionContext)
    {
        deleteMapper.execute((DeleteIdsFromListsRequest) inActionContext.getParams());
        return Boolean.TRUE;
    }
}
