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
package org.eurekastreams.server.action.execution;

import java.io.Serializable;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.async.AsyncActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.dto.StreamDiscoverListsDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Execution strategy to regenerate the Stream Discover Lists in cache.
 */
public class RegenerateStreamDiscoverListsExecution implements ExecutionStrategy<AsyncActionContext>
{
    /**
     * Mapper to get the stream discover lists mapper and force a cache refresh.
     */
    private DomainMapper<Serializable, StreamDiscoverListsDTO> streamDiscoverListsMapper;

    /**
     * Constructor.
     * 
     * @param inStreamDiscoverListsMapper
     *            mapper to get the stream discvoer lists mapper and force a cache refresh.
     */
    public RegenerateStreamDiscoverListsExecution(
            final DomainMapper<Serializable, StreamDiscoverListsDTO> inStreamDiscoverListsMapper)
    {
        streamDiscoverListsMapper = inStreamDiscoverListsMapper;
    }

    /**
     * Update the Stream Discover lists by executing the force-refresh mapper.
     * 
     * @param inActionContext
     *            the action context
     * @return nothing important
     * @throws ExecutionException
     *             on error
     */
    @Override
    public Serializable execute(final AsyncActionContext inActionContext) throws ExecutionException
    {
        streamDiscoverListsMapper.execute(null);
        return Boolean.TRUE;
    }

}
