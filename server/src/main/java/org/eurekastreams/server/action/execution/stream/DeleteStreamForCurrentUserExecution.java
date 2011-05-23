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

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.DeleteStreamRequest;

/**
 * Delete a stream for the current user.
 */
public class DeleteStreamForCurrentUserExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Domain mapper for deleting person_stream entry.
     */
    private final DomainMapper<DeleteStreamRequest, Boolean> deleteAndReorderStreamsMapper;

    /**
     * Constructor.
     * 
     * @param inDeleteAndReorderStreamsDbMapper
     *            Domain mapper for deleting person_stream entry.
     */
    public DeleteStreamForCurrentUserExecution(
            final DomainMapper<DeleteStreamRequest, Boolean> inDeleteAndReorderStreamsDbMapper)
    {
        deleteAndReorderStreamsMapper = inDeleteAndReorderStreamsDbMapper;
    }

    /**
     * Adds a stream for the current user.
     * 
     * @param inActionContext
     *            the action context.
     * @return the stream ID.
     * @exception ExecutionException
     *                not expected.
     */
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        deleteAndReorderStreamsMapper.execute(new DeleteStreamRequest(
                inActionContext.getPrincipal().getId(), (Long) inActionContext.getParams()));

        return inActionContext.getParams();
    }
}
