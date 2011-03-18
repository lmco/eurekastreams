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
package org.eurekastreams.server.action.execution.stream;

import java.io.Serializable;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.action.request.stream.SetSharedResourceLikeRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.SetSharedResourceLikeMapperRequest;

/**
 * Execution strategy to set the liked/unliked status of a shared resource.
 */
public class SetSharedResourceLikeExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /**
     * Mapper to update the like/unlike status of a shared resource.
     */
    private DomainMapper<SetSharedResourceLikeMapperRequest, Boolean> setLikedResourceStatusMapper;

    /**
     * @param inSetLikedResourceStatusMapper
     *            the mapper to use to update the person's liked status of the shared resource
     */
    public SetSharedResourceLikeExecution(
            final DomainMapper<SetSharedResourceLikeMapperRequest, Boolean> inSetLikedResourceStatusMapper)
    {
        setLikedResourceStatusMapper = inSetLikedResourceStatusMapper;
    }

    /**
     * Set the liked/unlked status of a shared resource for a person.
     * 
     * @param inActionContext
     *            the action context.
     * @return true
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
    {
        SetSharedResourceLikeRequest request = (SetSharedResourceLikeRequest) inActionContext.getActionContext()
                .getParams();
        final Long personId = inActionContext.getActionContext().getPrincipal().getId();

        SetSharedResourceLikeMapperRequest mapperRequest = new SetSharedResourceLikeMapperRequest(personId, request
                .getUniqueKey(), request.getResourceType(), request.getLikes());

        setLikedResourceStatusMapper.execute(mapperRequest);
        return new Boolean(true);
    }

}
