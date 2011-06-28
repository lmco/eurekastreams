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
package org.eurekastreams.server.action.execution.stream;

import java.io.Serializable;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.dto.FeaturedStreamDTO;
import org.eurekastreams.server.domain.stream.FeaturedStream;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;

/**
 * Action to add a featured stream.
 */
public class AddFeaturedStreamExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    // NOTE: Don't refactor these mappers to findById mappers, as these mappers should just be returning proxy objects
    // based on id. No need to hit DB to pull objects back.
    /**
     * StreamScope mapper.
     */
    private DomainMapper<Long, StreamScope> streamScopeProxyMapper;

    /**
     * Insert mapper.
     */
    private DomainMapper<PersistenceRequest<FeaturedStream>, Long> insertMapper;

    /**
     * Constructor.
     * 
     * @param inStreamScopeProxyMapper
     *            StreamScope mapper.
     * @param inInsertMapper
     *            Insert mapper.
     */
    public AddFeaturedStreamExecution(final DomainMapper<Long, StreamScope> inStreamScopeProxyMapper,
            final DomainMapper<PersistenceRequest<FeaturedStream>, Long> inInsertMapper)
    {
        streamScopeProxyMapper = inStreamScopeProxyMapper;
        insertMapper = inInsertMapper;
    }

    /**
     * Feature a stream, then kick off a task to rebuild the discover page cache.
     * 
     * @param inActionContext
     *            the action context
     * @return the new entity id
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        FeaturedStreamDTO dto = (FeaturedStreamDTO) inActionContext.getActionContext().getParams();

        // get streamScope from context state, or create proxy verison if not there.
        StreamScope streamScope = (inActionContext.getActionContext().getState().get("streamScope") == null) ? //
        streamScopeProxyMapper.execute(dto.getStreamId())
                : (StreamScope) inActionContext.getActionContext().getState().get("streamScope");

        FeaturedStream entity = new FeaturedStream(dto.getDescription(), streamScope);

        // insert into datastore.
        Long entityId = insertMapper.execute(new PersistenceRequest<FeaturedStream>(entity));

        // kick off the action to rebuild the Discover Page cache - but don't delete the key now, because it takes
        // seconds to rebuild
        inActionContext.getUserActionRequests().add(
                new UserActionRequest("regenerateStreamDiscoverListsJob", null, null));

        return entityId;
    }
}
