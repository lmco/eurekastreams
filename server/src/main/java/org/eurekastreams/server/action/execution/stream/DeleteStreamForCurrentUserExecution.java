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
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

/**
 * Delete a stream for the current user.
 */
@SuppressWarnings("deprecation")
public class DeleteStreamForCurrentUserExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Mapper used to retrieve and save the page that holds the streams.
     */
    private final FindByIdMapper<Person> personMapper;

    /**
     * Constructor.
     * 
     * @param inPersonMapper
     *            person mapper.
     */
    public DeleteStreamForCurrentUserExecution(final FindByIdMapper<Person> inPersonMapper)
    {
        personMapper = inPersonMapper;
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
        Person person = personMapper.execute(new FindByIdRequest("Person", inActionContext.getPrincipal().getId()));

        List<Stream> streams = person.getStreams();
        Long streamId = (Long) inActionContext.getParams();

        for (Stream s : streams)
        {
            if (s.getId() == streamId)
            {
                streams.remove(s);
                break;
            }
        }

        personMapper.flush();

        return streamId;
    }
}
